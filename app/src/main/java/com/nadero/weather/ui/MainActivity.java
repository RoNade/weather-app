package com.nadero.weather.ui;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.nadero.weather.R;
import com.nadero.weather.constants.Constants;
import com.nadero.weather.services.FetchAddressIntentService;
import com.nadero.weather.weather.Current;
import com.nadero.weather.weather.Day;
import com.nadero.weather.weather.Forecast;
import com.nadero.weather.weather.Hour;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.CipherSuite;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.TlsVersion;


public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();
    public static final String HOURLY_FORECAST = "HOURLY_FORECAST";
    public static final String DAILY_FORECAST = "DAILY_FORECAST";
    public static final int LOCATION_REQUEST_FOR_PERMISSION = 1;
    private FusedLocationProviderClient mFusedLocationClient;
    private String mLocality = "Unknown locality";
    private double mLongitude = -122.4233;
    private double mLatitude = 37.8267;
    private Forecast mForecast;

    LocationRequest locationRequest;
    LocationCallback locationCallback;
    AddressBroadcastReceiver addressBroadcastReceiver;

    @BindView(R.id.timeLabel) TextView mTimeLabel;
    @BindView(R.id.locationLabel) TextView mLocationLabel;
    @BindView(R.id.temperatureLabel) TextView mTemperatureLabel;
    @BindView(R.id.humidityValue) TextView mHumidityValue;
    @BindView(R.id.precipValue) TextView mPrecipValue;
    @BindView(R.id.summaryLabel) TextView mSummarylabel;
    @BindView(R.id.iconImageView) ImageView mIconImageView;
    @BindView(R.id.refreshImageView) ImageView mRefreshImageView;
    @BindView(R.id.progressBar) ProgressBar mProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(MainActivity.this);
        mProgressBar.setVisibility(View.INVISIBLE);

        boolean locationEnabled = isLocationAvailable();

        mFusedLocationClient = LocationServices
                .getFusedLocationProviderClient(this);

        locationRequest = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setFastestInterval(15 * 1000)
                .setInterval(30 * 1000);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                toggleRefresh();
                if(locationResult != null) getLocalityFromLocation(locationResult.getLastLocation());
            }
        };

        IntentFilter filter = new IntentFilter(Constants.ADDRESS_FROM_LOCATION);
        addressBroadcastReceiver = new AddressBroadcastReceiver();
        registerReceiver(addressBroadcastReceiver, filter);

        if(locationEnabled) {
            updateLocation();
        } else {
           requestLocationPermission();
        }

        mRefreshImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getForecast(mLatitude, mLongitude);
            }
        });

        Log.d(TAG, "Main UI code is running!");
    }

    @Override
    protected void onPause() {
        super.onPause();

        stopLocationUpdates();
    }

    @Override
    protected void onResume() {
        super.onResume();

        boolean locationEnabled = isLocationAvailable();

        if(locationEnabled) {
            startLocationUpdates();
        }
        else {
            requestLocationPermission();
        }
    }

    public void requestLocationPermission() {
        String[] permissions = {
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
        };

        ActivityCompat.requestPermissions(MainActivity.this, permissions, LOCATION_REQUEST_FOR_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == LOCATION_REQUEST_FOR_PERMISSION && grantResults.length > 0) {
            if(grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                MainActivity.this.finish();
                System.exit(0);
            }
            updateLocation();
        }
    }

    private void getForecast(double latitude, double longitude) {
        try {
            KeyStore keyStore = KeyStore.getInstance("BKS");
            Log.v(TAG, keyStore.toString());
        } catch (KeyStoreException e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }

        String apiKey = "495ae7624fba8a0e894a4b2407888f44";
        String forecastUrl = String.format("https://api.darksky.net/forecast/%1$s/%2$s,%3$s", apiKey, latitude, longitude);
        ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                .tlsVersions(TlsVersion.TLS_1_0, TlsVersion.TLS_1_1, TlsVersion.TLS_1_2)
                .cipherSuites(CipherSuite.TLS_DHE_RSA_WITH_AES_256_CBC_SHA)
                .build();

        Log.d(TAG, forecastUrl);

        if (isNetworkAvailable()) {
            toggleRefresh();

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectionSpecs(Collections.singletonList(spec))
                    .readTimeout(15, TimeUnit.SECONDS)
                    .build();

            Request request = new Request.Builder()
                    .url(forecastUrl)
                    .build();

            Call call = client.newCall(request);

            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleRefresh();
                        }
                    });
                    alertUserAboutError();
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleRefresh();
                        }
                    });
                    try {
                        ResponseBody responseBody = response.body();
                        String jsonData = (responseBody != null) ? responseBody.string() : null;
                        Log.v(TAG, jsonData);

                        if (response.isSuccessful()) {
                            mForecast = parseForecastDetails(jsonData);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateDisplay();
                                }
                            });
                        } else {
                            alertUserAboutError();
                        }
                    }
                    catch (IOException exception) {
                        Log.e(TAG, exception.getMessage());
                    }
                    catch (JSONException exception) {
                        Log.e(TAG, exception.getMessage());
                    }
                }
            });
        } else {
            Toast.makeText(this, R.string.network_unavailable_message, Toast.LENGTH_SHORT).show();
        }
    }

    private void toggleRefresh() {
        if (mProgressBar.getVisibility() == View.INVISIBLE) {
            mProgressBar.setVisibility(View.VISIBLE);
            mRefreshImageView.setVisibility(View.INVISIBLE);
        }
        else {
            mProgressBar.setVisibility(View.INVISIBLE);
            mRefreshImageView.setVisibility(View.VISIBLE);
        }

    }

    private void updateLocation() throws SecurityException {
        Log.d(TAG, "updateLocation......");
        toggleRefresh();

        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if(location != null) { getLocalityFromLocation(location); }
                    }
                });
    }

    private void startLocationUpdates() throws SecurityException {
        Log.d(TAG, "startLocationUpdates......");

        mFusedLocationClient
                .requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private void stopLocationUpdates() {
        Log.d(TAG,"stopLocationUpdates......");

        mFusedLocationClient
                .removeLocationUpdates(locationCallback);
    }

    private void startIntentService(Location location) {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.setAction(Constants.LOCATION_EXTRA);
        intent.putExtra(Constants.DATA, location);

        startService(intent);
    }

    private void updateDisplay() {
        Current current = mForecast.getCurrent();
        mTemperatureLabel.setText(String.format("%1$s", current.getTemperature()));
        mTimeLabel.setText(String.format("At %1$s it will be", current.getFormattedTime()));
        mHumidityValue.setText(String.format("%1$s", current.getHumidity()));
        mPrecipValue.setText(String.format("%1$s%2$s", current.getPrecipChance(), "%"));
        mSummarylabel.setText(current.getSummary());
        mLocationLabel.setText(mLocality);

        Drawable drawable = getResources().getDrawable(current.getIconId());
        mIconImageView.setImageDrawable(drawable);
    }

    private Forecast parseForecastDetails(String jsonData) throws JSONException {
        Forecast forecast = new Forecast();

        forecast.setCurrent(getCurrentDetails(jsonData));
        forecast.setHourlyForecast(getHourlyForecast(jsonData));
        forecast.setDailyForecast(getDailyForecast(jsonData));

        return forecast;
    }

    private Day[] getDailyForecast(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        JSONObject daily = forecast.getJSONObject("daily");
        JSONArray data = daily.getJSONArray("data");

        Day[] days = new Day[data.length()];
        for (int index = 0; index < data.length(); index++) {
            JSONObject jsonDay = data.optJSONObject(index);
            Day day = new Day();

            day.setLocation(mLocality);
            day.setTime(jsonDay.getLong("time"));
            day.setIcon(jsonDay.getString("icon"));
            day.setSummary(jsonDay.getString("summary"));
            day.setTimezone(forecast.getString("timezone"));
            day.setTemperatureMax(jsonDay.getDouble("temperatureMax"));

            days[index] = day;
        }

        return days;
    }

    private Hour[] getHourlyForecast(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        JSONObject hourly = forecast.getJSONObject("hourly");
        JSONArray data = hourly.getJSONArray("data");

        Hour[] hours = new Hour[data.length()];
        for (int index = 0; index < data.length(); index++) {
            JSONObject jsonHour = data.optJSONObject(index);
            Hour hour = new Hour();

            hour.setTime(jsonHour.getLong("time"));
            hour.setIcon(jsonHour.getString("icon"));
            hour.setSummary(jsonHour.getString("summary"));
            hour.setTimezone(forecast.getString("timezone"));
            hour.setTemperature(jsonHour.getDouble("temperature"));

            hours[index] = hour;
        }

        return hours;
    }

    private void getLocalityFromLocation(Location location) {
        mLongitude = location.getLongitude();
        mLatitude = location.getLatitude();
        startIntentService(location);
    }

    private Current getCurrentDetails(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        JSONObject currently = forecast.getJSONObject("currently");

        Current current = new Current();
        current.setTime(currently.getLong("time"));
        current.setIcon(currently.getString("icon"));
        current.setSummary(currently.getString("summary"));
        current.setTimezone(forecast.getString("timezone"));
        current.setHumidity(currently.getDouble("humidity"));
        current.setTemperature(currently.getDouble("temperature"));
        current.setPrecipChance(currently.getDouble("precipProbability"));
        current.setLocation(mLocality);

        Log.d(TAG, current.getFormattedTime());

        return current;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = (manager != null) ? manager.getActiveNetworkInfo() : null;
        return networkInfo != null && networkInfo.isConnected();
    }

    private boolean isLocationAvailable() {
        final int GRANTED = PackageManager.PERMISSION_GRANTED;

        final String[] permissions = {
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
        };

        int finePermission = ContextCompat
                .checkSelfPermission(MainActivity.this, permissions[1]);
        int coarsePermission = ContextCompat
                .checkSelfPermission(MainActivity.this, permissions[0]);

        return finePermission == GRANTED && coarsePermission == GRANTED;
    }

    private void alertUserAboutError() {
        AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.show(getFragmentManager(), "error_dialog");
    }

    @OnClick (R.id.dailyButton)
    public void startDailyActivity(View view) {
        Intent intent = new Intent(this, DailyForecastActivity.class);
        intent.putExtra(DAILY_FORECAST, mForecast.getDailyForecast());
        startActivity(intent);
    }

    @OnClick (R.id.hourlyButton)
    public void startHourlyActivity(View view) {
        Intent intent = new Intent(this, HourlyForecastActivity.class);
        intent.putExtra(HOURLY_FORECAST, mForecast.getHourlyForecast());
        startActivity(intent);
    }

    public class AddressBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            toggleRefresh();
            String action = intent.getAction();
            int responseCode = intent.getIntExtra(Constants.RESPONSE, 0);

            if(action != null && action.equals(Constants.ADDRESS_FROM_LOCATION)) {
                if(responseCode == Constants.SUCCES_RESULT) {
                    mLocality = intent.getStringExtra(Constants.DATA);
                    getForecast(mLatitude, mLongitude);
                }
            }
        }
    }
}
