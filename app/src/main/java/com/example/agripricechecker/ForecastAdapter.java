package com.example.agripricechecker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ViewHolder> {

    private Context context;
    // Corrected path to the ForecastDay class
    private List<WeatherAPI.WeatherResponse.ForecastDay> forecastList;

    public ForecastAdapter(Context context, List<WeatherAPI.WeatherResponse.ForecastDay> forecastList) {
        this.context = context;
        this.forecastList = forecastList;
    }

    @NonNull
    @Override
    public ForecastAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.forecast_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // FIX: Changed WeatherAPI.ForecastDay to WeatherAPI.WeatherResponse.ForecastDay
        WeatherAPI.WeatherResponse.ForecastDay dayData = forecastList.get(position);

        holder.tvDay.setText(dayData.date);
        holder.tvTemp.setText(dayData.day.avgtemp_c + "°C");

        // Ensure you have an 'icon' field in your WeatherAPI.WeatherResponse.Condition class
        if (dayData.day.condition != null) {
            // WeatherAPI usually provides relative URLs, hence the https: prefix
            Glide.with(context).load("https:" + dayData.day.condition.icon).into(holder.ivIcon);
        }
    }

    @Override
    public int getItemCount() {
        return forecastList != null ? forecastList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDay, tvTemp;
        ImageView ivIcon;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDay = itemView.findViewById(R.id.tvDay);
            tvTemp = itemView.findViewById(R.id.tvTemp);
            ivIcon = itemView.findViewById(R.id.ivIcon);
        }
    }
}