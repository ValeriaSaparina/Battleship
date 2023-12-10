package com.example.semestrovkalast;

import java.io.Serializable;

public class Message implements Serializable {

    private int idRoom;
    private int idEnemy;
    private int idSender;
    private int col;
    private int row;
    private String status;
    private String move; //TODO: replace String to Move class;

//    public Message(int idRoom, int idEnemy, String status) {
//        this.idRoom = idRoom;
//        this.idEnemy = idEnemy;
//        this.status = status;
//    }

    public Message(int idRoom, int idSender, String status) {
        this.idRoom = idRoom;
        this.idSender = idSender;
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

    public int getIdRoom() {
        return idRoom;
    }

    public int getIdEnemy() {
        return idEnemy;
    }

    public String getMove() {
        return move;
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
}
