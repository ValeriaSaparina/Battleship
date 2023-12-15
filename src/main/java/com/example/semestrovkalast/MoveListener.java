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
        System.out.println("Reading message is started");
        boolean noWinner = true;
        while (noWinner) {
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

                    char[][] enemyGameBoard = enemy.getCharGameBoard();
                    int col = message.getCol();
                    int row = message.getRow();
                    System.out.println("col: " + col + "; row: " + row);
                    char cell = enemyGameBoard[col][row];

                    try {
                        ObjectOutputStream toClientPlayerObj = new ObjectOutputStream(client.getOutputStream());
                        ObjectOutputStream toClientEnemyObj = new ObjectOutputStream(enemy.getPlayerSocket().getOutputStream());

                        String status;
                        String updCells = null;
                        if (isEmpty(cell)) {
                            status = Params.MISS;
                        } else {
                            enemyGameBoard[col][row] = 'h';
                            if (isDestroyed(col, row, enemyGameBoard)) {
                                status = Params.DESTROYED;
                                updCells = getUpdateCells(col, row, enemyGameBoard);
                                System.out.println(updCells);
                            } else status = Params.HIT;
                        }
                        System.out.println("status in server AAAAAAAAAAAAA: " + status);
                        updateCharGameBoard(enemyGameBoard, col, row, status);
                        message = new Message(col, row, status);
                        if (status.equals(Params.DESTROYED)) {
                            message.setMessage(updCells);
                            enemy.decNumberShips();
                        }
                        toClientEnemyObj.writeObject(message);
                        toClientEnemyObj.flush();
                        System.out.println("enemy sent");
                        toClientPlayerObj.writeObject(message);
                        toClientPlayerObj.flush();
                        System.out.println("player sent + " + System.nanoTime());
                        i = (i + 1) % listSize;
                        whoMove = 1 - whoMove;
                        System.out.println("i: " + i + "; whoMove: " + whoMove);

                        noWinner = enemy.getNumberShips() != 0;
                        toClientPlayerObj.writeBoolean(noWinner);
                        toClientPlayerObj.flush();
                        toClientEnemyObj.writeBoolean(noWinner);
                        toClientEnemyObj.flush();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (IOException | ClassNotFoundException e) {
                }
            }
        }
    }

    private boolean isDestroyed(int col, int row, char[][] enemyGameBoard) {
        int c = col - 1;
        int r = row;
        int size = 1;


        while (c >= 0 && enemyGameBoard[c][r] != 'm' && enemyGameBoard[c][r] != ' ') {
            if (enemyGameBoard[c][r] == 'X') {
                return false;
            }
            if (enemyGameBoard[c][r] == 'h') {
                size += 1;
            }
            c -= 1;
        }
        c = col + 1;
        while (c < enemyGameBoard.length && enemyGameBoard[c][r] != 'm' && enemyGameBoard[c][r] != ' ') {
            if (enemyGameBoard[c][r] == 'X') {
                return false;
            }
            if (enemyGameBoard[c][r] == 'h') {
                size += 1;
                c += 1;
            }
        }
        c = col;

        if (size != 1) return true;

        r = row - 1;
        while (r >= 0 && enemyGameBoard[c][r] != 'm' && enemyGameBoard[c][r] != ' ' && r < enemyGameBoard.length) {
            if (enemyGameBoard[c][r] == 'X') {
                return false;
            }
            if (enemyGameBoard[c][r] == 'h') {
                size += 1;
                r -= 1;
            }
        }

        r = row + 1;
        while (r < enemyGameBoard.length && enemyGameBoard[c][r] != 'm' && enemyGameBoard[c][r] != ' ') {
            if (enemyGameBoard[c][r] == 'X') {
                return false;
            }
            if (enemyGameBoard[c][r] == 'h') {
                size += 1;
                r += 1;
            }
        }
        return true;

    }

    private void updateCharGameBoard(char[][] board, int col, int row, String status) {
        if (status.equals(Params.MISS)) {
            board[col][row] = 'm';
            return;
        }
        if (status.equals(Params.HIT)) {
            board[col][row] = 'h';
            return;
        }
        if (status.equals(Params.DESTROYED)) {
            board[col][row] = 'd';
        }
    }

    private String getUpdateCells(int col, int row, char[][] board) {
        String res = "";
        int c = col - 1;
        int r = row;
        while (c >= 0 && board[c][r] == 'h') {
            board[c][r] = 'd';
            res += (c + " " + r + " ");
            c -= 1;
        }
        c = col + 1;
        while (c < board.length && board[c][r] == 'h') {
            board[c][r] = 'd';
            res += (c + " " + r + " ");
            c += 1;
        }
        c = col;
        r = row - 1;
        while (r >= 0 && board[c][r] == 'h') {
            board[c][r] = 'd';
            res += (c + " " + r + " ");
            r -= 1;
        }
        r = row + 1;
        while (r < board.length && board[c][r] == 'h') {
            board[c][r] = 'd';
            res += (c + " " + r + " ");
            r += 1;
        }
        return res;
    }

    private boolean isEmpty(char c) {
        return c == ' ';

    }

    public void setSockets(List<Socket> list) {
        socketsList.addAll(list);
    }

    public void setWhoMove(int whoMove) {
        this.whoMove = whoMove;
    }

}
