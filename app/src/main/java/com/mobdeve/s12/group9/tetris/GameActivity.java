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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

enum GameState {
    NEW,
    SPAWN,
    FALL,
    END_WIN,
    END_LOSE
}

enum GameMode {
    SPRINT,
    MARATHON,
    ENDLESS
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
    private SettingsService settingsService;
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

    private TextView tvScoreTitle;
    private TextView tvScoreValue;

    private ImageButton btnPause;
    private ImageButton btnHelp;

    // Pause menu and Settings menu components
    private ConstraintLayout clOverlay;
    private ConstraintLayout clPauseOverlay;

    private View pauseView;
    private View settingsView;

    private Button btnResume;
    private Button btnSettings;
    private Button btnExit;

    private GameMode mode;
    private int totalCleared;
    private int countdown;

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

        // Sets the game mode and relevant factors
        tvScoreTitle = findViewById(R.id.tv_board_score);
        tvScoreValue = findViewById(R.id.tv_board_score_value);
        setGameMode();

        // Start MusicService, plays the Tetris theme
        musicService = new MusicService(GameActivity.this);
        musicService.start();

        // Listen for pause button events
        pauseGame(btnPause);

        pauseInvoked = false;
        startGame();
    }

    /**
     * Sets the game mode based on the value passed by the intent.
     */
    private void setGameMode() {
        Bundle bundle = getIntent().getExtras();
        String result = bundle.getString(GameMode.class.getName());
        mode = GameMode.valueOf(result);

        switch(mode) {
            case SPRINT:
                tvScoreTitle.setText(R.string.board_score_sprint);
                tvScoreValue.setText("40");
                totalCleared = 0;
                break;
            case MARATHON:
                tvScoreTitle.setText(R.string.board_score_marathon);
                tvScoreValue.setText("2:00");
                countdown = 0;
                break;
            case ENDLESS:
                tvScoreTitle.setText(R.string.board_score_endless);
                tvScoreValue.setText("0");
                totalCleared = 0;
                break;
        }
        updateScore();
    }

    /**
     * Adds commas to number values in the thousands and greater
     * @param value Accepts any non-decimal numerical value
     * @return String of number containing commas
     */
    private String formatScore(int value) { return String.format(Locale.getDefault(), "%,d", value); }

    /**
     * Updates the displayed score for number of lines cleared
     */
    private void updateScore() {
        int sprintScore = 40 - totalCleared;
        switch(mode) {
            case SPRINT:
                tvScoreValue.setText(formatScore(sprintScore));
                break;
            case ENDLESS:
                tvScoreValue.setText(formatScore(totalCleared));
                break;
        }
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
                switch (gameState) {
                    case NEW:
                        populatePieceBag();
                        populatePieceBag();
                        gameState = GameState.SPAWN;
                        delay = DELAY_FAST;
                        break;

                    case SPAWN:
                        delay = DELAY_NORMAL;
                        gameState = spawnTetromino() ? GameState.FALL : GameState.END_LOSE;
                        usedHold = false;
                        break;

                    case FALL:
                        if(!fallingTetromino.MoveTetromino(Direction.DOWN, false)) counter++;

                        if (counter > 2) {
                            // Resets the delay speed after the down gesture
                            if (delay == DELAY_FAST) { delay = DELAY_NORMAL; }

                            if(spawnTetromino()){
                                counter = 1;
                                usedHold = false;

                                //clear lines.
                                totalCleared += clearLines();
                                updateScore();
                            }
                            else {
                                gameState = GameState.END_LOSE;
                            }
                            // Check for possible end-game conditions after score updates
                            switch(mode) {
                                case SPRINT:
                                    if (totalCleared == 40) { gameState = GameState.END_WIN; }
                                    break;
                                case MARATHON:
                                    if (countdown == 0) { gameState = GameState.END_LOSE; }
                                    break;
                            }
                        }
                        break;

                    case END_WIN:
                    case END_LOSE:
                        setEndScreen(gameState);
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
     * Displays the win or lose end screen
     */
    private void setEndScreen(GameState endState) {
        LayoutInflater inflater = GameActivity.this.getLayoutInflater();
        Button exitBtn;
        View view;

        if (endState == GameState.END_WIN) {
            view = inflater.inflate(R.layout.activity_win, null);
        } else {
            view = inflater.inflate(R.layout.activity_lose, null);
        }

        switch (endState) {
            case END_WIN:
            case END_LOSE:
                setViewToInflateToLayout(view);
                if (view.getParent() == null) {
                    handler.removeCallbacksAndMessages(null);
                    clOverlay.addView(view);
                }
                if (endState == GameState.END_WIN) {
                    exitBtn = view.findViewById(R.id.btn_win_back);
                } else {
                    exitBtn = view.findViewById(R.id.btn_lose_back);
                }

                if (view.getParent() != null) {
                    exitBtn.setOnClickListener(v -> {
                        Log.d("EXIT", "Clicked!");
                        GameActivity.this.finish();
                    });
                }
                break;
        }
    }

    /**
     * Stops the main thread of the game and opens the pause menu
     */
    private void pause() {
        // Pause only if the pause menu is not inflated
        switch(gameState) {
            case END_LOSE:
            case END_WIN:
                break;
            default:
                if (pauseView.getParent() == null) {
                    // Stops the thread
                    pauseInvoked = true;
                    handler.removeCallbacksAndMessages(null);
                    // Inflate pause menu
                    clOverlay.addView(pauseView);
                }

                clPauseOverlay = pauseView.findViewById(R.id.cl_pause_menu_overlay_container);
                btnResume = pauseView.findViewById(R.id.btn_pause_resume);
                btnSettings = pauseView.findViewById(R.id.btn_pause_settings);
                btnExit = pauseView.findViewById(R.id.btn_pause_exit);

                if (pauseView.getParent() != null) {
                    btnResume.setOnClickListener(v -> {
                        // Remove Settings view first
                        if (settingsView.getParent() == null) {
                            clPauseOverlay.removeView(settingsView);
                        }
                        // Resume game
                        if (pauseInvoked) { resumeGame(); }
                    });
                    btnSettings.setOnClickListener(v -> {
                        if (settingsView.getParent() == null) {
                            clPauseOverlay.addView(settingsView);
                        }
                        // Initialize SettingsService
                        settingsService = new SettingsService(pauseView,GameActivity.this);
                    });
                    btnExit.setOnClickListener(v -> {
                        Log.d("EXIT", "Clicked!");
                        GameActivity.this.finish();
                    });
                }
                break;
        }
    }

    /**
     * Restarts the main thread of the game and closes the menu if present
     */
    private void resumeGame() {
        if (pauseView != null) { clPauseOverlay.removeView(pauseView); }
        startGame();
    }

    /**
     * Pauses the game proper and inflates a view if needed.
     * @param button Specify the ImageButton to open the pause menu to listen for the button's onClick, otherwise, immediately pauses the game.
     */
    public void pauseGame(ImageButton button) {
        LayoutInflater inflater = GameActivity.this.getLayoutInflater();
        View viewPause = inflater.inflate(R.layout.activity_pause, null);
        View viewSettings = inflater.inflate(R.layout.activity_settings, null);

        setViewToInflateToLayout(viewPause);
        setViewToInflateToLayout(viewSettings);

        if (button != null) {
            button.setOnClickListener(v -> pause());
        } else {
            pause();
        }
        pauseView = viewPause;
        settingsView = viewSettings;
    }

    /**
     * Sets a View's bounds to match that of a ConstraintLayout
     * @param view A view that will be inflated onto a ConstraintLayout
     */
    private void setViewToInflateToLayout(View view) {
        view.setLayoutParams(new ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT)
        );
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

    /**
     * Test for checking if all Tetromino pieces render correctly on the grid
     */
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

        for (int j : array) { pieceBag.add(j); }

    }

    /**
     * Spawns a Tetromino on the top center of the grid
     * @return true if spawning conditions are legal, false if otherwise
     */
    public boolean spawnTetromino(){

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
                spawnTetromino();
            }

        }

        usedHold = true;
    }

    /**
     * Checks if the given row on the grid is filled up with blocks
     * @param row A row from the grid
     * @return true if full, false if empty
     */
    public boolean checkRowForFull(int[] row){
        for (int j : row) {
            if (j == Shape.EMPTY_SHAPE.ordinal()) {
                return false;
            }
        }
        return true;
    }

    /**
     *
     * @return Returns the number of lines cleared in this function call.
     */
    public int clearLines(){
        int cnt = NUM_HEIGHT - 1;
        int rt_val = 0;

        for (int i = NUM_HEIGHT - 1; i > -1; i--){  //from the bottom of the grid, go up
            if (checkRowForFull(blockData[i])){ //until you find a row that is full
                for (int j = 0; j < NUM_WIDTH; j++){
                    blockData[i][j] = 0;
                }
                rt_val++;
            }
        }

        if (rt_val != 0){
            int[][] new_block_data = new int[NUM_HEIGHT][NUM_WIDTH];

            for (int i = NUM_HEIGHT - 1; i > -1; i--){
                if (!checkRowForFull(blockData[i])){
                    new_block_data[cnt] = blockData[i];
                    cnt--;
                }
            }

            blockData = new_block_data;
        }

        return rt_val;
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