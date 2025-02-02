package com.neom108.SmartHire.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
    // this configuration class will map requests for /photos to serve files from a directory on our file system

    private static final String UPLOAD_DIR = "photos";

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // override the default implementation to set up a custom resource handler
        exposedDirectory(UPLOAD_DIR, registry);
    }

    private void exposedDirectory(String uploadDir, ResourceHandlerRegistry registry) {
        Path path = Paths.get(uploadDir);
        // Maps any request coming to url /photos/** to actual path in file system to show/share/expose given photos
        registry.addResourceHandler("/"+uploadDir+"/**").addResourceLocations("file:"+path.toAbsolutePath()+"/");
    }
}
