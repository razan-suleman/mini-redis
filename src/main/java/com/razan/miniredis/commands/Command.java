package com.razan.miniredis.commands;

import java.util.*;
public class Command {
    private String name;
    private List<String> args;
    public Command(String name , List<String> args){
        this.name= name;
        this.args = args;
    }
    public String getName() { return name;}
    public List<String> getArgs() { return args; }
}