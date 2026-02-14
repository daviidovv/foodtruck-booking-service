package org.example.foodtruckbookingservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.IOException;

/**
 * Web-Konfiguration für SPA (Single Page Application) Support.
 * Leitet alle nicht-API Routen auf index.html um, damit React Router funktioniert.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .resourceChain(true)
                .addResolver(new PathResourceResolver() {
                    @Override
                    protected Resource getResource(String resourcePath, Resource location) throws IOException {
                        Resource requestedResource = location.createRelative(resourcePath);

                        // Wenn die Ressource existiert (JS, CSS, Bilder), liefere sie
                        if (requestedResource.exists() && requestedResource.isReadable()) {
                            return requestedResource;
                        }

                        // API-Routen nicht hier behandeln (werden von Controllern bearbeitet)
                        if (resourcePath.startsWith("api/")) {
                            return null;
                        }

                        // Für alle anderen Routen: index.html (SPA Fallback)
                        return new ClassPathResource("/static/index.html");
                    }
                });
    }
}
