package com.example.merchmob;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
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
import java.io.FileOutputStream;
import java.io.IOException;

import io.realm.Realm;

public class EditAccount extends AppCompatActivity {
    String loggedUsername, userEditImageName;
    EditText editUsername;
    EditText editPassword;
    EditText editDescription;
    ImageView editImage;
    ImageButton editBackButton;
    AppCompatButton editSaveButton;
    Realm realm;
    SharedPreferences prefs;
    User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_account);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.bg), (v, insets) -> {
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

    // CAMERA IMPLEMENTATION STARTS HERE
    public static int REQUEST_CODE_IMAGE_SCREEN = 0;

    public void takePhoto() {
        Intent i = new Intent(this, ImageActivity.class);
        startActivityForResult(i, REQUEST_CODE_IMAGE_SCREEN);
    }

    // SINCE WE USE startForResult(), CODE WILL TRIGGER THIS ONCE NEXT SCREEN CALLS finish()
    @Override
    public void onActivityResult(int requestCode, int responseCode, Intent data) {
        super.onActivityResult(requestCode, responseCode, data);

        if (requestCode==REQUEST_CODE_IMAGE_SCREEN) {
            if (responseCode==ImageActivity.RESULT_CODE_IMAGE_TAKEN){
                // RECEIVE THE RAW JPEG FROM ImageActivity
                byte[] jpeg = data.getByteArrayExtra("rawJpeg");

                try {
                    // SAVE RAW IMAGE TO FILE
                    userEditImageName = System.currentTimeMillis()+".jpeg";
                    File savedImage = saveFile(jpeg, userEditImageName);
                    refreshImageView(editImage, savedImage);
                } catch(Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private File saveFile(byte[] jpeg, String name) throws IOException {
        // ROOT DIRECTORY FOR IMAGES
        File getImageDir = getExternalCacheDir();

        // SAMPLE
        File savedImage = new File(getImageDir, name);

        FileOutputStream fos = new FileOutputStream(savedImage);
        fos.write(jpeg);
        fos.close();
        return savedImage;
    }

    // CAMERA IMPLEMENTATION ENDS HERE

    private void refreshImageView(ImageView imageView, File savedImage) {
        // ADDS SAVED IMAGE TO THE IMAGEVIEW
        Picasso.get()
                .load(savedImage)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(imageView);
    }

    public void init(){
        editUsername = findViewById(R.id.editUsername);
        editPassword = findViewById(R.id.editPassword);
        editDescription = findViewById(R.id.editDescription);
        editBackButton = findViewById(R.id.editBackButton);
        editSaveButton = findViewById(R.id.editSaveButton);
        editImage = findViewById(R.id.editImage);

        prefs = getSharedPreferences("data", 0);
        realm = Realm.getDefaultInstance();

        loggedUsername = prefs.getString("loggedUsername", "");
        currentUser = realm.where(User.class).equalTo("username", loggedUsername).findFirst();

        if (currentUser != null) {
            userEditImageName = currentUser.getUserImageName();
            editUsername.setText(currentUser.getUsername());
            editPassword.setText(currentUser.getPassword());
            editDescription.setText(currentUser.getUserDescription());

            File userImage;
            if(currentUser.getUserImageName() != null){
                userImage = new File(getExternalCacheDir(), currentUser.getUserImageName());
            } else {
                userImage = null;
            }
            if(userImage!=null && userImage.exists()){
                refreshImageView(editImage, userImage);
            }
        }

        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });

        editSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editSaveClick();
            }
        });

        editBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void editSaveClick(){
        String inputtedUsername = editUsername.getText().toString();
        String inputtedPassword = editPassword.getText().toString();
        String inputtedDescription = editDescription.getText().toString();

        if(inputtedUsername.isEmpty()){
            Toast.makeText(EditAccount.this, "The username field must not be blank.", Toast.LENGTH_SHORT).show();
        } else {
            User otherUser = realm.where(User.class)
                    .equalTo("username", inputtedUsername)
                    .notEqualTo("userUUID", currentUser.getUserUUID())
                    .findFirst();

            if (otherUser != null) {
                Toast.makeText(EditAccount.this, "The username already exists.", Toast.LENGTH_SHORT).show();
            } else {
                realm.beginTransaction();
                currentUser.setUsername(inputtedUsername);
                currentUser.setPassword(inputtedPassword);
                currentUser.setUserDescription(inputtedDescription);
                currentUser.setUserImageName(userEditImageName);
                realm.commitTransaction();

                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("loggedUsername", inputtedUsername);
                editor.apply();

                Toast.makeText(EditAccount.this, "The account has been updated.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    public void onDestroy() {
        super.onDestroy();

        if (!realm.isClosed()) {
            realm.close();
        }
    }
}