package com.zjz.boot.config;

import com.zjz.boot.filter.ZjzFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zjz
 * @date 2022/8/3 18:22
 */
//@Configuration
public class WebFilter {

//    @Bean
//    public FilterRegistrationBean testFilterRegistration() {
//        FilterRegistrationBean registration = new FilterRegistrationBean();
//        registration.setFilter(new ZjzFilter());
//        List<String> urlList = new ArrayList<>();
//        urlList.add("/*");
//        registration.setUrlPatterns(urlList);
//        registration.setName("UrlFilter");
//        registration.setOrder(1);
//        return registration;
//    }
}
