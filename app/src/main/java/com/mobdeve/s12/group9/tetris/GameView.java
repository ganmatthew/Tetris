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

    private static final int EMPTY_PIECE = 0;
    private static final int I_PIECE = 1;
    private static final int O_PIECE = 2;
    private static final int T_PIECE = 3;
    private static final int S_PIECE = 4;
    private static final int Z_PIECE = 5;
    private static final int J_PIECE = 6;
    private static final int L_PIECE = 7;


    private int board_frame_left_offset;
    private int board_frame_top_offset;
    private int board_frame_right_offset;
    private int board_frame_bottom_offset;

    private int nxpc_frame_left_offset;
    private int nxpc_frame_top_offset;
    private int nxpc_frame_right_offset;
    private int nxpc_frame_bottom_offset;

    private int board_left_offset;
    private int board_top_offset;
    private int board_right_offset;
    private int board_bottom_offset;

    private int nxpc_left_offset;
    private int nxpc_top_offset;
    private int nxpc_right_offset;
    private int nxpc_bottom_offset;

    private Bitmap bitmap;
    private Canvas canvas;
    private Paint frame_painter;
    private Paint block_painter;

    public GameView(Context context) {
        super(context);

        board_left_offset =       GameActivity.GAME_OFFSET;
        board_top_offset =        GameActivity.GAME_OFFSET;
        board_right_offset =      ((GameActivity.NUM_BLOCKSIZE * GameActivity.NUM_WIDTH) + GameActivity.GAME_OFFSET);
        board_bottom_offset =     ((GameActivity.NUM_BLOCKSIZE * GameActivity.NUM_HEIGHT) + GameActivity.GAME_OFFSET);

        nxpc_left_offset =    board_frame_right_offset + 20;
        nxpc_top_offset =     board_frame_top_offset;
        nxpc_right_offset =   nxpc_frame_left_offset + (GameActivity.NUM_BLOCKSIZE * 4);
        nxpc_bottom_offset =  (GameActivity.NUM_BLOCKSIZE * 4) + GameActivity.GAME_OFFSET;

        bitmap = Bitmap.createBitmap(
                (GameActivity.NUM_BLOCKSIZE * GameActivity.NUM_WIDTH),
                (GameActivity.NUM_BLOCKSIZE * GameActivity.NUM_HEIGHT),
                Bitmap.Config.ARGB_8888);;

        canvas = new Canvas(bitmap);
        frame_painter = new Paint();
        block_painter = new Paint();
    }

    public void onDraw(Canvas canvas) {

        frame_painter.setStrokeWidth(10);
        frame_painter.setStyle(Paint.Style.STROKE);
        frame_painter.setColor(Color.GRAY);

        int[][] temp = GameActivity.get_gamedata();
        /*
        game frame
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

         */

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
                        block_painter.setColor(Color.rgb(255, 165, 0));
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


                int left =      GameActivity.GAME_OFFSET + (GameActivity.NUM_BLOCKSIZE * j);
                int top =       GameActivity.GAME_OFFSET + (GameActivity.NUM_BLOCKSIZE * i);
                int right =     left + GameActivity.NUM_BLOCKSIZE;
                int bottom =    top + GameActivity.NUM_BLOCKSIZE;

                canvas.drawRect(left, top, right, bottom, block_painter);

            }
        }







    }
}
