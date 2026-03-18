package com.razan.miniredis.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.razan.miniredis.commands.Command;
import com.razan.miniredis.commands.CommandExecutor;
import com.razan.miniredis.commands.CommandParser;

public class ClientHandler implements Runnable {

    private Socket socket;
    private CommandParser parser;
    private CommandExecutor executor;
    private PrintWriter writer;
    private BufferedReader reader;

    public ClientHandler(Socket socket,
                         CommandParser parser,
                         CommandExecutor executor) {
        this.socket = socket;
        this.parser = parser;
        this.executor = executor;
    }

    @Override
    public void run() {
        try {
            reader = createReader(socket);
            writer = createWriter(socket);

            handleClient(reader, writer);

        } catch (Exception e) {
            // log error
        } finally {
            closeSocket();
        }
    }

    private void handleClient(BufferedReader reader,
                             PrintWriter writer) throws IOException, Exception {
        String line;
        while ((line = reader.readLine()) != null) {
            String response = processLine(line);
            writer.println(response);
        }
    }

    private String processLine(String line) throws Exception {
        Command command = parser.parse(line);
        String response = executor.execute(command);
        return response;
    }

    private BufferedReader createReader(Socket socket) throws IOException {
        return new BufferedReader(
            new InputStreamReader(socket.getInputStream())
        );
    }

    private PrintWriter createWriter(Socket socket) throws IOException {
        return new PrintWriter(
            socket.getOutputStream(), true
        );
    }

    private void closeSocket() {
        try {
            if (writer != null) {
                writer.close();
            }
            if (reader != null) {
                reader.close();
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
        }
    }
}