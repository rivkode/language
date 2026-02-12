package com.learner.language.system.config

import com.learner.language.system.login.LoginUserArgumentResolver
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig(
    val loginUserArgumentResolver: LoginUserArgumentResolver,
    @Value("\${app.audio.storage.path}") // Injected property
    private val audioStorageLocation: String
): WebMvcConfigurer {
    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(loginUserArgumentResolver)
    }

    override fun addCorsMappings(registry: CorsRegistry) {
        super.addCorsMappings(registry)
    }

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("/audio/**")
            .addResourceLocations("file:" + audioStorageLocation) // Used injected property
            .setCachePeriod(3600)

        registry.addResourceHandler("/welcome/**")
            .addResourceLocations("classpath:/static/welcome/")
            .setCachePeriod(3600)
            .resourceChain(true)
    }

    override fun addViewControllers(registry: ViewControllerRegistry) {
        registry.addViewController("/welcome").setViewName("forward:/welcome/index.html")
    }
}
