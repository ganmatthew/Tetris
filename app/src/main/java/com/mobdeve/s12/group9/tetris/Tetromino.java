package com.mobdeve.s12.group9.tetris;

enum Shape {
    EMPTY_SHAPE,
    I_SHAPE,
    O_SHAPE,
    T_SHAPE,
    S_SHAPE,
    Z_SHAPE,
    J_SHAPE,
    L_SHAPE
}

enum Direction {
    LEFT,
    DOWN,
    RIGHT
}

enum Orientation {
    NORTH,
    EAST,
    SOUTH,
    WEST
}

public class Tetromino {
    private Shape shape;
    private int dataX[][];
    private int dataY[][];
    private int pos;

    public Shape getShape() {
        return shape;
    }

    public int[][] getDataX() {
        return dataX;
    }

    public int[][] getDataY() {
        return dataY;
    }

    public int getPos() {
        return pos;
    }

    public void setDataX(int[][] dataX) {
        this.dataX = dataX;
    }

    public void setDataY(int[][] dataY) {
        this.dataY = dataY;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public void setOrientation(Orientation orientation) {
        switch (orientation) {
            case NORTH:
                break;
            case EAST:
                break;
            case SOUTH:
                break;
            case WEST:
                break;
        }
    }

    public void addXOffset(int value){
        addOffsetData(dataX, value);
    }

    public void addYOffset(int value){
        addOffsetData(dataY, value);
    }

    private int addOffsetData(int[][] data, int value) {
        for (int i = 0; i < 4; i++){
            for (int j = 0; j < 4; j++){
                data[i][j] += value;
            }
        }
        return value;
    }

    public Tetromino(Shape shape) {
        this.pos = 0;
        this.shape = shape;
        switch (shape){
            case I_SHAPE:
                dataX = new int[][] { {0,1,2,3},{2,2,2,2},{3,2,1,0},{1,1,1,1} };
                dataY = new int[][] { {1,1,1,1},{0,1,2,3},{2,2,2,2},{3,2,1,0} };
                break;
            case J_SHAPE:
                dataX = new int[][] { {0,0,1,2},{2,1,1,1},{2,2,1,0},{0,1,1,1} };
                dataY = new int[][] { {0,1,1,1},{0,0,1,2},{2,1,1,1},{2,2,1,0} };
                break;
            case L_SHAPE:
                dataX = new int[][] { {2,2,1,0},{2,1,1,1},{0,0,1,2},{0,1,1,1} };
                dataY = new int[][] { {0,1,1,1},{2,2,1,0},{2,1,1,1},{0,0,1,2} };
                break;
            case T_SHAPE:
                dataX = new int[][] { {1,0,1,2},{2,1,1,1},{1,2,1,0},{0,1,1,1} };
                dataY = new int[][] { {0,1,1,1},{1,0,1,2},{2,1,1,1},{1,2,1,0} };
                break;
            case S_SHAPE:
                dataX = new int[][] { {2,1,1,0},{2,2,1,1},{0,1,1,2},{0,0,1,1} };
                dataY = new int[][] { {0,0,1,1},{2,1,1,0},{2,2,1,1},{0,1,1,2} };
                break;
            case Z_SHAPE:
                dataX = new int[][] { {0,1,1,2},{2,2,1,1},{2,1,1,0},{0,0,1,1} };
                dataY = new int[][] { {0,0,1,1},{0,1,1,2},{2,2,1,1},{2,1,1,0} };
                break;
            case O_SHAPE:
                dataX = new int[][] { {0,1,1,0},{1,1,0,0},{1,0,0,1},{0,0,1,1} };
                dataY = new int[][] { {0,0,1,1},{0,1,1,0},{1,1,0,0},{1,0,0,1} };
                break;
        }
    }

}
