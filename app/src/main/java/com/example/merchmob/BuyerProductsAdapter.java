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
    SharedPreferences prefs;
    Realm realm;

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

        // PRODUCT IMAGE RENDERING STARTS HERE
        File getImageDir = activity.getExternalCacheDir();

        File productImage;
        assert product != null;
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

        holder.bpaImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent productDetail = new Intent(activity, ProductDetail.class);
                productDetail.putExtra("productUUID", product.getProductUUID());
                activity.startActivity(productDetail);
            }
        });

        holder.bpaAddToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToCartButtonClick(product);
            }
        });
    }

    public void addToCartButtonClick(Product product){
        prefs = activity.getSharedPreferences("data", 0);
        String loggedUsername = prefs.getString("loggedUsername", "");

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                User user = realm.where(User.class).equalTo("username", loggedUsername).findFirst();

                CartItem existingItem = null;
                for(CartItem item : user.getUserCart()){
                    if(item.getProductUUID().equals(product.getProductUUID())){
                        existingItem = item;
                        break;
                    }
                }

                if(existingItem!=null){
                    existingItem.setQuantitySelected(existingItem.getQuantitySelected()+1);
                } else {
                    CartItem newItem = new CartItem();
                    newItem.setProductUUID(product.getProductUUID());
                    newItem.setQuantitySelected(1);
                    user.getUserCart().add(newItem);
                }
            }
        });
        Toast.makeText(activity, product.getItemName() + " added to cart", Toast.LENGTH_SHORT).show();
    }
}

