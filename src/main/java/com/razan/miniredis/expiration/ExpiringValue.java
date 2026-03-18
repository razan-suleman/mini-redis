package com.razan.miniredis.expiration;

public class ExpiringValue {
    private String value;
    private Long expireAt;// timestamp in milliseconds

    public ExpiringValue(String value, long expireAt){
        this.value = value;
        this.expireAt = expireAt;
    }

    public String getValue() {
        return value;
    }

    public Long getExpireAt() {
        return expireAt;
    }
}