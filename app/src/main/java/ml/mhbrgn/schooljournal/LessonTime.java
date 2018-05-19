package ml.mhbrgn.schooljournal;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

@SuppressWarnings("WeakerAccess")
public class LessonTime {

    static void restoreDefaults(Context context) {
        // Create DB connection
        LessonsDB con = new LessonsDB(context);
        SQLiteDatabase db = con.getWritableDatabase();

        // Copy from con.times to data
        db.execSQL("DELETE FROM times");
        for(int[] i : con.times) (new LessonTime(context,i[0],i[1])).write();
    }

    static LessonTime[] getTimesArray(Context context) {
        // Create DB connection
        LessonsDB con = new LessonsDB(context);
        SQLiteDatabase db = con.getWritableDatabase();
        // Define array
        ArrayList<LessonTime> out = new ArrayList<>();
        // Get All IDS
        Cursor c = db.rawQuery("SELECT id FROM times",null);
        if(c.moveToFirst()) {
            out.add(new LessonTime(context,c.getInt(c.getColumnIndex("id"))));
            while (c.moveToNext()) out.add(new LessonTime(context,c.getInt(c.getColumnIndex("id"))));
        }
        c.close();
        return out.toArray(new LessonTime[out.size()]);
    }

    LessonsDB lessonsDB;
    Context context;
    boolean isDefined = false;
    boolean isSaved = false;
    int number = -1;
    int startTime;
    int endTime;

    // To string converters
    String getTablePrefix() {
        return number+" "+startTimeString();
    }

    String startTimeString() {
        int h = (int) Math.floor(startTime/60);
        int m = (int) Math.floor(startTime-h*60);
        // 2 - To string
        String sh = String.valueOf(h);
        String sm = String.valueOf(m);
        // 3 - Length fix
        if(sh.length() < 2) sh = "0"+sh;
        if(sm.length() < 2) sm = "0"+sm;

        return sh+":"+sm;
    }

    int startHours() {return (int) Math.floor(startTime/60);}
    int startMinutes() {return (int) Math.floor(startTime-(Math.floor(startTime/60))*60);}
    int endHours() {return (int) Math.floor(endTime/60);}
    int endMinutes() {return (int) Math.floor(endTime-(Math.floor(endTime/60))*60);}

    String endTimeString() {
        int h = (int) Math.floor(endTime/60);
        int m = (int) Math.floor(endTime-h*60);
        // 2 - To string
        String sh = String.valueOf(h);
        String sm = String.valueOf(m);
        // 3 - Length fix
        if(sh.length() < 2) sh = "0"+sh;
        if(sm.length() < 2) sm = "0"+sm;

        return sh+":"+sm;
    }

    // Remove from storage
    void remove() {
        if(this.checkDefined()) {
            SQLiteDatabase db = lessonsDB.getWritableDatabase();
            db.execSQL("DELETE FROM times WHERE id="+this.number);
            // Rebase storage
            LessonTime[] times = LessonTime.getTimesArray(this.context);
            db.execSQL("DELETE FROM times");
            for(LessonTime i : times) (new LessonTime(this.context, i.startTime, i.endTime)).write();
        }
    }

    // Get from storage
    void storageGet() {
        SQLiteDatabase db = lessonsDB.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM times WHERE id="+this.number,null);
        if(cursor.moveToFirst()) {
            this.startTime = cursor.getInt(cursor.getColumnIndex("startTime"));
            this.endTime = cursor.getInt(cursor.getColumnIndex("endTime"));
            this.isDefined = true;
            this.isSaved = true;
            cursor.close();
            return;
        }
        cursor.close();
    }

    // Check defined
    boolean checkDefined() {
        SQLiteDatabase db = lessonsDB.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM times WHERE id="+this.number,null);
        if(cursor.moveToFirst()) {
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }


    // Write to storage
    void write() {
        SQLiteDatabase db = lessonsDB.getWritableDatabase();
        if(this.number == -1) this.number = LessonTime.getTimesArray(context).length+1;
        if(checkDefined()) {
            // Update
            db.execSQL("UPDATE times SET startTime="+this.startTime+", endTime="+this.endTime+" WHERE id="+this.number);
            this.isSaved = true;
        } else {
            // Insert
            db.execSQL("INSERT INTO times(id,startTime,endTime) VALUES ("+this.number+","+this.startTime+","+this.endTime+")");
        }
    }

    // Create new time
    LessonTime(Context context, int start, int end) {
        this.lessonsDB = new LessonsDB(context);
        this.context = context;
        this.isDefined = true;
        this.startTime = start;
        this.endTime = end;
    }

    // Create from ID
    LessonTime(Context context, int id) {
        this.lessonsDB = new LessonsDB(context);
        this.context = context;
        this.number = id;
        this.storageGet();
    }
}

