
package com.example.agripricechecker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PriceAdapter extends RecyclerView.Adapter<PriceAdapter.ViewHolder> {
    private final List<PriceModel> list;

    public PriceAdapter(List<PriceModel> list) {
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.price_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PriceModel item = list.get(position);
        holder.crop.setText(item.getCrop());
        holder.market.setText("Market: " + item.getMarket());
        holder.price.setText("Price: ₹" + item.getPrice());
        holder.date.setText("Date: " + item.getDate());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView crop, market, price, date;

        public ViewHolder(View itemView) {
            super(itemView);
            crop = itemView.findViewById(R.id.cropName);
            market = itemView.findViewById(R.id.marketName);
            price = itemView.findViewById(R.id.price);
            date = itemView.findViewById(R.id.date);
        }
    }
}
