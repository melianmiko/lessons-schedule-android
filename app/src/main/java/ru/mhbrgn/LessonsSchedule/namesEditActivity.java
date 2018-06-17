package ru.mhbrgn.LessonsSchedule;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

public class namesEditActivity extends AppCompatActivity {
    class NamesListAdapter extends ArrayAdapter<NamesListAdapter.ViewHolder> {
        final LessonName[] mData;

        NamesListAdapter(LessonName[] data) {
            super(namesEditActivity.this, R.layout.names_list_item);
            mData = data;
        }

        class ViewHolder {
            final TextView name_view;
            final ImageButton remove_button;
            final View root;
            ViewHolder(View r) {
                root = r;
                name_view = r.findViewById(R.id.text_name);
                remove_button = r.findViewById(R.id.btn_remove);
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
                .inflate(R.layout.names_list_item, parent, false);

            ViewHolder holder = new ViewHolder(v);
            holder.name_view.setText(mData[position].name);

            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    namesEditActivity.this.nameModDialog(position);
                }
            });

            holder.remove_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LessonName ln = new LessonName(namesEditActivity.this,position);
                    ln.remove();
                    namesEditActivity.this.updateList();
                }
            });

            return v;
        }
    }

    // ==========================================================
    private void updateList() {
        ListView lv = findViewById(R.id.names_container);
        LessonName[] names = LessonName.getNamesArray(this);
        ArrayAdapter ad = new NamesListAdapter(names);
        lv.setAdapter(ad);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_names_edit);
        this.updateList();

        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new NameEditUI(namesEditActivity.this, new LessonName(namesEditActivity.this,""), new NameEditUI.OnCompleteListener(){
                    @Override
                    void onComplete(LessonName name) {
                        name.write();
                        updateList();
                    }
                });
            }
        });

    }

    private void nameModDialog(int id) {
        LessonName active = new LessonName(this, id);
        new NameEditUI(this, active, new NameEditUI.OnCompleteListener(){
            @Override
            void onComplete(LessonName name) {
                name.write();
                updateList();
            }
        });
    }

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

                builder.setTitle(R.string.restore_names_title)
                        .setMessage(R.string.restore_names_desc)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                LessonName.restoreDefaults(namesEditActivity.this);
                                namesEditActivity.this.updateList();
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
