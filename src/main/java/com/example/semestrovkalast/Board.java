package com.example.semestrovkalast;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.List;

public class Board {
    private final GridPane board;
    private int[] ships;

    public Board(GridPane board) {
        this.board = board;
        setShips();
    }

    public void setShips() {
        ships = new int[]{4, 3, 2, 1};
    }

    public int[] getShips() {
        return ships;
    }

    public boolean placeShip(Ship ship, int x, int y) {
        System.out.println(ship.type);
        if (canPlaceShip(ship, x, y)) {
            int length = ship.type;
            if (ship.vertical) {
                for (int i = y; i < y + length; i++) {
                    Button btn = getCell(x, i);
                    btn.setText("X");
                }
            } else {
                for (int i = x; i < x + length; i++) {
                    Button btn = getCell(i, y);
                    btn.setText("X");
                }
            }
            return true;
        }
        return false;
    }

    private boolean canPlaceShip(Ship ship, int x, int y) {
        int length = ship.type;

        if (ship.vertical) {
            for (int i = y; i < y + length; i++) {
                if (!isValidPoint(x, i))
                    return false;

                Button btn = getCell(x, i);
                if (btn.getText().equals("X"))
                    return false;

                for (Button neighbor : getNeighbors(x, i)) {
                    if (!isValidPoint(x, i))
                        return false;

                    if (neighbor.getText().equals("X"))
                        return false;
                }
            }
        } else {
            for (int i = x; i < x + length; i++) {
                if (!isValidPoint(i, y))
                    return false;

                Button cell = getCell(i, y);
                if (cell.getText().equals("X"))
                    return false;

                for (Button neighbor : getNeighbors(i, y)) {
                    if (!isValidPoint(i, y))
                        return false;

                    if (neighbor.getText().equals("X"))
                        return false;
                }
            }
        }

        return true;
    }

    private Button[] getNeighbors(int x, int y) {
        Point2D[] points = new Point2D[]{
                new Point2D(x - 1, y),
                new Point2D(x + 1, y),
                new Point2D(x, y - 1),
                new Point2D(x, y + 1),
                new Point2D(x + 1, y + 1),
                new Point2D(x + 1, y - 1),
                new Point2D(x - 1, y - 1),
                new Point2D(x - 1, y + 1),
        };

        List<Button> neighbors = new ArrayList<>();

        for (Point2D p : points) {
            if (isValidPoint(p)) {
                neighbors.add(getCell((int) p.getX(), (int) p.getY()));
            }
        }

        return neighbors.toArray(new Button[0]);
    }

    private boolean isValidPoint(Point2D point) {
        return isValidPoint(point.getX(), point.getY());
    }

    private boolean isValidPoint(double x, double y) {
        return x >= 0 && x < 10 && y >= 0 && y < 10;
    }

    public GridPane getBoard() {
        return board;
    }

    private Button getCell(int col, int row) {
        for (Node node : board.getChildren()) {
            if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row && node instanceof Button) {
                return (Button) node;
            }
        }
        return null;
    }

    public void clear() {
        for (Node node : board.getChildren()) {
            ((Button) node).setText("");
        }
        setShips();
    }
}
