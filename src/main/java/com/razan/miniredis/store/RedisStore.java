package com.razan.miniredis.store;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.razan.miniredis.expiration.ExpiringValue;
import com.razan.miniredis.persistence.PersistenceManager;

public class RedisStore {
    private Map<String, ExpiringValue> data = new HashMap<>();
    private PersistenceManager persistenceManager;
    
    public RedisStore() {
    }
    
    public RedisStore(String persistenceFilePath) {
        this.persistenceManager = new PersistenceManager(persistenceFilePath);
    }
    
    public void setPersistenceManager(PersistenceManager persistenceManager) {
        this.persistenceManager = persistenceManager;
    } 
     
    public String set(String key, String value){
        ExpiringValue expiringValue = new ExpiringValue(value, 0);
        data.put(key, expiringValue);
        return "OK";
    }

    public String get(String key){
        ExpiringValue expiringValue = data.get(key);
        if (expiringValue != null && isExpired(expiringValue)){
            del(key);
            return null;
        }
        return expiringValue != null ? expiringValue.getValue() : null;
    }

    public int del(String key){
        if (data.containsKey(key)){
            data.remove(key);
            return 1;
        }
        else{
            return 0;
        }
    }

    private boolean isExpired(ExpiringValue val) {
        if (val.getExpireAt() == null || val.getExpireAt() == 0) return false;

        long now = System.currentTimeMillis();
        return now >= val.getExpireAt();
    }

    public boolean exists(String key) {
        ExpiringValue val = data.get(key);
        if (val != null && isExpired(val)) {
            del(key);
            return false;
        }
        return data.containsKey(key);
    }

    public int expire(String key, long seconds) {
        ExpiringValue val = data.get(key);
        if (val == null) return 0;
        
        long expireAt = System.currentTimeMillis() + (seconds * 1000);
        ExpiringValue newVal = new ExpiringValue(val.getValue(), expireAt);
        data.put(key, newVal);
        return 1;
    }

    public long ttl(String key) {
        ExpiringValue val = data.get(key);
        if (val == null) return -2; 
        if (val.getExpireAt() == 0) return -1;
        
        if (isExpired(val)) {
            del(key);
            return -2;
        }
        
        long now = System.currentTimeMillis();
        return (val.getExpireAt() - now) / 1000;
    }

    public Set<String> keys() {
        data.entrySet().removeIf(entry -> isExpired(entry.getValue()));
        return data.keySet();
    }

    public void flushDb() {
        data.clear();
    }

    public Map<String, ExpiringValue> getData() {
        return data;
    }
    
    public void save() throws IOException {
        if (persistenceManager == null) {
            throw new IllegalStateException("PersistenceManager not configured");
        }
        persistenceManager.save(data);
    }
    
    public void load() throws IOException {
        if (persistenceManager == null) {
            throw new IllegalStateException("PersistenceManager not configured");
        }
        Map<String, ExpiringValue> loadedData = persistenceManager.load();
        data.clear();
        data.putAll(loadedData);
    }

}