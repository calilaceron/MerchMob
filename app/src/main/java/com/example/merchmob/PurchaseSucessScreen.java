package com.example.merchmob;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class PurchaseSucessScreen extends AppCompatActivity {

    TextView itemsPurchasedText;
    Button backToHomeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_purchase_sucess_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        itemsPurchasedText = findViewById(R.id.textView27);
        backToHomeButton = findViewById(R.id.welcome_login_button2);

        int count = getIntent().getIntExtra("itemsCount", 0);
        itemsPurchasedText.setText(count + " Items Purchased");

        backToHomeButton.setOnClickListener(v -> {
            Intent intent = new Intent(PurchaseSucessScreen.this, BuyerHomeScreen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }
}
