package ru.mhbrgn.LessonsSchedule;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout;
import android.view.LayoutInflater;
import android.view.ViewGroup;

public class MainActivity extends AppCompatActivity {

    private int current_day = 1;
    private int work_days;
    private LessonsStatusProvider status;

    private void configRead() {
        // Get work days limit
        SharedPreferences prefs = getSharedPreferences("prefs",MODE_PRIVATE);
        work_days = prefs.getInt("work_days", 5);
        // Work day param update
        if(current_day > work_days) current_day = 1;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Open reader layout
        setContentView(R.layout.activity_main);
        setTitle(R.string.lessonsTitle);
        // Create status provider
        status = new LessonsStatusProvider(this, (TextView) findViewById(R.id.note_box));
        // Get current day
        current_day = TableTools.getCurrentDay();
        // Update list and read config
        configRead();
        updateList();
        // Setup buttons
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
    }

    private void updateList() {
        // 1/3 - Day name fill
        ((TextView)findViewById(R.id.text_weekday)).setText(TableTools.getDayName(current_day,this));

        // 2/3 - View lessons
        LessonsTableItem[] data = LessonsTable.getDay(this, current_day);
        int active = LessonsStatusProvider.getCurrentLesson(this);
        // Create view
        LinearLayout list = findViewById(R.id.main_ll);
        ((ViewGroup) list).removeAllViews();
        for(LessonsTableItem i : data) {
            if(i.lesson_id < 0) continue;

            View v = LayoutInflater.from(this).inflate(R.layout.main_list_item, list, false);

            ((TextView) v.findViewById(R.id.name_text)).setText(i.lesson);
            ((TextView) v.findViewById(R.id.num_text)).setText(i.getTime().getTablePrefix());

            if(i.num == active && i.day == TableTools.getCurrentDay()) 
                v.findViewById(R.id.active_flag).setVisibility(View.VISIBLE);

            list.addView(v);
        }

        // 3/3 - Update status
        status.update();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                Intent myIntent = new Intent(MainActivity.this, settingsActivity.class);
                MainActivity.this.startActivity(myIntent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        configRead();
        updateList();
    }
}
