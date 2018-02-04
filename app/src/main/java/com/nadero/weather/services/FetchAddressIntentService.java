package com.nadero.weather.services;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.util.Log;

import com.nadero.weather.constants.Constants;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class FetchAddressIntentService extends IntentService {
    private static final String TAG = FetchAddressIntentService.class.getSimpleName();

    double mLongitude;
    double mLatitude;

    public FetchAddressIntentService() {super("Default");};

    public FetchAddressIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@NonNull Intent intent) {
        String action = intent.getAction();

        if(action != null && action.equals(Constants.LOCATION_EXTRA)) {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            Location location = intent.getParcelableExtra(Constants.DATA);
            List<Address> addresses = null;

            mLongitude = location.getLongitude();
            mLatitude = location.getLatitude();

            try {
                addresses = geocoder.getFromLocation(mLatitude, mLongitude, 2);
            }
            catch(IOException exception) {
                Log.e(TAG, exception.getMessage());
                exception.printStackTrace();
            }

            if(addresses == null || addresses.isEmpty()) {
                String message = String.format("no address found for: %1$s, %2$s", mLatitude, mLongitude);
                deliverMessageToReceiver(Constants.FAILURE_RESULT, message);
            }
            else {
                Address address = (addresses.size() > 1) ? addresses.get(1) : addresses.get(0);
                String countryName = address.getCountryName();
                String locality = address.getLocality();

                countryName = (countryName != null) ? countryName : "Unknown";
                locality = (locality != null) ? locality : "Unknown";

                String locationLabel = String.format("%1$s, %2$s", locality, countryName);

                Log.d(TAG, "address found: " + locationLabel);
                deliverMessageToReceiver(Constants.SUCCES_RESULT, locationLabel);
            }
        }
    }

    private void deliverMessageToReceiver(int resultCode, String message) {
        Intent intent = new Intent();
        intent.setAction(Constants.ADDRESS_FROM_LOCATION);
        intent.putExtra(Constants.RESPONSE, resultCode);
        intent.putExtra(Constants.DATA, message);
        sendBroadcast(intent);
    }
}
