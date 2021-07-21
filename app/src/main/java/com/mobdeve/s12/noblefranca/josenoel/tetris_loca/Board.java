package com.mobdeve.s12.noblefranca.josenoel.tetris_loca;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.core.content.ContextCompat;

public class Board extends View {

    protected int block_size;

    protected int board_width = 10;
    protected int board_height = 24;

    protected Block blocks[][];

    protected Bitmap b;


    public Board(Context context) {
        super(context);
        b = getBitmap(getContext(), R.drawable.ic_sprite_modified_3);
        blocks = new Block[board_width][board_height];
        block_size = b.getHeight();

        for (int i = 0; i < board_width; i++) {
            for (int j = 0; j < board_height; j++) {
                blocks[i][j] = new Block(i, j);
            }
        }
    }

    /* Code for getting the vector */
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

    protected void onDraw(Canvas canvas){
        blocks[0][0].drawBlock(canvas, block_size, b);
        blocks[0][1].drawBlock(canvas, block_size, b);
        blocks[1][1].drawBlock(canvas, block_size, b);
        blocks[1][0].drawBlock(canvas, block_size, b);
        //canvas.drawBitmap(b, new Rect(0,0, block_size, block_size), boundbox, null);
        //canvas.drawBitmap(b, new Rect(block_size + 2,0, (block_size * 2) + 2, block_size), boundbox, null);
        //canvas.drawBitmap(b, new Rect(block_size * 2,0, (block_size * 3), block_size), boundbox, null);
        super.onDraw(canvas);
    }

}
