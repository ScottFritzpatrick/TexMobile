package ca.alexcomeau.texmobile;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.*;

public class HighScore {
    private final String DATABASE_NAME = "scoresdb";
    private final int DATABASE_VERSION = 1;

    private Cursor cursor;
    private SQLiteDatabase db;
    private Context context;
    private DBHelper dbHelper;

    // Constructor
    public HighScore(Context ctx)
    {
        context = ctx;
        cursor = null;
        db = null;
    }

    // Getters
    public Cursor getCursor() {
        return cursor;
    }

    // Public Methods =============================================================
    public void open() {
        dbHelper = new DBHelper();
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
        db = null;
    }

    public boolean writeScore(String name, int score, String time, String grade)
    {
        if(db == null) open();

        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("score", score);
        values.put("duration", time);
        values.put("grade", grade);
        db.insert("tblGames", null, values);

        return true;
    }

    public int getLowestScore()
    {
        try {
            cursor = null;

            if(db == null) open();

            cursor = db.rawQuery("SELECT MIN(score) FROM tblScores", null);

            if(cursor.getCount() == 0) return 0;

            cursor.moveToFirst();
            return cursor.getInt(0);
        }
        catch (Exception e) { return 0;}
    }

    // Inner class =================================================================
    private class DBHelper extends SQLiteOpenHelper {

        public DBHelper () { super(context, DATABASE_NAME, null, DATABASE_VERSION); }

        @Override
        public void onCreate(SQLiteDatabase db) {}

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
    }
}

