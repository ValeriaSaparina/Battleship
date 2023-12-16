package com.example.semestrovkalast;

import javafx.application.Platform;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.Socket;

public class BattleshipClient {
    private Socket socket;

    private Player player;
    private int gameRoomID;
    private BattleshipUI gameUI;

    public BattleshipClient(String serverAddress, int port) {
        try {
            socket = new Socket(serverAddress, port);
            BufferedReader fromServerString = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//            ObjectInputStream fromServerObject = new ObjectInputStream(socket.getInputStream());
            player = new Player(socket);

            int[] IDs = getDataFromServer(fromServerString);
            if (IDs.length == 2) {
                gameRoomID = IDs[0];
                player.setRoomID(gameRoomID);
                player.setId(IDs[1]);
                showStartWindow();
            }

//            int whoMove = fromServer.read();
//            if (whoMove != -1) {
//                gameUI.updateSetOnActions();
//            }

            while (true) {
                boolean noWinner = true;
                boolean isFirst = true;
                while (noWinner) {

                    if (isFirst) {
                        System.out.println("before reading from server: " + System.nanoTime());
                        String status = fromServerString.readLine();
                        System.out.println("message from server has been received: " + status);

                        if (status.equals(Params.SUCCESS)) {
                            System.out.println("SUCCESS has been received");
                            gameUI.updateSetOnActions();
                        }
                        isFirst = false;
                    }


                    int whoMove = Integer.parseInt(fromServerString.readLine());
                    System.out.println("whoMove from server: " + whoMove);
                    System.out.println("player.id: " + player.getId());
                    if (whoMove == player.getId()) {
                        System.out.println("setting ready");
                        gameUI.setNotification("Your turn");
    //                    player.setReady(true);
                        player.setMoving(true);
                    } else {
                        gameUI.setNotification("enemy's turn");
                        player.setMoving(false);
                    }

                    ObjectInputStream fromServer = new ObjectInputStream(player.getPlayerSocket().getInputStream());
                    Object rrr = fromServer.readObject();
                    if (rrr instanceof Message response) {
                        System.out.println("status in UI: " + response.getStatus());
                        if (player.getId() == whoMove) {
                            gameUI.updateBoardUI(response, gameUI.getEnemyBoard().getBoard());
                        } else {
                            gameUI.updateBoardUI(response, player.getGameBoard().getBoard());
                        }
                    } else {
                        System.out.println(rrr.toString());
                    }

                    noWinner = fromServer.readBoolean();

                }

                if (player.isMoving()) {
                    gameUI.setNotification("You're win");
                } else {
                    gameUI.setNotification("You're lose");
                }

                gameUI.showRestartButton();
                long endTime = System.currentTimeMillis();
                while ((System.currentTimeMillis() - endTime) / 1000 / 60 < 10) {
                    System.out.println(gameUI.isRestart());
                    if (gameUI.isRestart()) {
                        Platform.runLater(() -> {
                            player.setNumberShips(0);
                            player.setMoving(false);
                            player.setReady(false);
                            gameUI.initUI();
                        });
                        gameUI.setRestart(false);
                        break;
                    }

                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
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

    private int[] getDataFromServer(BufferedReader fromServer) throws IOException {
        try {
            System.out.println("getting roomID");
            String[] data = fromServer.readLine().split(" ");
            return new int[]{Integer.parseInt(data[0]), Integer.parseInt(data[1])};
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        System.out.println("ERROR");
        return new int[1];
    }

    private void showStartWindow() {
        Platform.startup(() -> {
            System.out.println("in startup");
            Stage primaryStage = new Stage();
            gameUI = new BattleshipUI(player, player.getGameRoom());
            System.out.println("playetID in startup: " + player.getId());
            System.out.println("playetMoving in startup: " + player.isMoving());
//            player.getGameRoom().setID(gameRoomID);
            gameUI.setGameRoomID(gameRoomID);
            gameUI.start(primaryStage);
        });
    }
}
