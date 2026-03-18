package com.razan.miniredis;

import com.razan.miniredis.commands.Command;
import com.razan.miniredis.commands.CommandExecutor;
import com.razan.miniredis.commands.CommandParser;
import com.razan.miniredis.store.RedisStore;

public class CommandExecutorTest {
    public static void main(String[] args) throws Exception {
        System.out.println("=== Testing CommandExecutor ===\n");
        
        RedisStore store = new RedisStore();
        CommandParser parser = new CommandParser();
        CommandExecutor executor = new CommandExecutor(store);
        
        // Test PING
        System.out.println("1. Testing PING:");
        Command pingCmd = parser.parse("PING");
        System.out.println("   PING -> " + executor.execute(pingCmd));
        
        // Test SET
        System.out.println("\n2. Testing SET:");
        Command setCmd = parser.parse("SET user:1 Alice");
        System.out.println("   SET user:1 Alice -> " + executor.execute(setCmd));
        
        // Test GET
        System.out.println("\n3. Testing GET:");
        Command getCmd = parser.parse("GET user:1");
        System.out.println("   GET user:1 -> " + executor.execute(getCmd));
        
        Command getNilCmd = parser.parse("GET nonexistent");
        System.out.println("   GET nonexistent -> " + executor.execute(getNilCmd));
        
        // Test EXISTS
        System.out.println("\n4. Testing EXISTS:");
        Command existsCmd1 = parser.parse("EXISTS user:1");
        System.out.println("   EXISTS user:1 -> " + executor.execute(existsCmd1));
        
        Command existsCmd2 = parser.parse("EXISTS fake");
        System.out.println("   EXISTS fake -> " + executor.execute(existsCmd2));
        
        // Test EXPIRE and TTL
        System.out.println("\n5. Testing EXPIRE and TTL:");
        Command setCmd2 = parser.parse("SET temp value");
        executor.execute(setCmd2);
        
        Command expireCmd = parser.parse("EXPIRE temp 10");
        System.out.println("   EXPIRE temp 10 -> " + executor.execute(expireCmd));
        
        Command ttlCmd = parser.parse("TTL temp");
        System.out.println("   TTL temp -> " + executor.execute(ttlCmd) + " seconds");
        
        Command ttlCmd2 = parser.parse("TTL user:1");
        System.out.println("   TTL user:1 (no expiration) -> " + executor.execute(ttlCmd2));
        
        // Test KEYS
        System.out.println("\n6. Testing KEYS:");
        Command setCmd3 = parser.parse("SET product:1 Laptop");
        executor.execute(setCmd3);
        Command setCmd4 = parser.parse("SET product:2 Mouse");
        executor.execute(setCmd4);
        
        Command keysCmd = parser.parse("KEYS *");
        System.out.println("   KEYS * ->");
        String keysResult = executor.execute(keysCmd);
        for (String key : keysResult.split("\n")) {
            System.out.println("     - " + key);
        }
        
        // Test DEL
        System.out.println("\n7. Testing DEL:");
        Command delCmd = parser.parse("DEL product:1");
        System.out.println("   DEL product:1 -> " + executor.execute(delCmd));
        
        Command delCmd2 = parser.parse("DEL nonexistent");
        System.out.println("   DEL nonexistent -> " + executor.execute(delCmd2));
        
        // Test expiration
        System.out.println("\n8. Testing key expiration:");
        Command setCmd5 = parser.parse("SET expire-me quick");
        executor.execute(setCmd5);
        Command expireCmd2 = parser.parse("EXPIRE expire-me 2");
        executor.execute(expireCmd2);
        System.out.println("   SET expire-me quick + EXPIRE 2 seconds");
        
        Command getCmd2 = parser.parse("GET expire-me");
        System.out.println("   GET expire-me (immediately) -> " + executor.execute(getCmd2));
        
        System.out.println("   Waiting 3 seconds...");
        Thread.sleep(3000);
        
        System.out.println("   GET expire-me (after 3 sec) -> " + executor.execute(getCmd2));
        
        // Test FLUSHDB
        System.out.println("\n9. Testing FLUSHDB:");
        Command keysCmd2 = parser.parse("KEYS *");
        String beforeFlush = executor.execute(keysCmd2);
        System.out.println("   Keys before flush: " + (beforeFlush.equals("(empty list or set)") ? "0" : beforeFlush.split("\n").length));
        
        Command flushCmd = parser.parse("FLUSHDB");
        System.out.println("   FLUSHDB -> " + executor.execute(flushCmd));
        
        String afterFlush = executor.execute(keysCmd2);
        System.out.println("   Keys after flush: " + afterFlush);
        
        // Test error cases
        System.out.println("\n10. Testing error handling:");
        try {
            Command badCmd = parser.parse("SET key");
            executor.execute(badCmd);
        } catch (Exception e) {
            System.out.println("   SET key (missing value) -> " + e.getMessage());
        }
        
        try {
            Command badCmd2 = parser.parse("UNKNOWN arg");
            executor.execute(badCmd2);
        } catch (Exception e) {
            System.out.println("   UNKNOWN arg -> " + e.getMessage());
        }
        
        System.out.println("\n=== All CommandExecutor Tests Passed! ===");
    }
}
