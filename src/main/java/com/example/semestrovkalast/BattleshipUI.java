package com.example.semestrovkalast;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

public class BattleshipUI extends Application {
    private Board playerBoard;
    private Board enemyBoard;
    private Player player;
    private boolean isStart = false;
    private int gameRoomID = 9;
    private boolean end = true;
//    private GameRoom gameRoom;

    public BattleshipUI(Player player, GameRoom gameRoom) {
        this.player = player;
        System.out.println("moving in constructor: " + player.isMoving());
//        this.gameRoom = gameRoom;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
//        gameRoom = player.getGameRoom();
        initGameBoards();
        showStage(primaryStage);
    }

    private Button getButtonFromGridPane(int col, int row, GridPane gridPane) {
        for (Node node : gridPane.getChildren()) {
            if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row && node instanceof Button) {
                return (Button) node;
            }
        }
        return null;  // Return null if the button is not found at the specified position
    }

    public boolean updateBoardUI(int col, int row, GridPane board, String message) {
        Button button = getButtonFromGridPane(col, row, board);
        if (button != null) {
            if (message.equals(Params.DESTROYED)) {
                button.setStyle("-fx-background-color: darkgray;");
                return true;
            } else {
                if (message.equals(Params.MISS)) {
                    button.setStyle("-fx-background-color: lightblue;");
                    return true;
                } else {
                    button.setStyle("-fx-background-color: red;");
                    return true;
                }
            }
        }
        return false;
    }

    public void updateBoardUI(Node node, String message) {
        if (node != null) {
            System.out.println("upd node is not null; message: " + message);
            System.out.println("message.equals(Params.HIT): " + message.equals(Params.HIT));
            if (message.equals(Params.DESTROYED)) {
                node.setStyle("-fx-background-color: darkgray;");
            } else {
                if (message.equals(Params.MISS)) {
                    node.setStyle("-fx-background-color: lightblue;");
                } else if (message.equals(Params.HIT)) {
                    System.out.println("upd we are in hit");
                    node.setStyle("-fx-background-color: red;");
                }
            }
        }
    }

    private HBox gethBox() {
        Button startButton = new Button("Start Game");
        startButton.setOnAction(event -> {
            startButton.setVisible(false);
            try {
                player.setCharGameBoard();
                ObjectOutputStream toServer = new ObjectOutputStream(player.getPlayerSocket().getOutputStream());
                toServer.writeObject(new Message(gameRoomID, player.getId(), player.getCharGameBoard(), Params.READY));
                toServer.flush();
                player.setReady(true);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        HBox hBox = new HBox(playerBoard.getBoard(), startButton, enemyBoard.getBoard());
        hBox.setSpacing(10);
        hBox.setPadding(new Insets(10, 10, 10, 10));
        return hBox;
    }

    private void showStage(Stage primaryStage) {
        primaryStage.setTitle("Battleship");

        Scene scene = new Scene(gethBox(), 500, 500);
        primaryStage.setScene(scene);
        System.out.println("in showStage");
        primaryStage.show();
    }


    private void initGameBoards() {

        playerBoard = initGameBoard(false);
        player.setGameBoard(playerBoard);
        enemyBoard = initGameBoard(true);

        // Initialize the game boards with the initial state and ship placements
        // For example:
        // placeShips(player1Board);
        // placeShips(player2Board);
    }

    private Board initGameBoard(boolean isEnemyBoard) {
        GridPane gridPane = new GridPane();
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                Button button = new Button();
                button.setMinSize(40, 40); // Set button size
                int x = i;
                int y = j;
                if (!isEnemyBoard) {
                    button.setOnMousePressed(event -> handleMouseClick(button, x, y, event.getButton()));
                }
                gridPane.add(button, j, i);
            }
        }
        return new Board(gridPane);
    }

    public void launchGUI() {
        launch();
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void updateSetOnActions() {
        System.out.println("update actions");
        updateActionForEnemyBoard();
        updateActionForPlayerBoard();
    }

    private void updateActionForPlayerBoard() {
        List<Node> nodes = playerBoard.getBoard().getChildren();
        for (Node node : nodes) {
            ((Button) node).setOnAction(event -> {
                System.out.println("clicked my board");
            });
            node.setOnMousePressed(mouseEvent -> {
            });
        }

    }

    private void updateActionForEnemyBoardOriginal() {
        System.out.println("enemy upd");
        List<Node> nodes = enemyBoard.getBoard().getChildren();
        for (Node node : nodes) {
            ((Button) node).setOnMousePressed(event -> {
                try {
                    System.out.println("player.isMoving() " + player.isMoving());
                    System.out.println("player.id in move: " + player.getId());
                    if (player.isMoving()) {
                        System.out.println("clicked on enemy board");
                        player.makeMove(GridPane.getColumnIndex(node), GridPane.getRowIndex(node));
//                        BufferedReader fromServer = new BufferedReader(new InputStreamReader(player.getPlayerSocket().getInputStream()));
//                        String response = fromServer.readLine();
//                        ObjectInputStream fromServer = new ObjectInputStream(player.getPlayerSocket().getInputStream());
//                        String response = ((Message) fromServer.readObject()).getStatus();
//                        System.out.println("status in UI: " + response);
//                        if (!response.equals(Params.ERROR)) {
//                            updateBoardUI(node, response);
//                        }
//                        this.end = true;
//                        System.out.println("end: " + end);
                        node.setDisable(true);
                    }
                } catch (IOException /*| ClassNotFoundException*/ e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private void updateActionForEnemyBoard() {
        System.out.println("enemy upd");
        List<Node> nodes = enemyBoard.getBoard().getChildren();
        for (Node node : nodes) {
            ((Button) node).setOnMousePressed(event -> {

                try {
                    System.out.println("player.isMoving() " + player.isMoving());
                    System.out.println("player.id in move: " + player.getId());
                    if (player.isMoving()) {
                        System.out.println("clicked on enemy board");
                        player.makeMove(GridPane.getColumnIndex(node), GridPane.getRowIndex(node));
                        node.setDisable(true);
//                        BufferedReader fromServer = new BufferedReader(new InputStreamReader(player.getPlayerSocket().getInputStream()));
//                        String response = fromServer.readLine();
//                        ObjectInputStream fromServer = new ObjectInputStream(player.getPlayerSocket().getInputStream());
//                        String response = ((Message) fromServer.readObject()).getStatus();
//                        System.out.println("status in UI: " + response);
//                        if (!response.equals(Params.ERROR)) {
//                            updateBoardUI(node, response);
//                        }
//                        this.end = true;
//                        System.out.println("end: " + end);
                    }
                } catch (IOException /*| ClassNotFoundException */e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private void handleMouseClick(Button button, int row, int col, MouseButton buttonType) {
        int[] ships = playerBoard.getShips();
        int id = 0;
        while (ships[id] == 0) id += 1;
        if (playerBoard.placeShip(new Ship(id + 1, buttonType == MouseButton.PRIMARY), col, row)) ships[id] -= 1;
    }


    public void setGameRoomID(int gameRoomID) {
//        gameRoom.setID(gameRoomID);
        this.gameRoomID = gameRoomID;
    }

    public boolean isEnd() {
//        System.out.println(end);
        return end;
    }

    public void setEnd(boolean end) {
        this.end = end;
    }

    public Board getEnemyBoard() {
        return enemyBoard;
    }
}

