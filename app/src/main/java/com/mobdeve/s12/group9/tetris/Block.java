package com.mobdeve.s12.group9.tetris;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class Block {

    //block color
    private int block_color;

    //if block is placed, floating, etc. or is empty or not.
    private int block_attribute;

    public Block() {
        block_color = -1;
        block_attribute = -1;
    }

    public void drawBlock(Canvas canvas, Bitmap b, Rect color){

    }
}
