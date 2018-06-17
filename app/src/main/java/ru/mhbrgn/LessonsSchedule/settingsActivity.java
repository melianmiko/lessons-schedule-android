package ru.mhbrgn.LessonsSchedule;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class settingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Get pref
        final SharedPreferences prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE);
        int work_days = prefs.getInt("work_days",5);

        // Setup click events
        findViewById(R.id.editTable).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingsActivity.this.startActivity(new Intent(settingsActivity.this, tableEditActivity.class));
            }
        });
        findViewById(R.id.editTimes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingsActivity.this.startActivity(new Intent(settingsActivity.this, timesEditActivity.class));
            }
        });
        findViewById(R.id.editNames).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingsActivity.this.startActivity(new Intent(settingsActivity.this, namesEditActivity.class));
            }
        });
        findViewById(R.id.set_weekend_0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prefs.edit().putInt("work_days",5).apply();
            }
        });
        findViewById(R.id.set_weekend_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prefs.edit().putInt("work_days",6).apply();
            }
        });
        findViewById(R.id.set_weekend_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prefs.edit().putInt("work_days",7).apply();
            }
        });

        // Select active weekend settings
        switch (work_days) {
            case 5: ((RadioButton) findViewById(R.id.set_weekend_0)).toggle();break;
            case 6: ((RadioButton) findViewById(R.id.set_weekend_1)).toggle();break;
            case 7: ((RadioButton) findViewById(R.id.set_weekend_2)).toggle();break;
        }
    }
}
