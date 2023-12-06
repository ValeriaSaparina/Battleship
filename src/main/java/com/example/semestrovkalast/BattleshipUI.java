package com.example.semestrovkalast;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.List;

public class BattleshipUI extends Application {
    private char[][] player1Board;
    private char[][] player2Board;
    private GridPane playerBoardGridPane;
    private GridPane enemyBoardGridPane;
    private Player player;
    private boolean isStart = false;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
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

    public void updateBoardUI(int col, int row, GridPane board, String message) {
        Button button = getButtonFromGridPane(col, row, board);
        if (button != null) {
            if (message.equals(Params.DESTROYED)) {
                button.setStyle("-fx-background-color: darkgray;");
            } else {
                if (message.equals(Params.MISS)) {
                    button.setStyle("-fx-background-color: lightblue;");
                } else {
                    button.setStyle("-fx-background-color: red;");
                }
            }
        }
    }

    private HBox gethBox() {
        Button startButton = new Button("Start Game");
        startButton.setOnAction(event -> {
            startButton.setVisible(false);
        });

        HBox hBox = new HBox(playerBoardGridPane, startButton, enemyBoardGridPane);
        hBox.setSpacing(10);
        hBox.setPadding(new Insets(10, 10, 10, 10));
        return hBox;
    }

    private void showStage(Stage primaryStage) {
        primaryStage.setTitle("Battleship");

        Scene scene = new Scene(gethBox(), 500, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void processMove(int x, int y, Button button,/* char[][] board,*/ boolean isEnemyBoard) {
        if (isStart) {
            if (isEnemyBoard) {
                button.setText("C");
                // TODO: implement move
//                ((Button)(enemyBoardGridPane.getChildren()).get(0)).setText("c");
//                System.out.println((enemyBoardGridPane.getChildren()).get(1));
            } else {
                button.setOnAction(event -> {});
            }
            // Handle the player's move
            // For example, update the game logic with the move and update the UI based on the game state
        } else {
            if (isEnemyBoard) {
                button.setOnAction(event -> {});
            } else {
//                button.
            }
        }
    }



    private void initGameBoards() {


        playerBoardGridPane = initGameBoard(false);
        enemyBoardGridPane = initGameBoard(true);

//        this.player = player;
//        player1Board = new char[10][10];
//        player1Board = player1.getGameBoard();
//        player2Board = new char[10][10];
//        int k = 0;
//        for (int i = 0; i < 10; i++) {
//            for (int j = 0; j < 10; j++) {
//                k++;
//                player2Board[i][j] = String.valueOf(k).charAt(0);
//            }
//        }


        // Initialize the game boards with the initial state and ship placements
        // For example:
        // placeShips(player1Board);
        // placeShips(player2Board);
    }

    private GridPane initGameBoard(boolean isEnemyBoard) {
        GridPane gridPane = new GridPane();
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                Button button = new Button();
                button.setMinSize(40, 40); // Set button size
                int x = j;
                int y = i;
                button.setOnAction(event -> {
                    processMove(x, y, button, /*board,*/ isEnemyBoard);
                });
//                button.setText(String.valueOf(board[i][j]));
                gridPane.add(button, j, i);
            }
        }
        return gridPane;
    }

    public void launchGUI() {
        launch();
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void updateSetOnActions() {
        updateActionForEnemyBoard();
    }

    private void updateActionForEnemyBoard() {
        List<Node> nodes = enemyBoardGridPane.getChildren();
        for (Node node : nodes) {
            ((Button) node).setOnAction(event -> {
                player.makeMove(GridPane.getColumnIndex(node), GridPane.getRowIndex(node));
            });
        }
    }
}

