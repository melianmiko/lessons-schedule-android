package ml.mhbrgn.schooljournal;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class LessonsStorage extends SQLiteOpenHelper  {

    class TimeRecord{
        int startTime; int endTime;
        TimeRecord(int s, int e) {startTime = s; endTime = e;}
    }

    private static final int version = 1;
    private static final String clearLsnDay = "[]";
    private static final int workDays = 5;

    LessonsStorage(Context context) {
        super(context, "lessonsDB", null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE lessons(id INTEGER PRIMARY KEY, name TEXT)");
        db.execSQL("CREATE TABLE times(id INTEGER, startTime INTEGER, endTime INTEGER)");
        db.execSQL("CREATE TABLE lessonsTable(day INTEGER PRIMARY KEY, jsonArray TEXT)");
        db.execSQL("CREATE TABLE preferences(name TINYTEXT, value TINYTEXT)");

        // Create days in lessonsTable
        for (int i = 0; i < 7; i++) { db.execSQL("INSERT INTO lessonsTable(day, jsonArray) VALUES ("+i+",\""+clearLsnDay+"\")"); }

        // Set work days pref
        db.execSQL("INSERT INTO preferences(name,value) VALUES (\"workDays\",\""+workDays+"\")");

        // Setup times
        this.setupDefTimes(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    // ===================================================================
    // Times manage
    private void setupDefTimes(SQLiteDatabase db) {
        db.execSQL("DELETE FROM times");

        db.execSQL("INSERT INTO times(id,startTime,endTime) VALUES (1,500,540)");
        db.execSQL("INSERT INTO times(id,startTime,endTime) VALUES (2,545,585)");
        db.execSQL("INSERT INTO times(id,startTime,endTime) VALUES (3,600,640)");
        db.execSQL("INSERT INTO times(id,startTime,endTime) VALUES (4,655,695)");
        db.execSQL("INSERT INTO times(id,startTime,endTime) VALUES (5,705,745)");
        db.execSQL("INSERT INTO times(id,startTime,endTime) VALUES (6,755,795)");
    }

    public void timeAdd(int startTime, int endTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("INSERT INTO times(id,startTime,endTime) VALUES ("+(this.getTimes().length+1)+","+startTime+","+endTime+")");
    }

    public void timeMod(int id, int startTime, int endTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE times SET startTIme="+startTime+", endTime="+endTime+" WHERE id="+id);
    }

    public void timeRem(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM times WHERE id="+id);
        this.timeRebase();
    }

    public TimeRecord getTime(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Get all records
        Cursor cursor = db.rawQuery("SELECT * FROM times WHERE id="+id, null);
        if(cursor.moveToFirst()) {
            TimeRecord tr = new TimeRecord(cursor.getInt(cursor.getColumnIndex("startTime")),
                    cursor.getInt(cursor.getColumnIndex("endTime")));

            cursor.close();

            return tr;
        } else return null;
    }

    public TimeRecord[] getTimes() {
        SQLiteDatabase db = this.getWritableDatabase();
        List<TimeRecord> read = new ArrayList<>();
        // Get all records
        Cursor cursor = db.rawQuery("SELECT * FROM times", null);
        if (cursor.moveToFirst()) {

            read.add(new TimeRecord(cursor.getInt(cursor.getColumnIndex("startTime")),
                    cursor.getInt(cursor.getColumnIndex("endTime"))));

            while (cursor.moveToNext()) {

                read.add(new TimeRecord(cursor.getInt(cursor.getColumnIndex("startTime")),
                        cursor.getInt(cursor.getColumnIndex("endTime"))));

            }

        }
        cursor.close();
        return read.toArray(new TimeRecord[read.size()]);
    }

    private void timeRebase() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Get backup
        TimeRecord[] bkp = this.getTimes();
        // Drop table
        db.execSQL("DELETE FROM times");
        for (TimeRecord i: bkp) {
            this.timeAdd(i.startTime,i.endTime);
        }
        // Rebase complete!
    }

}
