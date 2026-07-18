package com.example.merchmob;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
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
import java.io.FileOutputStream;
import java.io.IOException;

import io.realm.Realm;

public class RegisterScreenP1 extends AppCompatActivity {

    TextView usernameHeading;
    TextView passwordHeading;
    TextView descHeading;
    EditText usernameInput;
    EditText passwordInput;
    EditText confirmInput;
    EditText descInput;
    Button nextButton;
    ImageButton backButton;

    ImageView user_profile;
    byte[] imageBytes;

    Realm realm;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (realm != null && !realm.isClosed()) {
            realm.close();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.register_register_screen_p1);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.pa_product_card), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Realm.init(this);
        realm = Realm.getDefaultInstance();

        usernameHeading = findViewById(R.id.register_username_heading);
        passwordHeading = findViewById(R.id.register_password_heading);
        descHeading = findViewById(R.id.register_description_heading);

        usernameInput = findViewById(R.id.register_username_input);
        passwordInput = findViewById(R.id.register_password_input);
        confirmInput = findViewById(R.id.register_confirm_input);
        descInput = findViewById(R.id.register_description_input);

        checkPermissions();

        nextButton = findViewById(R.id.register_next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkInput();
            }
        });


        backButton = findViewById(R.id.register_1_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        user_profile = findViewById(R.id.register_add_userphoto);
        user_profile.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goImage();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == ImageActivity.RESULT_CODE_IMAGE_TAKEN) {
            if (data != null) {
                imageBytes = data.getByteArrayExtra("rawJpeg");
                if (imageBytes != null) {
                    try {
                        File savedImage = saveFile(imageBytes, "temp_register.jpg");
                        refreshImageView(user_profile, savedImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    private void goImage() {
        Intent intent = new Intent(RegisterScreenP1.this, ImageActivity.class);
        startActivityForResult(intent, 100);
    }
    private void checkInput() {
        String enteredUsername = usernameInput.getText().toString();
        String enteredPassword = passwordInput.getText().toString();

        if (enteredUsername.isEmpty()) {
            Toast.makeText(this, "Username cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (enteredPassword.isEmpty()) {
            Toast.makeText(this, "Password cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!confirmInput.getText().toString().equals(enteredPassword)) {
            Toast.makeText(this, "Confirm password does not match", Toast.LENGTH_SHORT).show();
            return;
        }

        User existingUser = realm.where(User.class).equalTo("username", enteredUsername).findFirst();
        if (existingUser != null) {
            Toast.makeText(this, "User Already Exists", Toast.LENGTH_SHORT).show();
            return;
        }

        User u = new User();
        u.setUsername(enteredUsername);
        u.setPassword(enteredPassword);
        u.setUserDescription(descInput.getText().toString());

        if (imageBytes != null) {
            String filename = u.getUserUUID() + ".jpg";
            try {
                saveFile(imageBytes, filename);
                u.setUserImageName(filename);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        realm.beginTransaction();
        realm.copyToRealmOrUpdate(u);
        realm.commitTransaction();
        Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show();

        goNext(u.getUserUUID());
    }

    private void goNext(String userUUID) {
        Intent intent = new Intent(RegisterScreenP1.this, RegisterScreenP2.class);
        intent.putExtra("USER_UUID", userUUID);
        startActivity(intent);
    }

    public void checkPermissions()
    {

        Dexter.withContext(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA

                )

                .withListener(new BaseMultiplePermissionsListener()
                {
                    public void onPermissionsChecked(MultiplePermissionsReport report)
                    {
                        if (report.areAllPermissionsGranted())
                        {
                            init();
                        }
                        else
                        {
                            toastRequirePermissions();
                        }
                    }
                })
                .check();

    }


    public void toastRequirePermissions()
    {
        Toast.makeText(this, "You must provide permissions for app to run", Toast.LENGTH_LONG).show();
        finish();
    }

    public void init()
    {
        user_profile = findViewById(R.id.register_add_userphoto);



        File getImageDir = getExternalCacheDir();
        File savedImage = new File(getImageDir, "savedImage.jpeg");

        if (savedImage.exists()) {
            refreshImageView(user_profile, savedImage);
        }
    }

    private File saveFile(byte[] jpeg, String filename) throws IOException
    {
        File getImageDir = getExternalCacheDir();

        File savedImage = new File(getImageDir, filename);


        FileOutputStream fos = new FileOutputStream(savedImage);
        fos.write(jpeg);
        fos.close();
        return savedImage;
    }



    private void refreshImageView(ImageView imageView, File savedImage) {

        Picasso.get()
                .load(savedImage)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(imageView);
    }
}

