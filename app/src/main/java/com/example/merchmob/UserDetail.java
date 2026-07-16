package com.example.merchmob;

import android.Manifest;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.listener.multi.BaseMultiplePermissionsListener;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;

import io.realm.Realm;

public class UserDetail extends AppCompatActivity {
    String usernameToView;
    ImageButton udBackButton;
    ImageView udImageView;
    TextView udUsername;
    TextView udRole;
    TextView udDescription;
    SharedPreferences prefs;
    Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        checkPermissions();
    }

    public void checkPermissions(){
        // REQUEST PERMISSIONS for Android 6+
        // THESE PERMISSIONS SHOULD MATCH THE ONES IN THE MANIFEST
        Dexter.withContext(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA

                )
                .withListener(new BaseMultiplePermissionsListener() {
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            // ALL PERMISSIONS ACCEPTED PROCEED
                            init();
                        } else {
                            // NOTIFY ABOUT PERMISSIONS
                            toastRequirePermissions();
                        }
                    }
                })
                .check();
    }

    public void toastRequirePermissions() {
        Toast.makeText(this, "You must provide permissions for app to run", Toast.LENGTH_LONG).show();
        finish();
    }

    public void init(){
        udBackButton = findViewById(R.id.udBackButton);
        udImageView = findViewById(R.id.udImageView);
        udUsername = findViewById(R.id.udUsername);
        udRole = findViewById(R.id.udRole);
        udDescription = findViewById(R.id.udDescription);

        prefs = getSharedPreferences("data", 0);
        realm = Realm.getDefaultInstance();

        usernameToView =prefs.getString("userToView", "");
        User userToView= realm.where(User.class).equalTo("username", usernameToView).findFirst();

        if (userToView != null) {
            udUsername.setText(userToView.getUsername());
            udRole.setText(userToView.getRole());
            udDescription.setText(userToView.getUserDescription());

            File userImage;
            if(userToView.getUserImageName() != null){
                userImage = new File(getExternalCacheDir(), userToView.getUserImageName());
            } else {
                userImage = null;
            }
            if(userImage!=null && userImage.exists()){
                refreshImageView(udImageView, userImage);
            }
        }

        udBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void refreshImageView(ImageView imageView, File savedImage) {
        // ADDS SAVED IMAGE TO THE IMAGEVIEW
        Picasso.get()
                .load(savedImage)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(imageView);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (!realm.isClosed()) {
            realm.close();
        }
    }
}