package ml.mhbrgn.LessonsSchedule;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

// ======================================================================

public class timesEditActivity extends AppCompatActivity {

    class TimesListAdapter extends RecyclerView.Adapter<TimesListAdapter.ViewHolder> {
        private final LessonTime[] mData;

        TimesListAdapter(LessonTime[] d) {mData = d;}

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.times_list_item, parent, false);

            return new ViewHolder(v);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            String startTime = mData[position].startTimeString();
            String endTime = mData[position].endTimeString();

            holder.num.setText(String.valueOf(mData[position].number+1));
            holder.name.setText(startTime+"-"+endTime);
            holder.removeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView tv = ((View)v.getParent()).findViewById(R.id.text_number);
                    String sid = tv.getText().toString();
                    int id = Integer.parseInt(sid)-1;
                    LessonTime time = new LessonTime(timesEditActivity.this,id);
                    time.remove();
                    timesEditActivity.this.updateList();
                }
            });
            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView tv = v.findViewById(R.id.text_number);
                    String sid = tv.getText().toString();
                    timesEditActivity.this.modTime(Integer.parseInt(sid)-1);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mData.length;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final View root;
            final TextView num;
            final TextView name;
            final ImageButton removeBtn;
            ViewHolder(View v) {
                super(v);
                root = v;
                num = v.findViewById(R.id.text_number);
                name = v.findViewById(R.id.text_name);
                removeBtn = v.findViewById(R.id.btn_remove);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_times_edit);
        updateList();

        // Setup add button
        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timesEditActivity.this.addTime();
            }
        });

        // Add scroll listener to hide FAB
        RecyclerView box = findViewById(R.id.times_list);
        box.addOnScrollListener(new RecyclerView.OnScrollListener() {
            final FloatingActionButton fab = findViewById(R.id.fab);
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy <= 0 && !fab.isShown()) fab.show();
                else if(dy > 0 && fab.isShown()) fab.hide();
            }
        });
    }

    private void modTime(int id) {
        LessonTime time = new LessonTime(this, id);
        TimeEditUI ui = new TimeEditUI(this, time);

        ui.setOnCompleteListener(new TimeEditUI.OnCompleteListener(){
            @Override
            void onComplete(LessonTime time) {
                time.write();
                timesEditActivity.this.updateList();
            }
        });

        ui.show();
    }

    private void addTime() {
        // Get last record
        LessonTime[] data = LessonTime.getTimesArray(this);
        LessonTime newTime = new LessonTime(this, 500, 540);
        if(data.length > 1) {
            LessonTime last = data[data.length - 1];
            // Create new time. ID = last+1, START = lastStart+10, END lastStart+50
            newTime = new LessonTime(this, last.startTime + 10, last.startTime + 50);
            newTime.number = last.number + 1;
        }
        // Create dialog
        TimeEditUI ui = new TimeEditUI(this,newTime);
        final LessonTime finalNewTime = newTime;
        ui.setOnCompleteListener(new TimeEditUI.OnCompleteListener(){
            @Override
            void onComplete(LessonTime time) {
                finalNewTime.write();
                updateList();
            }
        });
        ui.show();
    }

    private void updateList() {
        LessonTime[] data = LessonTime.getTimesArray(this);
        RecyclerView box = findViewById(R.id.times_list);
        RecyclerView.Adapter adapter = new TimesListAdapter(data);
        RecyclerView.LayoutManager layman = new LinearLayoutManager(this);

        box.setHasFixedSize(true);
        box.setLayoutManager(layman);
        box.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.restore_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.times_wipe:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle(R.string.times_wipe_title)
                        .setMessage(R.string.times_wipe_msg)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                LessonTime.restoreDefaults(timesEditActivity.this);
                                timesEditActivity.this.updateList();
                            }
                        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Nothing
                            }
                        }).create().show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
