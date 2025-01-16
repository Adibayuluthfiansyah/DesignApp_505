package com.adibayu.utsproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class PaymentActivity extends AppCompatActivity {
    private Button paymentButton;
    private Button backToMainButton;
    private int totalPrice;
    private String selectedSize;
    private boolean isGiftWrap;
    private String selectedDate; // Tambahan baru untuk tanggal

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        // Get total price from MainActivity
        totalPrice = getIntent().getIntExtra("total_price", 0);

        if (totalPrice == 0) {
            Toast.makeText(this, "Silahkan Tambahkan Item Anda", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            paymentButton = findViewById(R.id.paymentButton);
            backToMainButton = findViewById(R.id.backToMainButton);

            paymentButton = findViewById(R.id.paymentButton);
            backToMainButton = findViewById(R.id.backToMainButton);

            selectedSize = getIntent().getStringExtra("selected_size");
            isGiftWrap = getIntent().getBooleanExtra("is_gift_wrap", false);
            selectedDate = getIntent().getStringExtra("selected_date"); // Tambahan baru untuk mengambil tanggal

            Button logoutButton = findViewById(R.id.logoutButton);
            logoutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(PaymentActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            });

            paymentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Calculate tax and totals
                    double subtotal = totalPrice;
                    double taxRate = 0.07; // 7% PPN
                    double taxAmount = subtotal * taxRate;
                    double finalTotal = subtotal + taxAmount;

                    // Add gift wrap fee if selected
                    if (isGiftWrap) {
                        finalTotal += 10000; // Add gift wrap fee of 10000 IDR
                    }

                    // Create transaction ID
                    String transactionId = "TRX-" + System.currentTimeMillis();

                    // Navigate to PaymentSummaryActivity with all required data
                    Intent intent = new Intent(PaymentActivity.this, PaymentSummaryActivity.class);
                    intent.putExtra("TRANSACTION_ID", transactionId);
                    intent.putExtra("SUBTOTAL", subtotal);
                    intent.putExtra("TAX_AMOUNT", taxAmount);
                    intent.putExtra("TOTAL_AMOUNT", finalTotal);
                    intent.putExtra("SELECTED_SIZE", selectedSize);
                    intent.putExtra("IS_GIFT_WRAP", isGiftWrap);
                    intent.putExtra("SELECTED_DATE", selectedDate); // Tambahan baru untuk mengirim tanggal
                    intent.putExtra("ITEM_QUANTITIES", MainActivity.itemQuantities);
                    startActivity(intent);
                }
            });

            backToMainButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(PaymentActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }
}