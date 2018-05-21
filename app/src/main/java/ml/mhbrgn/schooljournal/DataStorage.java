package ml.mhbrgn.schooljournal;

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

    private Context context;
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
        } catch (FileNotFoundException e) {
            return false;
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

        } catch (FileNotFoundException e) {
            return fileCreate();
        } catch (IOException e) {
            return false;
        } catch (JSONException e) {
            return fileCreate();
        }
    }

    DataStorage(Context context) {
        this.context = context;

        boolean status = fileOpen();
        if(!status) {
            Toast.makeText(context, "File error!", Toast.LENGTH_SHORT).show();
        } else {
            try {

                if (root.isNull("lessons")) root.put("lessons", new JSONArray());
                if (root.isNull("times")) root.put("times", new JSONArray());
                if (root.isNull("preferences")) root.put("preferences",new JSONObject());
                if (root.isNull("table")) root.put("table",new JSONArray());

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
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
}





