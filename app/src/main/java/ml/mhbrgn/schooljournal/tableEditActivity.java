package ml.mhbrgn.schooljournal;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
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
    final LessonsStorage ls = new LessonsStorage(this);

    // ====================================================================================
    class DailyTableAdapter extends RecyclerView.Adapter<DailyTableAdapter.ViewHolder> {
        LessonsStorage.TableItem[] mData;
        int day;

        DailyTableAdapter(LessonsStorage.TableItem[] data,int day) {
            mData = data;
            this.day = day;
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
            final int d_id = position+1;
            final int d_day = day;

            holder.timeBox.setText(String.valueOf(d_id));

            if(mData[position] != null)
                holder.nameBox.setText(String.valueOf(ls.getName(mData[position].lesson)));
            else {
                holder.nameBox.setText(R.string.empy_list_item);
                holder.nameBox.setTextColor(getResources().getColor(R.color.grey));
            }

            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    BottomSheetDialog dialog = new BottomSheetDialog(tableEditActivity.this);
                    dialog.setCancelable(true);

                    LessonsStorage.NameRecord[] names = ls.getNames();

                    RecyclerView rv = (RecyclerView) getLayoutInflater().inflate(R.layout.names_list_recycleview,
                            (ViewGroup) tableEditActivity.this.findViewById(R.id.root), false);

                    RecyclerView.LayoutManager lm = new LinearLayoutManager(tableEditActivity.this);
                    RecyclerView.Adapter adapter = new NamesListAdapter(names, dialog,
                            new LessonsStorage.TableItem(d_day, d_id, 0));

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
        LessonsStorage.NameRecord[] mData;
        BottomSheetDialog dialog;
        final LessonsStorage.TableItem callback_item;

        NamesListAdapter(LessonsStorage.NameRecord[] data, BottomSheetDialog dialog, LessonsStorage.TableItem item) {
            // Build a array and add zero item
            ArrayList<LessonsStorage.NameRecord> names = new ArrayList<>();
            names.add(new LessonsStorage.NameRecord(0,getString(R.string.clean)));
            Collections.addAll(names, data);

            mData = names.toArray(new LessonsStorage.NameRecord[names.size()]);
            callback_item = item;
            this.dialog = dialog;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            View root; TextView text;
            ViewHolder(View itemView) {
                super(itemView);
                root = itemView;
                text = itemView.findViewById(R.id.textbox);
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
            final BottomSheetDialog d = dialog;
            holder.text.setText(mData[position].name);
            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    d.hide();
                    int day = callback_item.day;
                    int num = callback_item.n;
                    if(nameID > 0) ls.tableSet(day,num,nameID);
                    else ls.tableRem(day,num);
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
    int current_day = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_edit);

        this.updateList();
    }

    void updateList() {
        LessonsStorage.TableItem[] data = ls.getDayLessons(current_day).content;
        RecyclerView.Adapter adapter = new DailyTableAdapter(data, current_day);
        RecyclerView.LayoutManager lm = new LinearLayoutManager(this);

        RecyclerView rv = findViewById(R.id.table_edit_list);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(lm);
        rv.setAdapter(adapter);
    }
}
