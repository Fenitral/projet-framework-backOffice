package com.cousin.model;

import java.time.LocalDateTime;

public class TokenExpiration {
    private int id;
    private String token;
    private LocalDateTime expiration;

    public TokenExpiration() {
    }

    public TokenExpiration(int id, String token, LocalDateTime expiration) {
        this.id = id;
        this.token = token;
        this.expiration = expiration;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getExpiration() {
        return expiration;
    }

    public void setExpiration(LocalDateTime expiration) {
        this.expiration = expiration;
    }

    public boolean isExpired() {
        return expiration.isBefore(LocalDateTime.now());
    }

    @Override
    public String toString() {
        return "TokenExpiration{id=" + id + ", token='" + token + "', expiration=" + expiration + '}';
    }
}
