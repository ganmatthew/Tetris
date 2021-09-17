package com.mobdeve.s12.group9.tetris;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GestureDetectorCompat;
import androidx.lifecycle.MutableLiveData;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
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

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

enum GameState {
    NEW,
    SPAWN,
    FALL,
    END_WIN,
    END_LOSE,
    END_MARATHON
}

enum GameMode {
    SPRINT,
    MARATHON,
    ENDLESS,
    CONTINUE // only for loading data
}

public class GameActivity extends AppCompatActivity implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener{
    // Debugging tags
    private static final String GESTURE_TAG = "Gestures";

    // Grid size parameters
    public static final int NUM_HEIGHT = 20;                // number of grid rows
    public static final int NUM_WIDTH = 10;                 // number of grid columns
    public static final int NUM_BLOCKSIZE = 80;             // scale of blocks on the grid
    public static final int GAME_OFFSET = 10;               // offset in pixels for rendering borders

    // Game activity parameters
    public static final int DELAY_NORMAL = 800;             // delay in ms during normal gameplay
    public static final int DELAY_FAST = 100;               // delay in ms during swipe down action
    public static final int SPRINT_NUMBER_OF_LINES = 40;    // number of lines to clear in sprint mode
    public static final int MARATHON_TIME_LIMIT = 122;     // time limit in marathon mode + extra 2 seconds

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
    private DatabaseHandler database;
    private Handler handler;
    private Runnable loop;
    private CountDownTimer timer;
    private MutableLiveData<Boolean> inflatedListener;

    private boolean pauseInvoked;
    private boolean usedHold;
    private boolean timerInitialized;

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

    private View pauseView;
    private View settingsView;
    private View helpView;

    private Button btnResume;
    private Button btnSettings;
    private Button btnExit;

    // Game mode components
    private GameMode gameMode;
    private int totalCleared;
    private long timeDurationInMs;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize DisplayMetrics to get display data
        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        // Set up gesture detection and listeners
        mDetector = new GestureDetectorCompat(this,this);
        mDetector.setOnDoubleTapListener(this);

        // Initialize and set the views to the layouts
        helper = new ViewsHelper(GameActivity.this);
        BoardViewGroup group = helper.getViews();

        gridView = group.gv;
        holdView = group.hv;
        nextView = group.nv;

        // Set view to board, find res ids
        setContentView(R.layout.activity_board);

        clOverlay = findViewById(R.id.cl_board_overlay_container);

        clGridLayout = findViewById(R.id.cl_board_grid);
        clGridLayout.addView(gridView, 0);

        clHoldLayout = findViewById(R.id.cl_board_hold);

        clHoldLayout.addView(holdView, 0);

        clNextLayout = findViewById(R.id.cl_board_next);
        clNextLayout.addView(nextView, 0);

        btnPause = findViewById(R.id.btn_board_pause);
        btnHelp = findViewById(R.id.btn_board_help);

        tvScoreTitle = findViewById(R.id.tv_board_score);
        tvScoreValue = findViewById(R.id.tv_board_score_value);

        // Creates new board data or loads from existing data
        database = new DatabaseHandler(GameActivity.this);
        loadBoardData();

        // Listen for pause button events
        pauseGame(btnPause);
        pauseInvoked = false;

        timerInitialized = false;

        // Start MusicService, plays the Tetris theme
        musicService = new MusicService(GameActivity.this);
        musicService.start();

        // Listen for help button events
        showHelpScreen();

