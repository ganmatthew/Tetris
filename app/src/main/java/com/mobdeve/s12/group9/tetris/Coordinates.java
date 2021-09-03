package com.mobdeve.s12.group9.tetris;

public class Coordinates {
    int y, x;

    public Coordinates(int row, int col) {
        this.y = row;
        this.x = col;
    }

    public static Coordinates add(Coordinates A, Coordinates B) {
        return new Coordinates(A.y + B.y, A.x + B.x);
    }

    public static Coordinates subtract(Coordinates A, Coordinates B) {
        return new Coordinates(A.y - B.y, A.x - B.x);
    }

    public static Coordinates rotate(Coordinates A) {
        return new Coordinates(-A.x, A.y);
    }

    public static boolean isEqual(Coordinates A, Coordinates B) {
        return A.y == B.y && A.x == B.x;
    }


}
