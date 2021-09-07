package com.mobdeve.s12.group9.tetris;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import java.util.List;

public class GameView extends View {


    /*
    note:

    public static final int NUM_HEIGHT = 20;
    public static final int NUM_WIDTH = 10;
    public static final int NUM_BLOCKSIZE = 90;

    offset constant is 100.
    distance of next piece frame to the board is 50.

    public static final int NUM_HEIGHT = 20;
    public static final int NUM_WIDTH = 10;
    public static final int NUM_BLOCKSIZE = 50;

    public static final int board_frame_left_offset = 100;
    public static final int board_frame_top_offset = 100;
    public static final int board_frame_right_offset = 10 * 50 + 100; //
    public static final int board_frame_bottom_offset = 20 * 50 + 100; //

    public static final int hold_frame_left_offset = (20 * 50 + 100) + 50; //
    public static final int hold_frame_top_offset = 100;
    public static final int hold_frame_right_offset = 1950 + (4 * 50); //
    public static final int hold_frame_bottom_offset = 100 + (4 * 50); //


     */

    private float board_frame_left_offset;
    private float board_frame_top_offset;
    private float board_frame_right_offset;
    private float board_frame_bottom_offset;

    private float hold_frame_left_offset;
    private float hold_frame_top_offset;
    private float hold_frame_right_offset;
    private float hold_frame_bottom_offset;

    private float next_frame_left_offset;
    private float next_frame_top_offset;
    private float next_frame_right_offset;
    private float next_frame_bottom_offset;

    private float board_left_offset;
    private float board_top_offset;
    private float board_right_offset;
    private float board_bottom_offset;

    private float hold_left_offset;
    private float hold_top_offset;
    private float hold_right_offset;
    private float hold_bottom_offset;

    private float next_left_offset;
    private float next_top_offset;
    private float next_right_offset;
    private float next_bottom_offset;

    private Paint frame_painter;
    private Paint block_painter;

    public GameView(Context context) {
        super(context);

        board_left_offset =                     GameActivity.GAME_OFFSET;
        board_top_offset =                      GameActivity.GAME_OFFSET;
        board_right_offset =                    ((GameActivity.NUM_BLOCKSIZE * GameActivity.NUM_WIDTH) + GameActivity.GAME_OFFSET);
        board_bottom_offset =                   ((GameActivity.NUM_BLOCKSIZE * GameActivity.NUM_HEIGHT) + GameActivity.GAME_OFFSET);

        hold_left_offset =                      board_right_offset + 30;
        hold_top_offset =                       board_top_offset;
        hold_right_offset =                     hold_left_offset + (GameActivity.NUM_BLOCKSIZE * 4);
        hold_bottom_offset =                    (GameActivity.NUM_BLOCKSIZE * 4) + board_top_offset;

        next_left_offset =                      hold_left_offset;
        next_top_offset =                       hold_bottom_offset + 30;
        next_right_offset =                     next_left_offset + (GameActivity.NUM_BLOCKSIZE * 4);
        next_bottom_offset =                    ((GameActivity.NUM_BLOCKSIZE * GameActivity.NUM_HEIGHT) + GameActivity.GAME_OFFSET);

        board_frame_left_offset =               (float) (board_left_offset - 5.5);
        board_frame_top_offset =                (float) (board_top_offset - 5.6);
        board_frame_right_offset =              (float) (board_right_offset + 5.6);
        board_frame_bottom_offset =             (float) (board_bottom_offset + 5.5);

        hold_frame_left_offset =                hold_left_offset - 5;
        hold_frame_top_offset =                 hold_top_offset - 5;
        hold_frame_right_offset =               hold_right_offset + 5;
        hold_frame_bottom_offset =              hold_bottom_offset + 5;

        next_frame_left_offset =                next_left_offset - 5;
        next_frame_top_offset =                 next_top_offset - 5;
        next_frame_right_offset =               next_right_offset + 5;
        next_frame_bottom_offset =              next_bottom_offset + 5;

        frame_painter = new Paint();
        block_painter = new Paint();
    }

    public void drawBorders(Canvas canvas){
        frame_painter.setStrokeWidth(10);
        frame_painter.setStyle(Paint.Style.STROKE);
        frame_painter.setColor(Color.GRAY);

        canvas.drawRect(
            board_frame_left_offset,
            board_frame_top_offset,
            board_frame_right_offset,
            board_frame_bottom_offset,
            frame_painter);

        //next piece frame
        canvas.drawRect(
            hold_frame_left_offset,
            hold_frame_top_offset,
            hold_frame_right_offset,
            hold_frame_bottom_offset,
            frame_painter);

        canvas.drawRect(
                next_frame_left_offset,
                next_frame_top_offset,
                next_frame_right_offset,
                next_frame_bottom_offset,
                frame_painter);
    }

