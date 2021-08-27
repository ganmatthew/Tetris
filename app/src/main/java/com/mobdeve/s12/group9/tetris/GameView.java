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

import androidx.core.content.ContextCompat;

public class GameView extends View {

    //private static final int BLOCK_SIZE = 10;

    //rectangle colors...
    private Rect BLOCK_RED, BLOCK_ORANGE, BLOCK_YELLOW, BLOCK_GREEN, BLOCK_CYAN, BLOCK_BLUE, BLOCK_PINK, BLOCK_EMPTY;
    private Rect[] blockColor = {
        BLOCK_RED, BLOCK_ORANGE, BLOCK_YELLOW, BLOCK_GREEN, BLOCK_CYAN, BLOCK_BLUE, BLOCK_PINK, null, null, BLOCK_EMPTY
    };

    private static int BOARD_SCALE = 80;
    private static int BOARD_OFFSET_VERTICAL = 250;
    private static final int BOARD_HEIGHT = 20;
    private static final int BOARD_WIDTH = 10;

    private Bitmap block_skin;
    private int[][] data;

    public GameView(Context context, DisplayMetrics displayMetrics) {
        super(context);

        // Get display metrics from MainActivity
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int density = 440;

        if (displayMetrics != null) {
            density = displayMetrics.densityDpi;
            BOARD_SCALE = (int) (displayMetrics.densityDpi / 5.5);
            BOARD_OFFSET_VERTICAL = (int) (displayMetrics.densityDpi / 1.76);
        }

        Log.d("SCALING TAG", String.format("DPI: %d ppi\tOffset: %d", density, BOARD_OFFSET_VERTICAL));

        // Calculates the scale based on the device's approximate ppi density
        int base = (density * 21) / 110;

        for (int i = 0; i < 10; i++) {
            int j = i + 1;
            blockColor[i] = new Rect(base * i, 0, (base * j) + (i < 3 ? 0 : j), base);
        }

        /*
        BLOCK_RED = new Rect(0,0, base, base);  // x0+0, x0, x1+0, x1
        BLOCK_ORANGE = new Rect(84,0,168,84);   // x1+0, x0, x2+0, x1
        BLOCK_YELLOW = new Rect(168,0,252,84);  // x2+0, x0, x3+0, x1
        BLOCK_GREEN = new Rect(255,0,340,84);   // x3+3, x0, x4+4, x1
        BLOCK_CYAN = new Rect(340,0,425,84);    // x4+4, x0, x5+5, x1
        BLOCK_BLUE = new Rect(425,0,510,84);    // x5+5, x0, x6+6, x1
        BLOCK_PINK = new Rect(510,0,595,84);    // x6+6, x0, x7+7, x1
        BLOCK_EMPTY = new Rect(765,0,850,84);   // x9+9, x0, x10+10, x1
        */

        block_skin = getBitmap(context, R.drawable.ic_sprite_modified_5);
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


        for (int i = 0; i < BOARD_WIDTH; i++) {
            for (int j = 0; j < BOARD_HEIGHT; j++) {
                Rect boundbox = new Rect();
                boundbox.left = i * BOARD_SCALE;
                boundbox.top = BOARD_OFFSET_VERTICAL + (j * BOARD_SCALE);
                boundbox.right = BOARD_SCALE + (i * BOARD_SCALE);
                boundbox.bottom = BOARD_OFFSET_VERTICAL + BOARD_SCALE + (j * BOARD_SCALE);

                //boundbox.right = 82 + (i * 80);
                //boundbox.bottom = BOARD_OFFSET_VERTICAL + (81 + (j * 80));

                Rect colorData = (data[i][j] > 0 && data[i][j] < 7) ? blockColor[data[i][j]] : blockColor[9];
                canvas.drawBitmap(block_skin, colorData, boundbox, null);

                /*
                switch (data[i][j]) {
                    case 0:
                        canvas.drawBitmap(block_skin, BLOCK_EMPTY, boundbox, null);
                        break;
                    case 1:
                        canvas.drawBitmap(block_skin, BLOCK_RED, boundbox, null);
                        break;
                    case 2:
                        canvas.drawBitmap(block_skin, BLOCK_ORANGE, boundbox, null);
                        break;
                    case 3:
                        canvas.drawBitmap(block_skin, BLOCK_YELLOW, boundbox, null);
                        break;
                    case 4:
                        canvas.drawBitmap(block_skin, BLOCK_GREEN, boundbox, null);
                        break;
                    case 5:
                        canvas.drawBitmap(block_skin, BLOCK_CYAN, boundbox, null);
                        break;
                    case 6:
                        canvas.drawBitmap(block_skin, BLOCK_BLUE, boundbox, null);
                        break;
                    case 7:
                        canvas.drawBitmap(block_skin, BLOCK_PINK, boundbox, null);
                        break;
                }
                 */
            }
        }
    }
}
