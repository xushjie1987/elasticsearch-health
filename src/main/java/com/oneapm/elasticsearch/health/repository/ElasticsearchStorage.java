/**
 * Project Name:elasticsearch-health
 * File Name:ElasticsearchStorage.java
 * Package Name:com.oneapm.elasticsearch.health.repository
 * Date: 
 * Copyright (c) 2016, All Rights Reserved.
 *
 */
package com.oneapm.elasticsearch.health.repository;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.InternalHistogram;
import org.elasticsearch.search.aggregations.bucket.nested.InternalNested;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.metrics.max.InternalMax;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.oneapm.elasticsearch.health.document.NodeStatsDocument;
import com.oneapm.elasticsearch.health.util.JSONUtil;

/**
 * ClassName:ElasticsearchStorage <br/>
 * Function: <br/>
 * Date: <br/>
 * 
 * @author xushjie
 * @version
 * @since JDK 1.8
 * @see
 */
@Getter
@Setter
public class ElasticsearchStorage extends ElasticsearchRepository {
    
    public static final Logger log = LoggerFactory.getLogger(ElasticsearchStorage.class);
    
    private String             index;
    
    private String             type;
    
    /**
     * init: <br/>
     * 
     * @author xushjie
     * @since JDK 1.8
     */
    public void init() {
        settings = Settings.settingsBuilder()
                           .put("cluster.name",
                                clusterName)
                           .build();
        try {
            client = TransportClient.builder()
                                    .settings(settings)
                                    .build()
                                    .addTransportAddresses(new InetSocketTransportAddress(InetAddress.getByName(hostName),
                                                                                          port));
        } catch (UnknownHostException e) {
            log.error("{}",
                      e);
            System.exit(-1);
        }
    }
    
    /**
     * destroy: <br/>
     * 
     * @author xushjie
     * @since JDK 1.8
     */
    public void destroy() {
        client.close();
    }
    
    /**
     * insertNodeStats: <br/>
     * 
     * @author xushjie
     * @param nodeStats
     * @since JDK 1.8
     */
    public void insertNodeStats(NodeStatsDocument nodeStats) {
        //
        IndexResponse response = client.prepareIndex(index,
                                                     type)
                                       .setSource(JSONUtil.toJSON(nodeStats))
                                       .get();
        //
        log.info("{}",
                 response.toString());
    }
    
    /**
     * ClassName: OSStats <br/>
     * Function: <br/>
     * date: <br/>
     *
     * @author xushjie
     * @version ElasticsearchStorage
     * @since JDK 1.8
     */
    @Getter
    @Setter
    public static class OSStats {
        
        private Set<String>               nodeSet     = new HashSet<String>();
        
        private List<Map<String, Object>> osStatsList = new ArrayList<Map<String, Object>>();
        
        private Map<String, Object>       currentMap  = null;
        
        /**
         * putMap: <br/>
         * 
         * @author xushjie
         * @since JDK 1.8
         */
        public void putMap() {
            currentMap = new HashMap<String, Object>();
            osStatsList.add(currentMap);
        }
        
        /**
         * putStats: <br/>
         * 
         * @author xushjie
         * @param node
         * @param load
         * @since JDK 1.8
         */
        public void putStats(String node,
                             double load) {
            if (!StringUtils.isBlank(node)) {
                if (!nodeSet.contains(node)) {
                    nodeSet.add(node);
                }
                if (currentMap != null) {
                    currentMap.put(node,
                                   load);
                }
            }
        }
        
        /**
         * putDate: <br/>
         * 
         * @author xushjie
         * @param date
         * @since JDK 1.8
         */
        public void putDate(String date) {
            if (!StringUtils.isBlank(date)) {
                if (currentMap != null) {
                    currentMap.put("",
                                   date);
                }
            }
        }
        
