package com.mobdeve.s12.group9.tetris;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

public class Game extends View {

    //private static final int BLOCK_SIZE = 10;

    private static final int BOARD_HEIGHT = 20;
    private static final int BOARD_WIDTH = 10;

    private Bitmap block_skin;
    private int[][] data;

    public Game(Context context) {
        super(context);
        block_skin = getBitmap(getContext(), R.drawable.ic_sprite_modified_5);
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




        /*
        for (int i = 0; i < BOARD_WIDTH; i++){
            for (int j = 0; j < BOARD_HEIGHT; j++){
                canvas.drawBitmap(block_skin, new Rect(0,0,84,84),
                        new Rect((i * 80) - 6,(j * 80) - 5,80 + (i * 80), 80 + (j * 80)), null);
            }
        }
        */




        ConstraintLayout cl_board_layout = findViewById(R.id.cl_board_layout);
        int j = cl_board_layout.getHeight();

        canvas.drawBitmap(block_skin, new Rect(0,0,84,84), new Rect(0,j - 80,80, j), null);

        //super.onDraw(canvas);
    }

}
