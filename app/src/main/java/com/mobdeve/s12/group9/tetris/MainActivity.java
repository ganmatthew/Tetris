package com.mobdeve.s12.group9.tetris;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;

import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener{

    private static final String DEBUG_TAG = "Gestures";
    private GestureDetectorCompat mDetector;

    protected Board b;
    protected Game g;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();

        // Make app fullscreen
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        //WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        //b = new Board(this);
        g = new Game(this);

        //ConstraintLayout cl_board_layout = findViewById(R.id.cl_board_layout);
        //cl_board_layout.addView(g);
        //Board board = new Board(this);
        //cl.addView(board);
        //board.drawBoard(new Canvas());

        // Set up gesture detection
        mDetector = new GestureDetectorCompat(this,this);
        // Set up listener for double tap events
        mDetector.setOnDoubleTapListener(this);
    }

    /*
    Listen for different touch events
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.mDetector.onTouchEvent(event)) {
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) { return false; }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        // Get the median x-coordinate of the display
        Display defaultDisplay = getWindowManager().getDefaultDisplay();
        Point displaySize = new Point();
        defaultDisplay.getSize(displaySize);

        float medianLine = displaySize.x / 2;

        // Listen for double tap actions on either side
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                float fingerPosition = e.getX();
                if(fingerPosition < medianLine) {
                    Log.d(DEBUG_TAG, "\nDouble tap on LEFT SIDE\n");
                } else {
                    Log.d(DEBUG_TAG, "\nDouble tap on RIGHT SIDE\n");
                }
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) { return false; }

    @Override
    public boolean onDown(MotionEvent e) { return false; }

    @Override
    public void onShowPress(MotionEvent e) {}

    @Override
    public boolean onSingleTapUp(MotionEvent e) { return false; }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) { return false; }

    @Override
    public void onLongPress(MotionEvent e) {}

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        float angle = (float) Math.toDegrees(Math.atan2(e1.getY() - e2.getY(), e2.getX() - e1.getX()));

        if (angle > -45 && angle <= 45) {
            Log.d(DEBUG_TAG, "\nFling from RIGHT to LEFT\n");
            return true;
        }

        if (angle >= 135 && angle < 180 || angle < -135 && angle > -180) {
            Log.d(DEBUG_TAG, "\nFling from LEFT to RIGHT\n");
            return true;
        }

        if (angle < -45 && angle >= -135) {
            Log.d(DEBUG_TAG, "\nFling from UP to DOWN\n");
            return true;
        }

        if (angle > 45 && angle <= 135) {
            Log.d(DEBUG_TAG, "\nFling from DOWN to UP\n");
            return true;
        }

        return false;
    }
}