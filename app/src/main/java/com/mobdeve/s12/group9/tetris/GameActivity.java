package com.mobdeve.s12.group9.tetris;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Stack;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;

enum GameState {
    NEW,
    SPAWN,
    FALL,
    END
}

public class GameActivity extends AppCompatActivity implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener{
    private static final String GESTURE_TAG = "Gestures";

    // Grid size parameters
    public static final int NUM_HEIGHT = 20;
    public static final int NUM_WIDTH = 10;
    public static final int NUM_BLOCKSIZE = 80;
    public static final int GAME_OFFSET = 100;

    // Game activity parameters
    public static int DELAY = 1000;

    // Grid components
    private static int gameState;
    private static int[][] blockData;
    private static List <Integer> pieceBag;

    // Game components
    private DisplayMetrics displayMetrics;
    private GestureDetectorCompat mDetector;
    private GameView gameView;
    private Handler handler;
    private Runnable loop;
    private Tetromino tetromino;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize DisplayMetrics to get display data
        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        // Set up gesture detection and listeners
        mDetector = new GestureDetectorCompat(this,this);
        mDetector.setOnDoubleTapListener(this);

        // Initialize grid of given size
        blockData = new int[NUM_HEIGHT][NUM_WIDTH];

        // Set game state to starting
        gameState = 0;

        // Initialize a stack to choose the next tetrominoes from
        pieceBag = new ArrayList<Integer>();


        // Initialize and set the view to the grid canvas
        gameView = new GameView(this);
        gameView.setBackgroundColor(Color.BLACK);
        setContentView(gameView);

        startGame();
    }

    /*
        Touch gesture listeners
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

    /*
        Game board modifiers
     */

    public void startGame(){
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());
        loop = new Runnable() {
            int counter = 1;

            @Override
            public void run() {

                switch (gameState){
                    case 0:
                        populatePieceBag();
                        populatePieceBag();
                        gameState = 1;
                        DELAY = 100;
                        break;

                    case 1:
                        gameState = !SpawnTetromino() ? GameState.END.ordinal() : GameState.FALL.ordinal();
                        DELAY = 500;
                        break;

                    case 2:
                        if(!tetromino.MoveTetromino(Direction.DOWN)) counter++;

                        if (counter > 2){
                            if(SpawnTetromino()){
                                counter = 1;
                            }
                            else{
                                gameState = 3;
                            }
                        }
                        break;

                    case 3:
                        blockData = new int[][]{{0}};
                        handler.removeCallbacksAndMessages(this);
                        break;

                }

                gameView.invalidate();
                //Toast.makeText(GameActivity.this, "delay testing", Toast.LENGTH_SHORT).show();
                handler.postDelayed(this, DELAY);
            }
        };

        loop.run();
        //Toast.makeText(GameActivity.this, "loop done", Toast.LENGTH_SHORT).show();
    }

    public static int[][] getGameData(){ return blockData; }

    public static int getGameState(){ return gameState; }

    public static List<Integer> getPieceBag(){ return pieceBag;}

    public void debugTest(){
        if (blockData[0][0] == 6){
            blockData[0][0] = 2;
            blockData[19][0] = 3;
            blockData[0][9] = 4;
            blockData[19][9] = 5;
        } else{
            blockData[0][0] = 6;
            blockData[19][0] = 7;
            blockData[0][9] = 1;
            blockData[19][9] = 2;
        }
    }

    /*
        Tetromino spawning, movement, and collision functions
     */

    public void populatePieceBag(){
        //make an array of 7 elements..
        int array[] = {1,2,3,4,5,6,7};

        int index;
        Random random = new Random();
        for (int i = array.length - 1; i > 0; i--)
        {
            index = random.nextInt(i + 1);
            if (index != i)
            {
                array[index] ^= array[i];
                array[i] ^= array[index];
                array[index] ^= array[i];
            }
        }

        for (int i = 0; i < array.length; i++){
            pieceBag.add(array[i]);
        }

    }

    public boolean SpawnTetromino(){

        tetromino = new Tetromino(Shape.values()[pieceBag.remove(0)]);

        if (tetromino.getShape() == Shape.O_SHAPE){
            tetromino.addXOffset(4);
        }
        else{
            tetromino.addXOffset(3);
        }

        //check if spawning conditions are legal, return false otherwise.
        for (int i = 0; i < 4; i++){
            if (blockData[tetromino.getDataY()[tetromino.getPos()][i]][tetromino.getDataX()[tetromino.getPos()][i]] != 0){
                return false;
            }
        }

        //Toast.makeText(GameActivity.this, "number of pieces in bag: " + pieceBag.size(), Toast.LENGTH_SHORT).show();

        if (pieceBag.size() == 7){
           // Toast.makeText(GameActivity.this, "trigger", Toast.LENGTH_SHORT).show();
            populatePieceBag();
        }

        return true;
    }

}