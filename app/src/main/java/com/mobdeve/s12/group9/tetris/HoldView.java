package com.mobdeve.s12.group9.tetris;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;

public class HoldView extends View {
    private Paint block_painter;
    private Paint frame_painter;
    private ViewsHelper helper;

    public HoldView(Context context, ViewsHelper helper) {
        super(context);

        block_painter = new Paint();
        frame_painter = new Paint();

        this.helper = helper;
    }

    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        helper.drawHoldBorders(canvas, frame_painter, HoldView.this);

        switch (GameActivity.getGameState()) {
            case END_WIN:
            case END_LOSE:
                break;
            default:
                helper.drawHoldPieces(canvas, block_painter, HoldView.this);
                break;
        }
    }

}
