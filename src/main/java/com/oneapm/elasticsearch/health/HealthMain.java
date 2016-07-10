/**
 * Project Name:elasticsearch-health
 * File Name:HealthMain.java
 * Package Name:com.oneapm.elasticsearch.health
 * Date:
 * Copyright (c) 2016, All Rights Reserved.
 *
 */
package com.oneapm.elasticsearch.health;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.oneapm.elasticsearch.health.repository.ElasticsearchSource;
import com.oneapm.elasticsearch.health.repository.ElasticsearchStorage;

/**
 * ClassName:HealthMain <br/>
 * Function: <br/>
 * Date: <br/>
 *
 * @author xushjie
 * @version
 * @since JDK 1.8
 * @see
 */
@SpringBootApplication(scanBasePackages = {"com.oneapm.elasticsearch.health"})
public class HealthMain {
    
    /**
     * elasticsearchSource: es2 <br/>
     * 
     * @author xushjie
     * @return
     * @since JDK 1.8
     */
    @Bean(name = "elasticsearchSource",
          initMethod = "init",
          destroyMethod = "destroy")
    public ElasticsearchSource elasticsearchSource() {
        ElasticsearchSource esSource = new ElasticsearchSource();
        esSource.setClusterName("test_1");
        esSource.setHostName("10.46.176.126");
        esSource.setPort(9300);
        return esSource;
    }
    
    /**
     * elasticsearchStorage: es3 <br/>
     * 
     * @author xushjie
     * @return
     * @since JDK 1.8
     */
    @Bean(name = "elasticsearchStorage",
          initMethod = "init",
          destroyMethod = "destroy")
    public ElasticsearchStorage elasticsearchStorage() {
        ElasticsearchStorage esStorage = new ElasticsearchStorage();
        esStorage.setClusterName("cluster_1");
        esStorage.setHostName("10.45.10.213");
        esStorage.setPort(9300);
        esStorage.setIndex("health-es2");
        esStorage.setType("stats");
        return esStorage;
    }
    
    /**
     * main: <br/>
     * 
     * @author xushjie
     * @param args
     * @since JDK 1.8
     */
    public static void main(String[] args) {
        SpringApplication.run(new Object[] {HealthMain.class},
                              args);
    }
    
}
