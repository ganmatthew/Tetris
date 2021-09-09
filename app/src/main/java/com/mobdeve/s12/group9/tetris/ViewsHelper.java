package com.mobdeve.s12.group9.tetris;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;

import java.util.List;

// A return class for the different view types
class BoardViewGroup {
    GridView gv;
    HoldView hv;
    NextView nv;
    void newBoardViewGroup(GridView gv, HoldView hv, NextView nv) {
        this.gv = gv;
        this.hv = hv;
        this.nv = nv;
    }
}

public class ViewsHelper {
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

    private float next_frame_left_offset;
    private float next_frame_top_offset;
    private float next_frame_right_offset;
    private float next_frame_bottom_offset;

    private float board_left_offset;
    private float board_top_offset;
    private float board_right_offset;
    private float board_bottom_offset;

    private float hold_frame_left_offset;
    private float hold_frame_top_offset;
    private float hold_frame_right_offset;
    private float hold_frame_bottom_offset;

    private float hold_left_offset;
    private float hold_top_offset;
    private float hold_right_offset;
    private float hold_bottom_offset;

    private float next_left_offset;
    private float next_top_offset;
    private float next_right_offset;
    private float next_bottom_offset;

    private GridView grid;
    private HoldView hold;
    private NextView next;

    private BoardViewGroup group;

    public ViewsHelper(Context context) {
        board_left_offset =                     GameActivity.GAME_OFFSET;
        board_top_offset =                      GameActivity.GAME_OFFSET;
        board_right_offset =                    ( (GameActivity.NUM_BLOCKSIZE * GameActivity.NUM_WIDTH) + GameActivity.GAME_OFFSET );
        board_bottom_offset =                   ( (GameActivity.NUM_BLOCKSIZE * GameActivity.NUM_HEIGHT) + GameActivity.GAME_OFFSET );

        /*
        hold_left_offset =                      board_right_offset + 30;
        hold_top_offset =                       board_top_offset;
        hold_right_offset =                     hold_left_offset + (GameActivity.NUM_BLOCKSIZE * 4);
        hold_bottom_offset =                    (GameActivity.NUM_BLOCKSIZE * 4) + board_top_offset;
        */

        hold_left_offset =                      board_left_offset;
        hold_top_offset =                       board_top_offset;
        hold_right_offset =                     hold_left_offset + (GameActivity.NUM_BLOCKSIZE * 4) - 35;
        hold_bottom_offset =                    (GameActivity.NUM_BLOCKSIZE * 4) + board_left_offset - 35;

        /*
        next_left_offset =                      hold_left_offset;
        next_top_offset =                       hold_bottom_offset + 30;
        next_right_offset =                     next_left_offset + (GameActivity.NUM_BLOCKSIZE * 4);
        next_bottom_offset =                    ((GameActivity.NUM_BLOCKSIZE * GameActivity.NUM_HEIGHT) + GameActivity.GAME_OFFSET);
        */

        next_left_offset =                      hold_left_offset;
        next_top_offset =                       hold_top_offset;
        next_right_offset =                     next_left_offset + (GameActivity.NUM_BLOCKSIZE * 4) - 35;
        next_bottom_offset =                    (float) ((GameActivity.NUM_BLOCKSIZE * 16) * 0.7 + 10);

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

        // Initialize the different views
        grid = new GridView(context, ViewsHelper.this);
        hold = new HoldView(context, ViewsHelper.this);
        next = new NextView(context, ViewsHelper.this);

        // Store the views for the return class
        group = new BoardViewGroup();
        group.newBoardViewGroup(grid, hold, next);
    }

    /**
     * Drawing of borders
     */

    public void drawGridBorders(Canvas canvas, Paint frame_painter, View view) {
        drawBorders(canvas, frame_painter, view,
            board_frame_left_offset, board_frame_top_offset,
            board_frame_right_offset, board_frame_bottom_offset, "GRID"
        );
    }

    public void drawHoldBorders(Canvas canvas, Paint frame_painter,  View view) {
        drawBorders(canvas, frame_painter, view,
            hold_frame_left_offset, hold_frame_top_offset,
            hold_frame_right_offset, hold_frame_bottom_offset, "HOLD"
        );
    }

    public void drawNextBorders(Canvas canvas, Paint frame_painter,  View view) {
        drawBorders(canvas, frame_painter, view,
            next_frame_left_offset, next_frame_top_offset,
            next_frame_right_offset, next_frame_bottom_offset, "NEXT"
        );
    }

