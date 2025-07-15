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
    private CropClickListener listener;

    public CropCalendarAdapter(List<String> cropList, Map<String, CropCalendarModel> cropData, CropClickListener listener) {
        this.cropList = cropList;
        this.cropData = cropData;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CropCalendarAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.crop_item, parent, false); // crop_item.xml should contain a single TextView or layout
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CropCalendarAdapter.ViewHolder holder, int position) {
        String cropName = cropList.get(position);
        holder.cropNameText.setText(cropName);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCropClick(cropName);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cropList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView cropNameText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cropNameText = itemView.findViewById(R.id.cropNameText); // Make sure crop_item.xml has this ID
        }
    }
}
