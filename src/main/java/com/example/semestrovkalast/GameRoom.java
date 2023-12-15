package com.example.semestrovkalast;

import java.io.IOException;
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

    private MoveListener moveListener;
    private boolean isReady;
    private HashMap<Integer, Player> playerList;
    private boolean noWinner;
    private boolean restart = true;

    public GameRoom(BattleshipServer battleshipServer) {
        roomId += 1;
        isReady = false;
        playerList = new HashMap<>();
        noWinner = true;
        moveListener = new MoveListener(battleshipServer, this);
    }

    @Override
    public void run() {
        while (restart) {
            System.out.println("gameThread is started");

            waitUntilReady();
//        moveListener.processingMessages();
            startGame();
            moveListener.readingMessages();
//            System.out.println();
//            try {
//                new StartListener(BattleshipServer.getServerInstance(), this);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
    }

//    public

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
        System.out.println("Game start");

        for (Player player : playerList.values()) {
            player.setNumberShips(10);
        }

//        int whoMove = (new Random()).nextInt(2);
        int whoMove = 0;
        System.out.println("moving: " + whoMove);
//        for (Player player : playerList.values()) {
//            try {
//                BufferedWriter toClient = new BufferedWriter(new OutputStreamWriter(player.getPlayerSocket().getOutputStream()));
////                toClient.write(whoMove);
//                toClient.write(Params.SUCCESS);
//                toClient.flush();
        setFirstMovingPlayer(whoMove);
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }

//        while (noWinner) {
//            for (Player player : playerList.values()) {
//                player.move();
//                if (player.isWinner()) {
//                    noWinner = false;
//                    break;
//                }
//            }
//        }
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


    public boolean isReady() {
        return isReady;
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
        System.out.println("whoMove after generated: " + whoMove);
        moveListener.setWhoMove(whoMove);
    }

}
