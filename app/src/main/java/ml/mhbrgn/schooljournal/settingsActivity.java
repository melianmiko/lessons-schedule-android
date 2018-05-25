package ml.mhbrgn.schooljournal;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class settingsActivity extends AppCompatActivity {
    ListItem[] menuContent;

    class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.ViewHolder> {
        private settingsActivity.ListItem[] mData;

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            TextView mTextView;
            TextView mDescView;
            ImageView mIconView;
            View root;
            ViewHolder(View v) {
                super(v);
                root = v;
                mTextView = v.findViewById(R.id.title);
                mDescView = v.findViewById(R.id.description);
                mIconView = v.findViewById(R.id.icon);
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        SettingsAdapter(settingsActivity.ListItem[] data) {
            mData = data;
        }

        // Create new views (invoked by the layout manager)
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.settings_title_textview, parent, false);

            return new ViewHolder(v);
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.mTextView.setText(mData[position].title);
            holder.mDescView.setText(mData[position].description);
            holder.mIconView.setImageDrawable(mData[position].icon);
            holder.root.setOnClickListener(mData[position].click);
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mData.length;
        }
    }

    void fillMenuContent() {
        List<ListItem> menu = new ArrayList<>();
        // Add items to menu
        menu.add(new ListItem(R.string.editTable, R.string.editTableDesc, R.drawable.ic_today_black_24dp, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingsActivity.this.startActivity(new Intent(settingsActivity.this, tableEditActivity.class));
            }
        }));

        menu.add(new ListItem(R.string.editTimes, R.string.editTimesDesc, R.drawable.ic_access_time_black_24dp, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent timeEdit = new Intent(settingsActivity.this, timesEditActivity.class);
                settingsActivity.this.startActivity(timeEdit);
            }
        }));

        menu.add(new ListItem(R.string.editNames, R.string.editNamesDesc, R.drawable.ic_list_black_24dp, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingsActivity.this.startActivity(new Intent(settingsActivity.this, namesEditActivity.class));
            }
        }));

        menu.add(new ListItem(R.string.weekend_edit, R.string.weekend_desc, R.drawable.ic_weekend_black_24dp, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new WeekendSetupUI(settingsActivity.this);
            }
        }));

        menu.add(new ListItem(R.string.appearance, R.string.appearanceDesc, R.drawable.ic_palette_black_24dp, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        }));

        menu.add(new ListItem(R.string.about_title,R.string.about_info, R.drawable.ic_info_black_24dp, new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                BottomSheetDialog dialog = new BottomSheetDialog(settingsActivity.this);
                @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.about_dialog,null);
                dialog.setContentView(view);

                try {
                    InputStream i = getAssets().open("changelog.html");
                    BufferedReader in = new BufferedReader(new InputStreamReader(i));
                    StringBuilder fileContent = new StringBuilder();

                    String str;
                    while ((str = in.readLine()) != null) { fileContent.append(str); }
                    String out = fileContent.toString();
                    Spanned htmlOut = Html.fromHtml(out);

                    ((TextView)view.findViewById(R.id.changelog)).setText(htmlOut);

                } catch (IOException e) {
                    e.printStackTrace();
                }

                dialog.show();
            }
        }));

        menuContent = menu.toArray(new ListItem[menu.size()]);
    }

    class ListItem{
        String id;
        String title;
        String description;
        Drawable icon;
        View.OnClickListener click;
        ListItem(int t, int d, int i, View.OnClickListener c) {
            // Convert res-ids to strings
            title = getResources().getString(t);
            description = getResources().getString(d);
            icon = ContextCompat.getDrawable(settingsActivity.this, i);
            click = c;
        }
    }

    // ========================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        fillMenuContent();

        RecyclerView mRecyclerView;
        RecyclerView.Adapter mAdapter;
        RecyclerView.LayoutManager mLayoutManager;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mRecyclerView = findViewById(R.id.settings_menu);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new SettingsAdapter(menuContent);
        mRecyclerView.setAdapter(mAdapter);
    }
}
