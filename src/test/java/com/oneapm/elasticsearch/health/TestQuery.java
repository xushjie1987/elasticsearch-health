/**
 * Project Name:elasticsearch-health
 * File Name:TestQuery.java
 * Package Name:com.oneapm.elasticsearch.health
 * Date: 
 * Copyright (c) 2016, All Rights Reserved.
 *
 */
package com.oneapm.elasticsearch.health;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.InternalHistogram;
import org.elasticsearch.search.aggregations.bucket.nested.InternalNested;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.metrics.max.InternalMax;
import org.junit.Test;

/**
 * ClassName:TestQuery <br/>
 * Function: <br/>
 * Date: <br/>
 * 
 * @author xushjie
 * @version
 * @since JDK 1.8
 * @see
 */
public class TestQuery {
    
    private Settings        settings = null;
    
    private TransportClient client   = null;
    
    {
        settings = Settings.settingsBuilder()
                           .put("cluster.name",
                                "cluster_1")
                           .build();
        try {
            client = TransportClient.builder()
                                    .settings(settings)
                                    .build()
                                    .addTransportAddresses(new InetSocketTransportAddress(InetAddress.getByName("10.45.10.213"),
                                                                                          9300));
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
    
    /**
     * testQuery01: <br/>
     * 
     * @author xushjie
     * @since JDK 1.8
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testQuery01() {
        //
        SearchRequestBuilder request = client.prepareSearch("health-es2")
                                             .setTypes("stats")
                                             .setQuery(QueryBuilders.nestedQuery("nodes",
                                                                                 QueryBuilders.rangeQuery("nodes.timestamp")
                                                                                              .gte("now-2m")
                                                                                              .lte("now"))
                                                                    .scoreMode("avg"))
                                             .setFrom(0)
                                             .setSize(0)
                                             .addAggregation(AggregationBuilders.nested("nested_nodes")
                                                                                .path("nodes")
                                                                                .subAggregation(AggregationBuilders.dateHistogram("nodes_time")
                                                                                                                   .field("nodes.timestamp")
                                                                                                                   .interval(DateHistogramInterval.MINUTE)
                                                                                                                   .format("yyyy-MM-dd_HH:mm")
                                                                                                                   .timeZone("+08:00")
                                                                                                                   .subAggregation(AggregationBuilders.terms("nodes_terms")
                                                                                                                                                      .field("nodes.name")
                                                                                                                                                      .subAggregation(AggregationBuilders.max("max_os_load")
                                                                                                                                                                                         .field("nodes.os.load_average")))));
        //
        System.out.println(request.toString());
        //
        SearchResponse response = request.get();
        //
        System.out.println(response.toString());
        //
        response.getAggregations()
                .forEach(a -> {
                    InternalNested ai = (InternalNested) a;
                    ai.getAggregations()
                      .forEach(h -> {
                          InternalHistogram<InternalHistogram.Bucket> hi = (InternalHistogram<InternalHistogram.Bucket>) h;
                          hi.getBuckets()
                            .forEach(hb -> {
                                System.out.print(hb.getKeyAsString() +
                                                 "\t\t");
                                hb.getAggregations()
                                  .forEach(t -> {
                                      StringTerms st = (StringTerms) t;
                                      st.getBuckets()
                                        .forEach(tb -> {
                                            System.out.print(tb.getKeyAsString() +
                                                             "\t\t");
                                            tb.getAggregations()
                                              .forEach(m -> {
                                                  InternalMax mi = (InternalMax) m;
                                                  System.out.println(mi.getValue());
                                              });
                                        });
                                  });
                            });
                      });
                });
    }
    
}
