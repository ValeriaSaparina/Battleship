package com.example.semestrovkalast;

import javafx.application.Platform;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class BattleshipClient {
    private Socket socket;

    //    private BufferedWriter output;
    private Player player;
    private int gameRoomID;
    private BattleshipUI gameUI;

    public BattleshipClient(String serverAddress, int port) {
        try {
            socket = new Socket(serverAddress, port);
            BufferedReader fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            player = new Player(socket);

            int id = getGameRoomIDFromServer(fromServer);
            if (id != -1) {
                gameRoomID = id;
                player.setRoomID(id);
                showStartWindow();
            }

            int isStarted = fromServer.read();
            if (isStarted == 1) {
                gameUI.updateSetOnActions();
            }
            while (true) {}

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String serverAddress = "localhost";  // Replace with the actual server IP address
        int port = 4004;  // Replace with the actual server port

        BattleshipClient client = new BattleshipClient(serverAddress, port);

    }

    private boolean gameIsStarted() {
        return player.getGameRoom().isReady();
    }

    private int getGameRoomIDFromServer(BufferedReader fromServer) throws IOException {
        try {
            System.out.println("getting roomID");
            return fromServer.read();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        System.out.println("ERROR");
        return -1;
    }

    private void showStartWindow() {
        Platform.startup(() -> {
            System.out.println("in startup");
            Stage primaryStage = new Stage();
            gameUI = new BattleshipUI(player, player.getGameRoom());
//            player.getGameRoom().setID(gameRoomID);
            gameUI.setGameRoomID(gameRoomID);
            gameUI.start(primaryStage);
        });
    }
}
