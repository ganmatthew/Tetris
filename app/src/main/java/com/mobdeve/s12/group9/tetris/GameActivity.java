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

import java.util.Random;
import java.util.Stack;
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
    public static final int DELAY = 1000;

    // Grid components
    private static int gameState;
    private static int[][] blockData;

    // Game components
    private DisplayMetrics displayMetrics;
    private GestureDetectorCompat mDetector;
    private GameView gameView;
    private Handler handler;
    private Runnable loop;
    private Stack <Integer> pieceBag;
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
        pieceBag = new Stack<Integer>();

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
            moveTetromino(Direction.RIGHT);
            return true;
        }

        if (angle >= 135 && angle < 180 || angle < -135 && angle > -180) {
            Log.d(GESTURE_TAG, "\nFling from RIGHT to LEFT\n");
            moveTetromino(Direction.LEFT);
            return true;
        }

        if (angle < -45 && angle >= -135) {
            Log.d(GESTURE_TAG, "\nFling from UP to DOWN\n");
            moveTetromino(Direction.DOWN);
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
        handler = new Handler(Looper.getMainLooper());
        loop = new Runnable() {

            @Override
            public void run() {
                if (gameState == 0){
                    populatePieceBag();
                    gameState = 1;
                } else if (gameState == 1){
                    boolean check = spawnTetromino();
                    if (check){
                        gameState = 2;
                    } else {

                    }
                } else if (gameState == 2){
                    moveTetromino(Direction.DOWN);
                }

                //debugTest();

                gameView.invalidate();
                //Toast.makeText(GameActivity.this, "delay testing", Toast.LENGTH_SHORT).show();
                handler.postDelayed(this, DELAY);
            }
        };
        loop.run();
    }

    public static int[][] getGameData(){ return blockData; }

    public static int getGameState(){ return gameState; }

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
        int temp[] = {1,2,3,4,5,6,7};
        Random rnd = ThreadLocalRandom.current();
        for (int i = temp.length - 1; i > 0; i--){
            int index = rnd.nextInt(i + 1);
            int a = temp[index];
            temp[index] = temp[i];
            temp[i] = a;
        }

        for (int i = 0; i < temp.length; i++){
            pieceBag.push(temp[i]);
        }

    }

    public void updateBlockData(Shape shape){
        for (int i = 0; i < 4; i++){
            blockData[tetromino.getDataY()[tetromino.getPos()][i]][tetromino.getDataX()[tetromino.getPos()][i]] = shape.ordinal();
        }
    }

    public boolean checkIfBlockDataLegal(){
        //check if the current position of the tetromino is conflicting with the current block data.
        for (int i = 0; i < 4; i++){
            if (blockData[tetromino.getDataY()[tetromino.getPos()][i]][tetromino.getDataX()[tetromino.getPos()][i]] != 0)
                return false;
        }

        //check if the current x values of the tetromino is greater than 9, or less than 0.
        for (int i = 0; i < 4; i++){
            if (tetromino.getDataX()[tetromino.getPos()][i] > 9 || 0 > tetromino.getDataX()[tetromino.getPos()][i])
                return false;
        }

        //check if the current y value is greater than 19.
        for (int i = 0; i < 4; i++){
            if (tetromino.getDataY()[tetromino.getPos()][i] > 19)
                return false;
        }

        return true;
    }

    /***
     * Moves the Tetromino in a given direction
     * @param direction Direction enum
     **/
    public void moveTetromino(Direction direction){
        collisionMovementCheck(direction);
        switch (direction){
            case DOWN:
                //check for collision here first

                //then move piece down
                updateBlockData(Shape.EMPTY_SHAPE);
                //offset by 1
                tetromino.addYOffset(1);
                break;

            case LEFT:
                //check collision
                collisionMovementCheck(direction);
                //
                updateBlockData(Shape.EMPTY_SHAPE);
                tetromino.addXOffset(-1);
                break;
            case RIGHT:
                //check collision
                collisionMovementCheck(direction);
                //
                updateBlockData(Shape.EMPTY_SHAPE);
                tetromino.addXOffset(1);
                break;
        }

        updateBlockData(tetromino.getShape());
    }

    public boolean spawnTetromino() {
        //int PieceId = piece_bag.pop();
        tetromino = new Tetromino(Shape.J_SHAPE);

        //spawn the piece to spawn area, note its always +3? (no confirm on this)

        tetromino.addXOffset(3);

        if (checkIfBlockDataLegal()){
            updateBlockData(tetromino.getShape());
            return true;
        }
        return false;
    }

    public boolean collisionMovementCheck(Direction direction){
        int[][] temp;

        if (direction == Direction.LEFT || direction == Direction.RIGHT){
            temp = tetromino.getDataX();

            tetromino.addXOffset(direction.ordinal());

            if (checkIfBlockDataLegal()){
                return true;
            }

            tetromino.addXOffset(direction.ordinal() * -1);

        } else {
            temp = tetromino.getDataY();

            tetromino.addYOffset(direction.ordinal());

            if (checkIfBlockDataLegal()){
                return true;
            }

            tetromino.addYOffset(direction.ordinal() * -1);
        }

        return false;
    }

}