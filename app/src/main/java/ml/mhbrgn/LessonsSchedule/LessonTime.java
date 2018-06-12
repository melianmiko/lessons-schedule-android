package ml.mhbrgn.LessonsSchedule;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

//@SuppressWarnings("WeakerAccess")
@SuppressWarnings("WeakerAccess")
class LessonTime {
    private final DataStorage dataCon;
    int number = -1;
    int startTime;
    int endTime;

    static LessonTime[] getTimesArray(Context context) {return getTimesArray(new DataStorage(context));}
    static LessonTime[] getTimesArray(DataStorage dataCon) {
        ArrayList<LessonTime> out = new ArrayList<>();
        JSONArray times = dataCon.getTimes();
        for(int i = 0; i < times.length(); i++) out.add(new LessonTime(dataCon, i));
        return out.toArray(new LessonTime[out.size()]);
    }

    // Setup only ID
    LessonTime(DataStorage dataCon, int id) {
        this.dataCon = dataCon;
        this.number = id;
        dataRead();
    }

    LessonTime(Context context, int id) { this(new DataStorage(context), id); }

    // Setup times only
    LessonTime(DataStorage dataCon, int startTime, int endTime) {
        this.dataCon = dataCon;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    LessonTime(Context context, int startTime, int endTime) {
        this(new DataStorage(context),startTime,endTime);
    }

    void write() {
        JSONObject out = new JSONObject();

        if(number == -1) number = dataCon.getTimes().length();

        try {

            out.put("startTime", startTime);
            out.put("endTime",endTime);

            JSONArray times = dataCon.getTimes();
            times.put(number, out);

            dataCon.setTimes(times);

        } catch (JSONException ignored) { }
    }

    void remove() {
        JSONArray in = dataCon.getTimes();

        if(number == -1) return;
        if(in.isNull(number)) return;

        try {

            in.put(number, JSONObject.NULL);
            // Do rebase
            JSONArray out = new JSONArray();
            for(int i = 0; i < in.length(); i++) {
                if(!in.isNull(i)) out.put(in.getJSONObject(i));
            }

            dataCon.setTimes(out);

        } catch (JSONException ignored) { }
    }

    // Private (technical)

    private void dataRead() {
        if(number == -1) return; // No id
        JSONArray times = dataCon.getTimes();
        if(times == null) return;
        if(times.isNull(number)) return;
        // Get!
        try {
            JSONObject time = times.getJSONObject(number);
            startTime = time.getInt("startTime");
            endTime = time.getInt("endTime");
        } catch (JSONException e) { e.printStackTrace(); }
    }

    // Converters
    String getTablePrefix() {
        return (number+1)+" "+startTimeString();
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

    static void restoreDefaults(Context context) {restoreDefaults(new DataStorage(context));}
    static void restoreDefaults(DataStorage dataCon) {
        int[][] times = new int[][]{
                new int[]{500,540}, new int[]{545,585}, new int[]{600,640}, new int[]{655,695},
                new int[]{705,745}, new int[]{755,795}, new int[]{805,845}, new int[]{855,895}
        };

        dataCon.setTimes(new JSONArray());

        for (int[] time : times) (new LessonTime(dataCon, time[0], time[1])).write();
    }
}

