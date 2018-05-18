package ml.mhbrgn.schooljournal;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

@SuppressWarnings({"FieldCanBeLocal", "WeakerAccess"})
public class LessonsTableItem {
    private LessonsDB lessonsDB;
    int day; int num;
    int lesson_id;
    String lesson;
    LessonTime time;
    boolean defined = false;

    // Remove from table
    void remove() {
        SQLiteDatabase db = lessonsDB.getWritableDatabase();
        db.execSQL("DELETE FROM lessonsTable WHERE day="+day+" AND number="+num);
    }

    // Check defined
    boolean checkDefined() {
        SQLiteDatabase db = lessonsDB.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM lessonsTable WHERE day="+day+" AND number="+num,null);
        if(c.moveToFirst()) {
            c.close();
            return true;
        }
        c.close();
        return false;
    }

    // Write to storage
    void write() {
        SQLiteDatabase db = lessonsDB.getWritableDatabase();
        if(checkDefined()) {
            // Update
            db.execSQL("UPDATE lessonsTable SET lesson="+lesson_id+" WHERE day="+day+" AND number="+num);
        } else {
            // Insert
            db.execSQL("INSERT INTO lessonsTable(day,number,lesson) VALUES ("+day+","+num+","+lesson_id+")");
        }
    }

    // Get data from storage
    private void storageGet() {
        SQLiteDatabase db = lessonsDB.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM lessonsTable WHERE day="+day+" AND number="+num,null);
        if(c.moveToFirst()) {
            this.lesson_id = c.getInt(c.getColumnIndex("lesson"));
            this.defined = true;
            c.close();
            return;
        }
        c.close();
    }

    private void updateLessonString(Context context) {
        LessonName name = new LessonName(context,this.lesson_id);
        this.lesson = name.name;
    }

    // Create new item
    LessonsTableItem(Context context, int day, int number, int lesson) {
        this.lessonsDB = new LessonsDB(context);
        this.time = new LessonTime(context,number);
        this.day = day;
        this.num = number;
        this.lesson_id = lesson;
        this.defined = true;
        this.updateLessonString(context);
    }

    // Load from storage
    LessonsTableItem(Context context, int day, int number) {
        this.lessonsDB = new LessonsDB(context);
        this.time = new LessonTime(context,number);
        this.day = day;
        this.num = number;
        this.storageGet();
        this.updateLessonString(context);
    }
}
