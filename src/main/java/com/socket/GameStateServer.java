package com.socket;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.state.GameState;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class GameStateServer {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private BufferedWriter writer;

    public void start(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Game server started on port " + port);
            System.out.println("Waiting for client to connect...");

            clientSocket = serverSocket.accept();
            System.out.println("Client connected: " + clientSocket.getInetAddress());

            // Use BufferedWriter for sending plain text (JSON) over the socket
            writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"));

        } catch (IOException e) {
            System.err.println("Error starting server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void broadcastGameState(GameState gameState) {
        try {
            if (writer != null) {
                Gson gson = new Gson();
                String json = gson.toJson(gameState);
                writer.write(json + "\n"); // newline for clean message parsing
                writer.flush();
                // System.out.println("Broadcast: " + json); this was just for testing
            }
        } catch (IOException e) {
            System.err.println("Error broadcasting game state: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception ex) {
            System.err.println("❌ Error during JSON serialization:");
            ex.printStackTrace();
        }
    }

    public void stop() {
        try {
            if (writer != null) writer.close();
            if (clientSocket != null) clientSocket.close();
            if (serverSocket != null) serverSocket.close();
            System.out.println("Server stopped");
        } catch (IOException e) {
            System.err.println("Error stopping server: " + e.getMessage());
        }
    }
}
