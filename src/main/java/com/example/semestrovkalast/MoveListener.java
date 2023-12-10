package com.example.semestrovkalast;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.*;

public class MoveListener implements Runnable {

    private BattleshipServer battleshipServer;
//    private List<ObjectInputStream> fromClients;
    private Deque<Message> messagesPool;

    public MoveListener(BattleshipServer battleshipServer) {
        this.battleshipServer = battleshipServer;
        messagesPool = new ArrayDeque<>();
    }

    @Override
    public void run() {
        System.out.println("Move listener is started");
            new Thread(() -> {
                while (true) {
                    for (Socket client : battleshipServer.getAllSockets()) {
                        try {
                            ObjectInputStream fromClient = new ObjectInputStream(client.getInputStream());
                            messagesPool.add((Message) fromClient.readObject());
                        } catch (IOException | ClassNotFoundException e) {
                            // TODO: notification error
                        }
                    }
                }
            }).start();

            new Thread(() -> {
                while (true) {
                    Message message = messagesPool.poll();
                    if (message != null) {
                        GameRoom room = battleshipServer.getRoom(message.getIdRoom());
                        Player enemy = room.getPlayer(message.getIdEnemy());
                        Board enemyGameBoard = enemy.getGameBoard();
                        int col = message.getCol();
                        int row = message.getRow();
                        Player sender = room.getPlayer(message.getIdSender());
                        try {
                            ObjectOutputStream fromServer = new ObjectOutputStream(sender.getPlayerSocket().getOutputStream());
                            if (enemyGameBoard.isShot(col, row)) {
                                fromServer.writeObject(new Message(Params.ERROR));
                            } else {
                                enemyGameBoard.move(col, row);
                                fromServer.writeObject(new Message(Params.SUCCESS));
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();



    }

//    public void addInput(Socket clientSocket) {
//        try {
//            ObjectInputStream
//            fromClients.add(new ObjectInputStream(clientSocket.getInputStream()));
//            System.out.println("in the end of adding");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}
