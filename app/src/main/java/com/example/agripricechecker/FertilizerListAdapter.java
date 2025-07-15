package com.example.agripricechecker.adapters;

import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.agripricechecker.R;

import java.util.List;

public class FertilizerListAdapter extends RecyclerView.Adapter<FertilizerListAdapter.ViewHolder> {

    private List<String> cropList;
    private OnCropClickListener listener;

    public interface OnCropClickListener {
        void onCropClick(String cropName);
    }

    public FertilizerListAdapter(List<String> cropList, OnCropClickListener listener) {
        this.cropList = cropList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_crop_name, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String crop = cropList.get(position);
        holder.cropName.setText(crop);
        holder.itemView.setOnClickListener(v -> listener.onCropClick(crop));
    }

    @Override
    public int getItemCount() {
        return cropList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView cropName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cropName = itemView.findViewById(R.id.cropNameText);
        }
    }
}
