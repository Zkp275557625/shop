package com.zkp.shop.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author: hmc
 * @project: shop
 * @package: com.zkp.shop.config
 * @time: 2019/6/27 17:24
 * @description:
 */
@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //addResourceHandler是指你想在url请求的路径
        //addResourceLocations是图片存放的真实路径
        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
        registry.addResourceHandler("/**").addResourceLocations("D:/code/java/server/shop/src/main/resources/static/");
    }
}
