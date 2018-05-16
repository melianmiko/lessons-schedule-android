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

    class TableItem {
        int day; int n; int lesson;
        TableItem(int day, int n, int lsn) {this.day = day; this.n = n; this.lesson = lsn;}
    }

    class DailyTable {
        int day; TableItem[] content;
        DailyTable(int day, TableItem[] data) {this.day = day; this.content = data;}
    }

    class LessonsTable {
        DailyTable[] content;
        LessonsTable(DailyTable[] data) {content = data;}
    }

    private static final int version = 1;
    private static final String clearLsnDay = "[]";
    private static final int workDays = 5;

    LessonsStorage(Context context) {
        super(context, "lessonsDB", null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE lessons(id INT, name TEXT)");
        db.execSQL("CREATE TABLE times(id INT, startTime INT, endTime INT)");
        db.execSQL("CREATE TABLE lessonsTable(day INT, number INT, lesson INT, auditory INT)");
        db.execSQL("CREATE TABLE preferences(name TINYTEXT, value TINYTEXT)");

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

    private int getLessonsCount() {
        return this.getTimes().length;
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

    public void nameRem(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM lessons WHERE id="+id);
    }

    // ============================================================================================
    // Table manage

    public void tableSet(int day, int number, int lesson) {
        SQLiteDatabase db = this.getWritableDatabase();
        if(tableIsDefined(day,number)) db.execSQL("UPDATE lessonsTable SET lesson"+lesson+" WHERE day="+day+" AND number="+number);
        else db.execSQL("INSERT INTO lessonsTable(day,number,lesson) VALUES ("+day+","+number+","+lesson+")");
    }

    public DailyTable getDayLessons(int day) {
        ArrayList<TableItem> records = new ArrayList<>();
        int lessons = this.getLessonsCount();
        for(int i = 1; i <= lessons; i++) {
            records.add(this.getFromTable(day, i));
        }
        TableItem[] data = records.toArray(new TableItem[records.size()]);

        return new DailyTable(day,data);
    }

    public LessonsTable getTable() {
        ArrayList<DailyTable> out = new ArrayList<>();
        for(int i = 0; i <= 7; i++) out.add(this.getDayLessons(i));
        DailyTable[] data = out.toArray(new DailyTable[out.size()]);
        return new LessonsTable(data);
    }

    private boolean tableIsDefined(int day, int n) {
        return getFromTable(day, n) != null;
    }

    private TableItem getFromTable(int day, int n) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM lessonsTable WHERE day="+day+" AND number="+n, null);
        if(cursor.moveToFirst()) {
            TableItem ret = new TableItem(day,n,cursor.getInt(cursor.getColumnIndex("lesson")));
            cursor.close();
            return ret;
        } else {
            cursor.close();
            return null;
        }
    }
}
