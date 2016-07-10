/**
 * Project Name:elasticsearch-health
 * File Name:ClusterTask.java
 * Package Name:com.oneapm.elasticsearch.health.task
 * Date: 
 * Copyright (c) 2016, All Rights Reserved.
 *
 */
package com.oneapm.elasticsearch.health.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.oneapm.elasticsearch.health.document.NodeStatsDocument;
import com.oneapm.elasticsearch.health.repository.ElasticsearchSource;
import com.oneapm.elasticsearch.health.repository.ElasticsearchStorage;

/**
 * ClassName:ClusterTask <br/>
 * Function: <br/>
 * Date: <br/>
 * 
 * @author xushjie
 * @version
 * @since JDK 1.8
 * @see
 */
@Component(value = "clusterTask")
@EnableScheduling
public class ClusterTask {
    
    public static final Logger  log = LoggerFactory.getLogger(ClusterTask.class);
    
    @Autowired
    public ElasticsearchStorage elasticsearchStorage;
    
    @Autowired
    public ElasticsearchSource  elasticsearchSource;
    
    /**
     * pullClusterStats: <br/>
     * 
     * @author xushjie
     * @since JDK 1.8
     */
    @Scheduled(fixedRate = 60000)
    public void pullClusterStats() {
        //
        log.info("in...");
        //
        NodeStatsDocument nodeStats = elasticsearchSource.pullClusterStats();
        //
        elasticsearchStorage.insertNodeStats(nodeStats);
        //
        log.info("out...");
    }
    
}
