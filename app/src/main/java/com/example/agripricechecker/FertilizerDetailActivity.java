package com.example.agripricechecker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.agripricechecker.models.FertilizerModel;

import java.util.ArrayList;

public class FertilizerDetailActivity extends AppCompatActivity {

    public static void open(Context context, String cropName, FertilizerModel model) {
        Intent intent = new Intent(context, FertilizerDetailActivity.class);
        intent.putExtra("cropName", cropName);
        intent.putExtra("npk", model.getNpk());
        intent.putExtra("products", new ArrayList<>(model.getProducts()));
        intent.putExtra("advice", model.getAdvice());
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fertilizer_detail);

        TextView cropNameText = findViewById(R.id.detailCropName);
        TextView npkValue = findViewById(R.id.npkValue);
        TextView productList = findViewById(R.id.productList);
        TextView adviceText = findViewById(R.id.adviceText);

        // Get data from intent
        String cropName = getIntent().getStringExtra("cropName");
        String npk = getIntent().getStringExtra("npk");
        ArrayList<String> products = getIntent().getStringArrayListExtra("products");
        String advice = getIntent().getStringExtra("advice");

        cropNameText.setText(cropName);
        npkValue.setText("NPK: " + npk);
        productList.setText("Products: " + String.join(", ", products));
        adviceText.setText("Advice: " + advice);

        // Enable back arrow
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(cropName);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
