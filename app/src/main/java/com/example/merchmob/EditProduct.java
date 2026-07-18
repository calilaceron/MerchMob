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

public class EditProduct extends AppCompatActivity {
    String productEditImageName;
    EditText epProductName;
    EditText epPrice;
    EditText epStock;
    EditText epDescription;
    ImageButton epBackButton;
    ImageView epImageView;
    AppCompatButton epSaveButton;
    Realm realm;
    SharedPreferences prefs;
    Product productToEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_product);
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
                    productEditImageName = System.currentTimeMillis()+".jpeg";
                    File savedImage = saveFile(jpeg, productEditImageName);
                    refreshImageView(epImageView, savedImage);
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
        epProductName = findViewById(R.id.apProductName);
        epPrice = findViewById(R.id.apPrice);
        epStock = findViewById(R.id.apStock);
        epDescription = findViewById(R.id.apDescription);
        epBackButton = findViewById(R.id.epBackButton);
        epSaveButton = findViewById(R.id.apAddButton);
        epImageView = findViewById(R.id.apImageView);

        prefs = getSharedPreferences("data", 0);
        realm = Realm.getDefaultInstance();

        String productToEditUUID = prefs.getString("productToEditUUID", "");
        productToEdit = realm.where(Product.class).equalTo("productUUID", productToEditUUID).findFirst();

        if (productToEdit != null){
            productEditImageName = productToEdit.getProductImageName();
            epProductName.setText(productToEdit.getItemName());
            epPrice.setText(String.valueOf(productToEdit.getPrice()));
            epStock.setText(String.valueOf(productToEdit.getStock()));
            epDescription.setText(productToEdit.getProductDescription());

            File productImage;
            if(productToEdit.getProductImageName()!=null){
                productImage = new File(getExternalCacheDir(), productToEdit.getProductImageName());
            } else {
                productImage = null;
            }
            if(productImage!=null && productImage.exists()){
                refreshImageView(epImageView, productImage);
            }
        }

        epImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });

        epSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editProductSaveClick();
            }
        });

        epBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void editProductSaveClick(){
        String inputtedProductName = epProductName.getText().toString();
        String inputtedPrice = epPrice.getText().toString();
        String inputtedStock = epStock.getText().toString();
        String inputtedDescription = epDescription.getText().toString();

        if(inputtedProductName.isEmpty()){
            Toast.makeText(EditProduct.this, "The product name field must not be blank.", Toast.LENGTH_SHORT).show();
        } else {
            long productSpecificCount = realm.where(Product.class)
                    .equalTo("itemName", inputtedProductName)
                    .equalTo("sellerUUID", productToEdit.getSellerUUID())
                    .notEqualTo("productUUID", productToEdit.getProductUUID())
                    .count();
            boolean productExists = productSpecificCount > 0;

            if (productExists){
                Toast.makeText(EditProduct.this, "You already have another product with this name.", Toast.LENGTH_SHORT).show();
            } else {
                realm.beginTransaction();
                productToEdit.setItemName(inputtedProductName);
                productToEdit.setPrice(Float.parseFloat(inputtedPrice));
                productToEdit.setStock(Integer.parseInt(inputtedStock));
                productToEdit.setProductDescription(inputtedDescription);
                productToEdit.setProductImageName(productEditImageName);
                realm.commitTransaction();

                Toast.makeText(EditProduct.this, "The product has been updated.", Toast.LENGTH_SHORT).show();
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