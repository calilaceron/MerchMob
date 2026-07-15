package com.example.merchmob;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

import io.realm.Realm;
import io.realm.RealmQuery;

public class RegisterScreenP1 extends AppCompatActivity {

    TextView usernameHeading;
    TextView passwordHeading;
    TextView descHeading;
    EditText usernameInput;
    EditText passwordInput;
    EditText confirmInput;
    EditText descInput;
    Button nextButton;
    ImageButton backButton;

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
        setContentView(R.layout.register_register_screen_p1);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Realm.init(this);
        realm = Realm.getDefaultInstance();

        usernameHeading = findViewById(R.id.register_username_heading);
        passwordHeading = findViewById(R.id.register_password_heading);
        descHeading = findViewById(R.id.register_description_heading);

        usernameInput = findViewById(R.id.register_username_input);
        passwordInput = findViewById(R.id.register_password_input);
        confirmInput = findViewById(R.id.register_confirm_input);
        descInput = findViewById(R.id.register_description_input);


        nextButton = findViewById(R.id.register_next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkInput();
            }
        });


        backButton = findViewById(R.id.register_1_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void checkInput() {
        String enteredUsername = usernameInput.getText().toString();
        String enteredPassword = passwordInput.getText().toString();

        if (enteredUsername.isEmpty()) {
            Toast.makeText(this, "Username cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (enteredPassword.isEmpty()) {
            Toast.makeText(this, "Password cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!confirmInput.getText().toString().equals(enteredPassword)) {
            Toast.makeText(this, "Confirm password does not match", Toast.LENGTH_SHORT).show();
            return;
        }

        User existingUser = realm.where(User.class).equalTo("username", enteredUsername).findFirst();
        if (existingUser != null) {
            Toast.makeText(this, "User Already Exists", Toast.LENGTH_SHORT).show();
            return;
        }

        User u = new User();
        u.setUsername(enteredUsername);
        u.setPassword(enteredPassword);
        u.setUserDescription(descInput.getText().toString());

        realm.beginTransaction();
        realm.copyToRealmOrUpdate(u);
        realm.commitTransaction();
        Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show();

        goNext(u.getUserUUID());
    }

    private void goNext(String userUUID) {
        Intent intent = new Intent(RegisterScreenP1.this, RegisterScreenP2.class);
        intent.putExtra("USER_UUID", userUUID);
        startActivity(intent);
    }
}

