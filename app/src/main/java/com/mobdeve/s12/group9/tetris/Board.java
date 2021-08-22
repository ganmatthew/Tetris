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

import androidx.core.content.ContextCompat;

public class Board{

    public static final int DEFAULT_WIDTH = 10;
    public static final int DEFAULT_HEIGHT = 20;

    private int width;
    private int height;

    private Block[][] block_board;

    public Board(){
        this.width = DEFAULT_WIDTH;
        this.height = DEFAULT_HEIGHT;
        block_board = new Block[this.width][this.height];

        for (int i = 0; i < this.width; i++){
            for (int j = 0; j < this.height; j++){
                block_board[i][j] = new Block();
            }
        }
    }

    /*
    private void initColors() {
        Block.block_red =           new Rect(0,0,Block.block_size, Block.block_size);
        Block.block_orange =        new Rect(Block.block_size + 2, 0,(Block.block_size * 2) + 2, Block.block_size);
        Block.block_yellow =        new Rect((Block.block_size * 2) + 4, 0,(Block.block_size * 3) + 4, Block.block_size);
        Block.block_green =         new Rect((Block.block_size * 3) + 6, 0,(Block.block_size * 4) + 6, Block.block_size);
        Block.block_cyan =          new Rect((Block.block_size * 4) + 8, 0,(Block.block_size * 5) + 8, Block.block_size);
        Block.block_blue =          new Rect((Block.block_size * 5) + 10, 0,(Block.block_size * 6) + 10, Block.block_size);
        Block.block_violet =        new Rect((Block.block_size * 6) + 12, 0,(Block.block_size * 7) + 12, Block.block_size);
        Block.block_empty =         new Rect((Block.block_size * 9) + 20, 0,(Block.block_size * 10) + 19, Block.block_size);
    }
    */
}
