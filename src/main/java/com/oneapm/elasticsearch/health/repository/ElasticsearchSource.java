/**
 * Project Name:elasticsearch-health
 * File Name:ElasticsearchSource.java
 * Package Name:com.oneapm.elasticsearch.health.repository
 * Date: 
 * Copyright (c) 2016, All Rights Reserved.
 *
 */
package com.oneapm.elasticsearch.health.repository;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.stream.Stream;

import lombok.Getter;
import lombok.Setter;

import org.elasticsearch.action.admin.cluster.node.stats.NodesStatsResponse;
import org.elasticsearch.client.ClusterAdminClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oneapm.elasticsearch.health.document.NodeStatsDocument;
import com.oneapm.elasticsearch.health.document.NodeStatsDocument.Nodes;
import com.oneapm.elasticsearch.health.document.NodeStatsDocument.OS;

/**
 * ClassName:ElasticsearchSource <br/>
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
public class ElasticsearchSource extends ElasticsearchRepository {
    
    public static final Logger log = LoggerFactory.getLogger(ElasticsearchSource.class);
    
    private ClusterAdminClient admin;
    
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
        admin = client.admin()
                      .cluster();
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
     * pullClusterStats: <br/>
     * 
     * @author xushjie
     * @return
     * @since JDK 1.8
     */
    public NodeStatsDocument pullClusterStats() {
        //
        NodesStatsResponse response = admin.prepareNodesStats()
                                           .all()
                                           .get();
        //
        NodeStatsDocument nodeStats = new NodeStatsDocument();
        nodeStats.setCluster_name(response.getClusterName()
                                          .value());
        nodeStats.setNodes(Stream.of(response.getNodes())
                                 .map(ns -> {
                                     Nodes n = new Nodes();
                                     n.setHost(ns.getNode()
                                                 .getHostName());
                                     n.setName(ns.getNode()
                                                 .getName());
                                     n.setTimestamp(ns.getTimestamp());
                                     OS o = new OS();
                                     o.setTimestamp(ns.getOs()
                                                      .getTimestamp());
                                     o.setLoad_average(ns.getOs()
                                                         .getLoadAverage());
                                     n.setOs(o);
                                     return n;
                                 })
                                 .toArray(Nodes[]::new));
        //
        return nodeStats;
    }
    
}
