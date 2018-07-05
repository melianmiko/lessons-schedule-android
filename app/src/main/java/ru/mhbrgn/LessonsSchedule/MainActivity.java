package ru.mhbrgn.LessonsSchedule;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.view.LayoutInflater;

import android.widget.Toast;
import ru.mhbrgn.tools.UpdateCheckProvider;

import java.io.UnsupportedEncodingException;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final String LINK_PREFIX = "http://schedule.mhbrgn.ml/share/#";
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

        // Is shared some URI?
        final Intent intent = getIntent();
        final String action = intent.getAction();

        if (Intent.ACTION_VIEW.equals(action)) {
            try {

                String uri = Objects.requireNonNull(intent.getData()).toString();
                uri = uri.substring(uri.indexOf("#"));

                String json = new String(Base64.decode(uri, Base64.DEFAULT), "UTF-8");

                (new DataStorage(this)).loadJSON(json);
                Toast.makeText(this, R.string.import_ok, Toast.LENGTH_SHORT).show();

            } catch(Throwable e) {
                Toast.makeText(this, R.string.import_error, Toast.LENGTH_SHORT).show();
            }
        }

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

        // Update check
        UpdateCheckProvider upd = new UpdateCheckProvider(this);
        upd.setMessage(getString(R.string.new_version));
        upd.checkForUpdates();

        // Automatic list update
        new Handler().postDelayed(new Runnable() {
            public void run() {
                updateList();
            }
        }, 5000);
    }

    private void updateList() {
        // 1/3 - Day name fill
        ((TextView)findViewById(R.id.text_weekday)).setText(TableTools.getDayName(current_day,this));

        // 2/3 - View lessons
        LessonsTableItem[] data = LessonsTable.getDay(this, current_day);
        int active = LessonsStatusProvider.getCurrentLesson(this);
        // Create view
        LinearLayout list = findViewById(R.id.main_ll);
        list.removeAllViews();
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
            case R.id.action_share:
                doShareAction();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void doShareAction() {
        String json = (new DataStorage(this)).getJSON();
        try {

            String base64 = new String(Base64.encode(json.getBytes("UTF-8"), Base64.DEFAULT), "UTF-8");
            String link = LINK_PREFIX+base64;
            Toast.makeText(this, R.string.share_info, Toast.LENGTH_SHORT).show();

            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, link);
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)));

        } catch (UnsupportedEncodingException e) {
            Toast.makeText(this, "Share error", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        configRead();
        updateList();
    }
}
