package ml.mhbrgn.schooljournal;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

@SuppressWarnings("WeakerAccess")
public class LessonName {

    static LessonName[] getNamesArray(Context context) {
        // DB connection
        LessonsDB con = new LessonsDB(context);
        SQLiteDatabase db = con.getWritableDatabase();
        // Read all!
        ArrayList<LessonName> out = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT * FROM lessons",null);
        Log.i("SQL",c.toString());
        if(c.moveToFirst()) {
            out.add(new LessonName(context,c.getInt(c.getColumnIndex("id"))));

            while (c.moveToNext()) {
                int id = c.getInt(c.getColumnIndex("id"));
                Log.i("DEBUG_ID","Found lesson ID:"+id);
                out.add(new LessonName(context,c.getInt(c.getColumnIndex("id"))));
            }
        }
        c.close();
        return out.toArray(new LessonName[out.size()]);
    }

    private Context context;
    private LessonsDB lessonDB;

    boolean isDefined = false;
    boolean isSaved = false;
    int id = -1;
    String name;

    boolean defineCheck() {
        if(this.id == -1) return false;
        SQLiteDatabase db = lessonDB.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT name FROM lessons WHERE id="+this.id,null);
        if(cursor.moveToFirst()) {
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }

    // Get from storage
    boolean storageGet() {
        if(this.id == -1) return false;
        SQLiteDatabase db = lessonDB.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT name FROM lessons WHERE id="+this.id,null);
        if(cursor.moveToFirst()) {
            this.name = cursor.getString(cursor.getColumnIndex("name"));
            this.isDefined = true;
            this.isSaved = true;
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }

    // Remove from storage
    void remove() {
        SQLiteDatabase db = lessonDB.getWritableDatabase();
        if(this.defineCheck()) {
            db.execSQL("DELETE FROM lessons WHERE id="+this.id);
        }
    }

    // Save to storage
    void write() {
        SQLiteDatabase db = lessonDB.getWritableDatabase();
        if(this.defineCheck()) {
            // Update
            db.execSQL("UPDATE lessons SET name=\""+this.name+"\" WHERE id="+this.id);
        } else {
            // Insert
            db.execSQL("INSERT INTO lessons(name) VALUES (\""+this.name+"\")");
            // Get back my ID!
            Cursor c = db.rawQuery("SELECT id FROM lessons WHERE name=\""+this.name+"\"",null);
            c.moveToFirst();
            this.id = c.getInt(c.getColumnIndex("id"));
            Log.i("TEST","New lesson insert ID:"+this.id+", NAME: "+this.name);
            c.close();
        }
    }

    // Create from ID and NAME
    LessonName(Context context, int id, String name) {
        this.context = context;
        this.lessonDB = new LessonsDB(context);
        this.isDefined = true;
        this.name = name;
        this.id = id;
    }

    // Create new
    LessonName(Context context, String name) {
        this.context = context;
        this.lessonDB = new LessonsDB(context);
        this.isDefined = true;
        this.name = name;
    }

    // Load from id
    LessonName(Context context, int id) {
        this.context = context;
        this.lessonDB = new LessonsDB(context);
        this.id = id;
        this.storageGet();
    }
}
