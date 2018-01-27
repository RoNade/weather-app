package com.nadero.weather.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.nadero.weather.R;
import android.os.Parcelable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nadero.weather.adapters.DayAdapter;
import com.nadero.weather.weather.Day;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DailyForecastActivity extends Activity {
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

                String message = String.format("On %1$s the high will be %2$s and it will be %3$s",
                        dayOfTheWeek, highTemp, conditions);

                Toast.makeText(DailyForecastActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }
}
