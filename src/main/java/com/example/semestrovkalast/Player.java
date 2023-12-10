package com.example.semestrovkalast;

import javafx.scene.Parent;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.Arrays;

public class Player implements Serializable {

    private String name;
    private Socket playerSocket;
    private boolean isReady = false;
    private int id;
    private int[] message = null;
    private boolean isWinner = false;
    private Board gameBoard;
    private GameRoom gameRoom;
    private int roomID;

    public Player(Socket playerSocket) {
        this.playerSocket = playerSocket;
        this.name = "Name of player";
    }

    public Player(String name, Socket playerSocket) {
        this(playerSocket);
        this.name = name;
        // Initialize the game board and place ships as needed
    }

    public GameRoom getGameRoom() {
        return gameRoom;
    }

    public void setGameRoom(GameRoom gameRoom) {
        this.gameRoom = gameRoom;
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

    public String getName() {
        return name;
    }

    public Board getGameBoard() {
        return gameBoard;
    }
    public void setGameBoard(Board gameBoard) {
        this.gameBoard = gameBoard;
    }

//    @Override
//    public String toString() {
//        return "Player{" +
//                "name='" + name + '\'' +
//                ",\nplayerSocket=" + playerSocket +
////                ",\ngameBoard=" + Arrays.toString(gameBoard.getBoard().getChildren().toArray()) +
//                ",\nisReady=" + isReady +
//                ",\nmessage=" + Arrays.toString(message) +
//                ",\nisWinner=" + isWinner +
//                '}';
//    }

    public void makeMove(Integer columnIndex, Integer rowIndex) throws IOException {
        Message message = new Message(roomID, id, columnIndex, rowIndex, Params.SHOT);
        ObjectOutputStream output = new ObjectOutputStream(playerSocket.getOutputStream());
        output.writeObject(message);
        output.flush();
        System.out.println(message);
    }

    public int[] move() {
        while (message == null) {}
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRoomID() {
        return roomID;
    }

    public void setRoomID(int roomID) {
        this.roomID = roomID;
    }
}

