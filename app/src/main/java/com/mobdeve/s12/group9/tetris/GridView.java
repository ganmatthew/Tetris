package com.mobdeve.s12.group9.tetris;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

public class GridView extends View {
    private Paint block_painter;
    private Paint frame_painter;
    private ViewsHelper helper;

    public GridView(Context context, ViewsHelper helper) {
        super(context);

        block_painter = new Paint();
        frame_painter = new Paint();
        this.helper = helper;
    }

    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        helper.drawGridBorders(canvas, frame_painter, GridView.this);

        switch (GameActivity.getGameState()) {
            case END_WIN:
            case END_LOSE:
                break;
            default:
                helper.drawGridPieces(canvas, block_painter, GridView.this);
                break;
        }
    }
}
