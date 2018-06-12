package ml.mhbrgn.LessonsSchedule;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

@SuppressWarnings("WeakerAccess")
public class LessonName {

    private final DataStorage dataCon;
    String name; int id = -1;

    LessonName(Context context, int id) { this(new DataStorage(context), id); }
    LessonName(DataStorage storage, int id) {
        this.id = id;
        this.dataCon = storage;
        // Get string
        try {
            JSONArray names = storage.getNames();
            name = names.getString(id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    LessonName(Context context, @SuppressWarnings("SameParameterValue") String name) {this(new DataStorage(context),name);}
    LessonName(DataStorage storage, String name) {
        this.dataCon = storage;
        this.name = name;
    }

    LessonName(Context context, int id, String name) {this(new DataStorage(context), id, name);}
    LessonName(DataStorage storage, int id, String name) {
        this.id = id;
        this.name = name;
        this.dataCon = storage;
    }

    void write() {
        JSONArray arr = dataCon.getNames();

        if(id == -1) id = arr.length();

        try {
            arr.put(id,name);
            dataCon.setNames(arr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void remove() {
        if(id == -1) return;
        JSONArray arr = dataCon.getNames();
        try {
            arr.put(id,"");
            dataCon.setNames(arr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    static LessonName[] getNamesArray(Context context) {
        return getNamesArray(new DataStorage(context));
    }
    static LessonName[] getNamesArray(DataStorage dataCon) {
        JSONArray names = dataCon.getNames();
        ArrayList<LessonName> out = new ArrayList<>();

        for(int i = 0; i < names.length(); i++) {
            LessonName ln = new LessonName(dataCon, i);
            if(ln.name.equals("")) continue;
            out.add(ln);
        }

        return out.toArray(new LessonName[out.size()]);
    }

    static void restoreDefaults(Context context) {
        restoreDefaults(new DataStorage(context),context);
    }
    static void restoreDefaults(DataStorage ds,Context context) {
        String[] lessons = context.getResources().getStringArray(R.array.lesson_names_def);

        ds.setNames(new JSONArray());

        for(String i : lessons) {
            LessonName ln = new LessonName(ds,i);
            ln.write();
        }

    }
}
