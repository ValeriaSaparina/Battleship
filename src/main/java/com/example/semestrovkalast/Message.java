package com.example.semestrovkalast;

import java.io.Serializable;
import java.util.Arrays;

public class Message implements Serializable {

    private int idRoom;
    private int idEnemy;
    private int idSender;
    private int col;
    private int row;
    private String status;
    private char[][] charGameBoard;
    private String message;

//    public Message(int idRoom, int idEnemy, String status) {
//        this.idRoom = idRoom;
//        this.idEnemy = idEnemy;
//        this.status = status;
//    }

    public Message(int idRoom, int idSender, char[][] charGameBoard, String status) {
        this.idRoom = idRoom;
        this.idSender = idSender;
        this.charGameBoard = charGameBoard;
        this.status = status;
    }

    public Message() {}

    public Message(int idRoom, int idSender, int col, int row, String status) {
        this.idRoom = idRoom;
        this.idSender = idSender;
        this.col = col;
        this.row = row;
        this.status = status;
    }

    public Message(String status) {
        this.status = status;
    }

    public Message(int col, int row, String status) {
        this.col = col;
        this.row = row;
        this.status = status;
    }

    public int getIdRoom() {
        return idRoom;
    }

    public int getIdEnemy() {
        return idEnemy;
    }

    public String getMove() {
        return col + " " + row;
    }

    public int getIdSender() {
        return idSender;
    }

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }

    public String getStatus() {
        return status;
    }

    public char[][] getCharGameBoard() {
        return charGameBoard;
    }

    public void setCharGameBoard(char[][] charGameBoard) {
        this.charGameBoard = charGameBoard;
    }

    @Override
    public String toString() {
        return "Message{" +
                "idRoom=" + idRoom +
                ", idEnemy=" + idEnemy +
                ", idSender=" + idSender +
                ", col=" + col +
                ", row=" + row +
                ", status='" + status + '\'' +
                ", charGameBoard=" + Arrays.toString(charGameBoard) +
                '}';
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
