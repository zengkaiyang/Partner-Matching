package com.zzkkyy.usercenter.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置
 * 
 * 前后端分离架构说明：
 * - 前端：Vite 开发服务器 (localhost:5173)，处理所有前端路由 (/login, /home, /team/* 等)
 * - 后端：Spring Boot (localhost:8080/api)，仅处理 API 接口
 * - Vite 代理：将 /api 请求转发到后端
 * 
 * 因此后端不需要处理任何静态资源或前端路由
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * 不配置静态资源处理器
     * 
     * 原因：
     * 1. 前端由 Vite 开发服务器独立运行，有自己的路由系统
     * 2. 后端只负责 API 接口，通过 context-path: /api 隔离
     * 3. 如果配置了静态资源处理，会导致访问根路径时尝试返回不存在的 index.html
     * 
     * 生产环境部署建议：
     * - 方案1：前端构建后部署到 Nginx，Nginx 配置反向代理到后端 API
     * - 方案2：前端构建后的静态文件放到 resources/static，但需要调整路由配置
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 不添加任何自定义静态资源处理器
        // 让 Spring Boot 使用默认的静态资源配置（如果有 static 目录的话）
        // 对于纯 API 服务，这样配置最安全
    }
}
