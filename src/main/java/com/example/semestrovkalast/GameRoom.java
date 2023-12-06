package com.example.semestrovkalast;

import java.util.ArrayList;
import java.util.List;

public class GameRoom implements Runnable {
    private boolean isReady = false;
    private List<Player> playerList = new ArrayList<>();

    private BattleshipUI battleshipUI;
    private boolean noWinner = true;

    @Override
    public void run() {
        // Game room setup and player interactions

        // Wait for the game to be ready before starting
        waitUntilReady();
//        startGame();


        while (noWinner) {
            for (Player player : playerList) {
                player.move();
                if (player.isWinner()) {
                    noWinner = false;
                    break;
                }
            }
        }



        // The game room is ready, start the game
//        startGame();
    }

    private void waitUntilReady() {
        // Logic to wait until the game is ready
        while (!isReady) {
            setReady(playerList);
            try {
                Thread.sleep(100);  // Sleep for a short period
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void startGame() {
//        TODO: startGame()
//        smth like change setOnAction()

    }

    public void setReady(List<Player> players) {
        boolean ready = true;
        for (Player p : players) {
            ready &= p.isReady();
        }
        isReady = ready;
    }

    public boolean isFull() {
        if (playerList.size() != 2) return false;

        isReady = true;
        return true;
    }

    public void addPlayer(Player player) {
        playerList.add(player);
    }
}
