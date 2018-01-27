package com.nadero.weather.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.nadero.weather.R;
import com.nadero.weather.weather.Hour;

import android.view.View;
import android.widget.Toast;

public class HourAdapter extends RecyclerView.Adapter<HourAdapter.HourViewHolder> {
    private Hour[] mHours;
    private Context mContext;

    public HourAdapter(Context context, Hour[] hours) {
        mContext = context;
        mHours = hours;
    }

    @Override
    public HourViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.hourly_list_item, parent, false);

        HourViewHolder viewHolder = new HourViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(HourViewHolder holder, int position) {
        holder.bindHour(mHours[position]);
    }

    @Override
    public int getItemCount() {
        return mHours.length;
    }

    public class HourViewHolder extends RecyclerView.ViewHolder
        implements  View.OnClickListener {
        public TextView mTimeLabel;
        public TextView mSummaryLabel;
        public ImageView mIconImageView;
        public TextView mTemperatureLabel;

        public HourViewHolder(View itemView) {
            super(itemView);

            mTimeLabel = (TextView) itemView.findViewById(R.id.timeLabel);
            mSummaryLabel = (TextView) itemView.findViewById(R.id.summarylabel);
            mIconImageView = (ImageView) itemView.findViewById(R.id.iconImageView);
            mTemperatureLabel = (TextView) itemView.findViewById(R.id.temperatureLabel);

            itemView.setOnClickListener(this);
        }

        public void bindHour(Hour hour) {
            mTimeLabel.setText(hour.getHour());
            mSummaryLabel.setText(hour.getSummary());
            mIconImageView.setImageResource(hour.getIconId());
            mTemperatureLabel.setText(String.format("%1$s", hour.getTemperature()));
        }

        @Override
        public void onClick(View v) {
            String time = mTimeLabel.getText().toString();
            String temperature = mTemperatureLabel.getText().toString();
            String summary = mSummaryLabel.getText().toString();

            String message = String.format("At %1$s it will be %2$s and %3$s",
                    time, temperature, summary);

            Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
        }
    }
}
