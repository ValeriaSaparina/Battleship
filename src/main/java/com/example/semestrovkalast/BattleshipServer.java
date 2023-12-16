package com.example.semestrovkalast;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BattleshipServer {
    private ServerSocket serverSocket;

    private HashMap<Integer, GameRoom> startedGameRooms;
    private transient List<Socket> allSockets;

    private static BattleshipServer serverInstance;

    private BattleshipServer() {
        try {
            serverSocket = new ServerSocket(Params.PORT);
            startedGameRooms = new HashMap<>();
            allSockets = new ArrayList<>();
        } catch (IOException e) {
            e.fillInStackTrace();
        }
    }

    public static BattleshipServer getServerInstance() {
        if (serverInstance == null) {
            serverInstance = new BattleshipServer();
        }
        return serverInstance;
    }

    public static void main(String[] args) {
        BattleshipServer server = getServerInstance();
        server.start();
    }

    public void start() {
        EnterListener enterListener = new EnterListener(this);
        new Thread(enterListener).start();
    }


    public ServerSocket getServerSocket() {
        return serverSocket;
    }


    public void addStartedGameRoom(int id, GameRoom gameRoom) {
        startedGameRooms.put(id, gameRoom);
    }

    public GameRoom getRoom(int idRoom) {
        for (GameRoom room : startedGameRooms.values()) System.out.println(room.getId());
        return startedGameRooms.get(idRoom);
    }


    public void addSocket(Socket clientSocket) {
        allSockets.add(clientSocket);
        System.out.println("size list<socket> after adding: " + allSockets.size());
    }

}
