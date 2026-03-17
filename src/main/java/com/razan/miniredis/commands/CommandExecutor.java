package com.razan.miniredis.commands;

import com.razan.miniredis.store.RedisStore;

public class CommandExecutor {
    private RedisStore store;

    public CommandExecutor(RedisStore store) {
        this.store = store;
    }

    public String execute(Command command) throws Exception{ 
        switch (command.getName()) {
            case "SET":
                if (command.getArgs().size() == 2)
                {
                    store.set(command.getArgs().get(0) , command.getArgs().get(1));
                    return "OK";
                }
                else{
                    throw new Exception("Invalid number of args");
                }
            case "GET":
                if (command.getArgs().size() == 1)
                {
                    return store.get(command.getArgs().get(0));
                }
                else{
                    throw new Exception("Invalid number of args");
                }
            case "DEL":
                if (command.getArgs().size() == 1)
                {
                    int deleted = store.del(command.getArgs().get(0));
                    return String.valueOf(deleted);
                }
                else{
                    throw new Exception("Invalid number of args");
                }
            case "PING":
                return "PONG";
            default:
                throw new Exception("ERR unknown command");
        }
     }
}