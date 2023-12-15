package com.example.semestrovkalast;

import javafx.application.Platform;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class EnterListener implements Runnable {
    private static int idPlayer = 0;
    private static int idRoom = 0;
    private BattleshipServer battleshipServer;
    private List<GameRoom> availableGameRoomList;

    public EnterListener(BattleshipServer battleshipServer) {
        this.battleshipServer = battleshipServer;
        this.availableGameRoomList = new ArrayList<>();
    }

    @Override
    public void run() {

        // TODO: add new Thread for register players

        System.out.println("Accept thread is started");
        while (true) {
            try {
                Socket clientSocket = battleshipServer.getServerSocket().accept();
                System.out.println(clientSocket);
                Player player = new Player(clientSocket);
                System.out.println("New client connected: " + player.getPlayerSocket());

                GameRoom gameRoom = getAvailableGameRoom();
                gameRoom.addPlayer(player);

                battleshipServer.addSocket(clientSocket);
//                BufferedReader fromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//                String status = fromClient.readLine();
//                if (status.equals(Params.GET_ROOM_ID)) {
//                    BufferedWriter output = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
//                    output.write(String.valueOf(gameRoom.getId()));
////                    output.newLine();
//                    output.flush();
//                }
                System.out.println("room" + gameRoom.getId());
                BufferedWriter output = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                output.write(gameRoom.getId() + " " + idPlayer + "\n");
                output.flush();
                if (gameRoom.isFull()) {
                    Thread gameThread = new Thread(gameRoom);
                    battleshipServer.addStartedGameRoom(idRoom, gameRoom);
                    gameThread.start();
                    availableGameRoomList.remove(gameRoom);

                    (new StartListener(battleshipServer, gameRoom)).run();
                    idRoom += 1;
                }
                idPlayer += 1;

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public GameRoom getAvailableGameRoom() {
        for (GameRoom room : availableGameRoomList) {
            if (!room.isFull()) {
                return room;
            }
        }
        GameRoom newRoom = new GameRoom(battleshipServer);
        availableGameRoomList.add(newRoom);
        return newRoom;
    }


}
