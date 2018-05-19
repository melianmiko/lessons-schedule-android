package ml.mhbrgn.schooljournal;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Context;
import android.support.design.widget.BottomSheetDialog;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

public class TimeEditUI {
    static class OnCompleteListener {
        OnCompleteListener() {
            // Noting to do
        }
        void onComplete(LessonTime time) {

        }
    }

    private Context context;
    private OnCompleteListener complete;
    private LessonTime time;
    private View layout;
    private BottomSheetDialog dialog;

    @SuppressLint("InflateParams")
    TimeEditUI(Context context, LessonTime time) {
        this.context = context;
        this.time = time;
        this.complete = new OnCompleteListener();
        // Inflate layout
        layout = LayoutInflater.from(context).inflate(R.layout.time_edit_ui, null, false);
        layoutFill();
        updateLayout();
        // Create button sheet
        dialog = new BottomSheetDialog(context);
        dialog.setContentView(layout);
    }

    private void layoutFill() {
        // Buttons
        layout.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.hide();
            }
        });
        layout.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.hide();
                complete.onComplete(time);
            }
        });

        // Time picker
        layout.findViewById(R.id.start_time_root).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog td = new TimePickerDialog(new ContextThemeWrapper(context,R.style.Dialog), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        time.startTime = hourOfDay*60+minute;
                        updateLayout();
                    }
                },time.startHours(),time.startMinutes(),true);
                td.setCancelable(true);
                td.setTitle("");
                td.show();
            }
        });

        layout.findViewById(R.id.end_time_root).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog td = new TimePickerDialog(new ContextThemeWrapper(context,R.style.Dialog), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        time.endTime = hourOfDay*60+minute;
                        updateLayout();
                    }
                },time.endHours(),time.endMinutes(),true);
                td.setCancelable(true);
                td.setTitle("");
                td.show();
            }
        });
    }

    private void updateLayout() {
        TextView start = layout.findViewById(R.id.start_time_text);
        TextView end = layout.findViewById(R.id.end_time_text);

        String st = String.format(context.getResources().getString(R.string.start_time_ph),
                time.startTimeString());

        String et = String.format(context.getResources().getString(R.string.end_time_ph),
                time.endTimeString());

        start.setText(st);
        end.setText(et);
    }

    void show() {
        dialog.show();
    }

    void setOnCompleteListener(OnCompleteListener listener) {
        this.complete = listener;
    }
}
