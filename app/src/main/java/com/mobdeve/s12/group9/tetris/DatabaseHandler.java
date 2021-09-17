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
    public static final String BOARD_USERNAME = "board_username";
    public static final String BOARD_JSON = "board_json";

    // Table information
    private static final String CREATE_POST_TABLE = String.format(
        "CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT);",
        TABLE_BOARD, BOARD_ID, BOARD_USERNAME, BOARD_JSON
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

    public void addBoard(String username, String boardJSON) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(username, BOARD_USERNAME);
        values.put(boardJSON, BOARD_JSON);

        db.insert(TABLE_BOARD, null, values);
        db.close();
    }

    public String getBoard(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(getAllOfUsername(username), null);

        if (cursor.getCount() > 0) {
            ArrayList<String> jsons = new ArrayList<String>();
            do {
                jsons.add(cursor.getString(3));
            } while (cursor.moveToNext());

            cursor.close();
            return jsons.get(0);

        }
        cursor.close();
        return null;
    }

    public void deleteBoard(String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(getAllOfUsername(username), null);

        if (cursor.getCount() > 0) {
            do {
                db.delete(TABLE_BOARD, "username=?", new String[]{username});
            } while (cursor.moveToNext());
            cursor.close();
        }
        cursor.close();
    }

    private String getAllOfUsername(String username) {
        String query = String.format(
                "SELECT * FROM %s WHERE %s LIKE '%s';",
                TABLE_BOARD, BOARD_USERNAME, username
        );
        return query;
    }
}
