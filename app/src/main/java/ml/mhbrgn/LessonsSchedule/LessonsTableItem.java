package ml.mhbrgn.LessonsSchedule;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@SuppressWarnings("WeakerAccess")
class LessonsTableItem {
    private final DataStorage dataCon;
    int day = -1; int num = -1;
    int lesson_id = -1; String lesson;
    boolean defined = false;

    // Get from storage
    LessonsTableItem(Context context, int day, int num) {this(new DataStorage(context),day,num);}
    LessonsTableItem(DataStorage dataCon, int day, int num) {
        this.dataCon = dataCon;
        this.day = day;
        this.num = num;

        // Get from dataCon!
        try {
            JSONArray table = dataCon.getTable(); // Table
            JSONArray tableDay = table.getJSONArray(day); // Day
            JSONObject item = tableDay.getJSONObject(num); // Item

            this.lesson_id = item.getInt("lesson_id");
            this.defined = true;
            this.lesson = (new LessonName(dataCon,this.lesson_id)).name;

        } catch (JSONException ignore) { }
    }

    // Create new entry
    LessonsTableItem(Context context, int day, int num, int lesson_id) {this(new DataStorage(context),day,num,lesson_id);}
    LessonsTableItem(DataStorage dataCon, int day, int num, int lesson_id) {
        this.dataCon = dataCon;
        this.day = day;
        this.num = num;
        this.lesson_id = lesson_id;
        this.defined = true;
        this.lesson = (new LessonName(dataCon,this.lesson_id)).name;
    }

    void remove() {
        try{
            JSONArray table = dataCon.getTable();
            JSONArray tableDay = table.getJSONArray(day);

            tableDay.put(num,JSONObject.NULL);
            table.put(day,tableDay);
            dataCon.setTable(table);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void write() {
        try {
            JSONArray table = dataCon.getTable();
            if(table.isNull(day)) table.put(day, new JSONArray());
            JSONArray tableDay = table.getJSONArray(day);
            if(tableDay.isNull(num)) tableDay.put(num, new JSONObject());
            JSONObject item = tableDay.getJSONObject(num);

            item.put("lesson_id",lesson_id);
            tableDay.put(num,item);
            table.put(day,tableDay);
            Log.i("Debug",table.toString());
            dataCon.setTable(table);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    LessonTime getTime() {
        return new LessonTime(dataCon,num);
    }

}
