package com.example.semestrovkalast;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MoveListener {

    private final GameRoom gameRoom;
    private final List<Socket> socketsList;
    private int whoMove;
    private boolean firstIter;

    public MoveListener(GameRoom gameRoom) {
        this.gameRoom = gameRoom;
        socketsList = new CopyOnWriteArrayList<>();
        firstIter = true;
    }


    public void readingMessages() {
        System.out.println("Reading message is started");
        boolean noWinner = true;
        while (true) {
            while (noWinner) {
                int listSize = socketsList.size();
                int i = 0;
                while (i < listSize) {
                    Socket client = socketsList.get(i);
                    Player enemy = gameRoom.getPlayer(listSize - i - 1);
                    try {
                        BufferedWriter toClientPlayer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
                        BufferedWriter toClientEnemy = new BufferedWriter(new OutputStreamWriter(enemy.getPlayerSocket().getOutputStream()));

                        if (firstIter) {
                            toClientPlayer.write(Params.SUCCESS + "\n");
                            toClientPlayer.flush();
                            toClientEnemy.write(Params.SUCCESS + "\n");
                            toClientEnemy.flush();
                            firstIter = false;
                        }

                        toClientPlayer.write(whoMove + "\n");
                        toClientPlayer.flush();
                        toClientEnemy.write(whoMove + "\n");
                        toClientEnemy.flush();

                        ObjectInputStream fromClient = new ObjectInputStream(client.getInputStream());
                        Message message = (Message) fromClient.readObject();

                        char[][] enemyGameBoard = enemy.getCharGameBoard();
                        int col = message.getCol();
                        int row = message.getRow();
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
                            updateCharGameBoard(enemyGameBoard, col, row, status);
                            message = new Message(col, row, status);
                            if (status.equals(Params.DESTROYED)) {
                                message.setMessage(updCells);
                                enemy.decNumberShips();
                            }
                            toClientEnemyObj.writeObject(message);
                            toClientEnemyObj.flush();
                            toClientPlayerObj.writeObject(message);
                            toClientPlayerObj.flush();
                            i = (i + 1) % listSize;
                            whoMove = 1 - whoMove;

                            noWinner = enemy.getNumberShips() != 0;
                            toClientPlayerObj.writeBoolean(noWinner);
                            toClientPlayerObj.flush();
                            toClientEnemyObj.writeBoolean(noWinner);
                            toClientEnemyObj.flush();

                            if (!noWinner) {
                                Player player = gameRoom.getPlayer(i);
                                Thread.sleep(300);
                                new StartListener(gameRoom).run();
                                noWinner = true;
                                firstIter = true;
                                whoMove = 0;
                                enemy.setNumberShips(10);
                                player.setNumberShips(10);
                                break;
                            }
                        } catch (IOException | InterruptedException e) {
                            e.fillInStackTrace();
                        }
                    } catch (IOException | ClassNotFoundException ignored) {
                    }
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
