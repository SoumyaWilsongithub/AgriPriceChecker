package com.example.agripricechecker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MandiAdapter extends RecyclerView.Adapter<MandiAdapter.ViewHolder> {

    private List<MandiRecord> mandiList;

    // UP Mandi Hindi translations
    private Map<String, String> mandiTranslationMap = new HashMap<>();

    public MandiAdapter(List<MandiRecord> mandiList) {
        this.mandiList = mandiList;

        // Uttar Pradesh Mandis
        mandiTranslationMap.put("Kosikalan", "कोसीकलां");
        mandiTranslationMap.put("Anandnagar", "आनंदनगर");
        mandiTranslationMap.put("Maigalganj", "मैगलगंज");
        mandiTranslationMap.put("Lucknow", "लखनऊ");
        mandiTranslationMap.put("Kanpur", "कानपुर");
        mandiTranslationMap.put("Varanasi", "वाराणसी");
        mandiTranslationMap.put("Bareilly", "बरेली");
        mandiTranslationMap.put("Agra", "आगरा");
        mandiTranslationMap.put("Meerut", "मेरठ");
        mandiTranslationMap.put("Gorakhpur", "गोरखपुर");
        mandiTranslationMap.put("Prayagraj", "प्रयागराज");
        mandiTranslationMap.put("Jhansi", "झांसी");
        mandiTranslationMap.put("Mathura", "मथुरा");
        mandiTranslationMap.put("Aligarh", "अलीगढ़");
        mandiTranslationMap.put("Moradabad", "मुरादाबाद");
        mandiTranslationMap.put("Saharanpur", "सहारनपुर");
        mandiTranslationMap.put("Ayodhya", "अयोध्या");
        mandiTranslationMap.put("Fatehpur", "फतेहपुर");
        mandiTranslationMap.put("Sitapur", "सीतापुर");
        mandiTranslationMap.put("Lakhimpur", "लखीमपुर");
        mandiTranslationMap.put("Shahjahanpur", "शाहजहांपुर");
        mandiTranslationMap.put("Charra", "चर्रा");
        mandiTranslationMap.put("Lediyari", "लेदियारी");
        mandiTranslationMap.put("Sikarpur", "सिकरपुर");
        mandiTranslationMap.put("Kamlaganj", "कमालगंज");
        mandiTranslationMap.put("Shadabad", "सादाबाद");
        mandiTranslationMap.put("Shahaswan", "सहसवान");
        mandiTranslationMap.put("Samsabad", "शमसाबाद");
        mandiTranslationMap.put("Shamsabad", "शमसाबाद");
        mandiTranslationMap.put("Doharighat", "दोहरीघाट");
        mandiTranslationMap.put("Jasvantnagar", "जसवंतनगर");
        mandiTranslationMap.put("Kopaganj", "कोपागंज");
        mandiTranslationMap.put("Sambhal", "संभल");
        mandiTranslationMap.put("Sirsa", "सिरसा");
        mandiTranslationMap.put("Kairana", "कैराना");
        mandiTranslationMap.put("Hardoi", "हरदोई");
        mandiTranslationMap.put("Banda", "बांदा");
        mandiTranslationMap.put("Ballia", "बलिया");
        mandiTranslationMap.put("Azamgarh", "आजमगढ़");
        mandiTranslationMap.put("Basti", "बस्ती");
        mandiTranslationMap.put("Deoria", "देवरिया");
        mandiTranslationMap.put("Etawah", "इटावा");
        mandiTranslationMap.put("Firozabad", "फिरोजाबाद");
        mandiTranslationMap.put("Ghaziabad", "गाजियाबाद");
        mandiTranslationMap.put("Noida", "नोएडा");
        mandiTranslationMap.put("Bharwari", "भरवारी");
        mandiTranslationMap.put("Babrala", "बबराला");
        mandiTranslationMap.put("Panchpedwa", "पंचपेड़वा");
        mandiTranslationMap.put("Lalganj", "लालगंज");
        mandiTranslationMap.put("Salon", "सलोन");
        mandiTranslationMap.put("Ait", "ऐत");
        mandiTranslationMap.put("Payagpur","पयागपुर");
        mandiTranslationMap.put("Khair","ख़ैर");
        mandiTranslationMap.put("Hasanpur","हसनपुर");
        mandiTranslationMap.put("Anwala","आँवला");
        mandiTranslationMap.put("Bisoli","बिसौली");
        mandiTranslationMap.put("Jalalabad","जलालाबाद");
        mandiTranslationMap.put("Tulsipur","तुलसीपुर");
        mandiTranslationMap.put("Farukhabad"," फ़र्रूख़ाबाद");
        mandiTranslationMap.put("Achnera","अछनेरा");
        mandiTranslationMap.put("Bindki","बिन्दकी");
        mandiTranslationMap.put("Bangarmau","बाँगरमऊ");
        mandiTranslationMap.put("Viswan","विसवां");
        mandiTranslationMap.put("Buland Shahr","बुलन्दशहर");
        mandiTranslationMap.put("Chirgaon","चिरगाँव");
        mandiTranslationMap.put("Jarar","जर्रार ");
        mandiTranslationMap.put("Jagner","जगनेर ");
        mandiTranslationMap.put("Atrauli","अतरौली");
        mandiTranslationMap.put("Harduaganj","हरदुआगंज");
        mandiTranslationMap.put("Amroha","अमरोहा");
        mandiTranslationMap.put("Dhanaura","धनौरा");
        mandiTranslationMap.put("Hasanpur","हसनपुर");
        mandiTranslationMap.put("Achalda","अछल्दा ");
        mandiTranslationMap.put("Radauli","रुदौली");
        mandiTranslationMap.put("Dibiapur","दिबियापुर");
        mandiTranslationMap.put("Sikandraraau","सिकंदराराऊ");
        mandiTranslationMap.put("Durgapur","दुर्गापुर");
        mandiTranslationMap.put("Jangipura","जंगीपुरा");
        mandiTranslationMap.put("Kannauj","कननाउज");
        mandiTranslationMap.put("Haathras","हाथरस");
        mandiTranslationMap.put("Nawabganj","नवाबगंज");
        mandiTranslationMap.put("Robertsganj","राबर्ट्सगंज");
        mandiTranslationMap.put("Bahraich","बहराइच ");
        mandiTranslationMap.put("Chhibramau","छिबरामऊ");
        mandiTranslationMap.put("Ahirora","अहिरौरा");
        mandiTranslationMap.put("Jhijhank","झिझंक");
        mandiTranslationMap.put("Kadaura","कडौरा");
        mandiTranslationMap.put("Maholi","महोली");
        mandiTranslationMap.put("Risia","रिसाई");
        mandiTranslationMap.put("Lalitpur","ललितपुर");
        mandiTranslationMap.put("Jalaun","जगौन");
        mandiTranslationMap.put("Kalpi","कालपी");
        mandiTranslationMap.put("Powayan","पवयां");
        mandiTranslationMap.put("Ganj Dundwara","गंजडुंडवारा");
        mandiTranslationMap.put("Khairagarh","खैरागढ़");
        mandiTranslationMap.put("Gulavati","गुलावठी");
        mandiTranslationMap.put("Sikanderabad","सिकंदराबाद");
        mandiTranslationMap.put("Anoop Shahar","अनूपशहर");
        mandiTranslationMap.put("","");
        mandiTranslationMap.put("Kasganj","कासगंज");
        mandiTranslationMap.put("Mehmoodabad","मेहमोडबाद");
        mandiTranslationMap.put("Barabanki","बाराबंकी");
        mandiTranslationMap.put("Jahangirabad","जहाँगीराबाद ");
        mandiTranslationMap.put("Kayamganj","कायमगंज");
        mandiTranslationMap.put("Khekda","केकड़े");
        mandiTranslationMap.put("Shikohabad","शिकोहाबाद");
        mandiTranslationMap.put("Muradnagar","मुरादनगर");
        mandiTranslationMap.put("Safdarganj", "सफ़दरगंज");
        mandiTranslationMap.put("Shahabad(New Mandi)", "शाहबाद (नवीन मंडी)");
        mandiTranslationMap.put("Sikandraraau", "सिकंदराराऊ");
        mandiTranslationMap.put("Powayan", "पवयां");
        mandiTranslationMap.put("Meeranpur Katra", "मीरानपुर कटरा");
        mandiTranslationMap.put("Jangipura", "जंगीपुरा");
        mandiTranslationMap.put("Pukharayan", "पुखरायां");
        mandiTranslationMap.put("Chutmalpur", "छुटमलपुर");
        mandiTranslationMap.put("Pilibhit", "पीलीभीत");
        mandiTranslationMap.put("Rampurmaniharan", "रामपुर मनिहारन");
        mandiTranslationMap.put("Naugarh", "नौगढ़");
        mandiTranslationMap.put("Gulavati", "गुलावठी");
        mandiTranslationMap.put("Gonda", "गोंडा");


    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_mandi, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        MandiRecord record = mandiList.get(position);

        Context context = holder.itemView.getContext();

        String lang = LocaleHelper.getLanguage(context);

        String marketLabel = context.getString(R.string.market_label);
        String dateLabel = context.getString(R.string.date_label);
        String priceLabel = context.getString(R.string.price_label);
        String quintal = context.getString(R.string.quintal);

        String marketName = record.market;

        // Remove "APMC"
        marketName = marketName.replace("APMC", "").trim();

        // Convert mandi name to Hindi
        if (lang.equals("hi") && mandiTranslationMap.containsKey(marketName)) {
            marketName = mandiTranslationMap.get(marketName) + " मंडी";
        }

        holder.marketText.setText(marketLabel + ": " + marketName);

        holder.dateText.setText(dateLabel + ": " + record.arrival_date);

        double price = 0;

        try {
            price = Double.parseDouble(record.modal_price);
        } catch (Exception e) {
            price = 0;
        }

        String formattedPrice = String.format("%.3f", price);
        holder.priceText.setText(priceLabel + ": ₹" + formattedPrice + " / " + quintal);


        //holder.priceText.setText(priceLabel + ": ₹" + record.modal_price + " / " + quintal);
    }

    @Override
    public int getItemCount() {
        return mandiList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView marketText, dateText, priceText;

        public ViewHolder(View itemView) {
            super(itemView);

            marketText = itemView.findViewById(R.id.marketText);
            dateText = itemView.findViewById(R.id.dateText);
            priceText = itemView.findViewById(R.id.priceText);
        }
    }
}