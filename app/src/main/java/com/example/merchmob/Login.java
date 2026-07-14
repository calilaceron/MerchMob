package com.example.merchmob;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import io.realm.Realm;

public class Login extends AppCompatActivity {
    EditText loginUsername;
    EditText loginPassword;
    Button loginButton;
    ImageButton loginBackButton;
    Realm realm;
    SharedPreferences prefs = getSharedPreferences("data", 0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
    }

    public void init(){
        loginUsername =findViewById(R.id.loginUsername);
        loginPassword = findViewById(R.id.loginPassword);
        loginButton = findViewById(R.id.loginButton);
        loginBackButton = findViewById(R.id.loginBackButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginClick();
            }
        });

        loginBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void loginClick(){
        String inputtedUsername = loginUsername.getText().toString();
        String inputtedPassword = loginPassword.getText().toString();

        if (inputtedUsername.isEmpty() || inputtedPassword.isEmpty()){
            Toast.makeText(Login.this, "The username or password is empty.", Toast.LENGTH_SHORT).show();
        } else {
            realm =Realm.getDefaultInstance();
            long specificUserCount = realm.where(User.class).equalTo("username", inputtedUsername).count();
            boolean userExists = specificUserCount > 0 ;

            if(userExists){
                User user= realm.where(User.class).equalTo("username", inputtedUsername).findFirst();
                assert user != null;
                String recordedPassword = user.getPassword();

                if (inputtedPassword.equals(recordedPassword)){

                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("username", inputtedUsername);
                    editor.apply();

                    if (user.getRole().equals("buyer")){
//                                Intent buyerScreen = new Intent(Login.this, {{ buyerScreen }}.class);
//                                startActivity(buyerScreen);
                    } else if (user.getRole().equals("seller")){
//                                Intent sellerScreen = new Intent(Login.this, {{ sellerScreen }}.class);
//                                startActivity(sellerScreen);
                    }
                } else {
                    Toast.makeText(Login.this, "The password is incorrect", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(Login.this, "The inputted user does not exist.", Toast.LENGTH_SHORT).show();
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