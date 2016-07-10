/**
 * Project Name:elasticsearch-health
 * File Name:ElasticsearchRepository.java
 * Package Name:com.oneapm.elasticsearch.health.repository
 * Date: 
 * Copyright (c) 2016, All Rights Reserved.
 *
 */
package com.oneapm.elasticsearch.health.repository;

import lombok.Getter;
import lombok.Setter;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;

/**
 * ClassName:ElasticsearchRepository <br/>
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
public class ElasticsearchRepository {
    
    protected String          clusterName;
    
    protected String          hostName;
    
    protected Integer         port;
    
    protected Settings        settings;
    
    protected TransportClient client;
    
}
