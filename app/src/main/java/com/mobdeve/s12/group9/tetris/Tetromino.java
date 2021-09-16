package com.mobdeve.s12.group9.tetris;

import android.icu.text.Transliterator;
import android.util.Log;

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

    private static final int OFFSET_DATA_I_X[][] = {
            {0,-1,2,-1,2},
            {-1,0,0,0,0},
            {-1,1,-2,1,-2},
            {0,0,0,0,0}
    };

    private static final int OFFSET_DATA_I_Y[][] = {
            {0,0,0,0,0},
            {0,0,0,1,-2},
            {1,1,1,0,0},
            {0,1,1,-1,2}
    };

    private static final int OFFSET_DATA_O_X[][] = {
            {0,0,0,0,0},
            {0,0,0,0,0},
            {-1,0,0,0,0},
            {-1,0,0,0,0}
    };

    private static final int OFFSET_DATA_O_Y[][] = {
            {0,0,0,0,0},
            {-1,0,0,0,0},
            {-1,0,0,0,0},
            {0,0,0,0,0}
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


    //function to determine that if given a position and the coordinates of the block,
    //if that said position is valid
    public boolean LegitPosition(int position){
        int curr_data[][] = GameActivity.getGameData();

        //check all x data.

        for (int i = 0; i < 4; i++){
            if (dataX[position][i] > 9 || dataX[position][i] < 0 ){
                return false;
            }
        }

        //check all y data.

        for (int i = 0; i < 4; i++){
            if (dataY[position][i] > 19 || dataY[position][i] < 0){
                return false;
            }
        }

        //check if there is existing block.

        for (int i = 0; i < 4; i++){
            if(curr_data[dataY[position][i]][dataX[position][i]] != 0){
                return false;
            }
        }

        return true;
    }




    public boolean moveTetromino(Direction direction, boolean isMoving){
        if (!isMoving) {
            isMoving = true;
            int currData[][] = GameActivity.getGameData();

            //erase the data in curr_data..
            for (int i = 0; i < 4; i++){
                currData[dataY[pos][i]][dataX[pos][i]] = 0; //empty shape
            }

            switch(direction){
                case DOWN:
                    addYOffset(1);
                    if (!LegitPosition(pos)){
                        addYOffset(-1);
                        isMoving = false;
                    }
                    break;

                case RIGHT:
                    addXOffset(1);
                    if (!LegitPosition(pos)){
                        addXOffset(-1);
                        isMoving = false;
                    }
                    break;

                case LEFT:
                    addXOffset(-1);
                    if (!LegitPosition(pos)){
                        addXOffset(1);
                        isMoving = false;
                    }
                    break;
            }

            for (int i = 0; i < 4; i++){
                currData[dataY[pos][i]][dataX[pos][i]] = shape.ordinal();
            }
        }

        return isMoving;
    }



    public void Rotate(Rotation r){
        //test value to check if rotation is successful.
        boolean success_rotation = false;

        //get target position
        int target_pos = (r == Rotation.CLOCKWISE) ? (((pos + 1) % 4) + 4) % 4 : (((pos - 1) % 4) + 4) % 4;



        //get derived offsets
        int derived_offset_data_x[] = new int[4];
        int derived_offset_data_y[] = new int[4];

        //save current data before rotating.
        int curr_dataX[][] = getDataX();
        int curr_dataY[][] = getDataY();

        //erase the data in the current_data.
        int curr_data[][] = GameActivity.getGameData();
        for (int i = 0; i < 4; i++){
            curr_data[dataY[pos][i]][dataX[pos][i]] = 0; //empty shape
        }

        switch (shape){
            case J_SHAPE:
            case L_SHAPE:
            case S_SHAPE:
            case Z_SHAPE:
            case T_SHAPE:
                for (int i = 0; i < 4; i++){
                    Log.d("Rotate", pos + " -> " + target_pos);
                    derived_offset_data_x[i] = OFFSET_DATA_JLSTZ_X[pos][i] - OFFSET_DATA_JLSTZ_X[target_pos][i];
                    derived_offset_data_y[i] = OFFSET_DATA_JLSTZ_Y[pos][i] - OFFSET_DATA_JLSTZ_Y[target_pos][i];
                }
                break;
            case I_SHAPE:
                for (int i = 0; i < 4; i++){
                    derived_offset_data_x[i] = OFFSET_DATA_I_X[pos][i] - OFFSET_DATA_I_X[target_pos][i];
                    derived_offset_data_y[i] = OFFSET_DATA_I_Y[pos][i] - OFFSET_DATA_I_Y[target_pos][i];
                }
                break;
        }

        //for each possible offset rotation...

        for (int i = 0; i < 5; i++){
            //offset its data first,
            addYOffset(derived_offset_data_y[i]);
            addXOffset(derived_offset_data_x[i]);

            //then check if the offset, with the new rotation value, is equal.
            if(LegitPosition(target_pos)){
                //exit the loop, and set the value of the success_rotation to true.
                success_rotation = true;
                break;
            }
            else{
                //change the x and y data to the one we started, then run the loop again.
                setDataX(curr_dataX);
                setDataY(curr_dataY);
            }
        }


        if (success_rotation){
            pos = target_pos;
        }

        for (int i = 0; i < 4; i++){
            curr_data[dataY[pos][i]][dataX[pos][i]] = shape.ordinal();
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
