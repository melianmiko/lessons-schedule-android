package ml.mhbrgn.schooljournal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.design.widget.BottomSheetDialog;
import android.view.LayoutInflater;
import android.view.View;

class WeekendSetupUI {
    WeekendSetupUI(final Context context) {
        SharedPreferences prefs = context.getSharedPreferences("prefs",Context.MODE_PRIVATE);
        // Create menu
        final BottomSheetDialog dia = new BottomSheetDialog(context);
        @SuppressLint("InflateParams")
        View v = LayoutInflater.from(context).inflate(R.layout.weekend_select_ui,null);
        dia.setContentView(v);
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
