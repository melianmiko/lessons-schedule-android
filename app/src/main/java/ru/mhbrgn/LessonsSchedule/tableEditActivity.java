package ru.mhbrgn.LessonsSchedule;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.Collections;

public class tableEditActivity extends AppCompatActivity {
    class DailyTableAdapter extends ArrayAdapter<DailyTableAdapter.ViewHolder> {
        final LessonsTableItem[] mData;

        DailyTableAdapter(LessonsTableItem[] data) {
            super(tableEditActivity.this, R.layout.table_edit_list_item);
            mData = data;
        }

        class ViewHolder {
            final View root; final TextView timeBox; final TextView nameBox;
            ViewHolder(View v) {
                root = v;
                timeBox = v.findViewById(R.id.time_view);
                nameBox = v.findViewById(R.id.name_view);
            }
        }

        @Override
        public int getCount() {
            return mData.length;
        }

        @NonNull
        @Override
        public View getView(final int position, View currentView, @NonNull final ViewGroup parent) {
            View v = currentView;
            if(v == null) v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.table_edit_list_item, parent, false);

            ViewHolder holder = new ViewHolder(v);

            final LessonsTableItem data = mData[position];

            holder.timeBox.setText(data.getTime().getTablePrefix());

            if(data.defined) {
                holder.nameBox.setText(String.valueOf(data.lesson));
                holder.nameBox.setTextColor(ContextCompat.getColor(tableEditActivity.this,R.color.black));
            } else {
                holder.nameBox.setText(R.string.null_list_item);
                holder.nameBox.setTextColor(ContextCompat.getColor(tableEditActivity.this,R.color.grey));
            }

            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog dialog = (new AlertDialog.Builder(tableEditActivity.this)).create();
                    dialog.setCancelable(true);

                    LessonName[] names = LessonName.getNamesArray(tableEditActivity.this);

                    ListView lv = (ListView) getLayoutInflater().inflate(R.layout.names_list_listview,
                            (ViewGroup) tableEditActivity.this.findViewById(R.id.root), false);

                    NamesListAdapter ad = new NamesListAdapter(names, dialog, data);
                    lv.setAdapter(ad);
                    dialog.show();
                    dialog.setContentView(lv);

                }
            });

            return v;
        }
    }
    // ====================================================================================
    class NamesListAdapter extends ArrayAdapter<NamesListAdapter.ViewHolder> {
        final LessonName[] mData;
        final AlertDialog dialog;
        final LessonsTableItem callback_item;

        NamesListAdapter(LessonName[] data, AlertDialog dialog, LessonsTableItem item) {
            super(tableEditActivity.this, R.layout.names_droplist_item);
            // Build a array and add zero item
            ArrayList<LessonName> names = new ArrayList<>();
            names.add(new LessonName(tableEditActivity.this,-1,getString(R.string.clean)));
            Collections.addAll(names, data);

            mData = names.toArray(new LessonName[names.size()]);
            callback_item = item;
            this.dialog = dialog;
        }

        class ViewHolder {
            final View root; final TextView text;
            ViewHolder(View itemView) {
                root = itemView;
                text = itemView.findViewById(R.id.name_view);
            }
        }

        @NonNull
        @Override
        public View getView(final int position, View currentView, @NonNull final ViewGroup parent) {
            View v = currentView;
            if(v == null) v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.names_droplist_item, parent, false);

            ViewHolder holder = new ViewHolder(v);

            final int nameID = mData[position].id;
            holder.text.setText(mData[position].name);
            // Colorize active item
            if(callback_item.lesson_id == mData[position].id) holder.text.setTextColor(ContextCompat.getColor(tableEditActivity.this,R.color.colorAccent));
            else holder.text.setTextColor(ContextCompat.getColor(tableEditActivity.this,R.color.black));

            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.hide();
                    int day = callback_item.day;
                    int num = callback_item.num;
                    LessonsTableItem ti = new LessonsTableItem(tableEditActivity.this,day,num,nameID);
                    if(nameID < 0) ti.remove(); // Remove from table
                    else ti.write(); // Save
                    tableEditActivity.this.updateList();
                }
            });

            return v;
        }

        @Override
        public int getCount() {
            return mData.length;
        }
    }
    // ====================================================================================
    static private int current_day = TableTools.getCurrentDay();
    static private int work_days = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_edit);
        work_days = getSharedPreferences("prefs",MODE_PRIVATE).getInt("work_days",5);
        if(current_day > work_days) current_day = 1;

        findViewById(R.id.day_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current_day--;
                if(current_day < 1) current_day = work_days;
                updateList();
            }
        });

        findViewById(R.id.day_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current_day++;
                if(current_day > work_days) current_day = 1;
                updateList();
            }
        });

        this.updateList();
    }

    private void updateList() {
        // 1 - Update day in box
        ( (TextView) findViewById(R.id.text_weekday) ).setText(TableTools.getDayName(current_day,this));

        // 2 - Create table
        LessonsTableItem[] data = LessonsTable.getDay(this, current_day);
        ListView lv = findViewById(R.id.table_edit_list);
        DailyTableAdapter ad = new DailyTableAdapter(data);
        lv.setAdapter(ad);
    }
}
