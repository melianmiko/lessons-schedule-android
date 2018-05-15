package ml.mhbrgn.schooljournal;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class LessonsStorage extends SQLiteOpenHelper  {

    class TimeRecord{
        int startTime; int endTime;
        TimeRecord(int s, int e) {startTime = s; endTime = e;}
    }

    class NameRecord{
        int id; String name;
        NameRecord(int id, String name) {this.id = id; this.name = name;}
    }

    private static final int version = 1;
    private static final String clearLsnDay = "[]";
    private static final int workDays = 5;

    LessonsStorage(Context context) {
        super(context, "lessonsDB", null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE lessons(id INTEGER, name TEXT)");
        db.execSQL("CREATE TABLE times(id INTEGER, startTime INTEGER, endTime INTEGER)");
        db.execSQL("CREATE TABLE lessonsTable(day INTEGER, jsonArray TEXT)");
        db.execSQL("CREATE TABLE preferences(name TINYTEXT, value TINYTEXT)");

        // Create days in lessonsTable
        for (int i = 0; i < 7; i++) { db.execSQL("INSERT INTO lessonsTable(day, jsonArray) VALUES ("+i+",\""+clearLsnDay+"\")"); }

        // Set work days pref
        db.execSQL("INSERT INTO preferences(name,value) VALUES (\"workDays\",\""+workDays+"\")");

        // Setup times
        this.setupDefTimes(db);

        db.execSQL("INSERT INTO lessons VALUES (1,\"Algebra\")");
        db.execSQL("INSERT INTO lessons VALUES (2,\"Algebra2\")");
        db.execSQL("INSERT INTO lessons VALUES (3,\"Algebra3\")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    // ===================================================================
    // Times manage
    public void timesRestore() {
        this.setupDefTimes(this.getWritableDatabase());
    }

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

    // ============================================================================================
    // Names manage

    public NameRecord[] getNames() {
        SQLiteDatabase db = this.getWritableDatabase();
        List<NameRecord> read = new ArrayList<>();
        // Get all records
        Cursor cursor = db.rawQuery("SELECT * FROM lessons", null);
        if (cursor.moveToFirst()) {
            read.add(new NameRecord(cursor.getInt(cursor.getColumnIndex("id")),
                    cursor.getString(cursor.getColumnIndex("name"))));

            while (cursor.moveToNext()) {

                read.add(new NameRecord(cursor.getInt(cursor.getColumnIndex("id")),
                        cursor.getString(cursor.getColumnIndex("name"))));

            }

        }
        cursor.close();
        return read.toArray(new NameRecord[read.size()]);
    }

    public String getName(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM lessons WHERE id="+id, null);
        if(cursor.moveToFirst()) {
            String n = cursor.getString(cursor.getColumnIndex("name"));
            cursor.close();
            return n;
        } else {
            cursor.close();
            return null;
        }
    }

    public void nameAdd(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("INSERT INTO lessons(id,name) VALUES ("+(this.getNames().length+1)+",\""+name+"\")");
    }

    public void nameMod(int id, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE lessons SET name=\""+name+"\" WHERE id="+id);
    }
}
