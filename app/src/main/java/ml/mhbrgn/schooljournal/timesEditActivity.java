package ml.mhbrgn.schooljournal;

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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

// ======================================================================

public class timesEditActivity extends AppCompatActivity {

    class TimesListAdapter extends RecyclerView.Adapter<TimesListAdapter.ViewHolder> {
        private LessonsStorage.TimeRecord[] mData;

        TimesListAdapter(LessonsStorage.TimeRecord[] d) {mData = d;}

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
            String startTime = LessonsStorage.timeToString(mData[position].startTime);
            String endTime = LessonsStorage.timeToString(mData[position].endTime);

            // Add padding to last item
            if(position == this.getItemCount()-1) {
                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.root.getLayoutParams();
                params.setMargins(params.leftMargin, params.topMargin, params.rightMargin, 32);
                holder.root.setLayoutParams(params);
            }

            holder.num.setText(String.valueOf(position+1));
            holder.name.setText(startTime+"-"+endTime);
            holder.removeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView tv = ((View)v.getParent()).findViewById(R.id.text_number);
                    String sid = tv.getText().toString();
                    int id = Integer.parseInt(sid);
                    ls.timeRem(id);
                    timesEditActivity.this.updateList();
                }
            });
            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView tv = v.findViewById(R.id.text_number);
                    String sid = tv.getText().toString();
                    timesEditActivity.this.modTime(Integer.parseInt(sid));
                }
            });
        }

        @Override
        public int getItemCount() {
            return mData.length;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            View root;
            TextView num;
            TextView name;
            ImageButton removeBtn;
            ViewHolder(View v) {
                super(v);
                root = v;
                num = v.findViewById(R.id.text_number);
                name = v.findViewById(R.id.text_name);
                removeBtn = v.findViewById(R.id.btn_remove);
            }
        }
    }

    LessonsStorage ls = new LessonsStorage(this);

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
            FloatingActionButton fab = findViewById(R.id.fab);
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy <= 0 && !fab.isShown()) fab.show();
                else if(dy > 0 && fab.isShown()) fab.hide();
            }
        });
    }

    public void modTime(final int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dv = inflater.inflate(R.layout.time_enter_dialog, null);
        LessonsStorage.TimeRecord data = ls.getTime(id);

        // Find views and set values
        final EditText start_h = dv.findViewById(R.id.start_time_h);
        start_h.setText(String.valueOf(Math.floor(data.startTime/60)));
        final EditText start_m = dv.findViewById(R.id.start_time_m);
        start_m.setText(String.valueOf(data.startTime-Math.floor(Math.floor(data.startTime/60)*60)));

        final EditText end_h = dv.findViewById(R.id.end_time_h);
        end_h.setText(String.valueOf(Math.floor(data.endTime/60)));
        final EditText end_m = dv.findViewById(R.id.end_time_m);
        end_m.setText(String.valueOf(data.endTime-Math.floor(Math.floor(data.endTime/60)*60)));

        builder.setView(dv).setPositiveButton(R.string.add,new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int s_h = Integer.parseInt(start_h.getText().toString());
                int s_m = Integer.parseInt(start_m.getText().toString());
                int e_h = Integer.parseInt(end_h.getText().toString());
                int e_m = Integer.parseInt(end_m.getText().toString());

                Toast.makeText(timesEditActivity.this, "sH "+s_h+" sM "+s_m+" eH "+e_h+" eM "+e_m, Toast.LENGTH_SHORT).show();

                if(s_h > 23 || s_m > 60 || e_h > 23 || e_m > 60) {
                    Toast.makeText(timesEditActivity.this, R.string.incorrect_time, Toast.LENGTH_LONG).show();
                } else {
                    int st = s_h*60+s_m;
                    int et = e_h*60+e_m;
                    ls.timeMod(id, st, et);
                    timesEditActivity.this.updateList();
                }
            }
        }).setNegativeButton(R.string.cancel,new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Noting to do
            }
        });
        builder.create().show();
    }

    public void addTime() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dv = inflater.inflate(R.layout.time_enter_dialog, null);

        // Find views
        final EditText start_h = dv.findViewById(R.id.start_time_h);
        final EditText start_m = dv.findViewById(R.id.start_time_m);
        final EditText end_h = dv.findViewById(R.id.end_time_h);
        final EditText end_m = dv.findViewById(R.id.end_time_m);

        builder.setView(dv).setPositiveButton(R.string.add,new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int s_h = Integer.parseInt(start_h.getText().toString());
                int s_m = Integer.parseInt(start_m.getText().toString());
                int e_h = Integer.parseInt(end_h.getText().toString());
                int e_m = Integer.parseInt(end_m.getText().toString());

                if(s_h > 23 || s_m > 60 || e_h > 23 || e_m > 60) {
                    Toast.makeText(timesEditActivity.this, R.string.incorrect_time, Toast.LENGTH_LONG).show();
                } else {
                    int st = s_h*60+s_m;
                    int et = e_h*60+e_m;
                    ls.timeAdd(st, et);
                    timesEditActivity.this.updateList();
                }
            }
        }).setNegativeButton(R.string.cancel,new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Noting to do
            }
        });
        builder.create().show();
    }

    private void updateList() {
        LessonsStorage.TimeRecord[] data = ls.getTimes();
        RecyclerView box = findViewById(R.id.times_list);
        RecyclerView.Adapter adapter = new TimesListAdapter(data);
        RecyclerView.LayoutManager layman = new LinearLayoutManager(this);

        box.setHasFixedSize(true);
        box.setLayoutManager(layman);
        box.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.times_toolbar, menu);
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
                                ls.timesRestore();
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
