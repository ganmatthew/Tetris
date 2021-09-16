package com.mobdeve.s12.group9.tetris;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.sql.RowId;
import java.util.ArrayList;

public class DatabaseHandler extends SQLiteOpenHelper {
    // DB information
    private static final String DATABASE_NAME = "Board.db";
    private static final int DATABASE_VERSION = 1;

    // Column names
    public static final String TABLE_BOARD = "board_table";
    public static final String BOARD_ID = "_id";
    public static final String BOARD_JSON = "board_json";

    // Table information
    private static final String CREATE_POST_TABLE = String.format(
        "CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT);",
        TABLE_BOARD, BOARD_ID, BOARD_JSON
    );

    public DatabaseHandler(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_POST_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL( String.format("DROP TABLE IF EXISTS %s;", TABLE_BOARD) );
    }

    public void addBoard(String boardJSON) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(boardJSON, BOARD_JSON);

        db.insert(TABLE_BOARD, null, values);
        db.close();
    }

    public String getBoard() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery( String.format("SELECT * FROM %s", TABLE_BOARD), null );

        String result = null;

        if (cursor.moveToFirst()) {
            do {
                result = cursor.getString(1);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return result;
    }

    public void deleteBoard() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery ( String.format("SELECT * FROM %s", TABLE_BOARD), null );
        if (cursor.moveToFirst()) {
            do {
                db.delete(TABLE_BOARD, BOARD_ID + "=?",  new String[]{"1"});
            } while (cursor.moveToNext());
        }
        cursor.close();
    }
}
