package com.example.semestrovkalast;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class BattleshipServer {
    private ServerSocket serverSocket;
    private List<GameRoom> gameRooms;
//    private BufferedReader input;
        private BufferedWriter output;
//    private ObjectOutputStream output;

    public BattleshipServer(int port) {
        try {
            serverSocket = new ServerSocket(port);
            gameRooms = new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        int port = 12345;
        BattleshipServer server = new BattleshipServer(port);
        server.start();
    }

    public void start() {
        System.out.println("Server started. Waiting for clients...");
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                Player player = new Player(clientSocket);
                System.out.println("New client connected: " + player);
//                try {
                ObjectOutputStream output = new ObjectOutputStream(clientSocket.getOutputStream());
//                output = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                output.writeObject(player);
                output.flush();
                output.close();
//                    output.flush(); // Custom message to trigger game window opening
                    System.out.println(player);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                GameRoom gameRoom = getAvailableGameRoom();
                gameRoom.addPlayer(player);
                if (gameRoom.isFull()) {
                    Thread gameThread = new Thread(gameRoom);
                    gameThread.start();
                    gameRooms.remove(gameRoom);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private GameRoom getAvailableGameRoom() {
        for (GameRoom room : gameRooms) {
            if (!room.isFull()) {
                return room;
            }
        }
        GameRoom newRoom = new GameRoom();
        gameRooms.add(newRoom);
        return newRoom;
    }

    private void closeIOStreams() throws IOException {

//        output.close();
    }
}
