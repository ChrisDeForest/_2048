package com.socket;

import com.state.GameState;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class GameStateServer {
    // Port number to use (choose any unused port)
    private static final int PORT = 5999;
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private ObjectOutputStream outputStream;

    public void start() {
        try {
            // Create server socket that listens on the specified port
            serverSocket = new ServerSocket(PORT);
            System.out.println("Game server started on port " + PORT);
            System.out.println("Waiting for client to connect...");

            // Accept connection from client (blocks until client connects)
            clientSocket = serverSocket.accept();
            System.out.println("Client connected: " + clientSocket.getInetAddress());

            // Create output stream to send game state objects to client
            outputStream = new ObjectOutputStream(clientSocket.getOutputStream());

        } catch (IOException e) {
            System.err.println("Error starting server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void broadcastGameState(GameState gameState) {
        try {
            if (outputStream != null) {
                outputStream.writeObject(gameState);
                outputStream.flush();
                System.out.println("Broadcast: " + gameState);
            }
        } catch (IOException e) {
            System.err.println("Error broadcasting game state: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void stop() {
        try {
            if (outputStream != null) outputStream.close();
            if (clientSocket != null) clientSocket.close();
            if (serverSocket != null) serverSocket.close();
            System.out.println("Server stopped");
        } catch (IOException e) {
            System.err.println("Error stopping server: " + e.getMessage());
        }
    }
}