package com.razan.miniredis.commands;

import java.util.*;
public class CommandParser {
    public Command parse(String line) throws Exception { 
        if (line == null || line.isEmpty())
            throw new Exception("invalid command");
        line = line.trim();
        String[] commandArr = line.split("\\s+");
        String[] args = Arrays.copyOfRange(commandArr, 1, commandArr.length); 
        Command command = new Command(commandArr[0].toUpperCase() , Arrays.asList(args) );
        return command;
     }
}