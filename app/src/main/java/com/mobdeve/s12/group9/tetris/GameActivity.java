package com.mobdeve.s12.group9.tetris;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GestureDetectorCompat;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Random;
import java.util.Stack;
import java.util.concurrent.ThreadLocalRandom;

public class GameActivity extends AppCompatActivity implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener{
    private static final String GESTURE_TAG = "Gestures";

    public static final int NUM_HEIGHT = 20;
    public static final int NUM_WIDTH = 10;
    public static final int NUM_BLOCKSIZE = 80;
    public static final int GAME_OFFSET = 100;
    public static final int DELAY = 2000;

    public static final int GAMESTATE_NEW = 0;
    public static final int GAMESTATE_SPAWN = 1;
    public static final int GAMESTATE_FALL = 2;
    public static final int GAMESTATE_END = 2;

    private static int game_state;
    private static int[][] block_data;

    // Game components
    private DisplayMetrics displayMetrics;
    private GestureDetectorCompat mDetector;
    private GameView game_view;
    private Handler handler;
    private Runnable loop;
    private Stack <Integer> piece_bag;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize DisplayMetrics to get display data
        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        // Set up gesture detection
        mDetector = new GestureDetectorCompat(this,this);

        // Set up listener for double tap events
        mDetector.setOnDoubleTapListener(this);

        block_data = new int[NUM_HEIGHT][NUM_WIDTH];

        game_state = 0;

        piece_bag = new Stack<Integer>();

        game_view = new GameView(this);
        game_view.setBackgroundColor(Color.BLACK);
        setContentView(game_view);

        StartGame();
    }

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
        float medianLine = (float) displayMetrics.widthPixels / 2;

        // Listen for double tap actions on either side
        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            float fingerPosition = e.getX();
            if(fingerPosition < medianLine) {
                Log.d(GESTURE_TAG, "\nDouble tap on LEFT SIDE\n");
            } else {
                Log.d(GESTURE_TAG, "\nDouble tap on RIGHT SIDE\n");
            }
            return true;
        } else {
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
        // Get swipe direction based on delta of x and y coordinates
        float angle = (float) Math.toDegrees(Math.atan2(e1.getY() - e2.getY(), e2.getX() - e1.getX()));

        if (angle > -45 && angle <= 45) {
            Log.d(GESTURE_TAG, "\nFling from LEFT to RIGHT\n");
            return true;
        }

        if (angle >= 135 && angle < 180 || angle < -135 && angle > -180) {
            Log.d(GESTURE_TAG, "\nFling from RIGHT to LEFT\n");
            return true;
        }

        if (angle < -45 && angle >= -135) {
            Log.d(GESTURE_TAG, "\nFling from UP to DOWN\n");
            return true;
        }

        if (angle > 45 && angle <= 135) {
            Log.d(GESTURE_TAG, "\nFling from DOWN to UP\n");
            return true;
        }

        return false;
    }

    public static int[][] get_gamedata(){
        return block_data;
    }
    public static int getGame_state(){return game_state;}

    public void PopulatePieceBag(){
        //make an array of 7 elements..
        int temp[] = {1,2,3,4,5,6,7};
        Random rnd = ThreadLocalRandom.current();
        for (int i = temp.length - 1; i > 0; i--){
            int index = rnd.nextInt(i + 1);
            int a = temp[index];
            temp[index] = temp[i];
            temp[i] = a;
        }

        for (int i = 0; i < temp.length; i++){
            piece_bag.push(temp[i]);
        }

    }

    public void DebugTest(){
        if (block_data[0][0] == 6){
            block_data[0][0] = 2;
            block_data[19][0] = 3;
            block_data[0][9] = 4;
            block_data[19][9] = 5;
        }
        else{
            block_data[0][0] = 6;
            block_data[19][0] = 7;
            block_data[0][9] = 1;
            block_data[19][9] = 2;
        }
    }

    public void StartGame(){
        handler = new Handler(Looper.getMainLooper());
        loop = new Runnable() {
            @Override
            public void run() {
                PopulatePieceBag();
                game_state = 1;

                if (game_state == 1){

                }

                //DebugTest();

                game_view.invalidate();
                //Toast.makeText(GameActivity.this, "delay testing", Toast.LENGTH_SHORT).show();
                handler.postDelayed(this, DELAY);
            }
        };
        loop.run();
    }

}