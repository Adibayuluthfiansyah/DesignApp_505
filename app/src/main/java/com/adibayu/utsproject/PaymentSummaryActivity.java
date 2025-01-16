package com.adibayu.utsproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PaymentSummaryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_summary);

        // Get views
        TextView sizeTextView = findViewById(R.id.sizeTextView);
        TextView giftWrapTextView = findViewById(R.id.giftWrapTextView);
        TextView transactionDateTimeTextView = findViewById(R.id.transactionDateTimeTextView);
        TextView transactionIdTextView = findViewById(R.id.transactionIdTextView);
        TextView subtotalTextView = findViewById(R.id.subtotalTextView);
        TextView taxTextView = findViewById(R.id.taxTextView);
        TextView totalAmountTextView = findViewById(R.id.totalAmountTextView);
        TextView deliveryDateTextView = findViewById(R.id.deliveryDateTextView); // Tambahan baru
        LinearLayout itemsContainer = findViewById(R.id.itemsContainer);
        Button backToMainButton = findViewById(R.id.backToMainButton);

        // Get data from intent
        Intent intent = getIntent();
        String transactionId = intent.getStringExtra("TRANSACTION_ID");
        double subtotal = intent.getDoubleExtra("SUBTOTAL", 0);
        double taxAmount = intent.getDoubleExtra("TAX_AMOUNT", 0);
        double totalAmount = intent.getDoubleExtra("TOTAL_AMOUNT", 0);
        int[] itemQuantities = MainActivity.itemQuantities;

        // Format currency
        String selectedSize = intent.getStringExtra("SELECTED_SIZE");
        boolean isGiftWrap = intent.getBooleanExtra("IS_GIFT_WRAP", false);
        String selectedDate = intent.getStringExtra("SELECTED_DATE"); // Tambahan baru

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

        // Format date and time
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String currentDateTime = dateFormat.format(new Date());

        // Set transaction details
        transactionDateTimeTextView.setText("Tanggal: " + currentDateTime);
        transactionIdTextView.setText("No. Transaksi: " + transactionId);

        sizeTextView.setText("Ukuran: " + selectedSize);
        giftWrapTextView.setText("Gift Wrap: " + (isGiftWrap ? "Ya (+Rp 10.000)" : "Tidak"));

        // Set delivery date (Tambahan baru)
        if (selectedDate != null && !selectedDate.isEmpty()) {
            deliveryDateTextView.setText("Tanggal Pengiriman: " + selectedDate);
        } else {
            deliveryDateTextView.setText("Tanggal Pengiriman: Tidak dipilih");
        }

        // Define item names and prices (same as in MainActivity)
        String[] itemNames = {"Angel Longsleve", "Four Hoodie", "Tribal Longsleve", "Sunday Hoodie", "Evil Tee", "Sci-Fi Hoodie"};
        int[] itemPrices = {150000, 200000, 100000, 150000, 200000, 250000};

        // Clear existing views in itemsContainer
        itemsContainer.removeAllViews();

        // Display items
        for (int i = 0; i < itemQuantities.length; i++) {
            if (itemQuantities[i] > 0) {
                // Create and add item details as TextView
                TextView itemTextView = new TextView(this);
                int itemTotal = itemPrices[i] * itemQuantities[i];
                String itemText = itemNames[i] + " x" + itemQuantities[i] + " (Size: " + selectedSize + ")\n" +
                        currencyFormat.format(itemPrices[i]) + "  " +
                        currencyFormat.format(itemTotal);
                itemTextView.setText(itemText);
                itemTextView.setTextColor(getResources().getColor(android.R.color.darker_gray));
                itemTextView.setTextSize(14);

                // Add margin to bottom
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(0, 0, 0, 16); // 16dp bottom margin
                itemTextView.setLayoutParams(params);

                itemsContainer.addView(itemTextView);
            }
        }

        // Set payment details
        subtotalTextView.setText(currencyFormat.format(subtotal));
        taxTextView.setText(currencyFormat.format(taxAmount));
        totalAmountTextView.setText(currencyFormat.format(totalAmount));

        // Handle back button
        backToMainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(PaymentSummaryActivity.this, MainActivity.class);
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(mainIntent);
                finish();
            }
        });
    }
}