package com.mobdeve.s12.group9.tetris;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

public class NextView extends View {
    private Paint block_painter;
    private Paint frame_painter;
    private ViewsHelper helper;

    public NextView(Context context, ViewsHelper helper) {
        super(context);

        block_painter = new Paint();
        frame_painter = new Paint();
        this.helper = helper;
    }

    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        helper.drawNextBorders(canvas, frame_painter);

        if (GameActivity.getGameState() != GameState.END){
            helper.drawNextPieces(canvas, block_painter, NextView.this);
        }
    }
}
