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
                    for (int i = 0; i < 4; i++){
                        if (dataY[pos][i] > 19 || currData[dataY[pos][i]][dataX[pos][i]] != 0){
                            addYOffset(-1);
                            isMoving = false;
                        }
                    }
                    break;

                case RIGHT:
                    addXOffset(1);
                    for (int i = 0; i < 4; i++){
                        if (dataX[pos][i] > 9 || currData[dataY[pos][i]][dataX[pos][i]] != 0){
                            addXOffset(-1);
                            isMoving = false;
                        }
                    }
                    break;

                case LEFT:
                    addXOffset(-1);
                    for (int i = 0; i < 4; i++){
                        if (dataX[pos][i] < 0 || currData[dataY[pos][i]][dataX[pos][i]] != 0){
                            addXOffset(1);
                            isMoving = false;
                        }
                    }
                    break;
            }

            for (int i = 0; i < 4; i++){
                currData[dataY[pos][i]][dataX[pos][i]] = shape.ordinal();
            }
        }

        return isMoving;
    }





    //left is 90* acw, right is 90 cw
    public void Rotate(Rotation r){
        boolean success_rotation = false;

        int curr_pos = pos;
        int target_pos = pos;

        int derived_offset_data_x[] = new int[4];
        int derived_offset_data_y[] = new int[4];

        int curr_data[][] = GameActivity.getGameData();
        //erase the data in curr_data..

        for (int i = 0; i < 4; i++){
            curr_data[dataY[pos][i]][dataX[pos][i]] = 0; //empty shape
        }

        if (r == Rotation.CLOCKWISE){
            if (curr_pos == 3) { target_pos = 0; } else { target_pos++;}
        }
        else if (r == Rotation.ANTICLOCKWISE){
            if (curr_pos == 0) { target_pos = 3; } else { target_pos--; };
        }

        switch (shape){
            case J_SHAPE:
            case L_SHAPE:
            case S_SHAPE:
            case Z_SHAPE:
            case T_SHAPE:
                for (int i = 0; i < 4; i++){
                    derived_offset_data_x[i] = OFFSET_DATA_JLSTZ_X[curr_pos][i] - OFFSET_DATA_JLSTZ_X[target_pos][i];
                    derived_offset_data_y[i] = OFFSET_DATA_JLSTZ_Y[curr_pos][i] - OFFSET_DATA_JLSTZ_Y[target_pos][i];
                }
                break;
            case I_SHAPE:
                for (int i = 0; i < 4; i++){
                    derived_offset_data_x[i] = OFFSET_DATA_I_X[curr_pos][i] - OFFSET_DATA_I_X[target_pos][i];
                    derived_offset_data_y[i] = OFFSET_DATA_I_Y[curr_pos][i] - OFFSET_DATA_I_Y[target_pos][i];
                }
                break;
            case O_SHAPE:
                for (int i = 0; i < 4; i++){
                    derived_offset_data_x[i] = OFFSET_DATA_O_X[curr_pos][i] - OFFSET_DATA_O_X[target_pos][i];
                    derived_offset_data_y[i] = OFFSET_DATA_O_Y[curr_pos][i] - OFFSET_DATA_O_Y[target_pos][i];
                }
                break;

        }

        for (int i = 0; i < 5; i++){
            //offset tetromino first.
            addYOffset(derived_offset_data_y[i]);
            addXOffset(derived_offset_data_x[i]);

            //check legality of rotation.
            if (
                (dataY[target_pos][i] > 19 ||
                dataX[target_pos][i] > 9 ||
                dataX[target_pos][i] < 0 ||
                curr_data[dataY[target_pos][i]][dataX[target_pos][i]] != 0)
            ){
                addXOffset(derived_offset_data_x[i] * -1);
                addYOffset(derived_offset_data_y[i] * -1);
            }
            else {
                success_rotation = true;
                break;
            }
        }

        if (success_rotation){
            pos = target_pos;
            GameActivity.resetCounter();
        }else{
            pos = curr_pos;
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
