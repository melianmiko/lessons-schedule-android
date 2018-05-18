package ml.mhbrgn.schooljournal;

import android.content.Context;

import java.util.Calendar;

public class TableTools {
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

}
