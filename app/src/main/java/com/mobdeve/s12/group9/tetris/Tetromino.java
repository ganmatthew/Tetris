package com.mobdeve.s12.group9.tetris;

import java.util.stream.IntStream;

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

enum Rotation{
    CLOCKWISE,
    ANTICLOCKWISE
}

public class Tetromino {
    private static final int OFFSET_DATA_JLSTZ_X[][] = {
            {0,0,0,0,0},
            {0,1,1,0,1},
            {0,0,0,0,0},
            {0,-1,-1,0,-1}
    };

    private static final int OFFSET_DATA_JLSTZ_Y[][] = {
            {0,0,0,0,0},
            {0,0,-1,2,2},
            {0,0,0,0,0},
            {0,0,-1,2,2}
    };



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

    public void addYOffset(int value) { addOffsetData(dataY, value); }

    private int addOffsetData(int[][] data, int value) {
        for (int i = 0; i < 4; i++){
            for (int j = 0; j < 4; j++){
                data[i][j] += value;
            }
        }
        return value;
    }

    public boolean MoveTetromino(Direction direction){
        boolean rt_value = true;

        int curr_data[][] = GameActivity.getGameData();

        //erase the data in curr_data..
        for (int i = 0; i < 4; i++){
            curr_data[dataY[pos][i]][dataX[pos][i]] = 0; //empty shape
        }

        switch(direction){
            case DOWN:
                addYOffset(1);
                for (int i = 0; i < 4; i++){
                    if (dataY[pos][i] > 19 || curr_data[dataY[pos][i]][dataX[pos][i]] != 0){
                        addYOffset(-1);
                        rt_value = false;
                    }
                }
                break;

            case RIGHT:
                addXOffset(1);
                for (int i = 0; i < 4; i++){
                    if (dataX[pos][i] > 9 || curr_data[dataY[pos][i]][dataX[pos][i]] != 0){
                        addXOffset(-1);
                        rt_value = false;
                    }
                }
                break;

            case LEFT:
                addXOffset(-1);
                for (int i = 0; i < 4; i++){
                    if (dataX[pos][i] < 0 || curr_data[dataY[pos][i]][dataX[pos][i]] != 0){
                        addXOffset(1);
                        rt_value = false;
                    }
                }
                break;
        }

        for (int i = 0; i < 4; i++){
            curr_data[dataY[pos][i]][dataX[pos][i]] = shape.ordinal(); //empty shape
        }

        return rt_value;
    }

    //left is 90* acw, right is 90 cw
    public void Rotate(Rotation r){


        boolean success_rotation = false;

        int curr_pos = pos;
        int target_pos = pos;

        int offset_data_curr_pos_x;
        int offset_data_target_pos_x;

        int offset_data_curr_pos_y;
        int offset_data_target_pos_y;

        int curr_data[][] = GameActivity.getGameData();
        //erase the data in curr_data..
        for (int i = 0; i < 4; i++){
            curr_data[dataY[pos][i]][dataX[pos][i]] = 0; //empty shape
        }

        switch(r){
            case CLOCKWISE:
                if (curr_pos == 3) {target_pos = 0;} else {target_pos++;};

                switch(shape){
                    case J_SHAPE:
                    case L_SHAPE:
                    case S_SHAPE:
                    case Z_SHAPE:
                    case T_SHAPE:




                        break;
                }

                break;
            case ANTICLOCKWISE:
                if (curr_pos == 0) {target_pos = 3;} else {target_pos--;};
                break;
        }




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
