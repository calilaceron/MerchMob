package com.example.merchmob;

import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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

public class SellerProductAdapter extends RealmRecyclerViewAdapter<Product, SellerProductAdapter.ViewHolder> {
    SellerDashboard activity;
    Realm realm;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView sdProductName;
        TextView sdProductPrice;
        TextView sdStockTag;
        ImageButton sdImageView;
        ImageButton sdEditButton;
        ImageButton sdDeleteButton;

        public ViewHolder(View sellerProductsView) {
            super(sellerProductsView);

            sdProductName = sellerProductsView.findViewById(R.id.ci_product_name);
            sdProductPrice = sellerProductsView.findViewById(R.id.ci_desc);
            sdStockTag = sellerProductsView.findViewById(R.id.ci_qty);
            sdImageView = sellerProductsView.findViewById(R.id.pa_product_image);
            sdEditButton = sellerProductsView.findViewById(R.id.pa_edit_button);
            sdDeleteButton = sellerProductsView.findViewById(R.id.pa_delete_button);
        }
    }

    public SellerProductAdapter(SellerDashboard activity, @Nullable OrderedRealmCollection<Product> data){
        super(data, true);
        this.activity = activity;
        this.realm = Realm.getDefaultInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = activity.getLayoutInflater().inflate(R.layout.activity_product_adapter, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = getItem(position);
        assert product != null;

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
                    .into(holder.sdImageView);
        } else {
            holder.sdImageView.setImageResource(R.drawable.button_background);
        }
        // PRODUCT IMAGE RENDERING ENDS HERE

        holder.sdProductName.setText(product.getItemName());
        holder.sdProductPrice.setText("PHP " + product.getPrice());
        holder.sdStockTag.setText(product.getStock() + " in stock");

        holder.sdImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent productDetail = new Intent(activity, ProductDetail.class);
                productDetail.putExtra("productUUID", product.getProductUUID());
                activity.startActivity(productDetail);
            }
        });

        holder.sdEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = activity.getSharedPreferences("data", 0);
                prefs.edit().putString("productToEdit", product.getItemName()).apply();
                Intent editIntent = new Intent(activity, EditProduct.class);
                activity.startActivity(editIntent);
            }
        });

        holder.sdDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String productName = product.getItemName();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        product.deleteFromRealm();
                    }
                });
                Toast.makeText(activity, productName + " deleted", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
