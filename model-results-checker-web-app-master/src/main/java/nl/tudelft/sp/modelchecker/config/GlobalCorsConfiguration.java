package nl.tudelft.sp.modelchecker.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class GlobalCorsConfiguration implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("https://localhost:2000")
                .allowedMethods("POST", "GET")
                .exposedHeaders("Authorization",
                        "X-Requested-With", "Content-Type", "Accept", "oid");
    }
}

