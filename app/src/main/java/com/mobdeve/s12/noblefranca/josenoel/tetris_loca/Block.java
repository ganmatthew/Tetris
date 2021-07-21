package com.mobdeve.s12.noblefranca.josenoel.tetris_loca;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class Block {

    protected int x_value, y_value;
    protected int blockID;

    public Block(int x_value, int y_value) {
        this.x_value = x_value;
        this.y_value = y_value;
        this.blockID = -1;
    }

    public void drawBlock(Canvas canvas, int blockSize, Bitmap b){
        Rect boundBox = new Rect(blockSize * x_value, blockSize * y_value, blockSize * (x_value + 1), blockSize * (y_value + 1));
        canvas.drawBitmap(b, new Rect(0,0, blockSize, blockSize), boundBox, null);
    }
}
