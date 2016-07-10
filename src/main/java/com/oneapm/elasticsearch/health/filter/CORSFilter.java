/**
 * Project Name:elasticsearch-health
 * File Name:CORSFilter.java
 * Package Name:com.oneapm.elasticsearch.health.filter
 * Date:
 * Copyright (c) 2016, All Rights Reserved.
 *
 */
package com.oneapm.elasticsearch.health.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * ClassName:CORSFilter <br/>
 * Function: <br/>
 * Date: <br/>
 *
 * @author xushjie
 * @version
 * @since JDK 1.8
 * @see
 */
@Component(value = "corsFilter")
public class CORSFilter implements Filter {
    
    private final Logger log = LoggerFactory.getLogger(CORSFilter.class);
    
    /**
     * @see javax.servlet.Filter#destroy()
     */
    @Override
    public void destroy() {
    }
    
    /**
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest,
     *      javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    @Override
    public void doFilter(ServletRequest req,
                         ServletResponse res,
                         FilterChain chain) throws IOException, ServletException {
        //
        log.info("CORSFilter doFilter in...");
        //
        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;
        // 允许跨域访问
        response.setHeader("Access-Control-Allow-Origin",
                           request.getHeader("Origin"));
        // response.setHeader("Access-Control-Allow-Origin", "http://localhost:6999");
        response.setHeader("Access-Control-Allow-Methods",
                           "POST, PUT, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age",
                           "3600");
        response.setHeader("Access-Control-Allow-Headers",
                           "X-Requested-With, X-Auth-Token");
        response.setHeader("Access-Control-Allow-Credentials",
                           "true");
        //
        if (!"OPTIONS".equalsIgnoreCase(request.getMethod())) {
            chain.doFilter(req,
                           res);
        }
        //
        log.info("CORSFilter doFilter out...");
    }
    
    /**
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
     */
    @Override
    public void init(FilterConfig config) throws ServletException {
    }
    
}
