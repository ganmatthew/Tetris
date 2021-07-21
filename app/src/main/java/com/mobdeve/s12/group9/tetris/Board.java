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

public class Board extends View {

    protected int board_width = 10;
    protected int board_height = 20;

    protected Block[][] blocks;

    protected Bitmap b;


    public Board(Context context) {
        super(context);
        b = getBitmap(getContext(), R.drawable.ic_sprite_modified_5);
        blocks = new Block[board_width][board_height];
        Block.block_size = b.getHeight();
        this.initColors();

        for (int i = 0; i < board_width; i++) {
            for (int j = 0; j < board_height; j++) {
                blocks[i][j] = new Block(i, j);
            }
        }
    }

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
        for (int i = 0; i < board_width; i++){
            for (int j = 0; j < board_height; j++){
                blocks[i][j].drawBlock(canvas, b, Block.block_empty);
            }
        }
        //tests only

        /*
        blocks[1][1].drawBlock(canvas, b, Block.block_red);
        blocks[2][2].drawBlock(canvas, b, Block.block_orange);
        blocks[3][3].drawBlock(canvas, b, Block.block_yellow);
        blocks[4][4].drawBlock(canvas, b, Block.block_green);
        blocks[5][5].drawBlock(canvas, b, Block.block_cyan);
        blocks[6][6].drawBlock(canvas, b, Block.block_blue);
        blocks[7][7].drawBlock(canvas, b, Block.block_violet);
        blocks[8][8].drawBlock(canvas, b, Block.block_empty);
        */
        //blocks[0][0].drawBlock(canvas, block_size, b);
      //  blocks[0][1].drawBlock(canvas, block_size, b);
       // blocks[1][1].drawBlock(canvas, block_size, b);
       // blocks[1][0].drawBlock(canvas, block_size, b);
        //canvas.drawBitmap(b, new Rect(0,0, block_size, block_size), boundbox, null);
        //canvas.drawBitmap(b, new Rect(block_size + 2,0, (block_size * 2) + 2, block_size), boundbox, null);
        //canvas.drawBitmap(b, new Rect(block_size * 2,0, (block_size * 3), block_size), boundbox, null);
        //super.onDraw(canvas);
    }

}
