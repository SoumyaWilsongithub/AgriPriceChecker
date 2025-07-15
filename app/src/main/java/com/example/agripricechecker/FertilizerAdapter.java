package com.example.agripricechecker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agripricechecker.models.FertilizerModel;

import java.util.List;
import java.util.Map;

public class FertilizerAdapter extends RecyclerView.Adapter<FertilizerAdapter.ViewHolder> {

    private List<String> cropList;
    private Map<String, FertilizerModel> dataMap;
    private CropClickListener listener;

    public interface CropClickListener {
        void onCropClick(String cropName);
    }

    public FertilizerAdapter(List<String> cropList, Map<String, FertilizerModel> dataMap, CropClickListener listener) {
        this.cropList = cropList;
        this.dataMap = dataMap;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FertilizerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.crop_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FertilizerAdapter.ViewHolder holder, int position) {
        String crop = cropList.get(position);
        holder.cropNameText.setText(crop);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCropClick(crop);
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
            cropNameText = itemView.findViewById(R.id.cropNameText);
        }
    }
}
