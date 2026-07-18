package com.example.merchmob;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
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

public class SellerDashboard extends AppCompatActivity {

    TextView stockCount;
    TextView outOfStockCount;
    TextView totalCount;
    RecyclerView sdRecyclerView;
    ImageButton sdAddButton;

    Realm realm;
    SharedPreferences prefs;
    User seller;
    SellerProductAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_seller_dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.pa_product_card), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        init();
    }

    private void init() {
        stockCount = findViewById(R.id.sd_stock_count);
        outOfStockCount = findViewById(R.id.sd_outstock_count);
        totalCount = findViewById(R.id.sd_total_count);
        sdRecyclerView = findViewById(R.id.sd_recyclerview);
        sdAddButton = findViewById(R.id.sd_add_button);

        realm = Realm.getDefaultInstance();
        prefs = getSharedPreferences("data", 0);
        String loggedUsername = prefs.getString("loggedUsername", "");
        seller = realm.where(User.class).equalTo("username", loggedUsername).findFirst();

        if (seller != null) {
            setupRecyclerView();
            updateDashboardStats();
        }
    }

    private void setupRecyclerView() {
        sdRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        RealmResults<Product> products = realm.where(Product.class)
                .equalTo("sellerUUID", seller.getUserUUID())
                .findAll();
        
        adapter = new SellerProductAdapter(this, products);
        sdRecyclerView.setAdapter(adapter);

        // Update stats whenever data changes
        products.addChangeListener(results -> updateDashboardStats());
    }

    private void updateDashboardStats() {
        if (seller == null) return;

        long total = realm.where(Product.class)
                .equalTo("sellerUUID", seller.getUserUUID())
                .count();

        long inStock = realm.where(Product.class)
                .equalTo("sellerUUID", seller.getUserUUID())
                .greaterThan("stock", 0)
                .count();

        long outOfStock = realm.where(Product.class)
                .equalTo("sellerUUID", seller.getUserUUID())
                .equalTo("stock", 0)
                .count();

        totalCount.setText(String.valueOf(total));
        stockCount.setText(String.valueOf(inStock));
        outOfStockCount.setText(String.valueOf(outOfStock));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (realm != null && !realm.isClosed()) {
            realm.close();
        }
    }
}
