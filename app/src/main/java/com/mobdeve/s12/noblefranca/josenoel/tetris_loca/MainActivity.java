package com.mobdeve.s12.noblefranca.josenoel.tetris_loca;

import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Canvas;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Board board = new Board(this);
        this.setContentView(board);

        //board.drawBoard(new Canvas());

    }
}