    private void drawBorders(Canvas canvas, Paint frame_painter, View view,
                            float left, float top, float right, float bottom, String name) {
        frame_painter.setStrokeWidth(10);
        frame_painter.setStyle(Paint.Style.STROKE);
        frame_painter.setColor(getColor(view, R.color.board_border_color));

        Rect rect = new Rect( (int) left, (int) top, (int) right, (int) bottom );
        canvas.drawRect(rect, frame_painter);
        Log.d("HOLDTAG", String.format("width: %d\theight: %d\t\tparent: %s", rect.width(), rect.height(), name));
    }

    /**
     * Drawing of pieces
     */

    public void drawGridPieces(Canvas canvas, Paint block_painter, View view) {
        drawPieces(canvas, block_painter, view,
            GameActivity.getGameData(), GameActivity.NUM_HEIGHT, GameActivity.NUM_WIDTH,
            board_left_offset, board_top_offset, 0, 0, GameActivity.NUM_BLOCKSIZE
        );
    }

    public void drawHoldPieces(Canvas canvas, Paint block_painter, View view) {
        block_painter.setColor(getColor(view, R.color.tetromino_black));
        canvas.drawRect(hold_left_offset, hold_top_offset, hold_right_offset, hold_bottom_offset, block_painter);

        Shape holdTetromino = GameActivity.getHoldTetromino();

        if (holdTetromino != Shape.EMPTY_SHAPE) {
            Tetromino t = new Tetromino(holdTetromino);

            int temp[][] = new int[4][4];

            temp[t.getDataY()[0][0]][t.getDataX()[0][0]] = t.getShape().ordinal();
            temp[t.getDataY()[0][1]][t.getDataX()[0][1]] = t.getShape().ordinal();
            temp[t.getDataY()[0][2]][t.getDataX()[0][2]] = t.getShape().ordinal();
            temp[t.getDataY()[0][3]][t.getDataX()[0][3]] = t.getShape().ordinal();

            drawPieces(canvas, block_painter, view,
                temp, 4, 4,
                hold_left_offset, hold_top_offset, 20, 40, 50
            );
        }
    }

    public void drawNextPieces(Canvas canvas, Paint block_painter, View view) {
        //paint next block area first.
        block_painter.setColor(getColor(view, R.color.tetromino_black));
        canvas.drawRect(next_left_offset, next_top_offset, next_right_offset, next_bottom_offset, block_painter);

        List<Integer> piecebag = GameActivity.getPieceBag();

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

        drawPieces(canvas, block_painter, view,
            temp, 16, 4,
            next_left_offset, next_top_offset, 40, 80, 50
        );
    }

    private void drawPieces(Canvas canvas, Paint block_painter, View view,
                           int[][] temp, int height, int width,
                           float leftOffset, float topOffset, int leftOffset2, int topOffset2, int blockSize) {

        for (int i = 0; i < height; i++){
            for (int j = 0; j < width; j++){
                // Gets the ordinal of the Shape enum from the integer of the grid cell
                Shape shape = Shape.values()[temp[i][j]];
                switch (shape){
                    case I_SHAPE:
                        block_painter.setColor(getColor(view, R.color.tetromino_cyan));
                        break;
                    case O_SHAPE:
                        block_painter.setColor(getColor(view, R.color.tetromino_yellow));
                        break;
                    case T_SHAPE:
                        block_painter.setColor(getColor(view, R.color.tetromino_magenta));
                        break;
                    case J_SHAPE:
                        block_painter.setColor(getColor(view, R.color.tetromino_blue));
                        break;
                    case L_SHAPE:
                        block_painter.setColor(getColor(view, R.color.tetromino_orange));
                        break;
                    case S_SHAPE:
                        block_painter.setColor(getColor(view, R.color.tetromino_green));
                        break;
                    case Z_SHAPE:
                        block_painter.setColor(getColor(view, R.color.tetromino_red));
                        break;
                    default:
                        block_painter.setColor(getColor(view, R.color.tetromino_black));
                }

                float left =      leftOffset + (blockSize * j) + leftOffset2;
                float top =       topOffset + (blockSize * i) + topOffset2;
                float right =     left + blockSize;
                float bottom =    top + blockSize;

                canvas.drawRect(left, top, right, bottom, block_painter);

            }
        }
    }

    public BoardViewGroup getViews() { return this.group; }

    // Returns the ColorInt for a given resource color ID
    private int getColor(View view, int colorId) { return view.getResources().getColor(colorId); }
}

