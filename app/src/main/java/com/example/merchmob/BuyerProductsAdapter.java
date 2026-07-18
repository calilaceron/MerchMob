package com.example.merchmob;

import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;

public class BuyerProductsAdapter extends RealmRecyclerViewAdapter<Product, BuyerProductsAdapter.ViewHolder> {
    BuyerHomeScreen activity;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView bpaProductName;
        TextView bpaProductPrice;
        ImageView bpaImageView;
        ImageButton bpaAddToCartButton;

        public ViewHolder(View buyerProductsView) {
            super(buyerProductsView);

            bpaProductName = buyerProductsView.findViewById(R.id.bpaProductName);
            bpaProductPrice = buyerProductsView.findViewById(R.id.bpaProductPrice);
            bpaImageView = buyerProductsView.findViewById(R.id.bpaImageView);
            bpaAddToCartButton = buyerProductsView.findViewById(R.id.bpaAddToCartButton);
        }
    }

    public BuyerProductsAdapter(BuyerHomeScreen activity, @Nullable OrderedRealmCollection<Product> data){
        super(data, true);
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = activity.getLayoutInflater().inflate(R.layout.activity_buyer_products_adapter, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = getItem(position);
        if (product == null) return;

        // PRODUCT IMAGE RENDERING STARTS HERE
        File getImageDir = activity.getExternalCacheDir();

        File productImage;
        if(product.getProductImageName()!=null){
            productImage = new File(getImageDir, product.getProductImageName());
        } else {
            productImage = null;
        }

        if(productImage!=null && productImage.exists()){
            Picasso.get()
                    .load(productImage)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(holder.bpaImageView);
        } else {
            holder.bpaImageView.setImageResource(R.drawable.button_background);
        }
        // PRODUCT IMAGE RENDERING ENDS HERE

        holder.bpaProductName.setText(product.getItemName());
        holder.bpaProductPrice.setText("PHP " + product.getPrice());

        View.OnClickListener goToDetail = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent productDetail = new Intent(activity, ProductDetail.class);
                productDetail.putExtra("productUUID", product.getProductUUID());
                activity.startActivity(productDetail);
            }
        };

        holder.bpaImageView.setOnClickListener(goToDetail);
        holder.bpaAddToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToCart(product);
            }
        });
    }

    public void addToCart(Product product){
        if (product.getStock() <= 0) {
            Toast.makeText(activity, "This item is out of stock", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences prefs = activity.getSharedPreferences("data", 0);
        String loggedUsername = prefs.getString("loggedUsername", "");

        Realm realm = activity.realm;

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                User user = realm.where(User.class).equalTo("username", loggedUsername).findFirst();
                if (user == null) return;

                CartItem existingItem = null;
                for (CartItem item : user.getUserCart()) {
                    if (item.getProductUUID().equals(product.getProductUUID())) {
                        existingItem = item;
                        break;
                    }
                }

                int currentCartQty = (existingItem != null) ? existingItem.getQuantitySelected() : 0;

                // Prevent adding more than what's actually in stock
                if (currentCartQty + 1 > product.getStock()) {
                    Toast.makeText(activity, "No more stock available", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (existingItem != null) {
                    existingItem.setQuantitySelected(existingItem.getQuantitySelected() + 1);
                } else {
                    CartItem newItem = realm.createObject(CartItem.class);
                    newItem.setProductUUID(product.getProductUUID());
                    newItem.setQuantitySelected(1);
                    user.getUserCart().add(newItem);
                }
            }
        });

        Toast.makeText(activity, product.getItemName() + " added to cart", Toast.LENGTH_SHORT).show();
    }
}
