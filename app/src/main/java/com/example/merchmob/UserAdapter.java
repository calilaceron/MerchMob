package com.example.merchmob;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

public class UserAdapter extends RealmRecyclerViewAdapter<User,UserAdapter.ViewHolder> {

    Admin activity;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView uaUsername;
        TextView uaJoinDate;
        TextView uaRole;
        TextView uaProductInfo;
        ImageButton uaImageButton;

        public ViewHolder(View userView){
            super(userView);

            uaImageButton = userView.findViewById(R.id.uaImageButton);
            uaUsername = userView.findViewById(R.id.uaUsername);
            uaJoinDate = userView.findViewById(R.id.uaJoinDate);
            uaRole = userView.findViewById(R.id.uaRole);
            uaProductInfo = userView.findViewById(R.id.uaProductInfo);
        }
    }

    public UserAdapter(Admin activity, @Nullable OrderedRealmCollection<User> data){
        super(data,true);
        this.activity=activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = activity.getLayoutInflater().inflate(R.layout.activity_user_adapter, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = getItem(position);

        // USER AVATAR RENDERING STARTS HERE
        File getImageDir =activity.getExternalCacheDir();

        File userAvatar;
        assert user != null;
        if(user.getUserImageName()!=null){
            userAvatar = new File(getImageDir, user.getUserImageName());
        } else {
            userAvatar = null;
        }

        if(userAvatar!=null && userAvatar.exists()){
            Picasso.get()
                    .load(userAvatar)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(holder.uaImageButton);
        } else {
            holder.uaImageButton.setImageResource(R.drawable.admin_user_icon);
        }
        // USER AVATAR RENDERING ENDS HERE

        holder.uaUsername.setText(user.getUsername());
        holder.uaRole.setText(user.getRole());
        if(user.getRole().equals("Seller")){
            holder.uaProductInfo.setText(user.getProductsSold() + " Products");
        }else{
            holder.uaProductInfo.setText(user.getProductsBought() + " Products");
        }
    }
}