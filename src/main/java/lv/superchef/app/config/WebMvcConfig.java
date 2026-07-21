package lv.superchef.app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer
{
    private final String uploadsDir;

    public WebMvcConfig(@Value("${app.upload.recipes-dir:uploads/recipes}") String uploadsDir)
    {
        this.uploadsDir = Path.of(uploadsDir).toAbsolutePath().normalize().toUri().toString();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry)
    {
        registry
                .addResourceHandler("/images/recipes/**")
                .addResourceLocations(uploadsDir, "classpath:/static/images/recipes/");
    }
}

