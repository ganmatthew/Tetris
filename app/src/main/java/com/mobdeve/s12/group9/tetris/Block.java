package com.mobdeve.s12.group9.tetris;

public class Block {
    // Block attributes
    private int colorId, tetroId;
    private Coordinates coords;

    // Construct empty block
    public Block(int row, int column) {
        this.colorId = -1;
        this.tetroId = -1;
        this.coords = new Coordinates(row, column);
    }

    // Construct non-empty block
    public Block(int colorId, int tetroId, Coordinates coords) {
        this.colorId = colorId;
        this.tetroId = tetroId;
        this.coords = coords;
    }

    // Defines a non-empty block, part of a tetromino
    public void setColorBlock(Block B) {
        this.colorId = B.colorId;
        this.tetroId = B.tetroId;
        this.coords = B.coords;
    }

    // Defines an empty block, part of a board grid
    public void setEmptyBlock(Coordinates coords) {
        this.colorId = -1;
        this.tetroId = -1;
        this.coords = coords;
    }

}
