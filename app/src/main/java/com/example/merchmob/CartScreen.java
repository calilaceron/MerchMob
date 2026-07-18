package com.example.merchmob;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import io.realm.Realm;
import io.realm.RealmList;

public class CartScreen extends AppCompatActivity {

    TextView cartTotalAmount, cartItemTotal;
    RecyclerView cartRecyclerView;
    ImageButton backButton;
    Button checkoutButton;

    Realm realm;
    SharedPreferences prefs;
    User currentUser;
    CartItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        init();
    }

    private void init() {
        cartTotalAmount = findViewById(R.id.cart_total_amount);
        cartItemTotal = findViewById(R.id.cart_cartItem_total);
        cartRecyclerView = findViewById(R.id.cart_cartiItem_total); // Note the typo in XML ID
        backButton = findViewById(R.id.apBackButton2);
        checkoutButton = findViewById(R.id.cart_checkout_button);

        realm = Realm.getDefaultInstance();
        prefs = getSharedPreferences("data", 0);
        String loggedUsername = prefs.getString("loggedUsername", "");
        currentUser = realm.where(User.class).equalTo("username", loggedUsername).findFirst();

        if (currentUser != null) {
            setupRecyclerView();
            updateCartStats();
        }

        backButton.setOnClickListener(v -> finish());

        checkoutButton.setOnClickListener(v -> {
            if (currentUser == null || currentUser.getUserCart().isEmpty()) {
                Toast.makeText(this, "Your cart is empty!", Toast.LENGTH_SHORT).show();
            } else {
                performCheckout();
            }
        });
    }

    private void performCheckout() {
        realm.executeTransaction(r -> {
            int totalPurchased = 0;
            for (CartItem item : currentUser.getUserCart()) {
                Product product = r.where(Product.class).equalTo("productUUID", item.getProductUUID()).findFirst();
                if (product != null) {
                    // Update Stock
                    int newStock = product.getStock() - item.getQuantitySelected();
                    product.setStock(Math.max(0, newStock));

                    // Update Seller Stats
                    User seller = r.where(User.class).equalTo("userUUID", product.getSellerUUID()).findFirst();
                    if (seller != null) {
                        seller.setProductsSold(seller.getProductsSold() + item.getQuantitySelected());
                    }

                    totalPurchased += item.getQuantitySelected();
                }
            }

            // Update Buyer Stats
            currentUser.setProductsBought(currentUser.getProductsBought() + totalPurchased);

            // Clear Cart
            currentUser.getUserCart().clear();

            // Navigate to Success Screen
            Intent intent = new Intent(CartScreen.this, PurchaseSucessScreen.class);
            intent.putExtra("itemsCount", totalPurchased);
            startActivity(intent);
            finish();
        });
    }

    private void setupRecyclerView() {
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        RealmList<CartItem> cartItems = currentUser.getUserCart();
        adapter = new CartItemAdapter(this, cartItems);
        cartRecyclerView.setAdapter(adapter);

        // Update stats when cart changes
        cartItems.addChangeListener(results -> updateCartStats());
    }

    private void updateCartStats() {
        if (currentUser == null) return;

        RealmList<CartItem> cartItems = currentUser.getUserCart();
        int count = cartItems.size();
        float total = 0;

        for (CartItem item : cartItems) {
            Product p = realm.where(Product.class).equalTo("productUUID", item.getProductUUID()).findFirst();
            if (p != null) {
                total += (p.getPrice() * item.getQuantitySelected());
            }
        }

        cartItemTotal.setText(count + " Items in Cart");
        cartTotalAmount.setText("Php " + total);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (realm != null && !realm.isClosed()) {
            realm.close();
        }
    }
}
