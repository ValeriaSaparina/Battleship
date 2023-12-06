package com.example.semestrovkalast;

import java.io.Serializable;
import java.net.Socket;
import java.util.Arrays;

public class Player implements Serializable {

    private String name;
    private transient Socket playerSocket;
    //    private ObjectInputStream input;
    private char[][] gameBoard;
    private boolean isReady = false;
    private int[] message = null;
    private boolean isWinner = false;

    public Player(Socket playerSocket) {
        this.playerSocket = playerSocket;
//        try {
////            this.input = new ObjectInputStream(playerSocket.getInputStream());
//            System.out.println("HI");
//            this.output = new ObjectOutputStream(playerSocket.getOutputStream());
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
        this.gameBoard = new char[10][10];
        this.name = "Name of player";
    }

    public Player(String name, Socket playerSocket) {
        this(playerSocket);
        this.name = name;
        // Initialize the game board and place ships as needed
    }

    public boolean isReady() {
        return isReady;
    }

    public void setReady(boolean ready) {
        isReady = ready;
    }

    public String getName() {
        return name;
    }

    public char[][] getGameBoard() {
        return gameBoard;
    }

    @Override
    public String toString() {
        return "Player{" +
                "name='" + name + '\'' +
                ",\nplayerSocket=" + playerSocket +
                ",\ngameBoard=" + Arrays.toString(gameBoard) +
                ",\nisReady=" + isReady +
                ",\nmessage=" + Arrays.toString(message) +
                ",\nisWinner=" + isWinner +
                '}';
    }

    public void makeMove(Integer columnIndex, Integer rowIndex) {
        message = new int[]{columnIndex, rowIndex};
    }

    public int[] move() {
        while (message == null) {
        }
        int[] res = new int[]{message[0], message[1]};
        message = null;
        return res;
    }

    public boolean isWinner() {
        return isWinner;
    }

    public void setSocket(Socket socket) {
        this.playerSocket = socket;
    }

    // Additional methods or properties as needed, such as hit/miss tracking, ship placement, etc.
}

