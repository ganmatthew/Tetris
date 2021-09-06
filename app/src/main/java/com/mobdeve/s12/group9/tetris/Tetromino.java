package com.mobdeve.s12.group9.tetris;

enum Shape {
    I_SHAPE,
    J_SHAPE,
    L_SHAPE,
    O_SHAPE,
    S_SHAPE,
    T_SHAPE,
    Z_SHAPE
}

public class Tetromino {
    private int shape;
    private int dataX[][];
    private int dataY[][];
    private int pos;

    public int getShape() {
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

    public void addX_Offset(int value){
        for (int i = 0; i < 4; i++){
            for (int j = 0; j < 4; j++){
                dataX[i][j] += value;
            }
        }
    }

    public void addY_Offset(int value){
        for (int i = 0; i < 4; i++){
            for (int j = 0; j < 4; j++){
                dataY[i][j] += value;
            }
        }
    }

    public Tetromino(int Shape) {
        pos = 0;
        shape = Shape;
        switch (Shape){
            case GameView.I_PIECE:
                dataX = new int[][] {{0,1,2,3},{2,2,2,2},{3,2,1,0},{1,1,1,1}};
                dataY = new int[][] {{1,1,1,1},{0,1,2,3},{2,2,2,2},{3,2,1,0}};
                break;
            case GameView.J_PIECE:
                dataX = new int[][] {{0,0,1,2},{2,1,1,1},{2,2,1,0},{0,1,1,1}};
                dataY = new int[][] {{0,1,1,1},{0,0,1,2},{2,1,1,1},{2,2,1,0}};
                break;
            case GameView.L_PIECE:
                dataX = new int[][] {{2,2,1,0},{2,1,1,1},{0,0,1,2},{0,1,1,1}};
                dataY = new int[][] {{0,1,1,1},{2,2,1,0},{2,1,1,1},{0,0,1,2}};
                break;
            case GameView.T_PIECE:
                dataX = new int[][] {{1,0,1,2},{2,1,1,1},{1,2,1,0},{0,1,1,1}};
                dataY = new int[][] {{0,1,1,1},{1,0,1,2},{2,1,1,1},{1,2,1,0}};
                break;
            case GameView.S_PIECE:
                dataX = new int[][] {{2,1,1,0},{2,2,1,1},{0,1,1,2},{0,0,1,1}};
                dataY = new int[][] {{0,0,1,1},{2,1,1,0},{2,2,1,1},{0,1,1,2}};
                break;
            case GameView.Z_PIECE:
                dataX = new int[][] {{0,1,1,2},{2,2,1,1},{2,1,1,0},{0,0,1,1}};
                dataY = new int[][] {{0,0,1,1},{0,1,1,2},{2,2,1,1},{2,1,1,0}};
                break;
            case GameView.O_PIECE:
                dataX = new int[][] {{0,1,1,0},{1,1,0,0},{1,0,0,1},{0,0,1,1}};
                dataY = new int[][] {{0,0,1,1},{0,1,1,0},{1,1,0,0},{1,0,0,1}};
                break;

        }
    }

}
