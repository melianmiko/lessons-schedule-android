package ml.mhbrgn.schooljournal;

import android.content.Context;

public class LessonsTable {

    static LessonsTableItem[] getDay(Context context, int day) {
        // Get number of lessons in day
        int inDay = LessonTime.getTimesArray(context).length;
        // Create matrix
        LessonsTableItem[] content = new LessonsTableItem[inDay];

        for(int num = 0; num < inDay; num++)
            content[num] = new LessonsTableItem(context,day,num+1);

        return content;
    }

}
