package ml.mhbrgn.LessonsSchedule;

import android.content.Context;

class LessonsTable {

    static LessonsTableItem[] getDay(Context context, int day) {
        // Get number of lessons in day
        int inDay = LessonTime.getTimesArray(context).length;

        LessonsTableItem[] content = new LessonsTableItem[inDay];

        for(int num = 0; num < inDay; num++)
            content[num] = new LessonsTableItem(context,day,num);

        return content;
    }

}
