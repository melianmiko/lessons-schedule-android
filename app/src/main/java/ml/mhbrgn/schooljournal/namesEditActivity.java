package ml.mhbrgn.schooljournal;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
    LessonsStorage ls;

    class namesAdapter extends RecyclerView.Adapter<namesAdapter.ViewHolder> {
        LessonsStorage.NameRecord[] mData;

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

        namesAdapter(LessonsStorage.NameRecord[] data) {
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

            holder.remove_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LessonsStorage ls = new LessonsStorage(namesEditActivity.this);
                    ls.nameRem(nameID);
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

        LessonsStorage.NameRecord[] mames = ls.getNames();
        RecyclerView.Adapter adapter = new namesAdapter(mames);
        RecyclerView.LayoutManager lm= new LinearLayoutManager(this);

        rv.setHasFixedSize(true);
        rv.setLayoutManager(lm);
        rv.setAdapter(adapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_names_edit);
        ls = new LessonsStorage(this);

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
                            LessonsStorage ls = new LessonsStorage(namesEditActivity.this);
                            ls.nameAdd(prompt.getText().toString());
                            namesEditActivity.this.updateList();
                        }
                    }).create().show();

            }
        });
    }

    void nameModDlalog(int id) {
        final int fid = id;
        String name = ls.getName(id);
        AlertDialog.Builder builder = new AlertDialog.Builder(namesEditActivity.this);
        final EditText prompt = new EditText(namesEditActivity.this);
        prompt.setInputType(InputType.TYPE_CLASS_TEXT);
        prompt.setHint(R.string.input_lesson_name);
        prompt.setText(name);

        builder.setTitle(R.string.name_mod).setView(prompt).setNegativeButton(R.string.cancel,null)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LessonsStorage ls = new LessonsStorage(namesEditActivity.this);
                        ls.nameMod(fid, prompt.getText().toString());
                        namesEditActivity.this.updateList();
                    }
                }).create().show();

    }
}
