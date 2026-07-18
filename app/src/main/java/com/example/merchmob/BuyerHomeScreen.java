package com.example.merchmob;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import io.realm.Realm;
import io.realm.RealmResults;

public class BuyerHomeScreen extends AppCompatActivity {
    TextView bwsUsername;
    ImageView bwsAccountButton;
    ImageView bwsCartButton;
    RecyclerView bwsRecyclerView;
    SharedPreferences prefs;
    Realm realm;
    SellerAdapter sellerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_buyer_home_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.pa_product_card), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
    }

    public void init(){
        bwsUsername = findViewById(R.id.bwsUsername);
        bwsAccountButton = findViewById(R.id.bwsAccountButton);
        bwsCartButton = findViewById(R.id.bwsCartButton);
        bwsRecyclerView = findViewById(R.id.bwsRecyclerView);

        realm = Realm.getDefaultInstance();

        prefs = getSharedPreferences("data", 0);
        String loggedUsername = prefs.getString("loggedUsername", "");
        bwsUsername.setText(loggedUsername);

        bwsAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accountButtonClick();
            }
        });

        bwsCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cartButtonClick();
            }
        });

        // SELLER RECYCLER VIEW STARTS HERE
        LinearLayoutManager sellerLayoutManager = new LinearLayoutManager(this);
        sellerLayoutManager.setOrientation(RecyclerView.VERTICAL);
        bwsRecyclerView.setLayoutManager(sellerLayoutManager);

        RealmResults<User> sellerList = realm.where(User.class).equalTo("role", "Seller").findAll();
        sellerAdapter = new SellerAdapter(this, sellerList);
        bwsRecyclerView.setAdapter(sellerAdapter);
    }

    public void accountButtonClick(){
        Intent editUserScreen = new Intent(BuyerHomeScreen.this, EditAccount.class);
        startActivity(editUserScreen);
    }

    public void cartButtonClick(){
//        Intent buyerCartScreen = new Intent(BuyerHomeScreen.this, CartScreen.class);
//        startActivity(buyerCartScreen);
    }
}