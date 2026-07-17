package com.example.merchmob;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;

import io.realm.Realm;

public class ProductDetail extends AppCompatActivity {
    String productUUID;
    ImageButton pdBackButton;
    ImageView pdImageView;
    TextView pdProductName;
    TextView pdSellerName;
    TextView prPrice;
    TextView pdDescription;
    TextView pdStockStatus;
    TextView pdQuantity;
    ImageButton pdMinusButton;
    ImageButton pdPlusButton;
    AppCompatButton pdAddToCart;
    SharedPreferences prefs;
    Realm realm;
    Product product;
    int quantity = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
    }

    public void init(){
        pdBackButton = findViewById(R.id.pdBackButton);
        pdImageView = findViewById(R.id.pdImageView);
        pdProductName = findViewById(R.id.pdProductName);
        pdSellerName = findViewById(R.id.pdSellerName);
        prPrice = findViewById(R.id.prPrice);
        pdDescription = findViewById(R.id.pdDescription);
        pdStockStatus = findViewById(R.id.pdStockStatus);
        pdQuantity = findViewById(R.id.pdQuantity);
        pdMinusButton = findViewById(R.id.pdMinusButton);
        pdPlusButton = findViewById(R.id.pdPlusButton);
        pdAddToCart = findViewById(R.id.pdAddToCart);

        prefs = getSharedPreferences("data", 0);
        realm = Realm.getDefaultInstance();

        if(getIntent()!=null){
            productUUID = getIntent().getStringExtra("productUUID");
        }

        product = realm.where(Product.class).equalTo("productUUID", productUUID).findFirst();

        if(product!=null) {
            pdProductName.setText(product.getItemName());
            prPrice.setText(String.valueOf(product.getPrice()));
            pdDescription.setText(product.getProductDescription());
            User seller = realm.where(User.class).equalTo("userUUID", product.getSellerUUID()).findFirst();
            if(seller!=null){
                pdSellerName.setText(seller.getUsername());
            } else {
                pdSellerName.setText("No Seller");
            }

            if(product.getStock()>0){
                pdStockStatus.setText("Stock: "+product.getStock());
            } else {
                pdStockStatus.setText("Out of Stock");
            }

            // PRODUCT IMAGE RENDERING STARTS HERE
            File getImageDir = getExternalCacheDir();
            File productImage = null;
            if(getImageDir!=null){
                productImage = new File(getImageDir, product.getProductImageName());
            }

            if(productImage!=null && productImage.exists()){
                Picasso.get()
                        .load(productImage)
                        .networkPolicy(NetworkPolicy.NO_CACHE)
                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                        .into(pdImageView);
            } else {
                pdImageView.setImageResource(R.drawable.button_background);
            }
        }

        pdBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        pdMinusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(quantity>1){
                    quantity--;
                    pdQuantity.setText(String.valueOf(quantity));
                }
            }
        });

        pdPlusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantity++;
                pdQuantity.setText(String.valueOf(quantity));
            }
        });

        pdAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToCart();
            }
        });
    }

    public void addToCart(){
        String loggedUsername = prefs.getString("loggedUsername", "");

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                User user = realm.where(User.class).equalTo("username", loggedUsername).findFirst();

                CartItem existingItem = null;
                for(CartItem item : user.getUserCart()){
                    if(item.getProductUUID().equals(productUUID)){
                        existingItem = item;
                        break;
                    }
                }

                if(existingItem!=null){
                    existingItem.setQuantitySelected(existingItem.getQuantitySelected()+quantity);
                } else {
                    CartItem newItem = new CartItem();
                    newItem.setProductUUID(productUUID);
                    newItem.setQuantitySelected(quantity);
                    user.getUserCart().add(newItem);
                }
            }
        });

        Toast.makeText(this, quantity + " " + product.getItemName() + " added to cart", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (realm != null && !realm.isClosed()) {
            realm.close();
        }
    }
}