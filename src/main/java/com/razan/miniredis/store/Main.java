package com.razan.miniredis.store;

import com.razan.miniredis.server.RedisServer;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            System.out.println("Starting Redis Server on port 6379...");
            new RedisServer();
        } catch (IOException e) {
            System.err.println("Failed to start server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
