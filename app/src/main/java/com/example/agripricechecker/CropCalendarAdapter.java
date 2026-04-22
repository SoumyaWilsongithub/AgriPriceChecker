package com.example.agripricechecker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.agripricechecker.models.CropCalendarModel;
import java.util.List;
import java.util.Map;

public class CropCalendarAdapter extends RecyclerView.Adapter<CropCalendarAdapter.ViewHolder> {

    private List<String> cropList;
    private Map<String, CropCalendarModel> cropData;
    private OnItemClickListener listener;
    private boolean isHindi;

    public interface OnItemClickListener {
        void onItemClick(String cropName);
    }

    public CropCalendarAdapter(List<String> cropList, Map<String, CropCalendarModel> cropData, boolean isHindi, OnItemClickListener listener) {
        this.cropList = cropList;
        this.cropData = cropData;
        this.isHindi = isHindi; // Set directly from Activity
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String cropName = cropList.get(position);
        holder.textView.setText(cropName); // Text is already translated by the Activity
        holder.itemView.setOnClickListener(v -> listener.onItemClick(cropName));
    }

    @Override
    public int getItemCount() {
        return cropList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
        }
    }
}