        /**
         * toVectors: <br/>
         * 
         * @author xushjie
         * @return
         * @since JDK 1.8
         */
        public Object[][] toVectors() {
            //
            if (osStatsList.size() == 0) {
                return new Object[][] {};
            }
            //
            List<Object[]> vectors = new ArrayList<Object[]>();
            osStatsList.forEach(m -> {
                List<Object> v = new ArrayList<Object>(nodeSet.size());
                v.add(m.get(""));
                nodeSet.forEach(n -> {
                    v.add(m.containsKey(n)
                                          ? m.get(n)
                                          : 0.0);
                });
                vectors.add(v.toArray());
            });
            vectors.add(Lists.asList("date",
                                     nodeSet.toArray())
                             .toArray());
            List<Object[]> reversed = Lists.reverse(vectors);
            Object[][] convert = new Object[reversed.size()][];
            int i = 0;
            for (Object[] item : reversed) {
                convert[i++] = item;
            }
            return convert;
        }
    }
    
    /**
     * requestOSStats: <br/>
     * 
     * @author xushjie
     * @param gte
     * @param lte
     * @return
     * @since JDK 1.8
     */
    public Object[][][] requestOSStats(String gte,
                                       String lte) {
        return requestOSStats(gte,
                              lte,
                              DateHistogramInterval.MINUTE);
    }
    
    /**
     * requestOSStats: <br/>
     * 
     * @author xushjie
     * @param gte
     * @param lte
     * @param interval
     * @return
     * @since JDK 1.8
     */
    public Object[][][] requestOSStats(String gte,
                                       String lte,
                                       DateHistogramInterval interval) {
        //
        SearchRequestBuilder request = client.prepareSearch(index)
                                             .setTypes(type)
                                             .setQuery(QueryBuilders.nestedQuery("nodes",
                                                                                 QueryBuilders.rangeQuery("nodes.timestamp")
                                                                                              .gte(gte)
                                                                                              .lte(lte))
                                                                    .scoreMode("avg"))
                                             .setFrom(0)
                                             .setSize(0)
                                             .addAggregation(AggregationBuilders.nested("nested_nodes")
                                                                                .path("nodes")
                                                                                .subAggregation(AggregationBuilders.dateHistogram("nodes_time")
                                                                                                                   .field("nodes.timestamp")
                                                                                                                   .interval(interval)
                                                                                                                   .format("HH:mm_yyyy-MM-dd")
                                                                                                                   .timeZone("+08:00")
                                                                                                                   .subAggregation(AggregationBuilders.terms("nodes_terms")
                                                                                                                                                      .field("nodes.name")
                                                                                                                                                      .subAggregation(AggregationBuilders.max("max_os_load")
                                                                                                                                                                                         .field("nodes.os.load_average"))
                                                                                                                                                      .subAggregation(AggregationBuilders.max("max_os_mem_total")
                                                                                                                                                                                         .field("nodes.os.mem.total_in_bytes"))
                                                                                                                                                      .subAggregation(AggregationBuilders.max("max_os_mem_free")
                                                                                                                                                                                         .field("nodes.os.mem.free_in_bytes"))
                                                                                                                                                      .subAggregation(AggregationBuilders.max("max_os_mem_used")
                                                                                                                                                                                         .field("nodes.os.mem.used_in_bytes"))
                                                                                                                                                      .subAggregation(AggregationBuilders.max("max_os_mem_fp")
                                                                                                                                                                                         .field("nodes.os.mem.free_percent"))
                                                                                                                                                      .subAggregation(AggregationBuilders.max("max_os_mem_up")
                                                                                                                                                                                         .field("nodes.os.mem.used_percent"))
                                                                                                                                                      .subAggregation(AggregationBuilders.max("max_os_swap_total")
                                                                                                                                                                                         .field("nodes.os.swap.total_in_bytes"))
                                                                                                                                                      .subAggregation(AggregationBuilders.max("max_os_swap_free")
                                                                                                                                                                                         .field("nodes.os.swap.free_in_bytes"))
                                                                                                                                                      .subAggregation(AggregationBuilders.max("max_os_swap_used")
                                                                                                                                                                                         .field("nodes.os.swap.used_in_bytes")))));
        log.info("{}",
                 request.toString());
        SearchResponse response = request.get();
        //
        Object[][][] stats = new Object[9][][];
        stats[0] = getStats(response,
                            StatsType.LOADS).toVectors();
        stats[1] = getStats(response,
                            StatsType.MEM_TOTAL).toVectors();
        stats[2] = getStats(response,
                            StatsType.MEM_USED).toVectors();
        stats[3] = getStats(response,
                            StatsType.MEM_FREE).toVectors();
        stats[4] = getStats(response,
                            StatsType.MEM_FP).toVectors();
        stats[5] = getStats(response,
                            StatsType.MEM_UP).toVectors();
        stats[6] = getStats(response,
                            StatsType.SWAP_TOTAL).toVectors();
        stats[7] = getStats(response,
                            StatsType.SWAP_USED).toVectors();
        stats[8] = getStats(response,
                            StatsType.SWAP_FREE).toVectors();
        //
        return stats;
    }
    
