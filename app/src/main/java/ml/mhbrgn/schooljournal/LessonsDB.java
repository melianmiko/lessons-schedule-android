package ml.mhbrgn.schooljournal;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LessonsDB extends SQLiteOpenHelper {
    private Context context;

    int[][] times = new int[][]{
            new int[]{500,540}, new int[]{545,585}, new int[]{600,640}, new int[]{655,695},
            new int[]{705,745}, new int[]{755,795}, new int[]{805,845}, new int[]{855,895}
    };

    private int[] lessons = new int[]{
            R.string.algebra, R.string.english, R.string.literature,
            R.string.geometry, R.string.biology
    };

    LessonsDB(Context context) {
        super(context, "lessons.db", null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE lessons(id INTEGER PRIMARY KEY, name TEXT)");
        db.execSQL("CREATE TABLE times(id INT, startTime INT, endTime INT)");
        db.execSQL("CREATE TABLE lessonsTable(day INT, number INT, lesson INT, auditory INT)");
        db.execSQL("CREATE TABLE preferences(name TINYTEXT, value TINYTEXT)");

        // Set work days pref
        int workDays = 5;
        db.execSQL("INSERT INTO preferences(name,value) VALUES (\"workDays\",\""+ workDays +"\")");

        // Setup times
        for(int i = 0; i < times.length; i++) {
            int[] values = times[i];
            db.execSQL("INSERT INTO times(id,startTime,endTime) VALUES ("+(i+1)+","+values[0]+","+values[1]+")");
        }

        // Setup lesson names
        for (int c : lessons) {
            String cur = context.getResources().getString(c);
            db.execSQL("INSERT INTO lessons(name) VALUES (\"" + cur + "\")");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
