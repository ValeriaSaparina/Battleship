package com.example.semestrovkalast;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

public class BattleshipUI extends Application {
    private Board playerBoard;
    private Board enemyBoard;
    private Player player;
    private int gameRoomID = 9;
    private boolean end = true;

    private StringProperty notification;
    private Button restartButton;
    private boolean isRestart;
    private Scene scene;
    private Stage primaryStage;
//    private GameRoom gameRoom;

    public BattleshipUI(Player player, GameRoom gameRoom) {
        this.player = player;
        this.notification = new SimpleStringProperty("Place 10 ships");
        System.out.println("moving in constructor: " + player.isMoving());
//        this.gameRoom = gameRoom;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        initUI();

    }

    public void initUI() {
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

    public void updateBoardUI(Message message, /*int col, int row,*/ GridPane board/*, String message*/) {
        Button button = getButtonFromGridPane(message.getCol(), message.getRow(), board);
        String status = message.getStatus();
        if (button != null) {
            if (status.equals(Params.DESTROYED)) {
                button.setStyle("-fx-background-color: black;");
                if (message.getMessage() != null) {
                    String[] cells = message.getMessage().split(" ");
                    for (int i = 0; i < cells.length - 1; i += 2) {
                        getButtonFromGridPane(Integer.parseInt(cells[i]), Integer.parseInt(cells[i + 1]), board).setStyle("-fx-background-color: black;");
                    }
                }
            } else {
                if (status.equals(Params.MISS)) {
                    button.setStyle("-fx-background-color: lightblue;");
                } else if (status.equals(Params.HIT)) {
                    button.setStyle("-fx-background-color: red;");
                }
            }
        }
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

        VBox menu = getMenu();

        HBox hBox = new HBox(playerBoard.getBoard(), menu, enemyBoard.getBoard());
        hBox.setSpacing(10);
        hBox.setPadding(new Insets(10, 10, 10, 10));
        return hBox;
    }

    private VBox getMenu() {
        Button clearButton = getClearButton();
        Button startButton = getButton(clearButton);
        restartButton = getRestartButton();

        Label notification = new Label();
        notification.textProperty().bind(notificationProperty());

        return new VBox(startButton, clearButton, notification, restartButton);
    }

    private Button getRestartButton() {
        Button restart = new Button();
        restart.setOnMousePressed(mouseEvent -> {
            isRestart = true;
            System.out.println("set restart");
        });
        restart.setVisible(false);
        restart.setText("Play again");
        return restart;
    }

    private Button getClearButton() {
        Button clearButton = new Button();
        clearButton.setOnMousePressed(mouseEvent -> {
            playerBoard.clear();
            player.setNumberShips(0);
        });
        clearButton.setText("Clear board");
        return clearButton;
    }

    private Button getButton(Button clearButton) {
        Button startButton = new Button("Start Game");
        startButton.setOnAction(event -> {
            if (player.getNumberShips() == 10) {
                startButton.setVisible(false);
                clearButton.setVisible(false);
                try {
                    player.setCharGameBoard();
                    ObjectOutputStream toServer = new ObjectOutputStream(player.getPlayerSocket().getOutputStream());
                    toServer.writeObject(new Message(gameRoomID, player.getId(), player.getCharGameBoard(), Params.READY));
                    toServer.flush();
                    player.setReady(true);
                    notification.set("Waiting enemy");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                notification.set("place all 10 ships");
                System.out.println("place all 10 ships. Now only: " + player.getNumberShips());
            }
        });
        return startButton;
    }

    private void showStage(Stage primaryStage) {
        primaryStage.setTitle("Battleship");

        this.scene = new Scene(gethBox(), 500, 500);
        primaryStage.setScene(scene);
        System.out.println("in showStage");
        primaryStage.show();
    }


    private void initGameBoards() {

        playerBoard = initGameBoard(false);
        player.setGameBoard(playerBoard);
        enemyBoard = initGameBoard(true);

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
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private void handleMouseClick(Button button, int row, int col, MouseButton buttonType) {
        int[] ships = playerBoard.getShips();
        int id = 0;
        while (ships[id] == 0) id += 1;
        if (playerBoard.placeShip(new Ship(id + 1, buttonType == MouseButton.PRIMARY), col, row)) {
            ships[id] -= 1;
            player.incNumberShips();
        }
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

    public String getNotification() {
        return notification.get();
    }

    public void setNotification(String notification) {
        Platform.runLater(() -> {
            this.notification.set(notification);
        });
    }

    public StringProperty notificationProperty() {
        return notification;
    }

    public void showRestartButton() {
        Platform.runLater(() -> {
            restartButton.setVisible(true);
        });
    }

    public boolean isRestart() {
        return isRestart;
    }

    public void setRestart(boolean restart) {
        this.isRestart = restart;
    }

    public void closeWindow() {
        Platform.runLater(() -> {
            Stage stage = (Stage) scene.getWindow();
            stage.close();
        });
    }
}

