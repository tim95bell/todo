package com.tim95bell.todo_api.config;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

@Configuration
public class Config {
    private final String KEY_SET_URI;
    private final String WEB_URL;

    Config(@Value("${key_set_uri}") String keySetUri, @Value("#{environment.TIM95BELL_TODO_WEB_URL}") String webUrl) {
        this.KEY_SET_URI = keySetUri;
        this.WEB_URL = webUrl;
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

        http.oauth2ResourceServer(c -> c.jwt(j -> j.jwkSetUri(KEY_SET_URI)));
        http.authorizeHttpRequests(c -> c.anyRequest().authenticated());
        return http.build();
    }
}