        startGame();
    }

    /**
     * Sets the game mode based on the value passed by the intent.
     */
    private void setGameMode(GameMode setMode) {
        // Sets the game mode from the parameter if not null
        if (setMode != null) {
            gameMode = setMode;
        } else { // Otherwise get the game mode from the passed intent
            String result = getIntent().getStringExtra(GameMode.class.getName());
            gameMode = GameMode.valueOf(result);
        }

        switch(gameMode) {
            case SPRINT:
                tvScoreTitle.setText(R.string.board_score_sprint);
                tvScoreValue.setText("40");
                totalCleared = 0;
                break;
            case MARATHON:
                tvScoreTitle.setText(R.string.board_score_marathon);
                tvScoreValue.setText("2:00");
                timeDurationInMs = 0;
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
        int sprintScore = SPRINT_NUMBER_OF_LINES - totalCleared;
        switch(gameMode) {
            case SPRINT:
                tvScoreValue.setText(formatScore(sprintScore));
                break;
            case ENDLESS:
                tvScoreValue.setText(formatScore(totalCleared));
                break;
        }
    }

    /*
        Creates new board data if there is no saved board data, otherwise loads it
     */

    private void loadBoardData() {
        // Check if launched using continue button from main menu
        if (gameMode != GameMode.CONTINUE) {
            // Set game state to starting
            gameState = GameState.NEW;

            // Initialize grid of given size
            blockData = new int[NUM_HEIGHT][NUM_WIDTH];

            // Initialize a stack to choose the next tetrominoes from
            pieceBag = new ArrayList<Integer>();

            // Instantiate the Tetromino to be dropped as empty
            holdTetromino = Shape.EMPTY_SHAPE;

            // Set username via intent
            username = getIntent().getStringExtra(IntentKeys.PLAYER_USERNAME.name());
            Log.d("USERNAME", String.format("Username: %s", username));

            // Set game mode via intent
            setGameMode(null);
        } else {
            Board board = new Board(database.getBoard(username));

            blockData = board.getGrid();
            pieceBag = board.getNext();
            holdTetromino = board.getHold();
            fallingTetromino = board.getFalling();
            timeDurationInMs = board.getTimer();
            totalCleared = board.getTotalCleared();
            gameState = board.getState();

            // Set game mode via loaded data
            setGameMode(board.getMode());
        }
    }

    private void saveBoardData() {
        Board board = new Board(username,
            blockData, holdTetromino, pieceBag,
            fallingTetromino, timeDurationInMs,
            totalCleared, gameMode, gameState
        );

        if (database.getBoard(username) != null) {
            database.deleteBoard(username);
            database.addBoard(username, board.getObjectJSON());
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
                fallingTetromino.moveTetromino(Direction.LEFT, false);
                return true;
            }

            if (angle > -45 && angle <= 45) {
                Log.d(GESTURE_TAG, "\nScroll RIGHT, move Tetromino right\n");
                fallingTetromino.moveTetromino(Direction.RIGHT, false);
                return true;
            }
        }
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {}

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) { return false; }

    public static void resetCounter(){
        counter = 1;
    }

    /**
     * Initializes and starts a countdown timer.
     * @param startTimeInMs The time that the timer will count down from, in miliseconds
     */
    private void startTimer(long startTimeInMs) {
        timer = new CountDownTimer(startTimeInMs * 1000, 1000) {
            @Override
            public void onTick(long currentTimeInMs) {
                String timestamp = String.format(Locale.getDefault(), "%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(currentTimeInMs),
                    TimeUnit.MILLISECONDS.toSeconds(currentTimeInMs) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(currentTimeInMs))
                );

                timeDurationInMs = currentTimeInMs;
                tvScoreValue.setText(timestamp);
            }

            @Override
            public void onFinish() {
                if (totalCleared < 1) {
                    gameState = GameState.END_LOSE;
                } else {
                    gameState = GameState.END_MARATHON;
                }
            }
        };
        timer.start();
    }

    /**
     * Resumes the countdown timer by obtaining its last timestamp.
     */
    private void resumeTimer() { startTimer(timeDurationInMs / 1000); }

    /**
     * Stops the countdown timer.
     */
    private void pauseTimer() { timer.cancel(); }

    /*
        Game board modifiers
     */

    /**
     * Starts the main thread for the game's cycle
     */
    public void startGame(){
        handler = new Handler(Looper.getMainLooper());
        counter = 1;
        loop = new Runnable() {
            @Override
            public void run() {
                switch (gameState) {
                    case NEW:
                        // Generate pieces to next
                        populatePieceBag();
                        populatePieceBag();

                        // Set game state to spawn the Tetromino
                        gameState = GameState.SPAWN;

                        // Initiate timer if marathon mode
                        if (gameMode == GameMode.MARATHON && !timerInitialized) {
                            timerInitialized = true;
                            startTimer(MARATHON_TIME_LIMIT);
                        }

                        delay = DELAY_FAST;
                        break;

                    case SPAWN:
                        // Restore delay to normal
                        delay = DELAY_NORMAL;

                        // Spawns a Tetromino and checks if its in a legal position
                        gameState = spawnTetromino() ? GameState.FALL : GameState.END_LOSE;
                        usedHold = false;
                        break;

                    case FALL:
                        if(!fallingTetromino.moveTetromino(Direction.DOWN, false)) counter++;

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
                            switch(gameMode) {
                                case SPRINT:
                                    if (totalCleared == SPRINT_NUMBER_OF_LINES) { gameState = GameState.END_WIN; }
                                    break;
                                case MARATHON:
                                case ENDLESS:
                                    break;
                            }
                        }
                        break;

                    case END_WIN:
                    case END_LOSE:
                        if (gameMode == GameMode.MARATHON) { pauseTimer(); }
                        handler.removeCallbacksAndMessages(null);
                        setEndScreen(gameState);
                        break;
                }

                // Force the grid, hold, and next views to redraw
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
        View view = inflater.inflate(R.layout.activity_end_screen, null);

        setViewToInflateToLayout(view);

        if (view.getParent() == null) {
            clOverlay.addView(view);
        }

        // Clear the board entry, as the game has finished
        database.deleteBoard(username);

        ConstraintLayout clEnd = view.findViewById(R.id.cl_end);
        TextView tvEndTitle = view.findViewById(R.id.tv_end_title);
        TextView tvEndMessage = view.findViewById(R.id.tv_end_message);
        Button btnEndExit = view.findViewById(R.id.btn_end_back);

        String msg, linesClearedMsg = "You cleared " + totalCleared + " lines";

        if (view.getParent() != null) {
            // Set end screen background color and message
            switch (endState) {
                case END_WIN:
                    msg = "You won a game of Tetris!";
                    setEndScreenColor(view, clEnd, R.color.win_background_color);
                    setEndScreenText(view, tvEndTitle, R.string.win_title, tvEndMessage, msg);
                    break;
                case END_LOSE:
                    msg = linesClearedMsg + ". Good luck next time!";
                    setEndScreenColor(view, clEnd, R.color.lose_background_color);
                    setEndScreenText(view, tvEndTitle, R.string.lose_title, tvEndMessage, msg);
                    break;
                case END_MARATHON:
                    msg = linesClearedMsg + " in the given time!";
                    setEndScreenColor(view, clEnd, R.color.win_background_color);
                    setEndScreenText(view, tvEndTitle, R.string.timer_finished_title, tvEndMessage, msg);
                    break;
            }
            // Save result to leaderboard in JSON format
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy  HH:mm aaa", Locale.getDefault());
            String timestamp = dateFormat.format(new Date());

            LeaderboardEntry entry = new LeaderboardEntry(username,
                    totalCleared, timeDurationInMs, timestamp, gameMode);
            String json = new Gson().toJson(entry);

            // Listen for close button
            if (view.getParent() != null) {
                btnEndExit.setOnClickListener(v -> {
                    // Pass JSON of new leaderboard entry to MainActivity
                    Intent j = new Intent(GameActivity.this, MainActivity.class);
                    j.putExtra( IntentKeys.ADD_LEADERBOARD.name(), json );
                    setResult(RESULT_OK, j);

                    GameActivity.this.finish();
                });
            }

        }
    }

    /**
     * Sets the end screen's background color according to game state
     */
    private void setEndScreenColor(View view, ConstraintLayout clEnd, int colorId) {
        clEnd.setBackgroundColor(view.getResources().getColor(colorId));
    }

    /**
     * Sets the end screen's title and message according to game state
     */
    private void setEndScreenText(View view, TextView tvEndTitle, int resIdTitle,
                                  TextView tvEndMessage, String message) {
        tvEndTitle.setText(resIdTitle);
        tvEndMessage.setText(message);
    }

    /**
     * Inflates and deflates the help screen
     */
    public void showHelpScreen() {
        LayoutInflater inflater = GameActivity.this.getLayoutInflater();
        View viewHelp = inflater.inflate(R.layout.activity_help, null);
        View viewSettings = inflater.inflate(R.layout.activity_settings, null);

        setViewToInflateToLayout(viewSettings);
        settingsView = viewSettings;

        // Initialize SettingsService
        settingsService = new SettingsService(settingsView, clOverlay, GameActivity.this);

        // Listen to changes to inflated settings view
        inflatedListener = new MutableLiveData<Boolean>();
        inflatedListener.postValue(settingsService.getIsInflated());
        inflatedListener.observe(GameActivity.this, boolResult -> {
            musicService.setSettings(settingsService);
        });

        setViewToInflateToLayout(viewHelp);
        helpView = viewHelp;

        btnHelp.setOnClickListener(v -> {
            if (helpView.getParent() == null) {
                pause();
                clOverlay.addView(helpView);
            }
            if (pauseView.getParent() != null) { clOverlay.removeView(pauseView); }
        });

        TextView tvMode = helpView.findViewById(R.id.tv_help_mode);
        TextView tvModeDesc = helpView.findViewById(R.id.tv_help_mode_desc);

        // Set instructions accordingly
        switch(gameMode) {
            case SPRINT:
                tvMode.setText(R.string.sprint_name);
                tvModeDesc.setText(R.string.sprint_desc);
                break;
            case MARATHON:
                tvMode.setText(R.string.marathon_name);
                tvModeDesc.setText(R.string.marathon_desc);
                break;
            case ENDLESS:
                tvMode.setText(R.string.endless_name);
                tvModeDesc.setText(R.string.endless_desc);
                break;
        }

        // Listen for close button
        Button btnHelpExit = helpView.findViewById(R.id.btn_help_exit);
        btnHelpExit.setOnClickListener(v -> {
            if (helpView.getParent() != null) {
                clOverlay.removeView(helpView);
                resumeGame(false);
            }
        });
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

                    if (gameMode == GameMode.MARATHON) { pauseTimer(); }
                    handler.removeCallbacksAndMessages(null);

                    // Inflate pause menu
                    clOverlay.addView(pauseView);
                }

                if (pauseView.getParent() != null) {
                    btnResume = pauseView.findViewById(R.id.btn_pause_resume);
                    btnSettings = pauseView.findViewById(R.id.btn_pause_settings);
                    btnExit = pauseView.findViewById(R.id.btn_pause_exit);

                    btnResume.setOnClickListener(v -> {
                        // Resume game
                        resumeGame(false);
                    });

                    btnSettings.setOnClickListener(v -> {
                        // Remove pause menu first
                        if (pauseView.getParent() != null) {
                            clOverlay.removeView(pauseView);
                        }
                        // Open Settings view
                        if (settingsView.getParent() == null) {
                            clOverlay.addView(settingsView);
                        }
                    });

                    btnExit.setOnClickListener(v -> {
                        // Inform MainActivity that there is a saved game
                        Intent j = new Intent();
                        j.putExtra(IntentKeys.SAVED_GAME_PRESENT.name(), true);
                        setResult(RESULT_OK, j);

                        GameActivity.this.finish();
                    });

                    // Opens to pause menu when settings is closed
                    if (settingsService != null) {
                        if (settingsService.getIsInflated()) {
                            clOverlay.removeAllViews();
                            clOverlay.addView(pauseView);
                        }
                    }
                }
        }
    }

    /**
     * Restarts the main thread of the game and closes the menu if present
     */
    private void resumeGame(boolean usePauseMenu) {
        if (pauseInvoked) {
            if (usePauseMenu) {
                pauseGame(null);
            } else {
                pauseInvoked = false;
                if (gameMode == GameMode.MARATHON && timerInitialized) { resumeTimer(); }
                clOverlay.removeAllViews();
                startGame();
            }
        }
    }

    /**
     * Pauses the game proper and inflates a view if needed.
     * @param button Specify the ImageButton to open the pause menu to listen for the button's onClick, otherwise, immediately pauses the game.
     */
    public void pauseGame(ImageButton button) {
        LayoutInflater inflater = GameActivity.this.getLayoutInflater();
        View viewPause = inflater.inflate(R.layout.activity_pause, null);

        setViewToInflateToLayout(viewPause);

        if (button != null) {
            button.setOnClickListener(v -> pause());
        } else {
            pause();
        }

        pauseView = viewPause;

        // Save copy to database
        saveBoardData();
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
     * @return true if full, false if not full
     */
    public boolean checkRowForFull(int[] row){
        for (int i : row) {
            if (i == Shape.EMPTY_SHAPE.ordinal()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the given row on the grid has no blocks
     * @param row A row from the grid
     * @return true if empty, false if not empty
     */
    public boolean checkRowForEmpty(int[] row){
        for (int i : row) {
            if (i != Shape.EMPTY_SHAPE.ordinal()) {
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
                if (!checkRowForEmpty(blockData[i])){
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
        resumeGame(true);
        // Resume music
        if (musicService != null) { musicService.resume(); }

        super.onResume();
    }
}