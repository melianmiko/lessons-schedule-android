package ml.mhbrgn.schooljournal;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class namesEditActivity extends AppCompatActivity {
    class namesAdapter extends RecyclerView.Adapter<namesAdapter.ViewHolder> {
        LessonName[] mData;

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView name_textbox;
            ImageButton remove_button;
            View root;
            ViewHolder(View r) {
                super(r);
                root = r;
                name_textbox = r.findViewById(R.id.text_name);
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
            holder.name_textbox.setText(mData[position].name);

            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    namesEditActivity.this.nameModDlalog(nameID);
                }
            });

            // Add padding to last item
            if(position == this.getItemCount()-1) {
                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.root.getLayoutParams();
                params.setMargins(params.leftMargin, params.topMargin, params.rightMargin, 32);
                holder.root.setLayoutParams(params);
            }

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
    void updateList() {
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
                AlertDialog.Builder builder = new AlertDialog.Builder(namesEditActivity.this);
                final EditText prompt = new EditText(namesEditActivity.this);
                prompt.setInputType(InputType.TYPE_CLASS_TEXT);
                prompt.setHint(R.string.input_lesson_name);

                builder.setTitle(R.string.name_add).setView(prompt).setNegativeButton(R.string.cancel,null)
                    .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            LessonName newName = new LessonName(namesEditActivity.this, prompt.getText().toString());
                            newName.write();
                            namesEditActivity.this.updateList();
                        }
                    }).create().show();

            }
        });

        // Add scroll listener to hide FAB
        RecyclerView box = findViewById(R.id.names_container);
        box.addOnScrollListener(new RecyclerView.OnScrollListener() {
            FloatingActionButton fab = findViewById(R.id.fab);
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy <= 0 && !fab.isShown()) fab.show();
                else if(dy > 0 && fab.isShown()) fab.hide();
            }
        });
    }

    void nameModDlalog(int id) {
        final LessonName active = new LessonName(this, id);
        String name = active.name;

        AlertDialog.Builder builder = new AlertDialog.Builder(namesEditActivity.this);
        final EditText prompt = new EditText(namesEditActivity.this);
        prompt.setInputType(InputType.TYPE_CLASS_TEXT);
        prompt.setHint(R.string.input_lesson_name);
        prompt.setText(name);

        builder.setTitle(R.string.name_mod).setView(prompt).setNegativeButton(R.string.cancel,null)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        active.name = prompt.getText().toString();
                        active.write();
                        namesEditActivity.this.updateList();
                    }
                }).create().show();

    }
}
