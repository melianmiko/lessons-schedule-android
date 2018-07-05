package ru.mhbrgn.LessonsSchedule;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

@SuppressWarnings("unused")
class DataStorage {
    private final String FILENAME = "data.json";

    private final Context context;
    private JSONObject root;

    public String toString() {
        return root.toString();
    }

    private boolean fileWrite() {
        try {
            BufferedWriter o = new BufferedWriter(new OutputStreamWriter(context.openFileOutput(FILENAME, Context.MODE_PRIVATE)));
            o.write(root.toString());
            o.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private boolean fileCreate() {
        root = new JSONObject();
        return fileWrite();
    }

    private boolean fileOpen() {
        try {
            BufferedReader i = new BufferedReader(new InputStreamReader(context.openFileInput(FILENAME)));
            StringBuilder fileContent = new StringBuilder();

            String str;
            while ((str = i.readLine()) != null) { fileContent.append(str); }
            String out = fileContent.toString();

            root = new JSONObject(out);
            return true;

        } catch (FileNotFoundException | JSONException e) {
            return fileCreate();
        } catch (IOException e) {
            return false;
        }
    }

    DataStorage(Context context) {
        this.context = context;

        boolean status = fileOpen();
        if(!status) {
            Toast.makeText(context, "File error!", Toast.LENGTH_SHORT).show();
        } else {
            try {

                if (root.isNull("lessons")) {
                    root.put("lessons", new JSONArray());
                    LessonName.restoreDefaults(this,context);
                }
                if (root.isNull("times")) {
                    root.put("times", new JSONArray());
                    LessonTime.restoreDefaults(this);
                }
                if (root.isNull("preferences")) root.put("preferences",new JSONObject());
                if (root.isNull("table")) root.put("table",new JSONArray());

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    String getJSON() {
        return root.toString();
    }

    // =============================================================================================
    // Adapters
    JSONArray getTimes() {
        try {
            return root.getJSONArray("times");
        } catch (JSONException e) {
            return null;
        }
    }

    void setTimes(JSONArray times) {
        try {
            root.put("times",times);
            fileWrite();
            Log.i("TimeStorage", "Saved: "+this.toString());
        } catch (JSONException e) {
            Toast.makeText(context, "Times write failed", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    JSONArray getNames() {
        try {
            return root.getJSONArray("lessons");
        } catch (JSONException e) {
            return new JSONArray();
        }
    }

    void setNames(JSONArray names) {
        try {
            root.put("lessons",names);
            fileWrite();
            Log.i("lessonsStorage", "Saved: "+this.toString());
        } catch (JSONException e) {
            Toast.makeText(context, "Names write failed", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    JSONArray getTable() {
        try {
            return root.getJSONArray("table");
        } catch (JSONException e) {
            return new JSONArray();
        }
    }

    void setTable(JSONArray table) {
        try {
            root.put("table",table);
            fileWrite();
            Log.i("TableStorage", "Table updated");
        } catch (JSONException e) {
            Toast.makeText(context, "Error saving table content", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    void loadJSON(String json) throws JSONException {
        root = new JSONObject(json);
        fileWrite();
    }
}





