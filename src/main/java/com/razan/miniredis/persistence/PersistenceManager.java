package com.razan.miniredis.persistence;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.razan.miniredis.expiration.ExpiringValue;

public class PersistenceManager {

    private String filePath;

    public PersistenceManager(String filePath) {
        this.filePath = filePath;
    }

    public void save(Map<String, ExpiringValue> data) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            bw.write("key|value|expireAt\n");
            for (Map.Entry<String, ExpiringValue> entry : data.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue().getValue();
                Long expireAt = entry.getValue().getExpireAt();
                bw.write(key + "|" + value + "|" + expireAt + "\n");
            }
        }
    }

    public Map<String, ExpiringValue> load() throws IOException {
        Map<String, ExpiringValue> data = new HashMap<>();
        long now = System.currentTimeMillis();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine(); 
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 3) {
                    String key = parts[0];
                    String value = parts[1];
                    Long expireAt = Long.parseLong(parts[2]);
                    
                    if (expireAt != 0 && now >= expireAt) {
                        continue;
                    }
                    
                    data.put(key, new ExpiringValue(value, expireAt));
                }
            }
        }
        return data;
    }
}