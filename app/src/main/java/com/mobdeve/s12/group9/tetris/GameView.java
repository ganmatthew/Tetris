package com.mobdeve.s12.group9.tetris;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

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

    public static final int nxpc_frame_left_offset = (20 * 50 + 100) + 50; //
    public static final int nxpc_frame_top_offset = 100;
    public static final int nxpc_frame_right_offset = 1950 + (4 * 50); //
    public static final int nxpc_frame_bottom_offset = 100 + (4 * 50); //


     */
    public static final int EMPTY_PIECE = 0;
    public static final int I_PIECE = 1;
    public static final int O_PIECE = 2;
    public static final int T_PIECE = 3;
    public static final int S_PIECE = 4;
    public static final int Z_PIECE = 5;
    public static final int J_PIECE = 6;
    public static final int L_PIECE = 7;


    private float board_frame_left_offset;
    private float board_frame_top_offset;
    private float board_frame_right_offset;
    private float board_frame_bottom_offset;

    private float nxpc_frame_left_offset;
    private float nxpc_frame_top_offset;
    private float nxpc_frame_right_offset;
    private float nxpc_frame_bottom_offset;

    private float board_left_offset;
    private float board_top_offset;
    private float board_right_offset;
    private float board_bottom_offset;

    private float nxpc_left_offset;
    private float nxpc_top_offset;
    private float nxpc_right_offset;
    private float nxpc_bottom_offset;

    private Bitmap bitmap;
    private Canvas canvas;
    private Paint frame_painter;
    private Paint block_painter;

    public GameView(Context context) {
        super(context);

        board_left_offset =                     GameActivity.GAME_OFFSET;
        board_top_offset =                      GameActivity.GAME_OFFSET;
        board_right_offset =                    ((GameActivity.NUM_BLOCKSIZE * GameActivity.NUM_WIDTH) + GameActivity.GAME_OFFSET);
        board_bottom_offset =                   ((GameActivity.NUM_BLOCKSIZE * GameActivity.NUM_HEIGHT) + GameActivity.GAME_OFFSET);

        nxpc_left_offset =                      board_right_offset + 30;
        nxpc_top_offset =                       board_top_offset;
        nxpc_right_offset =                     nxpc_left_offset + (GameActivity.NUM_BLOCKSIZE * 4);
        nxpc_bottom_offset =                    (GameActivity.NUM_BLOCKSIZE * 4) + board_top_offset;

        board_frame_left_offset =               (float) (board_left_offset - 5.5);
        board_frame_top_offset =                (float) (board_top_offset - 5.6);
        board_frame_right_offset =              (float) (board_right_offset + 5.6);
        board_frame_bottom_offset =             (float) (board_bottom_offset + 5.5);

        nxpc_frame_left_offset =                nxpc_left_offset - 5;
        nxpc_frame_top_offset =                 nxpc_top_offset - 5;
        nxpc_frame_right_offset =               nxpc_right_offset + 5;
        nxpc_frame_bottom_offset =              nxpc_bottom_offset + 5;

        frame_painter = new Paint();
        block_painter = new Paint();
    }

    public void DrawBorders(Canvas canvas){
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
                nxpc_frame_left_offset,
                nxpc_frame_top_offset,
                nxpc_frame_right_offset,
                nxpc_frame_bottom_offset,
                frame_painter);
    }

    public void DrawPieces(Canvas canvas){
        int[][] temp = GameActivity.get_gamedata();
        for (int i = 0; i < GameActivity.NUM_HEIGHT; i++){
            for (int j = 0; j < GameActivity.NUM_WIDTH; j++){
                switch (temp[i][j]){
                    case I_PIECE:
                        block_painter.setColor(Color.CYAN);
                        break;
                    case O_PIECE:
                        block_painter.setColor(Color.YELLOW);
                        break;
                    case T_PIECE:
                        block_painter.setColor(Color.MAGENTA);
                        break;
                    case J_PIECE:
                        block_painter.setColor(Color.BLUE);
                        break;
                    case L_PIECE:
                        block_painter.setColor(Color.rgb(255, 165, 0)); //java is racist to orange
                        break;
                    case S_PIECE:
                        block_painter.setColor(Color.GREEN);
                        break;
                    case Z_PIECE:
                        block_painter.setColor(Color.RED);
                        break;
                    default:
                        block_painter.setColor(Color.BLACK);
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
        //int gamestate = GameActivity.getGame_state();
        DrawBorders(canvas);
        DrawPieces(canvas);
    }
}
