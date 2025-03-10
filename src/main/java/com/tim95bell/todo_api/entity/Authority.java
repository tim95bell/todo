package com.tim95bell.todo_api.entity;
import jakarta.persistence.*;

@Entity
@Table(name = "authorities")
public class Authority {
    public Authority() {}

    public Authority(User user, String authority) {
        this.data = new Data(user, authority);
    }

    @EmbeddedId
    Data data;

    @Embeddable
    static class Data {
        public Data(User user, String authority) {
            this.user = user;
            this.authority = authority;
        }

        @ManyToOne(optional = false)
        @JoinColumn(name = "username")
        public User user;
        @Column(length = 50)
        public String authority;
    }

    public User getUser() {
        return data.user;
    }

    public String getAuthority() {
        return data.authority;
    }
}
