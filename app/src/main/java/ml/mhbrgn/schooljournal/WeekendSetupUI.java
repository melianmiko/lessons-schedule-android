package ml.mhbrgn.schooljournal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

class WeekendSetupUI {
    WeekendSetupUI(final Context context) {
        SharedPreferences prefs = context.getSharedPreferences("prefs",Context.MODE_PRIVATE);
        int work_days = prefs.getInt("work_days",5);
        int colorAccent = ContextCompat.getColor(context,R.color.colorAccent);

        // Create menu
        final BottomSheetDialog dia = new BottomSheetDialog(context);
        @SuppressLint("InflateParams")
        View v = LayoutInflater.from(context).inflate(R.layout.weekend_select_ui,null);
        dia.setContentView(v);

        // Select active
        switch (work_days) {
            case 5: ((TextView)v.findViewById(R.id.weekend_0)).setTextColor(colorAccent);break;
            case 6: ((TextView)v.findViewById(R.id.weekend_1)).setTextColor(colorAccent);break;
            case 7: ((TextView)v.findViewById(R.id.weekend_2)).setTextColor(colorAccent);break;
        }

        // Setup
        v.findViewById(R.id.weekend_0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setWeekend(5,context);
                dia.hide();
            }
        });

        v.findViewById(R.id.weekend_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setWeekend(6,context);
                dia.hide();
            }
        });

        v.findViewById(R.id.weekend_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setWeekend(7,context);
                dia.hide();
            }
        });

        dia.show();
    }
    private static void setWeekend(int value, Context context) {
        SharedPreferences prefs = context.getSharedPreferences("prefs",Context.MODE_PRIVATE);
        prefs.edit().putInt("work_days",value).apply();
    }
}
