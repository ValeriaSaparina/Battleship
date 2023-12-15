package com.example.semestrovkalast;

import javafx.application.Platform;
import javafx.scene.control.skin.TableHeaderRow;
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

            System.out.println("before reading from server: " + System.nanoTime());
            String status = fromServerString.readLine();
            System.out.println("message from server has been received: " + status);

            if (status.equals(Params.SUCCESS)) {
                System.out.println("SUCCESS has been received");
                gameUI.updateSetOnActions();
            }

            System.out.println("эээээ");

//            int whoMove = Integer.parseInt(fromServerString.readLine());
//            System.out.println("whoMove from server: " + whoMove);
//            System.out.println("player.id: " + player.getId());
//            if (whoMove == player.getId()) {
//                System.out.println("setting ready");
////                    player.setReady(true);
//                player.setMoving(true);
//            }
//            gameUI.setEnd(false);
//            while (true) {

                while (true) {


                    int whoMove = Integer.parseInt(fromServerString.readLine());
                    System.out.println("whoMove from server: " + whoMove);
                    System.out.println("player.id: " + player.getId());
                    if (whoMove == player.getId()) {
                        System.out.println("setting ready");
//                    player.setReady(true);
                        player.setMoving(true);
                    } else {
                        player.setMoving(false);
                    }


//                    Thread.sleep(5000);

                    ObjectInputStream fromServer = new ObjectInputStream(player.getPlayerSocket().getInputStream());
                    Object rrr = fromServer.readObject();
                    if (rrr instanceof Message response) {
                        System.out.println("status in UI: " + response.getStatus());
                        if (player.getId() == whoMove) {
                           gameUI.updateBoardUI(response, gameUI.getEnemyBoard().getBoard());
//                            gameUI.updateBoardUI(response.getCol(), response.getRow(), gameUI.getEnemyBoard().getBoard(), response.getStatus());
                        } else {
                            gameUI.updateBoardUI(response, player.getGameBoard().getBoard());
//                            gameUI.updateBoardUI(response.getCol(), response.getRow(), player.getGameBoard().getBoard(), response.getStatus());
                        }
                    } else {
                        System.out.println(rrr.toString());
                    }

//                    if (player.getId() != whoMove) {
//                        ObjectInputStream fromServerObj = new ObjectInputStream(socket.getInputStream());
//                        Message message = (Message) fromServerObj.readObject();
//                        System.out.println(message);
//                        gameUI.updateBoardUI(message.getCol(), message.getRow(), player.getGameBoard().getBoard(), message.getStatus());
//                    }
//
//                    if ((gameUI.isEnd() && player.isMoving()) ||(gameUI.isEnd() && player.getId() != whoMove)) {
//                        String rr = fromServerString.readLine();
//                        whoMove = Integer.parseInt(rr);
//                        System.out.println("whoMove from server: " + whoMove);
//                        System.out.println("player.id: " + player.getId());
//                        if (whoMove == player.getId()) {
//                            System.out.println("setting ready. again");
////                    player.setReady(true);
//                            player.setMoving(true);
//                        } else {
//                            System.out.println("oops, it's good news");
//                            player.setMoving(false);
//                        }
//                    }

//                    if (whoMove != player.getId() && )

//                    System.out.println("Maybe here " + System.nanoTime());

//                    if (gameUI.isEnd()) {
//                        System.out.println("change move");
////                    System.out.println("player.id: " + player.getId());
////                    if (whoMove == player.getId()) {
////                        System.out.println("setting ready");
//////                    player.setReady(true);
////                        player.setMoving(true);
////                    }
//                        player.setMoving(!player.isMoving());
//                        System.out.println("setMoving after move: " + player.isMoving());
//                        gameUI.setEnd(false);
//                    }

                }
//            }

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
