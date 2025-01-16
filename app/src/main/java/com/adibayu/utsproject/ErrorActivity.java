package com.adibayu.utsproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ErrorActivity extends AppCompatActivity {
    private Button retryLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);

        retryLoginButton = findViewById(R.id.retryLoginButton);

        retryLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Return to the login page
                Intent intent = new Intent(ErrorActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}