    /**
     * ClassName: StatsType <br/>
     * Function: <br/>
     * date: <br/>
     *
     * @author xushjie
     * @version ElasticsearchStorage
     * @since JDK 1.8
     */
    public static enum StatsType {
        LOADS("max_os_load", "nodes.os.load_average"),
        MEM_TOTAL("max_os_mem_total", "nodes.os.mem.total_in_bytes"),
        MEM_USED("max_os_mem_used", "nodes.os.mem.used_in_bytes"),
        MEM_FREE("max_os_mem_free", "nodes.os.mem.free_in_bytes"),
        MEM_FP("max_os_mem_fp", "nodes.os.mem.free_percent"),
        MEM_UP("max_os_mem_up", "nodes.os.mem.used_percent"),
        SWAP_TOTAL("max_os_swap_total", "nodes.os.swap.total_in_bytes"),
        SWAP_USED("max_os_swap_used", "nodes.os.swap.used_in_bytes"),
        SWAP_FREE("max_os_swap_free", "nodes.os.swap.free_in_bytes");
        public String aggregationName;
        public String field;
        
        /**
         * Creates a new instance of StatsType.
         * 
         * @param aggregationName
         * @param field
         */
        private StatsType(String aggregationName,
                          String field) {
            this.aggregationName = aggregationName;
            this.field = field;
        }
        
    }
    
    /**
     * getStats: <br/>
     * 
     * @author xushjie
     * @param response
     * @param type
     * @return
     * @since JDK 1.8
     */
    @SuppressWarnings("unchecked")
    private OSStats getStats(SearchResponse response,
                             StatsType type) {
        //
        OSStats stats = new OSStats();
        response.getAggregations()
                .forEach(a -> {
                    InternalNested ai = (InternalNested) a;
                    ai.getAggregations()
                      .forEach(h -> {
                          InternalHistogram<InternalHistogram.Bucket> hi = (InternalHistogram<InternalHistogram.Bucket>) h;
                          hi.getBuckets()
                            .forEach(hb -> {
                                // date
                                stats.putMap();
                                stats.putDate(hb.getKeyAsString());
                                hb.getAggregations()
                                  .forEach(t -> {
                                      StringTerms st = (StringTerms) t;
                                      st.getBuckets()
                                        .forEach(tb -> {
                                            Aggregation m = tb.getAggregations()
                                                              .get(type.aggregationName);
                                            InternalMax mi = (InternalMax) m;
                                            // max_data
                                            stats.putStats(tb.getKeyAsString(),
                                                           mi.getValue());
                                        });
                                  });
                            });
                      });
                });
        //
        return stats;
    }
    
    public void requestJVMStats() {
        
    }
    
}
