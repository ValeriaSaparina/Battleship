package com.example.semestrovkalast;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

public class Board {
    private GridPane board;

    public Board(GridPane board) {
        this.board = board;
    }

    public void placeShips() {

    }

    private void updateBoard() {}

    public void checkNeighbors() {}

    public GridPane getBoard() {
        return board;
    }

    public void setBoard(GridPane board) {
        this.board = board;
    }

    public boolean isShot(int col, int row) {
        Button cell = getCell(col, row);
        if (cell == null) return false;
        Color color = (Color) cell.getBackground().getFills().get(0).getFill();
        return color == Color.RED || color == Color.BLACK;
    }

    private Button getCell(int col, int row) {
        for (Node node : board.getChildren()) {
            if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row && node instanceof Button) {
                return (Button) node;
            }
        }
        return null;  // Return null if the button is not found at the specified position
    }

    public void move(int col, int row) {
        updateBoard();
    }
}
