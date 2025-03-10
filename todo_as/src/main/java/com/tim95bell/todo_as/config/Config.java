package com.tim95bell.todo_as.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
public class Config {

    private final String WEB_CLIENT_ID;
    private final String WEB_CLIENT_SECRET;
    private final String WEB_REDIRECT_URI;
    private final String ADMIN_PASSWORD;
    private final String WEB_URL;

    public Config(
        @Value(
            "#{environment.TIM95BELL_TODO_AS_WEB_CLIENT_ID}"
        ) String webClientId,
        @Value(
            "#{environment.TIM95BELL_TODO_AS_WEB_CLIENT_SECRET}"
        ) String webClientSecret,
        @Value(
            "#{environment.TIM95BELL_TODO_AS_WEB_REDIRECT_URI}"
        ) String webRedirectUri,
        @Value(
            "#{environment.TIM95BELL_TODO_AS_ADMIN_USER_PASSWORD}"
        ) String adminPassword,
        @Value(
                "#{environment.TIM95BELL_TODO_WEB_URL}"
        ) String webUrl
    ) {
        this.WEB_CLIENT_ID = webClientId;
        this.WEB_CLIENT_SECRET = webClientSecret;
        this.WEB_REDIRECT_URI = webRedirectUri;
        this.ADMIN_PASSWORD = adminPassword;
        this.WEB_URL = webUrl;
    }

    @Bean
    @Order(1)
    public SecurityFilterChain asFilterChain(HttpSecurity http)
        throws Exception {
        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer =
            OAuth2AuthorizationServerConfigurer.authorizationServer();

        http.cors(cors -> {
            CorsConfigurationSource source = request -> {
                CorsConfiguration config = new CorsConfiguration();
                config.setAllowedOrigins(
                        List.of(WEB_URL));
                config.setAllowedMethods(
                        List.of("OPTIONS", "GET", "POST", "PUT", "DELETE", "PATCH", "HEAD"));
                config.setAllowedHeaders(List.of("*"));
                return config;
            };
            cors.configurationSource(source);
        });

        http
            .securityMatcher(
                authorizationServerConfigurer.getEndpointsMatcher()
            )
            .with(authorizationServerConfigurer, authorizationServer ->
                authorizationServer.oidc(Customizer.withDefaults())
            )
            .authorizeHttpRequests(authorize ->
                authorize.anyRequest().authenticated()
            )
            .exceptionHandling(exceptions ->
                exceptions.defaultAuthenticationEntryPointFor(
                    new LoginUrlAuthenticationEntryPoint("/login"),
                    new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
                )
            );

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http)
        throws Exception {
        http.cors(cors -> {
            CorsConfigurationSource source = request -> {
                CorsConfiguration config = new CorsConfiguration();
                config.setAllowedOrigins(
                        List.of(WEB_URL));
                config.setAllowedMethods(
                        List.of("OPTIONS", "GET", "POST", "PUT", "DELETE", "PATCH", "HEAD"));
                config.setAllowedHeaders(List.of("*"));
                return config;
            };
            cors.configurationSource(source);
        });
        http
            .formLogin(Customizer.withDefaults())
            .authorizeHttpRequests(authorize ->
                authorize.anyRequest().authenticated()
            );

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(DataSource dataSource) {
        var m = new JdbcUserDetailsManager(dataSource);
        m.createUser(
            User.withUsername("admin")
                .passwordEncoder(passwordEncoder()::encode)
                .password(ADMIN_PASSWORD)
                .roles("admin")
                .build()
        );
        return m;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        RegisteredClient registeredClient = RegisteredClient.withId(
            UUID.randomUUID().toString()
        )
            .clientId(WEB_CLIENT_ID)
            // NOTE(TB): client secret is getting encoded with password encoder, so need to encode it here so it matches
            .clientSecret(passwordEncoder().encode(WEB_CLIENT_SECRET))
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
            .clientAuthenticationMethod(
                ClientAuthenticationMethod.CLIENT_SECRET_BASIC
            )
            .scope(OidcScopes.OPENID)
            .redirectUri(WEB_REDIRECT_URI)
            .tokenSettings(
                TokenSettings.builder()
                    .accessTokenTimeToLive(Duration.ofHours(6))
                    .build()
            )
            .clientSettings(
                ClientSettings.builder()
                    .requireProofKey(true)
                    .requireAuthorizationConsent(true)
                    .build()
            )
            .build();
        return new InMemoryRegisteredClientRepository(registeredClient);
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource()
        throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        RSAKey rsaKey = new RSAKey.Builder(publicKey)
            .privateKey(privateKey)
            .keyID(UUID.randomUUID().toString())
            .build();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return new ImmutableJWKSet<>(jwkSet);
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder().build();
    }
}
