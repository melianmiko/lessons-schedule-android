package ml.mhbrgn.LessonsSchedule;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;


public class tableEditActivity extends AppCompatActivity {
    class DailyTableAdapter extends RecyclerView.Adapter<DailyTableAdapter.ViewHolder> {
        final LessonsTableItem[] mData;

        DailyTableAdapter(LessonsTableItem[] data) {
            mData = data;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final View root; final TextView timeBox; final TextView nameBox;
            ViewHolder(View v) {
                super(v);
                root = v;
                timeBox = v.findViewById(R.id.time_view);
                nameBox = v.findViewById(R.id.name_view);
            }
        }

        @NonNull
        @Override
        public DailyTableAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.table_edit_list_item, parent, false);

            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull DailyTableAdapter.ViewHolder holder, int position) {
            final LessonsTableItem data = mData[position];

            holder.timeBox.setText(data.getTime().getTablePrefix());

            if(data.defined)
                holder.nameBox.setText(String.valueOf(data.lesson));
            else {
                holder.nameBox.setText(R.string.null_list_item);
                holder.nameBox.setTextColor(ContextCompat.getColor(tableEditActivity.this,R.color.grey));
            }

            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    BottomSheetDialog dialog = new BottomSheetDialog(tableEditActivity.this);
                    dialog.setCancelable(true);

                    LessonName[] names = LessonName.getNamesArray(tableEditActivity.this);

                    RecyclerView rv = (RecyclerView) getLayoutInflater().inflate(R.layout.names_list_recycleview,
                            (ViewGroup) tableEditActivity.this.findViewById(R.id.root), false);

                    RecyclerView.LayoutManager lm = new LinearLayoutManager(tableEditActivity.this);
                    RecyclerView.Adapter adapter = new NamesListAdapter(names, dialog, data);

                    rv.setLayoutManager(lm);
                    rv.setAdapter(adapter);

                    dialog.setContentView(rv);

                    dialog.show();

                }
            });
        }

        @Override
        public int getItemCount() {
            return mData.length;
        }
    }

    // ====================================================================================
    class NamesListAdapter extends RecyclerView.Adapter<NamesListAdapter.ViewHolder> {
        final LessonName[] mData;
        final BottomSheetDialog dialog;
        final LessonsTableItem callback_item;

        NamesListAdapter(LessonName[] data, BottomSheetDialog dialog, LessonsTableItem item) {
            // Build a array and add zero item
            ArrayList<LessonName> names = new ArrayList<>();
            names.add(new LessonName(tableEditActivity.this,-1,getString(R.string.clean)));
            Collections.addAll(names, data);

            mData = names.toArray(new LessonName[names.size()]);
            callback_item = item;
            this.dialog = dialog;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final View root; final TextView text;
            ViewHolder(View itemView) {
                super(itemView);
                root = itemView;
                text = itemView.findViewById(R.id.name_view);
            }
        }

        @NonNull
        @Override
        public NamesListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.names_droplist_item, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull NamesListAdapter.ViewHolder holder, int position) {
            final int nameID = mData[position].id;
            holder.text.setText(mData[position].name);
            // Colorize active item
            if(callback_item.lesson_id == mData[position].id) holder.text.setTextColor(ContextCompat.getColor(tableEditActivity.this,R.color.colorAccent));

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
        }

        @Override
        public int getItemCount() {
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
        RecyclerView.Adapter adapter = new DailyTableAdapter(data);
        RecyclerView.LayoutManager lm = new LinearLayoutManager(this);

        RecyclerView rv = findViewById(R.id.table_edit_list);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(lm);
        rv.setAdapter(adapter);
    }
}
