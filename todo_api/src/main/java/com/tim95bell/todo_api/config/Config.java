
package com.tim95bell.todo_api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

@Configuration
public class Config {
    private final String KEY_SET_URI;
    private final String WEB_URL;

    private final JwtAuthenticationConverter converter;

    Config(@Value("${key_set_uri}") String keySetUri,
           @Value("#{environment.TIM95BELL_TODO_WEB_URL}") String webUrl,
           JwtAuthenticationConverter converter
    ) {
        this.KEY_SET_URI = keySetUri;
        this.WEB_URL = webUrl;
        this.converter = converter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {
        http.cors(cors -> {
            CorsConfigurationSource source = request -> {
                CorsConfiguration config = new CorsConfiguration();
                config.setAllowedOrigins(List.of(WEB_URL));
                config.setAllowedMethods(
                        List.of(
                                "OPTIONS",
                                "GET",
                                "POST",
                                "PUT",
                                "DELETE",
                                "PATCH",
                                "HEAD"
                        )
                );
                config.setAllowedHeaders(List.of("*"));
                return config;
            };
            cors.configurationSource(source);
        });

        http.authorizeHttpRequests(c -> {
            c.requestMatchers("/api/admin/*").hasRole("admin");
            c.anyRequest().hasRole("user");
        });

        http.oauth2ResourceServer(c -> {
            c.jwt(j -> {
                j.jwkSetUri(KEY_SET_URI)
                        .jwtAuthenticationConverter(converter);
            });
        });

        return http.build();
    }
}
