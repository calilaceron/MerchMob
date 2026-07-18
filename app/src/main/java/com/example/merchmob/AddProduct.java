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

public class AddProduct extends AppCompatActivity {
    String productImageName;
    EditText apProductName;
    EditText apPrice;
    EditText apStock;
    EditText apDescription;
    ImageButton apBackButton;
    ImageView apImageView;
    AppCompatButton apAddButton;
    Realm realm;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_product);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.bg), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        checkPermissions();
    }

    public void checkPermissions(){
        Dexter.withContext(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA
                )
                .withListener(new BaseMultiplePermissionsListener() {
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            init();
                        } else {
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

    public static int REQUEST_CODE_IMAGE_SCREEN = 0;

    public void takePhoto() {
        Intent i = new Intent(this, ImageActivity.class);
        startActivityForResult(i, REQUEST_CODE_IMAGE_SCREEN);
    }

    @Override
    public void onActivityResult(int requestCode, int responseCode, Intent data) {
        super.onActivityResult(requestCode, responseCode, data);

        if (requestCode==REQUEST_CODE_IMAGE_SCREEN) {
            if (responseCode==ImageActivity.RESULT_CODE_IMAGE_TAKEN){
                byte[] jpeg = data.getByteArrayExtra("rawJpeg");

                try {
                    productImageName = System.currentTimeMillis()+".jpeg";
                    File savedImage = saveFile(jpeg, productImageName);
                    refreshImageView(apImageView, savedImage);
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private File saveFile(byte[] jpeg, String name) throws IOException {
        File getImageDir = getExternalCacheDir();
        File savedImage = new File(getImageDir, name);
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

    public void init(){
        apProductName = findViewById(R.id.apProductName);
        apPrice = findViewById(R.id.apPrice);
        apStock = findViewById(R.id.apStock);
        apDescription = findViewById(R.id.apDescription);
        apBackButton = findViewById(R.id.apBackButton);
        apAddButton = findViewById(R.id.apAddButton);
        apImageView = findViewById(R.id.apImageView);

        prefs = getSharedPreferences("data", 0);
        realm = Realm.getDefaultInstance();

        apImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });

        apAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProductSaveClick();
            }
        });

        apBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void addProductSaveClick(){
        String inputtedProductName = apProductName.getText().toString();
        String inputtedPrice = apPrice.getText().toString();
        String inputtedStock = apStock.getText().toString();
        String inputtedDescription = apDescription.getText().toString();

        if(inputtedProductName.isEmpty() || inputtedPrice.isEmpty() || inputtedStock.isEmpty()){
            Toast.makeText(AddProduct.this, "Product name, price and stock must not be blank.", Toast.LENGTH_SHORT).show();
        } else {
            long productSpecificCount = realm.where(Product.class).equalTo("itemName", inputtedProductName).count();
            boolean productExists = productSpecificCount > 0;

            if (productExists){
                Toast.makeText(AddProduct.this, "The product name already exists.", Toast.LENGTH_SHORT).show();
            } else {
                String loggedUsername = prefs.getString("loggedUsername", "");
                User seller = realm.where(User.class).equalTo("username", loggedUsername).findFirst();
                
                if (seller == null) {
                    Toast.makeText(this, "Seller not found. Please log in again.", Toast.LENGTH_SHORT).show();
                    return;
                }

                realm.executeTransaction(r -> {
                    Product newProduct = r.createObject(Product.class, java.util.UUID.randomUUID().toString());
                    newProduct.setItemName(inputtedProductName);
                    newProduct.setPrice(Float.parseFloat(inputtedPrice));
                    newProduct.setStock(Integer.parseInt(inputtedStock));
                    newProduct.setProductDescription(inputtedDescription);
                    newProduct.setProductImageName(productImageName);
                    newProduct.setSellerUUID(seller.getUserUUID());
                });

                Toast.makeText(AddProduct.this, "The product has been added.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (realm != null && !realm.isClosed()) {
            realm.close();
        }
    }
}