package com.example.semestrovkalast;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.List;

public class StartListener implements Runnable {

    private final GameRoom gameRoom;
    private ObjectInputStream fromFirstPlayer;
    private ObjectInputStream fromSecondPlayer;
    private final List<Player> playerList;

    public StartListener(GameRoom gameRoom) throws IOException {
        this.gameRoom = gameRoom;
        this.playerList = gameRoom.getPlayerList().values().stream().toList();
        initInputStreams();
    }

    private void initInputStreams() throws IOException {
        this.fromFirstPlayer = new ObjectInputStream(playerList.get(0).getPlayerSocket().getInputStream());
        this.fromSecondPlayer = new ObjectInputStream(playerList.get(1).getPlayerSocket().getInputStream());

    }

    @Override
    public void run() {
        Thread t1 = new Thread(() -> {
            Player player = playerList.get(0);
            player.setReady(false);
            readFromPlayer(player, fromFirstPlayer);
        });
        Thread t2 = new Thread(() -> {
            Player player = playerList.get(1);
            player.setReady(false);
            readFromPlayer(player, fromSecondPlayer);
        });

        t1.start();
        t2.start();

        while (t1.isAlive() || t2.isAlive()) {}

        gameRoom.setReady(true);
    }

    private void readFromPlayer(Player player, ObjectInputStream input) {
        while (!player.isReady()) {
            try {
                Object message = input.readObject();
                if (message instanceof Message) {
                    if (((Message) message).getStatus().equals(Params.READY)) {
                        player.setReady(true);
                        player.setCharGameBoard(((Message) message).getCharGameBoard());
                        System.out.println(Arrays.deepToString(player.getCharGameBoard()));
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
