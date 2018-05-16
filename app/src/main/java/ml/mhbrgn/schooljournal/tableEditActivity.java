package ml.mhbrgn.schooljournal;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class tableEditActivity extends AppCompatActivity {
    final LessonsStorage ls = new LessonsStorage(this);

    // ====================================================================================
    class DailyTableAdapter extends RecyclerView.Adapter<DailyTableAdapter.ViewHolder> {
        LessonsStorage.TableItem[] mData;

        DailyTableAdapter(LessonsStorage.TableItem[] data) {
            mData = data;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            View root; TextView timeBox; TextView nameBox;
            ViewHolder(View v) {
                super(v);
                root = v;
                timeBox = v.findViewById(R.id.timebox);
                nameBox = v.findViewById(R.id.namebox);
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
            if(mData[position] != null) {
                holder.timeBox.setText(String.valueOf(mData[position].n));
                holder.nameBox.setText(mData[position].lesson);
            } else {
                holder.nameBox.setText(R.string.empy_list_item);
            }
        }

        @Override
        public int getItemCount() {
            return mData.length;
        }
    }
    // ====================================================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_edit);

        this.updateList();
    }

    void updateList() {
        LessonsStorage.TableItem[] data = ls.getDayLessons(0).content;
        RecyclerView.Adapter adapter = new DailyTableAdapter(data);
        RecyclerView.LayoutManager lm = new LinearLayoutManager(this);

        RecyclerView rv = findViewById(R.id.table_edit_list);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(lm);
        rv.setAdapter(adapter);
    }
}
