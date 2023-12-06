package com.example.semestrovkalast;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;

public class BattleshipClient {
    private Socket socket;

    //    private BufferedWriter output;
    private Player player;
    private BattleshipUI gameUI = new BattleshipUI();

    public BattleshipClient(String serverAddress, int port) {
        try {
            // Connect to the server
            socket = new Socket(serverAddress, port);
//            initIOStreams();
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
//            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (true) {

                if (getPlayerFromServer(input)) {
                showStartWindow();
                }
//                if (gameIsStart()) {
//                    gameUI.updateSetOnActions();
//                }
            }

        } catch (IOException /*| ClassNotFoundException*/ e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
//        } finally {
//            closeIOStreams();
//        }
    }

    public static void main(String[] args) {
        String serverAddress = "127.0.0.1";  // Replace with the actual server IP address
        int port = 12345;  // Replace with the actual server port

        BattleshipClient client = new BattleshipClient(serverAddress, port);

    }

    private boolean gameIsStart(ObjectInputStream input) throws IOException, ClassNotFoundException {
        Object inputObj = input.readObject();
        if (inputObj instanceof Boolean) {
            return (boolean) inputObj;
        }
        return false;
    }

    private boolean getPlayerFromServer(ObjectInputStream input /*BufferedReader input*/) throws IOException, ClassNotFoundException {
        Object inputObj = input.readObject();
        if (inputObj instanceof Player) {
            player = (Player) inputObj;
            player.setSocket(socket);
            System.out.println(player);
            return true;
        }
        return false;
//        return  str != null;
    }

    private void initIOStreams() throws IOException {
//        input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//        input = new ObjectInputStream(socket.getInputStream());
//        output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    private void showStartWindow() {
        Platform.startup(() -> {
            Stage primaryStage = new Stage();
            gameUI.setPlayer(player);
            gameUI.start(primaryStage);
        });
    }

//    public void sendMove(int x, int y) throws IOException {
//        // Send the player's move to the server
//        output.write("MOVE " + x + " " + y);
//    }

    public void closeIOStreams() {
        try {
            // Close the connections
//            input.close();
//            output.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
