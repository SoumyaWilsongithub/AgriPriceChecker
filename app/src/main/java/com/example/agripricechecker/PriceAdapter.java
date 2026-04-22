package com.example.agripricechecker;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PriceAdapter extends RecyclerView.Adapter<PriceAdapter.ViewHolder> {
    private final List<PriceModel> list;
    private final Map<String, String> cropTranslationMap = new HashMap<>();
    private boolean isHindi = false;
    private final Context context;

    public PriceAdapter(List<PriceModel> list, Context context) {
        this.list = list;
        this.context = context;
        initTranslations();
        checkLanguage();
    }

    /**
     * Call this method from your Activity's onResume() or after
     * a language change to update the list items immediately.
     */
    public void refreshLanguage() {
        checkLanguage();
        notifyDataSetChanged();
    }

    private void checkLanguage() {
        SharedPreferences prefs = context.getSharedPreferences("LanguagePref", Context.MODE_PRIVATE);
        String currentLang = prefs.getString("current_lang", "en");
        isHindi = "hi".equals(currentLang);
    }

    private void initTranslations() {
        // Essential Crops
        cropTranslationMap.put("Wheat", "गेहूँ");
        cropTranslationMap.put("Rice", "धान");
        cropTranslationMap.put("Maize", "मक्का");
        cropTranslationMap.put("Barley", "जौ");
        cropTranslationMap.put("Potato", "आलू");
        cropTranslationMap.put("Onion", "प्याज");
        cropTranslationMap.put("Tomato", "टमाटर");
        cropTranslationMap.put("Moong", "मूंग");
        cropTranslationMap.put("Chana", "चना");
        cropTranslationMap.put("Masoor", "मसूर");
        cropTranslationMap.put("Arhar", "अरहर");
        cropTranslationMap.put("Urad", "उड़द");
        // Fruits
        cropTranslationMap.put("Apple", "सेब");
        cropTranslationMap.put("Banana", "केला");
        cropTranslationMap.put("Grapes", "अंगूर");
        cropTranslationMap.put("Mango", "आम");
        cropTranslationMap.put("Orange", "संतरा");
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.price_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PriceModel item = list.get(position);

        // 1. Translate Crop Name
        String cropName = item.getCrop();
        if (isHindi && cropTranslationMap.containsKey(cropName)) {
            cropName = cropTranslationMap.get(cropName);
        }
        holder.crop.setText(cropName);

        // 2. Translate Labels (Market, Price, Date)
        if (isHindi) {
            holder.market.setText("मंडी: " + item.getMarket());
            holder.price.setText("भाव: ₹" + item.getPrice() + " प्रति क्विंटल");
            holder.date.setText("दिनांक: " + item.getDate());
        } else {
            holder.market.setText("Market: " + item.getMarket());
            holder.price.setText("Price: ₹" + item.getPrice() + " per quintal");
            holder.date.setText("Date: " + item.getDate());
        }
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