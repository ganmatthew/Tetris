package com.mobdeve.s12.group9.tetris;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

public class _GameView extends View {

    //private static final int BLOCK_SIZE = 10;

    //rectangle colors...
    private static final Rect BLOCK_RED =           new Rect(0,0,84,84);
    private static final Rect BLOCK_ORANGE =        new Rect(85,0,169,84);
    private static final Rect BLOCK_YELLOW =        new Rect(170,0,255,84);
    private static final Rect BLOCK_GREEN =         new Rect(255,0,339,84);
    private static final Rect BLOCK_CYAN =          new Rect(338,0,424,84);
    private static final Rect BLOCK_BLUE =          new Rect(425,0,509,84);
    private static final Rect BLOCK_PINK =          new Rect(510,0,594,84);
    //private static final Rect BLOCK_EMPTY =         new Rect(766,0,850,84);

    private static int BOARD_SCALE = 80;
    private static int BOARD_OFFSET_VERTICAL = 250;
    private static int BOARD_OFFSET_HORIZONTAL = 50;

    private static final int BOARD_HEIGHT = 20;
    private static final int BOARD_WIDTH = 10;

    private ConstraintLayout clBoardLayout;
    private Bitmap block_skin;
    private int[][] data;

    public _GameView(Context context, DisplayMetrics displayMetrics) {
        super(context);

        // Get the ConstraintLayout to draw on later
        this.clBoardLayout = findViewById(R.id.cl_board_layout);

        // Get display metrics from MainActivity
        /*
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int density = 440;

        if (displayMetrics != null) {
            density = displayMetrics.densityDpi;
            BOARD_SCALE = (int) (displayMetrics.densityDpi / 5.5);
            BOARD_OFFSET_VERTICAL = (int) (displayMetrics.densityDpi / 1.76);
        }

        Log.d("SCALING TAG", String.format("DPI: %d ppi\tOffset: %d", density, BOARD_OFFSET_VERTICAL));


        */
        // Calculates the scale based on the device's approximate ppi density
        //int base = (density * 21) / 110;

        block_skin = getBitmap(context, R.drawable.ic_sprite_modified_7);
        data = new int[BOARD_WIDTH][BOARD_HEIGHT];
    }

    /*Required code to get Bitmap from Vector, pls don't move :D*/
    private static Bitmap getBitmap(VectorDrawable vectorDrawable) {
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return bitmap;
    }

    private static Bitmap getBitmap(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (drawable instanceof BitmapDrawable) {
            return BitmapFactory.decodeResource(context.getResources(), drawableId);
        } else if (drawable instanceof VectorDrawable) {
            return getBitmap((VectorDrawable) drawable);
        } else {
            throw new IllegalArgumentException("unsupported drawable type");
        }
    }

    public ConstraintLayout getClBoardLayout() {
        return this.clBoardLayout;
    }

    protected void onDraw(Canvas canvas) {

        //render I piece:
        data[1][1] = 5;
        data[2][1] = 5;
        data[3][1] = 5;
        data[4][1] = 5;

        //render L piece:

        data[1][3] = 6;
        data[1][4] = 6;
        data[2][4] = 6;
        data[3][4] = 6;

        //render J piece:
        data[1][7] = 2;
        data[2][7] = 2;
        data[3][7] = 2;
        data[3][6] = 2;

        //render Z piece:
        data[1][9] = 1;
        data[2][9] = 1;
        data[2][10] = 1;
        data[3][10] = 1;

        //render S piece:
        data[3][12] = 4;
        data[2][12] = 4;
        data[2][13] = 4;
        data[1][13] = 4;

        //render o piece
        data[1][15] = 3;
        data[1][16] = 3;
        data[2][15] = 3;
        data[2][16] = 3;

        //render t piece
        data[5][9] = 7;
        data[6][9] = 7;
        data[7][9] = 7;
        data[6][8] = 7;

        /*
        data[1][1] = 1;
        data[1][2] = 2;
        data[1][3] = 3;
         */

        for (int i = 0; i < BOARD_WIDTH; i++) {
            for (int j = 0; j < BOARD_HEIGHT; j++) {
                Rect boundBox = new Rect();
                boundBox.left =     (BOARD_OFFSET_HORIZONTAL + (i * BOARD_SCALE));
                boundBox.top =      BOARD_OFFSET_VERTICAL + (j * BOARD_SCALE);
                boundBox.right =    BOARD_OFFSET_HORIZONTAL + BOARD_SCALE + (i * BOARD_SCALE);
                boundBox.bottom =   BOARD_OFFSET_VERTICAL + BOARD_SCALE + (j * BOARD_SCALE);

                //post processing

                //boundbox.right = 82 + (i * 80);
                //boundbox.bottom = BOARD_OFFSET_VERTICAL + (81 + (j * 80));

                //Rect colorData = (data[i][j] > 0 && data[i][j] < 7) ? blockColor[data[i][j]] : blockColor[9];
                //canvas.drawBitmap(block_skin, colorData, boundbox, null);

                switch (data[i][j]) {
                    case 0:
                        //canvas.drawBitmap(block_skin, BLOCK_EMPTY, boundBox, null);
                        break;
                    case 1:
                        canvas.drawBitmap(block_skin, BLOCK_RED, boundBox, null);
                        break;
                    case 2:
                        canvas.drawBitmap(block_skin, BLOCK_ORANGE, boundBox, null);
                        break;
                    case 3:
                        canvas.drawBitmap(block_skin, BLOCK_YELLOW, boundBox, null);
                        break;
                    case 4:
                        canvas.drawBitmap(block_skin, BLOCK_GREEN, boundBox, null);
                        break;
                    case 5:
                        canvas.drawBitmap(block_skin, BLOCK_CYAN, boundBox, null);
                        break;
                    case 6:
                        canvas.drawBitmap(block_skin, BLOCK_BLUE, boundBox, null);
                        break;
                    case 7:
                        canvas.drawBitmap(block_skin, BLOCK_PINK, boundBox, null);
                        break;
                }

            }
        }
    }
}
