package com.example.semestrovkalast;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class StartListener implements Runnable {

    private BattleshipServer server;
    private GameRoom gameRoom;
    private ObjectInputStream fromFirstPlayer;
    private ObjectInputStream fromSecondPlayer;
    private List<Player> playerList;
    private boolean started = false;

    public StartListener(BattleshipServer server, GameRoom gameRoom) throws IOException {
        this.server = server;
        this.gameRoom = gameRoom;
        this.playerList = gameRoom.getPlayerList().values().stream().toList();
        initInputStreams();
    }

    private void initInputStreams() throws IOException {
//        this.fromFirstPlayer = new ObjectInputStream(playerList.get(0).getPlayerSocket().getInputStream());
//        this.fromSecondPlayer = new ObjectInputStream(playerList.get(1).getPlayerSocket().getInputStream());
        System.out.println("init" + server.getAllSockets().get(0).isClosed());
        this.fromFirstPlayer = new ObjectInputStream(server.getAllSockets().get(0).getInputStream());
        this.fromSecondPlayer = new ObjectInputStream(server.getAllSockets().get(1).getInputStream());
    }

    @Override
    public void run() {
        Thread t1 = new Thread(() -> {
            System.out.println("First starListener is started");
            readFromPlayer(playerList.get(0), fromFirstPlayer);
        });
        Thread t2 = new Thread(() -> {
            System.out.println("Second starListener is started");
            readFromPlayer(playerList.get(1), fromSecondPlayer);
        });

        t1.start();
        t2.start();

        while (t1.isAlive() || t2.isAlive()) {
        }

        gameRoom.setReady(true);
    }

    private void readFromPlayer(Player player, ObjectInputStream input) {
        while (!player.isReady()) {
            try {
                Object message = input.readObject();
                if (message instanceof Message) {
                    if (((Message) message).getStatus().equals(Params.READY)) {
                        player.setReady(true);
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
