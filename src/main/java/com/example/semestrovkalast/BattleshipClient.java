package com.example.semestrovkalast;

import javafx.application.Platform;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.Socket;

public class BattleshipClient {

    private Player player;
    private int gameRoomID;
    private BattleshipUI gameUI;

    public BattleshipClient(String serverAddress, int port) {
        try {
            Socket socket = new Socket(serverAddress, port);
            BufferedReader fromServerString = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            player = new Player(socket);

            int[] IDs = getDataFromServer(fromServerString);
            if (IDs.length == 2) {
                gameRoomID = IDs[0];
                player.setRoomID(gameRoomID);
                player.setId(IDs[1]);
                showStartWindow();
            }


            while (true) {
                boolean noWinner = true;
                boolean isFirst = true;
                while (noWinner) {

                    if (isFirst) {
                        String status = fromServerString.readLine();
                        if (status.equals(Params.SUCCESS)) {
                            gameUI.updateSetOnActions();
                        }
                        isFirst = false;
                    }


                    int whoMove = Integer.parseInt(fromServerString.readLine());
                    if (whoMove == player.getId()) {
                        System.out.println("setting ready");
                        gameUI.setNotification("Your turn");
                        player.setMoving(true);
                    } else {
                        gameUI.setNotification("enemy's turn");
                        player.setMoving(false);
                    }

                    ObjectInputStream fromServer = new ObjectInputStream(player.getPlayerSocket().getInputStream());
                    Object rrr = fromServer.readObject();
                    if (rrr instanceof Message response) {
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
            e.fillInStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {

        BattleshipClient client = new BattleshipClient(Params.SERVER_ADDRESS, Params.PORT);

    }

    private int[] getDataFromServer(BufferedReader fromServer) throws IOException {
        try {
            String[] data = fromServer.readLine().split(" ");
            return new int[]{Integer.parseInt(data[0]), Integer.parseInt(data[1])};
        } catch (NumberFormatException e) {
            e.fillInStackTrace();
        }
        return new int[1];
    }

    private void showStartWindow() {
        Platform.startup(() -> {
            Stage primaryStage = new Stage();
            gameUI = new BattleshipUI(player);
            gameUI.setGameRoomID(gameRoomID);
            gameUI.start(primaryStage);
        });
    }
}
