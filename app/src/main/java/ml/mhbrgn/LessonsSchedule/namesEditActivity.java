package ml.mhbrgn.LessonsSchedule;

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

public class namesEditActivity extends AppCompatActivity {
    class namesAdapter extends RecyclerView.Adapter<namesAdapter.ViewHolder> {
        final LessonName[] mData;

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView name_view;
            final ImageButton remove_button;
            final View root;
            ViewHolder(View r) {
                super(r);
                root = r;
                name_view = r.findViewById(R.id.text_name);
                remove_button = r.findViewById(R.id.btn_remove);
            }
        }

        namesAdapter(LessonName[] data) {
            mData = data;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.names_list_item, parent, false);

            return new namesAdapter.ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            final int nameID = mData[position].id;
            holder.name_view.setText(mData[position].name);

            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    namesEditActivity.this.nameModDialog(nameID);
                }
            });

            holder.remove_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LessonName ln = new LessonName(namesEditActivity.this,nameID);
                    ln.remove();
                    namesEditActivity.this.updateList();
                }
            });
        }

        @Override
        public int getItemCount() {
            return mData.length;
        }
    }

    // ==========================================================
    private void updateList() {
        RecyclerView rv = findViewById(R.id.names_container);

        RecyclerView.Adapter adapter = new namesAdapter(LessonName.getNamesArray(this));
        RecyclerView.LayoutManager lm= new LinearLayoutManager(this);

        rv.setHasFixedSize(true);
        rv.setLayoutManager(lm);
        rv.setAdapter(adapter);
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

        // Add scroll listener to hide FAB
        RecyclerView box = findViewById(R.id.names_container);
        box.addOnScrollListener(new RecyclerView.OnScrollListener() {
            final FloatingActionButton fab = findViewById(R.id.fab);
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy <= 0 && !fab.isShown()) fab.show();
                else if(dy > 0 && fab.isShown()) fab.hide();
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
