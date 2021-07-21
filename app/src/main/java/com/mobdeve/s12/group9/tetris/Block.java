package com.mobdeve.s12.group9.tetris;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class Block {

    protected static int block_size;

    protected static Rect block_red;
    protected static Rect block_orange;
    protected static Rect block_yellow;
    protected static Rect block_green;
    protected static Rect block_cyan;
    protected static Rect block_blue;
    protected static Rect block_violet;
    protected static Rect block_empty;

    protected int x_value, y_value;
    protected int blockID;


    public Block(int x_value, int y_value) {
        this.x_value = x_value;
        this.y_value = y_value;
        this.blockID = -1;
    }

    public void drawBlock(Canvas canvas, Bitmap b, Rect color){
        Rect boundBox = new Rect(block_size * x_value, block_size * y_value, block_size * (x_value + 1), block_size * (y_value + 1));
        canvas.drawBitmap(b, color, boundBox, null);
    }
}
