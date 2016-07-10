/**
 * Project Name:elasticsearch-health
 * File Name:JSONUtil.java
 * Package Name:com.oneapm.elasticsearch.health.util
 * Date: 
 * Copyright (c) 2016, All Rights Reserved.
 *
 */
package com.oneapm.elasticsearch.health.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * ClassName:JSONUtil <br/>
 * Function: <br/>
 * Date: <br/>
 * 
 * @author xushjie
 * @version
 * @since JDK 1.8
 * @see
 */
public class JSONUtil {
    
    public static final Logger       log    = LoggerFactory.getLogger(JSONUtil.class);
    
    public static final ObjectMapper mapper = new ObjectMapper();
    
    /**
     * toJSON: <br/>
     * 
     * @author xushjie
     * @param o
     * @return
     * @since JDK 1.8
     */
    public static <O> String toJSON(O o) {
        String json = "";
        try {
            json = mapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            log.error("{}",
                      e);
        }
        return json;
    }
    
}
