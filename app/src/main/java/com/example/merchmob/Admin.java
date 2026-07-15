package com.example.merchmob;

import android.os.Bundle;
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

public class Admin extends AppCompatActivity {
    TextView adminTotalUserCount;
    TextView adminBuyerCount;
    TextView adminSellerCount;
    RecyclerView adminRecyclerView;
    Realm realm;
    UserAdapter userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
    }

    public void init(){
        adminTotalUserCount = findViewById(R.id.adminTotalUserCount);
        adminBuyerCount = findViewById(R.id.adminBuyerCount);
        adminSellerCount = findViewById(R.id.adminSellerCount);
        adminRecyclerView = findViewById(R.id.adminRecyclerView);
        realm = Realm.getDefaultInstance();

        RealmResults<User> userList = realm.where(User.class).findAll();
        adminTotalUserCount.setText(userList.size() + " Total Users");

        RealmResults<User> buyerList = realm.where(User.class).equalTo("role", "Buyer").findAll();
        adminBuyerCount.setText(buyerList.size()+"");

        RealmResults<User> sellerList = realm.where(User.class).equalTo("role", "Seller").findAll();
        adminSellerCount.setText(sellerList.size()+"");

        LinearLayoutManager adminLayoutManager = new LinearLayoutManager(this);
        adminLayoutManager.setOrientation(RecyclerView.VERTICAL);
        adminRecyclerView.setLayoutManager(adminLayoutManager);

        userAdapter = new UserAdapter(this, userList);
        adminRecyclerView.setAdapter(userAdapter);
    }

    public void onDestroy(){
        super.onDestroy();
        if(!realm.isClosed()){
            realm.close();
        }
    }
}