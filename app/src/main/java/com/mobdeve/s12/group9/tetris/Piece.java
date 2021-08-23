package com.mobdeve.s12.group9.tetris;

public class Piece {
    public static final int PIECE_NONE = -1,
                            PIECE_I = 0,
                            PIECE_L = 1,
                            PIECE_O = 2,
                            PIECE_Z = 3,
                            PIECE_T = 4,
                            PIECE_J = 5,
                            PIECE_S = 6;

    public static final String[] PIECE_NAMES = {"I","L","O","Z","T","J","S"};

    public static final int[][][] DEFAULT_PIECE_DATA_X = {
            {{0,1,2,3},{2,2,2,2},{3,2,1,0},{1,1,1,1}},	// I
            {{2,2,1,0},{2,1,1,1},{0,0,1,2},{0,1,1,1}},	// L
            {{0,1,1,0},{1,1,0,0},{1,0,0,1},{0,0,1,1}},	// O
            {{0,1,1,2},{2,2,1,1},{2,1,1,0},{0,0,1,1}},	// Z
            {{1,0,1,2},{2,1,1,1},{1,2,1,0},{0,1,1,1}},	// T
            {{0,0,1,2},{2,1,1,1},{2,2,1,0},{0,1,1,1}},	// J
            {{2,1,1,0},{2,2,1,1},{0,1,1,2},{0,0,1,1}}	// S
    };

    public static final int[][][] DEFAULT_PIECE_DATA_Y = {
            {{1,1,1,1},{0,1,2,3},{2,2,2,2},{3,2,1,0}},	// I
            {{0,1,1,1},{2,2,1,0},{2,1,1,1},{0,0,1,2}},	// L
            {{0,0,1,1},{0,1,1,0},{1,1,0,0},{1,0,0,1}},	// O
            {{0,0,1,1},{0,1,1,2},{2,2,1,1},{2,1,1,0}},	// Z
            {{0,1,1,1},{1,0,1,2},{2,1,1,1},{1,2,1,0}},	// T
            {{0,1,1,1},{0,0,1,2},{2,1,1,1},{2,2,1,0}},	// J
            {{0,0,1,1},{2,1,1,0},{2,2,1,1},{0,1,1,2}}	// S
    };

    public static final int [][][] OFFSET_DATA_I_PIECE = {
            {{0,0}, {-1,0}, {2,0}, {-1,0}, {2,0}}, //POS 0
            {{-1,0}, {0,0}, {0,0}, {0,1}, {0,-2}}, //POS 1
            {{-1,1}, {1,1}, {-2,1}, {1,0}, {-2,0}}, //POS 2
            {{0,1}, {0,1}, {0,1}, {0,-1}, {0,2}} //POS 3
    };

    public static final int [][][] OFFSET_DATA_JLSTZ_PIECE = {
            {{0,0}, {0,0}, {0,0}, {0,0}, {0,0}}, //POS 0
            {{0,0}, {1,0}, {1,-1}, {0,2}, {1,2}}, //POS 1
            {{0,0}, {0,0}, {0,0}, {0,0}, {0,0}}, //POS 2
            {{0,0}, {-1,0}, {-1,-1}, {0,2}, {-1,2}} //POS 3
    };

    public static final int [][] OFFSET_DATA_O_PIECE = {
            {0,0}, //POS 0
            {0,-1},  //POS 1
            {-1,-1},  //POS 2
            {-1,0} //POS 3
    };



    public static final int DIRECTION_UP = 0, DIRECTION_RIGHT = 1, DIRECTION_DOWN = 2, DIRECTION_LEFT = 3, DIRECTION_RANDOM = 4;

    public static final int DIRECTION_COUNT = 4;



}
