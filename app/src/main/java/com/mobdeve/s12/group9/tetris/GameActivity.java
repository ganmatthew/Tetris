package com.mobdeve.s12.group9.tetris;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GestureDetectorCompat;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    //public static final int GAME_OFFSET = 100;
    public static final int GAME_OFFSET = 10;

    // Game activity parameters
    public static final int DELAY_NORMAL = 1000;
    public static final int DELAY_FAST = 100;
    public static int delay = DELAY_NORMAL;

    // Grid components
    private static GameState gameState;
    private static int[][] blockData;
    private static List <Integer> pieceBag;

    private static Tetromino fallingTetromino;
    private static Shape holdTetromino;

    // Game components
    private DisplayMetrics displayMetrics;
    private GestureDetectorCompat mDetector;
    private ViewsHelper helper;
    private Handler handler;
    private Runnable loop;
    private boolean usedHold;

    // Layout components
    private ConstraintLayout clGridLayout;
    private ConstraintLayout clHoldLayout;
    private ConstraintLayout clNextLayout;

    private GridView gridView;
    private HoldView holdView;
    private NextView nextView;

    private MusicService musicService;

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
        gameState = GameState.NEW;

        // Initialize a stack to choose the next tetrominoes from
        pieceBag = new ArrayList<Integer>();

        holdTetromino = Shape.EMPTY_SHAPE;

        // Initialize and set the views to the layouts
        helper = new ViewsHelper(GameActivity.this);
        BoardViewGroup group = helper.getViews();

        gridView = group.gv;
        holdView = group.hv;
        nextView = group.nv;

        setContentView(R.layout.activity_board);

        clGridLayout = findViewById(R.id.cl_board_grid);
        clGridLayout.addView(gridView, 0);

        clHoldLayout = findViewById(R.id.cl_board_hold);

        clHoldLayout.addView(holdView, 0);

        clNextLayout = findViewById(R.id.cl_board_next);
        clNextLayout.addView(nextView, 0);

        // Start MusicService, plays the Tetris theme
        musicService = new MusicService(GameActivity.this);
        musicService.start();

        startGame();
    }

    /*
        Touch gesture listeners
     */

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.mDetector.onTouchEvent(event)) { return true; }
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

    //on scroll is called on the motion, onfling is called after the motion.

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        float angle = (float) Math.toDegrees(Math.atan2(e1.getY() - e2.getY(), e2.getX() - e1.getX()));
        if (angle < -45 && angle >= -135) {
            Log.d(GESTURE_TAG, "\nScroll gesture from UP to DOWN\n");
            delay = DELAY_FAST;
            return true;
        }
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {}

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        // Get swipe direction based on delta of x and y coordinates
        float angle = (float) Math.toDegrees(Math.atan2(e1.getY() - e2.getY(), e2.getX() - e1.getX()));

        if (angle > -45 && angle <= 45) {
            Log.d(GESTURE_TAG, "\nFling from LEFT to RIGHT\n");
            fallingTetromino.MoveTetromino(Direction.RIGHT);
            return true;
        }

        if (angle >= 135 && angle < 180 || angle < -135 && angle > -180) {
            Log.d(GESTURE_TAG, "\nFling from RIGHT to LEFT\n");
            fallingTetromino.MoveTetromino(Direction.LEFT);
            return true;
        }

        if (angle < -45 && angle >= -135) {
            Log.d(GESTURE_TAG, "\nFling from UP to DOWN\n");

            return true;
        }

        if (angle > 45 && angle <= 135) {
            Log.d(GESTURE_TAG, "\nFling from DOWN to UP\n");
            swapTetromino();
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
                    case NEW:
                        populatePieceBag();
                        populatePieceBag();
                        gameState = GameState.SPAWN;
                        delay = DELAY_FAST;
                        break;

                    case SPAWN:
                        delay = DELAY_NORMAL;
                        gameState = !SpawnTetromino() ? GameState.END : GameState.FALL;
                        usedHold = false;
                        break;

                    case FALL:
                        if(!fallingTetromino.MoveTetromino(Direction.DOWN)) counter++;

                        if (counter > 2){
                            // Resets the delay speed after the down gesture
                            if (delay == DELAY_FAST) { delay = DELAY_NORMAL; }

                            if(SpawnTetromino()){
                                counter = 1;
                                usedHold = false;
                            }
                            else{
                                gameState = GameState.END;
                            }
                        }
                        break;

                    case END:
                        blockData = new int[][]{{0}};
                        handler.removeCallbacksAndMessages(this);
                        break;

                }

                gridView.invalidate();
                holdView.invalidate();
                nextView.invalidate();

                //Toast.makeText(GameActivity.this, "delay testing", Toast.LENGTH_SHORT).show();
                handler.postDelayed(this, delay);
            }
        };

        loop.run();
        //Toast.makeText(GameActivity.this, "loop done", Toast.LENGTH_SHORT).show();
    }

    public static int[][] getGameData(){ return blockData; }

    public static GameState getGameState(){ return gameState; }

    public static Shape getHoldTetromino(){ return holdTetromino; }

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

        fallingTetromino = new Tetromino(Shape.values()[pieceBag.remove(0)]);

        if (fallingTetromino.getShape() == Shape.O_SHAPE){
            fallingTetromino.addXOffset(4);
        }
        else{
            fallingTetromino.addXOffset(3);
        }

        //check if spawning conditions are legal, return false otherwise.
        for (int i = 0; i < 4; i++){
            if (blockData[fallingTetromino.getDataY()[fallingTetromino.getPos()][i]][fallingTetromino.getDataX()[fallingTetromino.getPos()][i]] != 0){
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

    // Places the falling Tetromino into hold
    public void swapTetromino(){
        if (!usedHold){
            Shape temp;
            // Gets the falling Tetromino and removes it from the grid
            for (int i = 0; i < 4; i++){
                blockData[fallingTetromino.getDataY()[fallingTetromino.getPos()][i]][fallingTetromino.getDataX()[fallingTetromino.getPos()][i]] = 0; //empty shape
            }
            // Places the Tetromino into hold
            if (holdTetromino != Shape.EMPTY_SHAPE){
                temp = holdTetromino;
                holdTetromino = fallingTetromino.getShape();
                fallingTetromino = new Tetromino(temp);

                if (fallingTetromino.getShape() == Shape.O_SHAPE){
                    fallingTetromino.addXOffset(4);
                } else{
                    fallingTetromino.addXOffset(3);
                }
            } else{
                // Removes the Tetromino from hold and sets it to spawn again
                holdTetromino = fallingTetromino.getShape();
                SpawnTetromino();
            }

        }

        usedHold = true;
    }

    /***
     * Life cycle
     */

    @Override
    protected void onPause() {
        super.onPause();
        if (musicService != null) {
            musicService.pause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (musicService != null) {
            musicService.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (musicService != null) {
            musicService.stop();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (musicService != null) {
            musicService.resume();
        }
    }
}