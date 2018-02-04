package com.nadero.weather.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import com.nadero.weather.R;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nadero.weather.adapters.DayAdapter;
import com.nadero.weather.weather.Day;

import java.util.Arrays;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DailyForecastActivity extends AppCompatActivity {
    private Day[] mDays;

    @BindView(android.R.id.list) ListView mListView;
    @BindView(android.R.id.empty) TextView mEmptyTextView;
    @BindView(R.id.locationLabel) TextView mLocationLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_forecast);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        Parcelable[] parcelables = intent.getParcelableArrayExtra(MainActivity.DAILY_FORECAST);
        mDays = Arrays.copyOf(parcelables, parcelables.length, Day[].class);

        String locationLabel = mDays[0].getLocation();
        mLocationLabel.setText(locationLabel);

        DayAdapter adapter = new DayAdapter(this, mDays);
        mListView.setAdapter(adapter);
        mListView.setEmptyView(mEmptyTextView);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String highTemp = String.format("%1$s", mDays[position].getTemperatureMax());
                String dayOfTheWeek = mDays[position].getDayOfTheWeek();
                String conditions = mDays[position].getSummary();

                String message = String.format(getString(R.string.toast_weekly),
                        dayOfTheWeek, highTemp, conditions);

                Toast.makeText(DailyForecastActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
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
                Intent homeIntent = new Intent(DailyForecastActivity.this, MainActivity.class);
                startActivity(homeIntent);
                break;
            case R.id.about:
                new AlertDialog.Builder(DailyForecastActivity.this, R.style.Theme_Custom_Dialog_Alert)
                        .setNegativeButton(R.string.alertdialog_button_cancel, null)
                        .setMessage(R.string.about_message)
                        .setTitle(R.string.about_title)
                        .setPositiveButton(R.string.error_ok_button, null)
                        .show();
                break;
            default:
                Toast.makeText(DailyForecastActivity.this, "Unknown option", Toast.LENGTH_SHORT).show();
                break;
        }

        return true;
    }
}
