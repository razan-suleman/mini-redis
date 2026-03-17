package com.razan.miniredis.store;

public class Main {
    public static void main(String[] args) {
        RedisStore store = new RedisStore();
        store.set("name", "razan");
        System.out.println(store.get("name"));
        System.out.println(store.del("name"));
        System.out.println(store.get("name"));
    }
}