    public void drawPieces(Canvas canvas){
        int[][] temp = GameActivity.getGameData();
        for (int i = 0; i < GameActivity.NUM_HEIGHT; i++){
            for (int j = 0; j < GameActivity.NUM_WIDTH; j++){
                // Gets the ordinal of the Shape enum from the integer of the grid cell
                Shape shape = Shape.values()[temp[i][j]];
                switch (shape){
                    case I_SHAPE:
                        block_painter.setColor(getColor(R.color.tetromino_cyan));
                        break;
                    case O_SHAPE:
                        block_painter.setColor(getColor(R.color.tetromino_yellow));
                        break;
                    case T_SHAPE:
                        block_painter.setColor(getColor(R.color.tetromino_magenta));
                        break;
                    case J_SHAPE:
                        block_painter.setColor(getColor(R.color.tetromino_blue));
                        break;
                    case L_SHAPE:
                        block_painter.setColor(getColor(R.color.tetromino_orange));
                        break;
                    case S_SHAPE:
                        block_painter.setColor(getColor(R.color.tetromino_green));
                        break;
                    case Z_SHAPE:
                        block_painter.setColor(getColor(R.color.tetromino_red));
                        break;
                    default:
                        block_painter.setColor(getColor(R.color.tetromino_black));
                }

                float left =      board_left_offset + (GameActivity.NUM_BLOCKSIZE * j);
                float top =       board_top_offset + (GameActivity.NUM_BLOCKSIZE * i);
                float right =     left + GameActivity.NUM_BLOCKSIZE;
                float bottom =    top + GameActivity.NUM_BLOCKSIZE;

                canvas.drawRect(left, top, right, bottom, block_painter);

            }
        }
    }
    

    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBorders(canvas);
        if (GameActivity.getGameState() != 3){
            drawPieces(canvas);
            drawNextPieces(canvas);
            drawHoldPiece(canvas);
        }
    }

    private void drawHoldPiece(Canvas canvas) {
        block_painter.setColor(getColor(R.color.tetromino_black));
        canvas.drawRect(hold_left_offset, hold_top_offset, hold_right_offset, hold_bottom_offset, block_painter);

        int checkval = GameActivity.getHold_tetromino_value();

        if (checkval != 0){
            Tetromino t = new Tetromino(Shape.values()[checkval]);

            int temp[][] = new int[4][4];

            temp[t.getDataY()[0][0]][t.getDataX()[0][0]] = t.getShape().ordinal();
            temp[t.getDataY()[0][1]][t.getDataX()[0][1]] = t.getShape().ordinal();
            temp[t.getDataY()[0][2]][t.getDataX()[0][2]] = t.getShape().ordinal();
            temp[t.getDataY()[0][3]][t.getDataX()[0][3]] = t.getShape().ordinal();

            for (int i = 0; i < 4; i++){
                for (int j = 0; j < 4; j++){
                    Shape shape = Shape.values()[temp[i][j]];
                    switch (shape){
                        case I_SHAPE:
                            block_painter.setColor(getColor(R.color.tetromino_cyan));
                            break;
                        case O_SHAPE:
                            block_painter.setColor(getColor(R.color.tetromino_yellow));
                            break;
                        case T_SHAPE:
                            block_painter.setColor(getColor(R.color.tetromino_magenta));
                            break;
                        case J_SHAPE:
                            block_painter.setColor(getColor(R.color.tetromino_blue));
                            break;
                        case L_SHAPE:
                            block_painter.setColor(getColor(R.color.tetromino_orange));
                            break;
                        case S_SHAPE:
                            block_painter.setColor(getColor(R.color.tetromino_green));
                            break;
                        case Z_SHAPE:
                            block_painter.setColor(getColor(R.color.tetromino_red));
                            break;
                        default:
                            block_painter.setColor(getColor(R.color.tetromino_black));
                    }

                    float left =      hold_left_offset + (70 * j) + 20;
                    float top =       hold_top_offset + (70 * i) + 40;
                    float right =     left + 70;
                    float bottom =    top + 70;

                    canvas.drawRect(left, top, right, bottom, block_painter);
                }
            }
        }



    }

    private void drawNextPieces(Canvas canvas) {
        //paint next block area first.
        block_painter.setColor(getColor(R.color.tetromino_black));
        canvas.drawRect(next_left_offset, next_top_offset, next_right_offset, next_bottom_offset, block_painter);


        List <Integer> piecebag = GameActivity.getPieceBag();

        Tetromino t[] = new Tetromino[4];

        //Tetromino t = new Tetromino(Shape.values()[piecebag.get(0)]);

        int[][] temp = new int[16][4];


        for (int i = 0; i < 4; i++) {
            t[i] = new Tetromino(Shape.values()[piecebag.get(i)]);
            t[i].addYOffset(i * 4);

            temp[t[i].getDataY()[0][0]][t[i].getDataX()[0][0]] = t[i].getShape().ordinal();
            temp[t[i].getDataY()[0][1]][t[i].getDataX()[0][1]] = t[i].getShape().ordinal();
            temp[t[i].getDataY()[0][2]][t[i].getDataX()[0][2]] = t[i].getShape().ordinal();
            temp[t[i].getDataY()[0][3]][t[i].getDataX()[0][3]] = t[i].getShape().ordinal();
        }

        for (int i = 0; i < 16; i++){
            for (int j = 0; j < 4; j++){
                Shape shape = Shape.values()[temp[i][j]];
                switch (shape){
                    case I_SHAPE:
                        block_painter.setColor(getColor(R.color.tetromino_cyan));
                        break;
                    case O_SHAPE:
                        block_painter.setColor(getColor(R.color.tetromino_yellow));
                        break;
                    case T_SHAPE:
                        block_painter.setColor(getColor(R.color.tetromino_magenta));
                        break;
                    case J_SHAPE:
                        block_painter.setColor(getColor(R.color.tetromino_blue));
                        break;
                    case L_SHAPE:
                        block_painter.setColor(getColor(R.color.tetromino_orange));
                        break;
                    case S_SHAPE:
                        block_painter.setColor(getColor(R.color.tetromino_green));
                        break;
                    case Z_SHAPE:
                        block_painter.setColor(getColor(R.color.tetromino_red));
                        break;
                    default:
                        block_painter.setColor(getColor(R.color.tetromino_black));
                }

                float left =      next_left_offset + (70 * j) + 20;
                float top =       next_top_offset + (70 * i) + 80;
                float right =     left + 70;
                float bottom =    top + 70;

                canvas.drawRect(left, top, right, bottom, block_painter);

            }
        }
    }

    // Returns the ColorInt for a given resource color ID
    private int getColor(int colorId) { return getResources().getColor(colorId); }
}
