package com.example.merchmob;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

public class UserAdapter extends RealmRecyclerViewAdapter<User,UserAdapter.ViewHolder> {

    Admin activity;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView uaInitial;
        TextView uaUsername;
        TextView uaJoinDate;
        TextView uaRole;
        TextView uaProductInfo;

        public ViewHolder(View userView){
            super(userView);

            uaInitial = userView.findViewById(R.id.uaInitial);
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

        if (user != null) {
            holder.uaInitial.setText(String.valueOf(user.getUsername().charAt(0)));
            holder.uaUsername.setText(user.getUsername());
            holder.uaRole.setText(user.getRole());
            if(user.getRole().equals("Seller")){
                holder.uaProductInfo.setText(user.getProductsSold() + " Products");
            }else{
                holder.uaProductInfo.setText(user.getProductsBought() + " Products");
            }
        }
    }

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_user_adapter);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//    }
}