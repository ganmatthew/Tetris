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
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

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
    // Debugging tags
    private static final String GESTURE_TAG = "Gestures";

    // Grid size parameters
    public static final int NUM_HEIGHT = 20;
    public static final int NUM_WIDTH = 10;
    public static final int NUM_BLOCKSIZE = 80;
    public static final int GAME_OFFSET = 10;

    // Game activity parameters
    public static final int DELAY_NORMAL = 800;
    public static final int DELAY_FAST = 100;
    public static int delay = DELAY_NORMAL;
    private static int counter;

    // Grid components
    private static GameState gameState;
    private static int[][] blockData;
    private static List <Integer> pieceBag;

    private static Tetromino fallingTetromino;
    private static Shape holdTetromino;

    // Game components
    private MusicService musicService;
    private DisplayMetrics displayMetrics;
    private GestureDetectorCompat mDetector;
    private ViewsHelper helper;
    private Handler handler;
    private Runnable loop;

    private boolean pauseInvoked;
    private boolean usedHold;

    // Layout components
    private ConstraintLayout clGridLayout;
    private ConstraintLayout clHoldLayout;
    private ConstraintLayout clNextLayout;

    private GridView gridView;
    private HoldView holdView;
    private NextView nextView;

    private ImageButton btnPause;
    private ImageButton btnHelp;

    // Pause menu and Settings menu components
    private ConstraintLayout clOverlay;

    private View pauseView;
    private View settingsView;

    private Button btnResume;
    private Button btnSettings;
    private Button btnExit;

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

        clOverlay = findViewById(R.id.cl_board_overlay_container);

        clGridLayout = findViewById(R.id.cl_board_grid);
        clGridLayout.addView(gridView, 0);

        clHoldLayout = findViewById(R.id.cl_board_hold);

        clHoldLayout.addView(holdView, 0);

        clNextLayout = findViewById(R.id.cl_board_next);
        clNextLayout.addView(nextView, 0);

        btnPause = findViewById(R.id.btn_board_pause);

        // Start MusicService, plays the Tetris theme
        musicService = new MusicService(GameActivity.this);
        musicService.start();

        // Listen for pause button events
        pauseView = pauseGame(btnPause);

        pauseInvoked = false;
        startGame();
    }

    /*
        Button listeners
     */

    /**
     * Overrides the back button to pause the game
     */
    @Override
    public void onBackPressed() { pauseGame(null); }

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

    /**
     * Handles double tap events, used for rotation gestures.
     * @return true if double tap is on the left or right side, false if otherwise
     */
    @Override
    public boolean onDoubleTap(MotionEvent e) {
        // Get the median x-coordinate of the display
        float medianLine = (float) displayMetrics.widthPixels / 2;

        // Listen for double tap actions on either side
        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            float fingerPosition = e.getX();
            if (fingerPosition < medianLine) {
                Log.d(GESTURE_TAG, "\nDouble tap on LEFT SIDE\n");
                fallingTetromino.Rotate(Rotation.ANTICLOCKWISE);
            } else {
                Log.d(GESTURE_TAG, "\nDouble tap on RIGHT SIDE\n");
                fallingTetromino.Rotate(Rotation.CLOCKWISE);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) { return false; }

    @Override
    public boolean onDown(MotionEvent e) { return false; }

    @Override
    public void onShowPress(MotionEvent e) {}

    @Override
    public boolean onSingleTapUp(MotionEvent e) { return false; }

    // Stores the starting points for the scroll gesture to throttle it
    private float scrollStartX1;
    private float scrollStartY1;

    /**
     * Handles scrolling events called on the motion, used for the four directional gestures.
     * @return true if scroll direction is north, south, east, or west, false if otherwise
     */
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (scrollStartX1 != e1.getX() || scrollStartY1 != e1.getY()) {
            scrollStartX1 = e1.getX();
            scrollStartY1 = e1.getY();

            float angle = (float) Math.toDegrees(Math.atan2(e1.getY() - e2.getY(), e2.getX() - e1.getX()));
            if (angle < -45 && angle >= -135) {
                Log.d(GESTURE_TAG, "\nScroll DOWN, move Tetromino faster\n");
                delay = DELAY_FAST;
                return true;
            }

            if (angle > 45 && angle <= 135) {
                Log.d(GESTURE_TAG, "\nScroll UP, move Tetromino to hold or back from hold\n");
                swapTetromino();
                return true;
            }

            if (angle >= 135 && angle < 180 || angle < -135 && angle > -180) {
                Log.d(GESTURE_TAG, "\nScroll LEFT, move Tetromino left\n");
                fallingTetromino.MoveTetromino(Direction.LEFT, false);
                return true;
            }

            if (angle > -45 && angle <= 45) {
                Log.d(GESTURE_TAG, "\nScroll RIGHT, move Tetromino right\n");
                fallingTetromino.MoveTetromino(Direction.RIGHT, false);
                return true;
            }
        }
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {}

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) { return false; }

    public static void ResetCounter(){
        counter = 1;
    }
    /*
        Game board modifiers
     */

    /**
     * Starts the main thread for the game's cycle
     */
    public void startGame(){
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());
        counter = 1;
        loop = new Runnable() {
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
                        if(!fallingTetromino.MoveTetromino(Direction.DOWN, false)) counter++;

                        if (counter > 2){
                            // Resets the delay speed after the down gesture
                            if (delay == DELAY_FAST) { delay = DELAY_NORMAL; }

                            if(SpawnTetromino()){
                                counter = 1;
                                usedHold = false;

                                //clear lines.
                                ClearLines();

                            }
                            else{
                                gameState = GameState.END;
                            }
                        }
                        break;

                    case END:
                        blockData = new int[][]{{0}};
                        pauseGame(null);
                        break;

                }

                gridView.invalidate();
                holdView.invalidate();
                nextView.invalidate();

                // Sets how frequent each loop will run
                //Toast.makeText(GameActivity.this, "delay testing", Toast.LENGTH_SHORT).show();
                handler.postDelayed(this, delay);
            }
        };

        loop.run();
        //Toast.makeText(GameActivity.this, "loop done", Toast.LENGTH_SHORT).show();
    }

    /**
     * Stops the main thread of the game and opens the pause menu
     */
    private void pause(View view) {
        // Pause only if the pause menu is not inflated
        if (view.getParent() == null) {
            // Stops the thread
            pauseInvoked = true;
            handler.removeCallbacksAndMessages(null);
            // Inflate pause menu
            clOverlay.addView(view);
        }
    }

    /**
     * Restarts the main thread of the game and closes the menu if present
     */
    private void resume() {
        // Resume game
        if (pauseInvoked) { startGame(); }
        // Deflate pause menu
        if (pauseView != null) {
            if (pauseView.getParent() != null) { clOverlay.removeView(pauseView); }
        }
    }

    /**
     * Pauses the game proper.
     * @param button Specify the ImageButton to open the pause menu to listen for the button's onClick, otherwise, immediately pauses the game.
     * @return Returns the View that was inflated.
     */
    public View pauseGame(ImageButton button) {
        LayoutInflater inflater = GameActivity.this.getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_pause, null);
        view.setLayoutParams(new ConstraintLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT)
        );

        if (button != null) {
            button.setOnClickListener(v -> {
                pause(view);
            });
            // Sets up listener for the resume button
            resumeGame();
        } else {
            pause(view);
        }

        return view;
    }

    /**
     * Restarts the main thread of the game
     */
    public void resumeGame() {
        if (pauseView != null) {
            Button btnResume = pauseView.findViewById(R.id.btn_pause_resume);
            btnResume.setOnClickListener(v -> {
                resume();
            });
        } else {
            resume();
        }
    }

    /**
     * Gets the game's grid data
     * @return 2D array with ordinal representations of Tetrominoes on the grid
     */
    public static int[][] getGameData(){ return blockData; }

    /**
     * Gets the game's current state
     * @return GameState enum indicating NEW, SPAWN, FALL, or END
     */
    public static GameState getGameState(){ return gameState; }

    /**
     * Gets the shape of the Tetromino in the hold
     * @return Shape enum indicating the specific shape of the Tetromino
     */
    public static Shape getHoldTetromino(){ return holdTetromino; }

    /**
     * Gets the List of Tetrominoes that are next for spawning
     * @return List of ordinals representing the shape type of the Tetrominoes
     */
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

    /**
     * Generates the next Tetrominoes that will be spawned
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

    /**
     * Spawns a Tetromino on the top center of the grid
     * @return true if spawning conditions are legal, false if otherwise
     */
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

    /**
     * Places the currently falling Tetromino into the hold
     */
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



    public boolean ClearLines_Sub(int[] row){
        for (int i = 0; i < row.length; i++){
            if (row[i] == 0){
                return false;
            }
        }

        return true;
    }

    public void ClearLines(){
        for (int i = NUM_HEIGHT - 1; i > -1; i--){  //from the bottom of the grid, go up

            if (ClearLines_Sub(blockData[i])){ //until you find a row that is full

                for (int j = i; j > 0; j--){ // if so, loop until the highest point

                    for (int k = 0; k < NUM_WIDTH; k++){
                        blockData[j][k] = blockData[j - 1][k]; //and shift all of the blocks by 1
                    }

                }
            }

        }
    }

    /***
     * Life cycle
     */

    @Override
    protected void onPause() {
        // Pause the running game
        pauseGame(null);
        // Pause music
        if (musicService != null) { musicService.pause(); }

        super.onPause();

    }

    @Override
    protected void onStop() {
        // Pause the running game
        pauseGame(null);
        // Stop music
        if (musicService != null) { musicService.pause(); }

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        // Stop the running game
        pauseGame(null);
        // Stop music
        if (musicService != null) { musicService.stop(); }

        super.onDestroy();
    }

    @Override
    protected void onResume() {
        // Resume the running game
        if (pauseInvoked) { resumeGame(); }
        // Resume music
        if (musicService != null) { musicService.resume(); }

        super.onResume();
    }
}