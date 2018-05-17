package ml.mhbrgn.schooljournal;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class LessonsStorage extends SQLiteOpenHelper  {
    // Default data
    private int[][] times = new int[][]{
            new int[]{500,540}, new int[]{545,585}, new int[]{600,640}, new int[]{655,695},
            new int[]{705,745}, new int[]{755,795}, new int[]{805,845}, new int[]{855,895}
    };

    private int[] lessons = new int[]{
           R.string.algebra, R.string.english, R.string.literature,
           R.string.geometry, R.string.biology
    };

    // DAY 1 = MONDAY
    static int getCurrentDay() {
        Calendar calendar = Calendar.getInstance();

        int day = calendar.get(Calendar.DAY_OF_WEEK);
        --day;

        if ( day == 0 ) day = 7;
        return day;
    }

    static String getDayName(int dayNumber, Context context) {
        int[] dayStrings = new int[]{0, R.string.day_1, R.string.day_2, R.string.day_3, R.string.day_4,
                R.string.day_5, R.string.day_6, R.string.day_7};

        return context.getResources().getString(dayStrings[dayNumber]);
    }

    static String timeToString(int time) {
        int hours = (int) Math.floor(time/60);
        int minutes = (int) Math.floor(time-hours*60);

        String h = String.valueOf(hours);
        if(h.length() == 1) h = '0'+h;
        String m = String.valueOf(minutes);
        if(m.length() == 1) m = '0'+m;

        return h+':'+m;
    }

    String getLessonTimeString(int lesson) {
        TimeRecord tr = getTime(lesson);
        return lesson+" "+timeToString(tr.startTime);
    }

    // CLASSES ==========================================================================

    class TimeRecord{
        int startTime; int endTime;
        TimeRecord(int s, int e) {startTime = s; endTime = e;}
    }

    static class NameRecord{
        int id; String name;
        NameRecord(int id, String name) {this.id = id; this.name = name;}
    }

    static class TableItem {
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
    private static final int workDays = 5;
    private Context context;

    LessonsStorage(Context context) {
        super(context, "lessonsDB", null, version);
        this.context = context;
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

        // Setup lesson names
        for (int i = 0; i < lessons.length; i++) {
            int c = lessons[i];
            String cur = context.getResources().getString(c);
            db.execSQL("INSERT INTO lessons(id,name) VALUES ("+(i+1)+",\""+cur+"\")");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    // ===================================================================
    // Times manage
    public void timesRestore() {
        this.setupDefTimes(getWritableDatabase());
    }

    private void setupDefTimes(SQLiteDatabase db) {
        db.execSQL("DELETE FROM times");

        for(int i = 0; i < times.length; i++) {
            int[] values = times[i];
            db.execSQL("INSERT INTO times(id,startTime,endTime) VALUES ("+(i+1)+","+values[0]+","+values[1]+")");
        }
    }

    public void timeAdd(int startTime, int endTime) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("INSERT INTO times(id,startTime,endTime) VALUES ("+(this.getTimes().length+1)+","+startTime+","+endTime+")");
    }

    public void timeMod(int id, int startTime, int endTime) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE times SET startTIme="+startTime+", endTime="+endTime+" WHERE id="+id);
    }

    public void timeRem(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM times WHERE id="+id);
        this.timeRebase();
    }

    public TimeRecord getTime(int id) {
        SQLiteDatabase db = getWritableDatabase();
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
        SQLiteDatabase db = getWritableDatabase();
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
        SQLiteDatabase db = getWritableDatabase();
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
        SQLiteDatabase db = getWritableDatabase();
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
        SQLiteDatabase db = getWritableDatabase();
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
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("INSERT INTO lessons(id,name) VALUES ("+(this.getNames().length+1)+",\""+name+"\")");
    }

    public void nameMod(int id, String name) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE lessons SET name=\""+name+"\" WHERE id="+id);
    }

    public void nameRem(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM lessons WHERE id="+id);
    }

    // ============================================================================================
    // Table manage

    public void tableSet(int day, int number, int lesson) {
        SQLiteDatabase db = this.getWritableDatabase();
        if(tableIsDefined(day,number)) db.execSQL("UPDATE lessonsTable SET lesson="+lesson+" WHERE day="+day+" AND number="+number);
        else db.execSQL("INSERT INTO lessonsTable(day,number,lesson) VALUES ("+day+","+number+","+lesson+")");
    }

    public void tableRem(int day, int n) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM lessonsTable WHERE day="+day+" AND number="+n);
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
        SQLiteDatabase db = getWritableDatabase();
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

    // ============================================================================================
    // Preferences

    public void setPref(String key, String value) {
        SQLiteDatabase db = getWritableDatabase();
        if(this.getPref(key) == null) {
            db.execSQL("INSERT INTO preferences(name,value) VALUES (\""+key+"\",\""+value+"\")");
        } else {
            db.execSQL("UPDATE TABLE preferences SET value=\""+value+"\" WHERE name=\""+key+"\"");
        }
    }

    public String getPref(String key) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM preferences WHERE name=\""+key+"\"", null);
        if(cursor.moveToFirst()) {
            String value = cursor.getString(cursor.getColumnIndex("value"));
            cursor.close();
            return value;
        } else {
            cursor.close();
            return null;
        }
    }
}
