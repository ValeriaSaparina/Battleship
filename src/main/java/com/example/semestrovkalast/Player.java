package com.example.semestrovkalast;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

public class Player implements Serializable {

    private final Socket playerSocket;
    private boolean isReady = false;
    private boolean isMoving = false;
    private int id;
    private Board gameBoard;
    private char[][] charGameBoard = new char[10][10];
    private int roomID;
    private int numberShips;

    public Player(Socket playerSocket) {
        this.playerSocket = playerSocket;
    }

    public Socket getPlayerSocket() {
        return playerSocket;
    }

    public boolean isReady() {
        return isReady;
    }

    public void setReady(boolean ready) {
        isReady = ready;
    }

    public Board getGameBoard() {
        return gameBoard;
    }

    public void setGameBoard(Board gameBoard) {
        this.gameBoard = gameBoard;
    }

    public void makeMove(Integer columnIndex, Integer rowIndex) throws IOException {
        Message message = new Message(roomID, id, columnIndex, rowIndex, Params.SHOT);
        ObjectOutputStream output = new ObjectOutputStream(playerSocket.getOutputStream());
        output.writeObject(message);
        output.flush();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setRoomID(int roomID) {
        this.roomID = roomID;
    }

    public boolean isMoving() {
        return isMoving;
    }

    public void setMoving(boolean moving) {
        this.isMoving = moving;
    }

    public char[][] getCharGameBoard() {
        return charGameBoard;
    }

    public void setCharGameBoard(char[][] charGameBoard) {
        this.charGameBoard = charGameBoard;
    }

    public void setCharGameBoard() {
        GridPane gridPane = gameBoard.getBoard();
        for (Node node : gridPane.getChildren()) {
            String text = ((Button) node).getText();
            charGameBoard[GridPane.getColumnIndex(node)][GridPane.getRowIndex(node)] = !text.isEmpty() ? text.charAt(0) : ' ';
        }
    }

    public void decNumberShips() {
        numberShips -= 1;
    }

    public void incNumberShips() {
        numberShips += 1;
    }

    public int getNumberShips() {
        return numberShips;
    }

    public void setNumberShips(int numberShips) {
        this.numberShips = numberShips;
    }
}

