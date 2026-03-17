package com.razan.miniredis.store;

import java.util.HashMap;
import java.util.Map;

public class RedisStore {
    private Map<String, String> data = new HashMap<>();

    public String set(String key, String value){
        data.put(key, value);
        return "OK";
    }

    public String get(String key){
        try{
        return data.get(key);
    }
        catch(Exception e){
            return null;
        }
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
}