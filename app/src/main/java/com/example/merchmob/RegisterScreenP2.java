package com.example.merchmob;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioGroup;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import io.realm.Realm;

public class RegisterScreenP2 extends AppCompatActivity {

    RadioGroup radioGroup;
    Button nextButton;
    String userUUID;
    Realm realm;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (realm != null && !realm.isClosed()) {
            realm.close();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.register_register_screen_p2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        realm = Realm.getDefaultInstance();
        userUUID = getIntent().getStringExtra("USER_UUID");

        radioGroup = findViewById(R.id.radioGroup);
        nextButton = findViewById(R.id.register_next_button2);

        nextButton.setOnClickListener(v -> {
            int selectedId = radioGroup.getCheckedRadioButtonId();
            String role = "Buyer";

            if (selectedId == R.id.role_saller_radio) {
                role = "Seller";
            }

            final String finalRole = role;
            realm.executeTransaction(r -> {
                User user = r.where(User.class).equalTo("userUUID", userUUID).findFirst();
                if (user != null) {
                    user.setRole(finalRole);
                }
            });
            goWelcome();

        });
    }

    private void goWelcome() {
        // Proceed to Buyer Screen
        Intent intent = new Intent(RegisterScreenP2.this, WelcomeScreen.class);
        startActivity(intent);
        finish();
    }
}
