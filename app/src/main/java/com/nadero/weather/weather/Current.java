package com.nadero.weather.weather;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Current {
    private long mTime;
    private String mIcon;
    private String mSummary;
    private String mLocation;
    private double mHumidity;
    private double mTemperature;
    private double mPrecipChance;
    private String mTimezone;


    public String getTimezone() {
        return mTimezone;
    }

    public void setTimezone(String mTimezone) {
        this.mTimezone = mTimezone;
    }

    public String getIcon() {
        return mIcon;
    }

    public void setIcon(String mIcon) {
        this.mIcon = mIcon;
    }

    public int getIconId() {
        return Forecast.geticonId(mIcon);
    }

    public String getLocation() {
        return mLocation;
    }

    public void setLocation(String location) {
        mLocation = location;
    }

    public long getTime() {
        return mTime;
    }

    public String getFormattedTime () {
        SimpleDateFormat formatter = new SimpleDateFormat("h:mm a");
        formatter.setTimeZone(TimeZone.getTimeZone(mTimezone));

        Date datTime = new Date(mTime * 1000);
        String timeString = formatter.format(datTime);
        return timeString;
    }

    public void setTime(long mTime) {
        this.mTime = mTime;
    }

    public int getTemperature() {
        return (int) Math.round(mTemperature);
    }

    public void setTemperature(double mTemperature) {
        this.mTemperature = mTemperature;
    }

    public double getHumidity() {
        return mHumidity;
    }

    public void setHumidity(double mHumidity) {
        this.mHumidity = mHumidity;
    }

    public int getPrecipChance() {
        double precipPercentage = mPrecipChance * 100;
        return (int) Math.round(precipPercentage);
    }

    public void setPrecipChance(double mPrecipChance) {
        this.mPrecipChance = mPrecipChance;
    }

    public String getSummary() {
        return mSummary;
    }

    public void setSummary(String mSummary) {
        this.mSummary = mSummary;
    }
}
