package com.example.semestrovkalast;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;

public class GameRoom implements Runnable {
    private static int playerID;
    private boolean isReady;
    private HashMap<Integer, Player> playerList;
    private boolean noWinner;
    private static int roomId;

    static {
        playerID = 0;
        roomId = 0;
    }

    @Override
    public void run() {
        waitUntilReady();
        startGame();
    }

    public GameRoom() {
        roomId += 1;
        isReady = false;
        playerList = new HashMap<>();
        noWinner = true;
    }

    private void waitUntilReady() {
        while (!isReady) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }


    private void startGame() {
        System.out.println("Game start");
        for (Player player : playerList.values()) {
            try {
                BufferedWriter toClient = new BufferedWriter(new OutputStreamWriter(player.getPlayerSocket().getOutputStream()));
                toClient.write(1);
                toClient.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }


        while (noWinner) {
            for (Player player : playerList.values()) {
                player.move();
                if (player.isWinner()) {
                    noWinner = false;
                    break;
                }
            }
        }
//        TODO: smth like change setOnAction()
    }


    public boolean isFull() {
        return playerList.size() == 2;
    }

    public void addPlayer(Player player) {
        // TODO: get ID from server
        playerList.put(playerID, player);
        playerID += 1;
    }

    public Player getPlayer(int id) {
        return playerList.get(id);
    }

    public int getId() {
        return roomId;
    }

    public HashMap<Integer, Player> getPlayerList() {
        return playerList;
    }


    public boolean isReady() {
        return isReady;
    }

    public void setReady(boolean isReady) {
        this.isReady = isReady;
    }

}
