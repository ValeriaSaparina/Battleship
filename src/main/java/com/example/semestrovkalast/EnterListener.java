package com.example.semestrovkalast;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class EnterListener implements Runnable {
    private static int idPlayer = 0;
    private static int idRoom = 0;
    private final BattleshipServer battleshipServer;
    private final List<GameRoom> availableGameRoomList;

    public EnterListener(BattleshipServer battleshipServer) {
        this.battleshipServer = battleshipServer;
        this.availableGameRoomList = new ArrayList<>();
    }

    @Override
    public void run() {
        while (true) {
            try {
                Socket clientSocket = battleshipServer.getServerSocket().accept();
                System.out.println(clientSocket);
                Player player = new Player(clientSocket);

                GameRoom gameRoom = getAvailableGameRoom();
                gameRoom.addPlayer(player);

                battleshipServer.addSocket(clientSocket);

                BufferedWriter output = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                output.write(gameRoom.getId() + " " + idPlayer + "\n");
                output.flush();
                if (gameRoom.isFull()) {
                    Thread gameThread = new Thread(gameRoom);
                    battleshipServer.addStartedGameRoom(idRoom, gameRoom);
                    gameThread.start();
                    availableGameRoomList.remove(gameRoom);

                    (new StartListener(gameRoom)).run();
                    idRoom += 1;
                }
                idPlayer += 1;

            } catch (IOException e) {
                e.fillInStackTrace();
            }
        }
    }

    public GameRoom getAvailableGameRoom() {
        for (GameRoom room : availableGameRoomList) {
            if (!room.isFull()) {
                return room;
            }
        }
        GameRoom newRoom = new GameRoom();
        availableGameRoomList.add(newRoom);
        return newRoom;
    }


}
