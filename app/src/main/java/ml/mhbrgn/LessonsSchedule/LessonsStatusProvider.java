package ml.mhbrgn.LessonsSchedule;

import android.content.Context;
import android.widget.TextView;

class LessonsStatusProvider {
    TextView view;
    Context context;

    LessonsStatusProvider(Context context, TextView textView) {
        this.view = textView;
        this.context = context;
        update();
    }

    static int getCurrentLesson(Context context) {
        int time = TableTools.getMinutesFromDayStart();
        LessonTime[] times = LessonTime.getTimesArray(context);
        return findCurrentLesson(time,times);
    }

    static private int findCurrentLesson(int time, LessonTime[] times) {
        for(LessonTime t : times) {
            if(time >= t.startTime && time < t.endTime) return t.number;
        }

        return -1;
    }

    private int findPreviousLesson(int time, LessonTime[] times) {
        int prev = -1;
        for(LessonTime t : times) {
            if(time > t.endTime) prev = t.number;
        }
        return prev;
    }

    void update() {
        int time = TableTools.getMinutesFromDayStart();
        int current_day = TableTools.getCurrentDay();
        LessonsTableItem[] data = LessonsTable.getDay(context,current_day);
        LessonTime[] times = LessonTime.getTimesArray(context);

        int current = findCurrentLesson(time, times);
        if(current >= data.length) current = -1;
        int prev = findPreviousLesson(time, times);
        if(prev >= data.length) prev = -1;

        if(data.length < 1) {
            // No lessons today
            view.setText(R.string.status_no_lessons);
        } else if(time > data[data.length-1].getTime().endTime) {
            // Lessons complete
            view.setText(R.string.status_complete);
        } else if(current != -1) {

            view.setText(String.format(
                    context.getResources().getString(R.string.status_time_to_left),
                    data[current].lesson, data[current].getTime().endTime-time
            ));

        } else {
            int next = prev+1;
            if(times[next] == null || next >= data.length) {
                view.setText(R.string.status_error);return;
            }

            view.setText(String.format(
                    context.getResources().getString(R.string.status_time_to_start),
                    data[next].lesson, data[next].getTime().startTime-time
            ));

        }
    }
}
