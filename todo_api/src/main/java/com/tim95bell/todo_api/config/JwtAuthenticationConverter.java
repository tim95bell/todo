
package com.tim95bell.todo_api.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JwtAuthenticationConverter
        implements Converter<Jwt, JwtAuthenticationToken> {
    @Override
    public JwtAuthenticationToken convert(Jwt source) {
        List<SimpleGrantedAuthority> authorities = source.getClaimAsStringList("authorities").stream().map(SimpleGrantedAuthority::new).toList();
        return new JwtAuthenticationToken(source,
                authorities);
    }
}
