package com.socket;

import com.google.gson.Gson;
import com.state.GameState;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.concurrent.CopyOnWriteArrayList;

public class GameStateServer {
    private ServerSocket serverSocket;
    private final CopyOnWriteArrayList<BufferedWriter> clients = new CopyOnWriteArrayList<>();
    private volatile boolean running = true;

    public void start(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Game server started on port " + port);

            // Start a thread to continuously accept clients
            new Thread(() -> {
                while (running) {
                    try {
                        Socket clientSocket = serverSocket.accept();
                        System.out.println("Client connected: " + clientSocket.getInetAddress());

                        BufferedWriter writer = new BufferedWriter(
                                new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8")
                        );
                        clients.add(writer);
                        // Send valid initial state instead of empty JSON
                        GameState initialState = new GameState(
                                0, 0, false, false, new int[4][4]  // Default empty board
                        );
                        writer.write(new Gson().toJson(initialState) + "\n");
                        writer.flush();
                    } catch (IOException e) {
                        if (running) System.err.println("Accept error: " + e.getMessage());
                    }
                }
            }).start();
        } catch (IOException e) {
            System.err.println("Error starting server: " + e.getMessage());
        }
    }

    public void broadcastGameState(GameState gameState) {
        Gson gson = new Gson();
        String json = gson.toJson(gameState) + "\n";
        // System.out.println("Broadcasting JSON: " + json);  // todo just for debugging

        clients.removeIf(writer -> {
            try {
                writer.write(json);
                writer.flush();
                return false;
            } catch (IOException e) {
                System.err.println("Removing disconnected client");
                return true;
            }
        });
    }

    public void stop() {
        running = false;
        try {
            serverSocket.close();
            for (BufferedWriter writer : clients) {
                writer.close();
            }
            System.out.println("Server stopped");
        } catch (IOException e) {
            System.err.println("Error stopping server: " + e.getMessage());
        }
    }
}
