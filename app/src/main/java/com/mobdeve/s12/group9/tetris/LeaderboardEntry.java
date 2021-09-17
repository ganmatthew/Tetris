package com.mobdeve.s12.group9.tetris;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class LeaderboardEntry {
    private String username;
    private String desc;
    private int totalCleared;
    private long timeRemaining;
    private String timestamp;
    private GameMode mode;

    public LeaderboardEntry(String username, int totalCleared, long timeRemaining, String timestamp, GameMode mode) {
        this.username = username;
        this.totalCleared = totalCleared;
        this.timeRemaining = timeRemaining;
        this.timestamp = timestamp;
        this.mode = mode;
    }

    public String getUsername() {
        return username;
    }

    public String getScore() {
        switch(mode) {
            case SPRINT:
            case ENDLESS:
                desc = String.format("%d lines cleared", totalCleared);
                break;
            case MARATHON:
                desc = String.format("%d lines cleared in %s", totalCleared, parseTimeRemaining(timeRemaining));
                break;
        }
        return desc;
    }

    public String getTimestamp() {
        return timestamp;
    }

    private String parseTimeRemaining(long timeInMs) {
        String timestamp = String.format(Locale.getDefault(), "%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(timeInMs),
                TimeUnit.MILLISECONDS.toSeconds(timeInMs) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeInMs)));
            return timestamp;
    }
}
