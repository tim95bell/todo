package com.tim95bell.todo.config;

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

import javax.sql.DataSource;

@Configuration
public class Config {
    private final String adminPassword;

    public Config(@Value("#{environment.TIM95BELL_TODO_ADMIN_USER_PASSWORD}") String adminPassword) {
        this.adminPassword = adminPassword;
    }

    @Bean
    public UserDetailsService userDetailsService(DataSource dataSource) {
        var m = new JdbcUserDetailsManager(dataSource);
        m.createUser(User.withUsername("admin").passwordEncoder(passwordEncoder()::encode).password(adminPassword).roles("admin").build());
        return m;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(CsrfConfigurer<HttpSecurity>::disable)
            .authorizeHttpRequests((c) -> {
                c.anyRequest().authenticated();
            })
            .httpBasic(Customizer.withDefaults())
            .build();
    }
}
