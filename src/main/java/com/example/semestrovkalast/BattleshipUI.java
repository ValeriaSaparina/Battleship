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
    private final Player player;
    private final StringProperty notification;
    private Board playerBoard;
    private Board enemyBoard;
    private int gameRoomID = 9;
    private Button restartButton;
    private boolean isRestart;
    private Stage primaryStage;

    public BattleshipUI(Player player) {
        this.player = player;
        this.notification = new SimpleStringProperty("Place 10 ships");
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
        return null;
    }

    public void updateBoardUI(Message message, GridPane board) {
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

    private HBox gethBox() {

        VBox menu = getMenu();

        HBox hBox = new HBox(playerBoard.getBoard(), menu, enemyBoard.getBoard());
        hBox.setSpacing(10);
        hBox.setPadding(new Insets(10, 10, 10, 10));
        return hBox;
    }

    private VBox getMenu() {
        Button clearButton = getClearButton();
        Button startButton = getStartButton(clearButton);
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

    private Button getStartButton(Button clearButton) {
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
            }
        });
        return startButton;
    }

    private void showStage(Stage primaryStage) {
        Scene scene = new Scene(gethBox(), 950, 500);
        primaryStage.setTitle("Battleship");
        primaryStage.setScene(scene);
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
                button.setMinSize(40, 40);
                int x = i;
                int y = j;
                if (!isEnemyBoard) {
                    button.setOnMousePressed(event -> handleMouseClick(x, y, event.getButton()));
                }
                gridPane.add(button, j, i);
            }
        }
        return new Board(gridPane);
    }


    public void updateSetOnActions() {
        updateActionForEnemyBoard();
        updateActionForPlayerBoard();
    }

    private void updateActionForPlayerBoard() {
        List<Node> nodes = playerBoard.getBoard().getChildren();
        for (Node node : nodes) {
            ((Button) node).setOnAction(event -> {
            });
            node.setOnMousePressed(mouseEvent -> {
            });
        }

    }

    private void updateActionForEnemyBoard() {
        List<Node> nodes = enemyBoard.getBoard().getChildren();
        for (Node node : nodes) {
            node.setOnMousePressed(event -> {

                try {
                    if (player.isMoving()) {
                        player.makeMove(GridPane.getColumnIndex(node), GridPane.getRowIndex(node));
                        node.setDisable(true);
                    }
                } catch (IOException e) {
                    e.fillInStackTrace();
                }
            });
        }
    }

    private void handleMouseClick(int row, int col, MouseButton buttonType) {
        int[] ships = playerBoard.getShips();
        int id = 0;
        while (ships[id] == 0) id += 1;
        if (playerBoard.placeShip(new Ship(id + 1, buttonType == MouseButton.PRIMARY), col, row)) {
            ships[id] -= 1;
            player.incNumberShips();
        }
    }


    public void setGameRoomID(int gameRoomID) {
        this.gameRoomID = gameRoomID;
    }

    public Board getEnemyBoard() {
        return enemyBoard;
    }

    public void setNotification(String notification) {
        Platform.runLater(() -> this.notification.set(notification));
    }

    public StringProperty notificationProperty() {
        return notification;
    }

    public void showRestartButton() {
        Platform.runLater(() -> restartButton.setVisible(true));
    }

    public boolean isRestart() {
        return isRestart;
    }

    public void setRestart(boolean restart) {
        this.isRestart = restart;
    }

}

