package com.mobdeve.s12.group9.tetris;

import com.google.gson.Gson;

import java.util.List;

public class Board {

    private String board_username;
    private int[][] board_grid;
    private Shape board_hold;
    private List<Integer> board_next;
    private Tetromino board_falling;
    private long board_timer;
    private int board_cleared;
    private GameMode board_mode;
    private GameState board_state;

    public Board(String username, int[][] grid, Shape hold, List<Integer> next, Tetromino falling,
                 long timerInMs, int totalCleared, GameMode mode, GameState state) {
        this.board_username = username;
        this.board_grid = grid;
        this.board_hold = hold;
        this.board_next = next;
        this.board_falling = falling;
        this.board_timer = timerInMs;
        this.board_cleared = totalCleared;
        this.board_mode = mode;
        this.board_state = state;
    }

    public Board(String json) {
        Board board = new Gson().fromJson(json, Board.class);
        this.board_username = board.getUsername();
        this.board_grid = board.getGrid();
        this.board_hold = board.getHold();
        this.board_next = board.getNext();
        this.board_falling = board.getFalling();
        this.board_timer = board.getTimer();
        this.board_cleared = board.getTotalCleared();
        this.board_mode = board.getMode();
        this.board_state = board.getState();
    }

    public String getObjectJSON() { return new Gson().toJson(Board.this); }

    // Get functions

    public String getUsername() { return board_username; }

    public int[][] getGrid() {
        return board_grid;
    }

    public Shape getHold() {
        return board_hold;
    }

    public List<Integer> getNext() {
        return board_next;
    }

    public Tetromino getFalling() {
        return board_falling;
    }

    public long getTimer() {
        return board_timer;
    }

    public int getTotalCleared() {
        return board_cleared;
    }

    public GameMode getMode() {
        return board_mode;
    }

    public GameState getState() { return board_state; }
}
