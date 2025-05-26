package com.example.hotel_reservation_app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir}")
    private String configuredUploadPath;

    private String getPublicUrlSegment(String pathStr) {
        return Paths.get(pathStr).normalize().toString().replace("\\", "/");
    }


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path actualFileSystemDir = Paths.get(configuredUploadPath).toAbsolutePath().normalize();

        try {
            Files.createDirectories(actualFileSystemDir); // Create if it doesn't exist
            System.out.println("Ensured upload directory exists at: " + actualFileSystemDir);
        } catch (IOException e) {
            System.err.println("Could not create or access upload directory: " + actualFileSystemDir);
            return;
        }

        String resourceLocation = actualFileSystemDir.toUri().toString();
        if (!resourceLocation.endsWith("/")) {
            resourceLocation += "/";
        }

        String publicUrlPath = "/" + getPublicUrlSegment(configuredUploadPath) + "/**";
        if (publicUrlPath.startsWith("//")) {
            publicUrlPath = publicUrlPath.substring(1);
        }


        System.out.println("Configuring resource handler:");
        System.out.println("  Public URL path: " + publicUrlPath);
        System.out.println("  Serving from filesystem (absolute): " + resourceLocation);

        registry.addResourceHandler(publicUrlPath)
                .addResourceLocations(resourceLocation);
    }
}
