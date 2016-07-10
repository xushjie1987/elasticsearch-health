/**
 * Project Name:elasticsearch-health
 * File Name:ClusterStatsController.java
 * Package Name:com.oneapm.elasticsearch.health.controller
 * Date: 
 * Copyright (c) 2016, All Rights Reserved.
 *
 */
package com.oneapm.elasticsearch.health.controller;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.oneapm.elasticsearch.health.repository.ElasticsearchStorage;

/**
 * ClassName:ClusterStatsController <br/>
 * Function: <br/>
 * Date: <br/>
 * 
 * @author xushjie
 * @version
 * @since JDK 1.8
 * @see
 */
@RestController(value = "clusterStatsController")
@RequestMapping(path = {"/cluster"})
public class ClusterStatsController {
    
    public static final Integer MAX_POINTS = 101;
    
    @Autowired
    public ElasticsearchStorage elasticsearchStorage;
    
    /**
     * pullMetric: <br/>
     * 
     * @author xushjie
     * @return
     * @since JDK 1.8
     */
    @RequestMapping(path = {"/sample"},
                    method = {RequestMethod.GET})
    public Object[][] sample() {
        Object[][] objs = new Object[][] { {"year", "sale", "extend"}, {"2000", 100, 500}, {"2001", 200, 400}, {"2002", 400, 200}, {"2003", 800, 100}};
        return objs;
    }
    
    /**
     * os: <br/>
     * 
     * @author xushjie
     * @param startTime
     * @param endTime
     * @param interval
     * @return
     * @since JDK 1.8
     */
    @RequestMapping(path = {"/os/{startTime}/{endTime}"},
                    method = {RequestMethod.GET})
    public Object[][] os(@PathVariable(value = "startTime") String startTime,
                         @PathVariable(value = "endTime") String endTime,
                         @RequestParam(name = "interval",
                                       required = false,
                                       defaultValue = "") String interval) {
        //
        Object[][] result = null;
        if (StringUtils.isBlank(interval)) {
            result = elasticsearchStorage.requestOSStats(startTime,
                                                         endTime);
        } else {
            result = elasticsearchStorage.requestOSStats(startTime,
                                                         endTime,
                                                         new DateHistogramInterval(interval));
        }
        //
        return Arrays.copyOf(result,
                             result.length > MAX_POINTS
                                                       ? MAX_POINTS
                                                       : result.length);
    }
    
    @RequestMapping(path = {"/jvm"},
                    method = {RequestMethod.GET})
    public Object[][] jvm() {
        Object[][] objs = new Object[][] { {"year", "sale", "extend"}, {"2000", 100, 500}, {"2001", 200, 400}, {"2002", 400, 200}, {"2003", 800, 100}};
        return objs;
    }
    
}
