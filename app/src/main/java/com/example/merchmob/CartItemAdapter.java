package com.example.merchmob;

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

public class CartItemAdapter extends RealmRecyclerViewAdapter<CartItem, CartItemAdapter.ViewHolder> {

    CartScreen activity;
    Realm realm;

    public CartItemAdapter(CartScreen activity, @Nullable OrderedRealmCollection<CartItem> data) {
        super(data, true);
        this.activity = activity;
        this.realm = Realm.getDefaultInstance();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView ciProductName, ciDesc, ciQty, ciPrice;
        ImageButton paProductImage, paDeleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ciProductName = itemView.findViewById(R.id.ci_product_name);
            ciDesc = itemView.findViewById(R.id.ci_desc);
            ciQty = itemView.findViewById(R.id.ci_qty);
            ciPrice = itemView.findViewById(R.id.ci_desc2);
            paProductImage = itemView.findViewById(R.id.pa_product_image);
            paDeleteButton = itemView.findViewById(R.id.pa_delete_button);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = activity.getLayoutInflater().inflate(R.layout.activity_cart_item_adapter, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem cartItem = getItem(position);
        if (cartItem == null) return;

        Product product = realm.where(Product.class).equalTo("productUUID", cartItem.getProductUUID()).findFirst();

        if (product != null) {
            holder.ciProductName.setText(product.getItemName());
            holder.ciDesc.setText(product.getProductDescription());
            holder.ciPrice.setText("Php " + (product.getPrice() * cartItem.getQuantitySelected()));
            holder.ciQty.setText(cartItem.getQuantitySelected() + " selected");

            // IMAGE
            File getImageDir = activity.getExternalCacheDir();
            if (product.getProductImageName() != null) {
                File productImage = new File(getImageDir, product.getProductImageName());
                if (productImage.exists()) {
                    Picasso.get()
                            .load(productImage)
                            .networkPolicy(NetworkPolicy.NO_CACHE)
                            .memoryPolicy(MemoryPolicy.NO_CACHE)
                            .into(holder.paProductImage);
                }
            }
        }

        holder.paDeleteButton.setOnClickListener(v -> {
            realm.executeTransaction(r -> {
                // Restore Stock
                Product p = r.where(Product.class).equalTo("productUUID", cartItem.getProductUUID()).findFirst();
                if (p != null) {
                    p.setStock(p.getStock() + cartItem.getQuantitySelected());
                }
                cartItem.deleteFromRealm();
            });
            Toast.makeText(activity, "Item removed from cart", Toast.LENGTH_SHORT).show();
        });
    }
}
