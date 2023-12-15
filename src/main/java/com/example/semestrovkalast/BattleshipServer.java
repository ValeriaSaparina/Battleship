package com.example.semestrovkalast;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BattleshipServer {
    private ServerSocket serverSocket;

    private HashMap<Integer, GameRoom> startedGameRooms;
    private transient List<Socket> allSockets;

    private MoveListener moveListener;
    private EnterListener enterListener;

    private static BattleshipServer serverInstance;
    private final int PORT = 4004;

    private BattleshipServer() {
        try {
            serverSocket = new ServerSocket(PORT);
            startedGameRooms = new HashMap<>();
            allSockets = new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
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
        System.out.println("Server started. Waiting for clients...");
//        moveListener = new MoveListener(this);
        enterListener = new EnterListener(this);
        new Thread(enterListener).start();
//        new Thread(moveListener).start();
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

    public MoveListener getMoveListener() {
        return moveListener;
    }

    public List<Socket> getAllSockets() {
        return allSockets;
    }


    public void addSocket(Socket clientSocket) {
        allSockets.add(clientSocket);
        System.out.println("size list<socket> after adding: " + allSockets.size());
    }

    public Map<Integer, GameRoom> getStartedGameRooms() {
        return startedGameRooms;
    }
}
