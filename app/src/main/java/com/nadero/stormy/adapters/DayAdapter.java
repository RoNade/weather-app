package com.nadero.stormy.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nadero.stormy.R;
import com.nadero.stormy.weather.Day;

public class DayAdapter extends BaseAdapter {

    private Context mContext;
    private Day[] mDays;

    public DayAdapter(Context context, Day[] days) {
        mContext = context;
        mDays = days;
    }

    @Override
    public int getCount() {
        return mDays.length;
    }

    @Override
    public Object getItem(int position) {
        return mDays[position];
    }

    @Override
    public long getItemId(int position) {
        return 0; // tag items for easy reference
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        // create the viewHolder
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.daily_list_item, null);
            holder = new ViewHolder();

            holder.dayLabel = convertView.findViewById(R.id.dayNameLabel);
            holder.iconImageView = convertView.findViewById(R.id.iconImageView);
            holder.temperatureLabel = convertView.findViewById(R.id.temperatureLabel);

            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        Day day = mDays[position];

        if (position == 0) {
            holder.dayLabel.setText("Today");
        }
        else {
            holder.dayLabel.setText(day.getDayOfTheWeek());
        }

        holder.iconImageView.setImageResource(day.getIconId());
        holder.temperatureLabel.setText(String.format("%1$s", day.getTemperatureMax()));

        return convertView;
    }

    private static class ViewHolder {
        public ImageView iconImageView;
        public TextView temperatureLabel;
        public TextView dayLabel;
    }
}
