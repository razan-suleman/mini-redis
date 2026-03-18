package com.razan.miniredis;

import com.razan.miniredis.store.RedisStore;
import com.razan.miniredis.persistence.PersistenceManager;
import java.util.Set;

public class TestMain {
    public static void main(String[] args) throws Exception {
        System.out.println("=== Testing Mini-Redis ===\n");
        
        RedisStore store = new RedisStore();
        
        // Test SET and GET
        System.out.println("1. Testing SET and GET:");
        String result = store.set("name", "John");
        System.out.println("   SET name John -> " + result);
        String value = store.get("name");
        System.out.println("   GET name -> " + value);
        assert "John".equals(value) : "GET failed!";
        
        // Test multiple keys
        System.out.println("\n2. Testing multiple keys:");
        store.set("age", "25");
        store.set("city", "New York");
        System.out.println("   SET age 25 -> OK");
        System.out.println("   SET city 'New York' -> OK");
        System.out.println("   GET age -> " + store.get("age"));
        System.out.println("   GET city -> " + store.get("city"));
        
        // Test EXISTS
        System.out.println("\n3. Testing EXISTS:");
        System.out.println("   EXISTS name -> " + store.exists("name"));
        System.out.println("   EXISTS nonexistent -> " + store.exists("nonexistent"));
        
        // Test DEL
        System.out.println("\n4. Testing DEL:");
        int deleted = store.del("age");
        System.out.println("   DEL age -> " + deleted);
        System.out.println("   GET age -> " + store.get("age"));
        System.out.println("   EXISTS age -> " + store.exists("age"));
        
        // Test EXPIRE and TTL
        System.out.println("\n5. Testing EXPIRE and TTL:");
        store.set("temp", "value");
        int expireResult = store.expire("temp", 5);
        System.out.println("   EXPIRE temp 5 -> " + expireResult);
        long ttl = store.ttl("temp");
        System.out.println("   TTL temp -> " + ttl + " seconds");
        
        // Test expiration (wait 2 seconds)
        System.out.println("\n6. Testing key expiration:");
        store.set("quick", "expire-me");
        store.expire("quick", 2);
        System.out.println("   SET quick 'expire-me' and EXPIRE 2 seconds");
        System.out.println("   GET quick (immediately) -> " + store.get("quick"));
        System.out.println("   Waiting 3 seconds...");
        Thread.sleep(3000);
        System.out.println("   GET quick (after 3 seconds) -> " + store.get("quick"));
        
        // Test KEYS
        System.out.println("\n7. Testing KEYS:");
        store.set("user:1", "Alice");
        store.set("user:2", "Bob");
        store.set("product:1", "Laptop");
        Set<String> keys = store.keys();
        System.out.println("   All keys: " + keys);
        
        // Test TTL edge cases
        System.out.println("\n8. Testing TTL edge cases:");
        store.set("persistent", "no-expiration");
        System.out.println("   TTL persistent (no expiration) -> " + store.ttl("persistent"));
        System.out.println("   TTL nonexistent -> " + store.ttl("nonexistent"));
        
        // Test FLUSHDB
        System.out.println("\n9. Testing FLUSHDB:");
        System.out.println("   Keys before flush: " + store.keys().size());
        store.flushDb();
        System.out.println("   Keys after flush: " + store.keys().size());
        
        // Test Persistence
        System.out.println("\n10. Testing Persistence:");
        store.set("save-me", "important-data");
        store.set("save-me-too", "also-important");
        PersistenceManager pm = new PersistenceManager("test-data.rdb");
        pm.save(store.getData());
        System.out.println("   Saved 2 keys to test-data.rdb");
        
        // Clear store and load back
        store.flushDb();
        System.out.println("   Cleared store (keys: " + store.keys().size() + ")");
        var loadedData = pm.load();
        System.out.println("   Loaded " + loadedData.size() + " keys from file");
        for (var entry : loadedData.entrySet()) {
            System.out.println("   - " + entry.getKey() + " = " + entry.getValue().getValue());
        }
        
        // Test persistence with expiration
        System.out.println("\n11. Testing Persistence with Expiration:");
        RedisStore store2 = new RedisStore();
        store2.set("will-expire", "old-data");
        store2.expire("will-expire", 1);
        store2.set("persistent-key", "forever");
        pm.save(store2.getData());
        System.out.println("   Saved 1 expiring key and 1 persistent key");
        System.out.println("   Waiting 2 seconds...");
        Thread.sleep(2000);
        var loadedData2 = pm.load();
        System.out.println("   Loaded " + loadedData2.size() + " keys (expired key should be filtered)");
        for (var entry : loadedData2.entrySet()) {
            System.out.println("   - " + entry.getKey() + " = " + entry.getValue().getValue());
        }
        
        System.out.println("\n=== All Tests Completed Successfully! ===");
    }
}
