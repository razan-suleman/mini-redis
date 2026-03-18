package com.razan.miniredis.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.razan.miniredis.commands.CommandExecutor;
import com.razan.miniredis.commands.CommandParser;
import com.razan.miniredis.store.RedisStore;

public class RedisServer {
    private RedisStore store;
    private CommandParser parser;
    private CommandExecutor executor;

    @SuppressWarnings("resource")
    public RedisServer() throws IOException {
        this.store = new RedisStore();
        this.parser = new CommandParser();
        this.executor = new CommandExecutor(store);

        ServerSocket server = new ServerSocket(6379);
        System.out.println("Mini-Redis server started on port 6379");

        while (true) {
            Socket socket = server.accept();
            System.out.println("Client connected: " + socket.getRemoteSocketAddress());

            ClientHandler handler = new ClientHandler(socket, parser, executor);
            Thread clientThread = new Thread(handler);
            clientThread.start();
        }

        
    }
}