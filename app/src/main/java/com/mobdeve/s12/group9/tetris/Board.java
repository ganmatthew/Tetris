package com.mobdeve.s12.group9.tetris;

public class Board {

    private int rows, columns;
    private Block[][] grid;

    public Board (int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        createBoardGrid();
    }

    private void createBoardGrid() {
        this.grid = new Block[this.rows][this.columns];

        // Generate 2D array of blocks for the grid
        for (int row = 0; row < this.rows; row++) {
            for (int column = 0; column < this.columns; column++) {
                this.grid[row][column] = new Block(row, column);
            }
        }
    }

    private Block getBlockCoords(Coordinates coords) {
        return this.grid[coords.y][coords.x];
    }

}
