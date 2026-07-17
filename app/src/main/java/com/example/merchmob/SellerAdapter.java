package com.example.merchmob;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;

public class SellerAdapter extends RealmRecyclerViewAdapter<User, SellerAdapter.ViewHolder> {
    BuyerHomeScreen activity;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView bwsSellerName;
        RecyclerView bwsSellerRecyclerView;
        BuyerProductsAdapter buyerProductsAdapter;
        Realm realm;

        public ViewHolder(View sellerView){
            super(sellerView);

            bwsSellerName = sellerView.findViewById(R.id.bwsSellerName);
            bwsSellerRecyclerView = sellerView.findViewById(R.id.bwsSellerRecyclerView);

            realm = Realm.getDefaultInstance();
        }
    }

    public SellerAdapter(BuyerHomeScreen activity, @Nullable OrderedRealmCollection<User> data){
        super(data, true);
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = activity.getLayoutInflater().inflate(R.layout.activity_seller_adapter, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User seller = getItem(position);

        holder.bwsSellerName.setText(seller.getUsername());

        // SELLING PRODUCTS RECYCLER VIEW STARTS HERE
        LinearLayoutManager sellerLayoutManager = new LinearLayoutManager(activity);
        sellerLayoutManager.setOrientation(RecyclerView.VERTICAL);
        holder.bwsSellerRecyclerView.setLayoutManager(sellerLayoutManager);

        RealmResults<Product> sellerProductList = holder.realm.where(Product.class).equalTo("sellerUUID", seller.getUserUUID()).findAll();
        holder.buyerProductsAdapter = new BuyerProductsAdapter(activity, sellerProductList);
        holder.bwsSellerRecyclerView.setAdapter(holder.buyerProductsAdapter);
        // SELLING PRODUCTS RECYCLER VIEW ENDS HERE

    }
}
