package com.adibayu.utsproject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;

public class AddEditProductActivity extends AppCompatActivity {
    private EditText nameET, descriptionET, priceET;
    private Button saveButton, selectImageButton;
    private ImageView productImageView;
    private AppDatabase db;
    private ProductModel editProduct;
    private Uri selectedImageUri;
    private String imagePath; // Path to stored image

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {
                        try {
                            // Generate unique filename
                            String fileName = "product_" + System.currentTimeMillis() + ".jpg";

                            // Save image and get its path
                            imagePath = saveImageToInternalStorage(selectedImageUri, fileName);

                            // Display image
                            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                            productImageView.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            Toast.makeText(this, "Error saving image: " + e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
    );

    private String saveImageToInternalStorage(Uri imageUri, String fileName) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(imageUri);
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

        // Create directory for product images
        File directory = new File(getFilesDir(), "product_images");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Create file to save the image
        File file = new File(directory, fileName);
        FileOutputStream fos = new FileOutputStream(file);

        // Compress and save the image
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
        fos.flush();
        fos.close();
        inputStream.close();

        return file.getAbsolutePath();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        db = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "product-database")
                .allowMainThreadQueries()
                .build();

        // Initialize views
        nameET = findViewById(R.id.productNameET);
        descriptionET = findViewById(R.id.productDescriptionET);
        priceET = findViewById(R.id.productPriceET);
        saveButton = findViewById(R.id.saveButton);
        selectImageButton = findViewById(R.id.selectImageButton);
        productImageView = findViewById(R.id.productImageView);

        // Check if editing existing product
        editProduct = getIntent().getParcelableExtra("product");
        if (editProduct != null) {
            // Edit mode
            nameET.setText(editProduct.getName());
            descriptionET.setText(editProduct.getDescription());
            priceET.setText(String.valueOf(editProduct.getPrice()));

            // Load existing image if available
            String savedImagePath = editProduct.getImageFileName();
            if (savedImagePath != null && !savedImagePath.isEmpty()) {
                imagePath = savedImagePath;
                Bitmap bitmap = BitmapFactory.decodeFile(savedImagePath);
                if (bitmap != null) {
                    productImageView.setImageBitmap(bitmap);
                }
            } else {
                // Show default image
                productImageView.setImageResource(R.drawable.longsleve1);
            }
        }

        selectImageButton.setOnClickListener(v -> openImagePicker());
        saveButton.setOnClickListener(v -> saveProduct());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void saveProduct() {
        String name = nameET.getText().toString();
        String description = descriptionET.getText().toString();
        int price;

        try {
            price = Integer.parseInt(priceET.getText().toString());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid price", Toast.LENGTH_SHORT).show();
            return;
        }

        if (name.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (editProduct != null) {
            // Update existing product
            editProduct.setName(name);
            editProduct.setDescription(description);
            editProduct.setPrice(price);
            editProduct.setImageFileName(imagePath);
            db.productDao().update(editProduct);
            Toast.makeText(this, "Product updated successfully", Toast.LENGTH_SHORT).show();
        } else {
            // Add new product
            ProductModel newProduct = new ProductModel(0, name, description, price,
                    R.drawable.longsleve1, "default");
            newProduct.setImageFileName(imagePath);
            db.productDao().insert(newProduct);
            Toast.makeText(this, "Product added successfully", Toast.LENGTH_SHORT).show();
        }

        finish();
    }
}