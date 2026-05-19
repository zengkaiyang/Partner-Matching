package com.zzkkyy.usercenter.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.IOException;

/**
 * Web MVC 配置
 * 用于处理前端路由刷新404问题
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * 配置静态资源处理器
     * 将所有非API请求都转发到 index.html，由前端路由处理
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .resourceChain(true)
                .addResolver(new PathResourceResolver() {
                    @Override
                    protected Resource getResource(String resourcePath, Resource location) throws IOException {
                        Resource requestedResource = location.createRelative(resourcePath);
                        
                        // 如果资源存在且可读，直接返回
                        if (requestedResource.exists() && requestedResource.isReadable()) {
                            return requestedResource;
                        }
                        
                        // 如果是API请求，返回null让其他处理器处理
                        if (resourcePath.startsWith("api/")) {
                            return null;
                        }
                        
                        // 其他所有请求都返回 index.html，让前端路由处理
                        return new ClassPathResource("/static/index.html");
                    }
                });
    }
}
