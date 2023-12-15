package com.example.semestrovkalast;

import java.io.*;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MoveListener {

    private BattleshipServer battleshipServer;
    private GameRoom gameRoom;
    private List<Socket> socketsList;
    //    private List<ObjectInputStream> fromClients;
    private Deque<Message> messagesPool;
    private int whoMove;
    private boolean firstIter;

    public MoveListener(BattleshipServer battleshipServer, GameRoom gameRoom) {
        this.battleshipServer = battleshipServer;
        this.gameRoom = gameRoom;
        socketsList = new CopyOnWriteArrayList<>();
        messagesPool = new ArrayDeque<>();
        firstIter = true;
    }


    public void readingMessages() {
//        socketsList = Collections.synchronizedList(socketsList);
//        new Thread(() -> {
        System.out.println("Reading message is started");
        while (true) {
//                List<Socket> allSockets = battleshipServer.getAllSockets();
            int listSize = socketsList.size();
            int i = 0;
            while (i < listSize) {
                System.out.println("getting sockets");
                Socket client = socketsList.get(i);
                Player enemy = gameRoom.getPlayer(listSize - i - 1);
                System.out.println("enemyID: " + (listSize - i - 1));
                System.out.println("sockets was got");
                try {
                    BufferedWriter toClientPlayer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
                    BufferedWriter toClientEnemy = new BufferedWriter(new OutputStreamWriter(enemy.getPlayerSocket().getOutputStream()));
                    System.out.println("toClient and toEnemy were got");

                    if (firstIter) {
                        toClientPlayer.write(Params.SUCCESS + "\n");
                        toClientPlayer.flush();
                        toClientEnemy.write(Params.SUCCESS + "\n");
                        toClientEnemy.flush();
                        System.out.println("send message to player that connect is success");
//                            System.out.println("whoMove in sending: " + whoMove);
//                            toClientPlayer.write(whoMove + "\n");
//                            toClientPlayer.flush();
//                            toClientEnemy.write(whoMove + "\n");
//                            toClientEnemy.flush();
//                            System.out.println("message who first moving was sent");
                        firstIter = false;
                    }

                    System.out.println("whoMove in sending: " + whoMove);
                    toClientPlayer.write(whoMove + "\n");
                    toClientPlayer.flush();
                    toClientEnemy.write(whoMove + "\n");
                    toClientEnemy.flush();
                    System.out.println("message who first moving was sent");

                    ObjectInputStream fromClient = new ObjectInputStream(client.getInputStream());
                    Message message = (Message) fromClient.readObject();
                    System.out.println("server message from user: " + message.getIdSender() + " move: " + message.getMove() + " roomID: " + message.getIdRoom());

//                        Player enemy = gameRoom.getPlayer(1 - socketsList.indexOf(client));
                    char[][] enemyGameBoard = enemy.getCharGameBoard();
                    int col = message.getCol();
                    int row = message.getRow();
                    System.out.println("col: " + col + "; row: " + row);
                    char cell = enemyGameBoard[col][row];

                    try {
                        ObjectOutputStream toClientPlayerObj = new ObjectOutputStream(client.getOutputStream());
                        ObjectOutputStream toClientEnemyObj = new ObjectOutputStream(enemy.getPlayerSocket().getOutputStream());

                        if (isShot(cell)) {
                            System.out.println("we are in 'inShot'");
                            toClientPlayerObj.writeObject(Params.ERROR + "\n");
                            toClientPlayerObj.flush();
                        } else {
                            String status = Params.HIT;
                            toClientEnemyObj.writeObject(new Message(col, row, status));
                            toClientEnemyObj.flush();
                            System.out.println("enemy sent");
                            toClientPlayerObj.writeObject(new Message(col, row, status));
                            toClientPlayerObj.flush();
                            System.out.println("player sent + " + System.nanoTime());
                            i = (i + 1) % listSize;
                            whoMove = 1 - whoMove;
                            System.out.println("i: " + i + "; whoMove: " + whoMove);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (IOException | ClassNotFoundException e) {
                    // TODO: notification error
                }
            }
        }
//        }).start();
    }

    private boolean isShot(char c) {
        System.out.println("isShot. text cell: " + c);
        if (c == ' ') return false;
        return true;

    }

    public void processingMessages() {
        new Thread(() -> {
            System.out.println("Obrabotka message is started");
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
//                            enemyGameBoard.move(col, row);
                            fromServer.writeObject(new Message(Params.HIT));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void setSockets(List<Socket> list) {
        socketsList.addAll(list);
    }

    public int getWhoMove() {
        return whoMove;
    }

    public void setWhoMove(int whoMove) {
        this.whoMove = whoMove;
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
