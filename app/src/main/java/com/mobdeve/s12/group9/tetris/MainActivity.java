package com.mobdeve.s12.group9.tetris;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;

import java.net.CookieHandler;

public class MainActivity extends AppCompatActivity {

    protected Board b;
    protected Game g;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        b = new Board(this);
        g = new Game(b);

        ConstraintLayout cl_board_layout = findViewById(R.id.cl_board_layout);
        cl_board_layout.addView(b);

        g.run();

        //Board board = new Board(this);
        //cl.addView(board);
        //board.drawBoard(new Canvas());
    }
}