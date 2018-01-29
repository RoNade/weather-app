package com.nadero.weather.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import com.nadero.weather.R;
import com.nadero.weather.adapters.HourAdapter;
import com.nadero.weather.weather.Hour;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HourlyForecastActivity extends AppCompatActivity {
    private Hour[] mHours;

    @BindView(R.id.recylclerView) RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hourly_forecast);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        Parcelable[] parcelables = intent.getParcelableArrayExtra(MainActivity.HOURLY_FORECAST);
        mHours = Arrays.copyOf(parcelables, parcelables.length, Hour[].class);

        HourAdapter adapter = new HourAdapter(this, mHours);
        mRecyclerView.setAdapter(adapter);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setHasFixedSize(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.home:
                Intent homeIntent = new Intent(HourlyForecastActivity.this, MainActivity.class);
                startActivity(homeIntent);
                break;
            case R.id.about:
                new AlertDialog.Builder(HourlyForecastActivity.this, R.style.Theme_Custom_Dialog_Alert)
                        .setNegativeButton("CANCEL", null)
                        .setMessage(R.string.about_message)
                        .setTitle(R.string.about_title)
                        .setPositiveButton("OK", null)
                        .show();
                break;
            default:
                Toast.makeText(HourlyForecastActivity.this, "Unknown option", Toast.LENGTH_SHORT).show();
                break;
        }

        return true;
    }
}
