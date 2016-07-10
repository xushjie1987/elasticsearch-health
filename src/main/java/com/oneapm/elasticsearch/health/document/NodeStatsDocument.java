/**
 * Project Name:elasticsearch-health
 * File Name:NodeStatsDocument.java
 * Package Name:com.oneapm.elasticsearch.health.document
 * Date: 
 * Copyright (c) 2016, All Rights Reserved.
 *
 */
package com.oneapm.elasticsearch.health.document;

import lombok.Getter;
/**
 * ClassName:NodeStatsDocument <br/>
 * Function:  <br/>
 * Date:       <br/>
 * @author   xushjie
 * @version  
 * @since    JDK 1.8
 * @see 	 
 */
import lombok.Setter;

@Getter
@Setter
public class NodeStatsDocument {
    
    private String cluster_name;
    
    @Getter
    @Setter
    public static class Nodes {
        
        private Long   timestamp;
        
        private String name;
        
        private String host;
        
        private OS     os;
        
    }
    
    @Getter
    @Setter
    public static class OS {
        
        private Long   timestamp;
        
        private Double load_average;
        
    }
    
    private Nodes[] nodes;
    
}
