package com.tim95bell.todo_as.model;

import com.tim95bell.todo_as.entity.User;
import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class SecurityUser implements UserDetails {

    private final User user;

    public SecurityUser(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.authorities
            .stream()
            .map(x -> new SimpleGrantedAuthority(x.getAuthority()))
            .toList();
    }

    @Override
    public String getPassword() {
        return user.password;
    }

    @Override
    public String getUsername() {
        return user.username;
    }
}
