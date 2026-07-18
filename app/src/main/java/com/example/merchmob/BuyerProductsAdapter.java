package com.example.merchmob;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;

import io.realm.OrderedRealmCollection;
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
        holder.bpaAddToCartButton.setOnClickListener(goToDetail);
    }
}
