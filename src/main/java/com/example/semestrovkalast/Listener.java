package com.example.semestrovkalast;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Listener implements Runnable{
    @Override
    public void run() {
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
