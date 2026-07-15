package com.example.merchmob;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class WelcomeScreen extends AppCompatActivity {
    TextView subtitle;
    TextView title;
    Button registerButton;
    Button loginButton;
    ImageButton adminButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.register_welcome_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        title = findViewById(R.id.welcome_title);
        subtitle = findViewById(R.id.welcome_subtitle);

        registerButton = findViewById(R.id.welcome_register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goRegister();
            }
        });

        loginButton = findViewById(R.id.welcome_login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goLogin();
            }
        });

        adminButton = findViewById(R.id.welcome_admin_button);
        adminButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goAdmin();
            }
        });
    }

    private void goLogin() {
        Intent intent = new Intent(WelcomeScreen.this, Login.class);
        startActivity(intent);
    }

    private void goRegister() {
        Intent intent = new Intent(WelcomeScreen.this, RegisterScreenP1.class);
        startActivity(intent);
    }

    private void goAdmin() {
//        Intent intent = new Intent(WelcomeScreen.this, AdminScreen.class);
//        startActivity(intent);
    }
}