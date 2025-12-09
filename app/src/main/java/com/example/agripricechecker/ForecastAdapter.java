package com.example.agripricechecker;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agripricechecker.R;
import com.squareup.picasso.Picasso;
import java.util.List;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ViewHolder> {

    private Context context;
    private List<Forecast> forecastList;

    public ForecastAdapter(Context context, List<Forecast> forecastList) {
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
    public void onBindViewHolder(@NonNull ForecastAdapter.ViewHolder holder, int position) {
        Forecast forecast = forecastList.get(position);
        holder.tvDay.setText(forecast.getDay());
        holder.tvTemp.setText(forecast.getTemp());
        Picasso.get().load(forecast.getIconUrl()).into(holder.ivIcon);
    }

    @Override
    public int getItemCount() {
        return forecastList.size();
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
