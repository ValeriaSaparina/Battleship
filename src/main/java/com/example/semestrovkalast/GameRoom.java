package com.example.semestrovkalast;

import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GameRoom implements Runnable {
    private static int playerID;
    private static int roomId;

    static {
        playerID = 0;
        roomId = 0;
    }

    private final MoveListener moveListener;
    private boolean isReady;
    private final HashMap<Integer, Player> playerList;

    public GameRoom() {
        roomId += 1;
        isReady = false;
        playerList = new HashMap<>();
        moveListener = new MoveListener(this);
    }

    @Override
    public void run() {
        while (true) {
            waitUntilReady();
            startGame();
            moveListener.readingMessages();
        }
    }

    private void waitUntilReady() {
        while (!isReady) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        isReady = false;
    }


    private void startGame() {

        for (Player player : playerList.values()) {
            player.setNumberShips(10);
        }

        int whoMove = 0;
        setFirstMovingPlayer(whoMove);

    }


    public boolean isFull() {
        return playerList.size() == 2;
    }

    public void addPlayer(Player player) {
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


    public void setReady(boolean isReady) {
        this.isReady = isReady;
    }

    public void setFirstMovingPlayer(int whoMove) {
        List<Player> values = playerList.values().stream().toList();
        values.get(whoMove).setMoving(true);
        List<Socket> list = new ArrayList<>();
        list.add(values.get(whoMove).getPlayerSocket());
        list.add(values.get(1 - whoMove).getPlayerSocket());
        moveListener.setSockets(list);
        moveListener.setWhoMove(whoMove);
    }

}
