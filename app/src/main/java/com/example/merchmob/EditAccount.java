package com.example.merchmob;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import io.realm.Realm;

public class EditAccount extends AppCompatActivity {
    EditText editUsername;
    EditText editPassword;
    EditText editDescription;
    ImageButton editBackButton;
    AppCompatButton editSaveButton;
    Realm realm;
    SharedPreferences prefs;
    String loggedUsername;
    User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_account);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
    }

    public void init(){
        editUsername = findViewById(R.id.editUsername);
        editPassword = findViewById(R.id.editPassword);
        editDescription = findViewById(R.id.editDescription);
        editBackButton = findViewById(R.id.editBackButton);
        editSaveButton = findViewById(R.id.editSaveButton);

        prefs = getSharedPreferences("data", 0);
        realm = Realm.getDefaultInstance();

        loggedUsername = prefs.getString("loggedUsername", "");
        currentUser = realm.where(User.class).equalTo("username", loggedUsername).findFirst();

        if (currentUser != null) {
            editUsername.setText(currentUser.getUsername());
            editPassword.setText(currentUser.getPassword());
            editDescription.setText(currentUser.getUserDescription());
        }

        editSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editSaveClick();
            }
        });

        editBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void editSaveClick(){
        String inputtedUsername = editUsername.getText().toString();
        String inputtedPassword = editPassword.getText().toString();
        String inputtedDescription = editDescription.getText().toString();

        if(inputtedUsername.isEmpty()){
            Toast.makeText(EditAccount.this, "The username field must not be blank.", Toast.LENGTH_SHORT).show();
        } else {
            long userSpecificCount = realm.where(User.class).equalTo("username", inputtedUsername).count();
            boolean userExists = userSpecificCount > 1;

            if (userExists) {
                Toast.makeText(EditAccount.this, "The username already exists.", Toast.LENGTH_SHORT).show();
            } else {
                realm.beginTransaction();
                currentUser.setUsername(inputtedUsername);
                currentUser.setPassword(inputtedPassword);
                currentUser.setUserDescription(inputtedDescription);
                realm.commitTransaction();

                Toast.makeText(EditAccount.this, "The account has been updated.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    public void onDestroy() {
        super.onDestroy();

        if (!realm.isClosed()) {
            realm.close();
        }
    }
}