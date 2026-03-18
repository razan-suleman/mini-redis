package com.razan.miniredis.commands;

import com.razan.miniredis.store.RedisStore;
import java.util.Set;

public class CommandExecutor {
    private RedisStore store;

    public CommandExecutor(RedisStore store) {
        this.store = store;
    }

    public String execute(Command command) throws Exception{ 
        switch (command.getName()) {
            case "SET":
                if (command.getArgs().size() == 2) {
                    store.set(command.getArgs().get(0), command.getArgs().get(1));
                    return "OK";
                } else {
                    throw new Exception("ERR wrong number of arguments for 'set' command");
                }
                
            case "GET":
                if (command.getArgs().size() == 1) {
                    String value = store.get(command.getArgs().get(0));
                    return value != null ? value : "(nil)";
                } else {
                    throw new Exception("ERR wrong number of arguments for 'get' command");
                }
                
            case "DEL":
                if (command.getArgs().size() == 1) {
                    int deleted = store.del(command.getArgs().get(0));
                    return String.valueOf(deleted);
                } else {
                    throw new Exception("ERR wrong number of arguments for 'del' command");
                }
                
            case "EXISTS":
                if (command.getArgs().size() == 1) {
                    boolean exists = store.exists(command.getArgs().get(0));
                    return exists ? "1" : "0";
                } else {
                    throw new Exception("ERR wrong number of arguments for 'exists' command");
                }
                
            case "EXPIRE":
                if (command.getArgs().size() == 2) {
                    String key = command.getArgs().get(0);
                    long seconds = Long.parseLong(command.getArgs().get(1));
                    int result = store.expire(key, seconds);
                    return String.valueOf(result);
                } else {
                    throw new Exception("ERR wrong number of arguments for 'expire' command");
                }
                
            case "TTL":
                if (command.getArgs().size() == 1) {
                    long ttl = store.ttl(command.getArgs().get(0));
                    return String.valueOf(ttl);
                } else {
                    throw new Exception("ERR wrong number of arguments for 'ttl' command");
                }
                
            case "KEYS":
                if (command.getArgs().size() == 1) {
                    String pattern = command.getArgs().get(0);
                    if (!"*".equals(pattern)) {
                        throw new Exception("ERR only '*' pattern is supported");
                    }
                    Set<String> keys = store.keys();
                    if (keys.isEmpty()) {
                        return "(empty list or set)";
                    }
                    return String.join("\n", keys);
                } else {
                    throw new Exception("ERR wrong number of arguments for 'keys' command");
                }
                
            case "FLUSHDB":
                if (command.getArgs().size() == 0) {
                    store.flushDb();
                    return "OK";
                } else {
                    throw new Exception("ERR wrong number of arguments for 'flushdb' command");
                }
                
            case "PING":
                return "PONG";
                
            default:
                throw new Exception("ERR unknown command '" + command.getName() + "'");
        }
     }
}