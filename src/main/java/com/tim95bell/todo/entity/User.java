package com.tim95bell.todo.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    public User() {}

    public User(String username, String password, boolean enabled) {
        this.username = username;
        this.password = password;
        this.enabled = enabled;
    }

    @Id
    @Column(length = 50)
    // TODO(TB): ignorecase?
    // TODO(TB): not empty?
    public String username;
    // TODO(TB): ignorecase?
    // TODO(TB): not empty?
    public String password;
    public boolean enabled;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "data.user", orphanRemoval = true)
    public List<Authority> authorities;
}
