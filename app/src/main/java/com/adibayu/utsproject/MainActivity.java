package com.adibayu.utsproject;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import androidx.appcompat.widget.SearchView;



public class MainActivity extends AppCompatActivity {

    private EditText orderET, orderET2, orderET3, orderET4, orderET5, orderET6;
    private TextView totalPriceTV;
    private Button openPaymentButton;
    private Button logoutButton;
    private int totalPrice = 0;
    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private List<ProductModel> productList;
    private DatabaseHelper dbHelper;
    private Button datePickerButton;
    private RadioGroup sizeRadioGroup;
    private CheckBox giftWrapCheckbox;
    private AppDatabase db;
    private ApiService apiService;
    private String selectedSize = "";
    private boolean isGiftWrap = false;
    private String selectedDate = "";
    private final Executor executor = Executors.newSingleThreadExecutor();

    private static final String[] itemNames = {"Angel Longsleve", "Four Hoodie", "Tribal Longsleve", "Sunday Hoodie", "Evil Tee", "Sci-Fi Hoodie"};
    private static final int[] itemPrices = {150000, 200000, 100000, 150000, 200000, 250000};
    public static int[] itemQuantities = new int[6];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Initialize product list first
        productList = new ArrayList<>();

        // Initialize Room database
        db = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "product-database")
                .addMigrations(AppDatabase.MIGRATION_1_2, AppDatabase.MIGRATION_2_3)
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();

        // Initialize views
        orderET = findViewById(R.id.orderET);
        orderET2 = findViewById(R.id.orderET2);
        orderET3 = findViewById(R.id.orderET3);
        orderET4 = findViewById(R.id.orderET4);
        orderET5 = findViewById(R.id.orderET5);
        orderET6 = findViewById(R.id.orderET6);
        totalPriceTV = findViewById(R.id.totalPriceTV);
        openPaymentButton = findViewById(R.id.openPaymentButton);
        logoutButton = findViewById(R.id.logoutButton);
        sizeRadioGroup = findViewById(R.id.sizeRadioGroup);
        giftWrapCheckbox = findViewById(R.id.giftWrapCheckbox);

        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.isEmpty()) {
                    // Intent untuk mengarahkan ke SearchActivity
                    Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                    intent.putExtra("search_query", query); // Kirim query ke SearchActivity
                    startActivity(intent);
                    return true;
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.productRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize adapter with the product list
        adapter = new ProductAdapter(productList, new ProductAdapter.OnProductActionListener() {
            @Override
            public void onDeleteProduct(ProductModel product) {
                deleteProduct(product);
            }

            @Override
            public void onEditProduct(ProductModel product) {
                editProduct(product);
            }
        });
        recyclerView.setAdapter(adapter);

        // Initialize other UI components
        sizeRadioGroup = findViewById(R.id.sizeRadioGroup);
        giftWrapCheckbox = findViewById(R.id.giftWrapCheckbox);
        datePickerButton = findViewById(R.id.datePickerButton);
        dbHelper = new DatabaseHelper(this);

        // Load products from database
        loadProductsFromDatabase();

        // Initialize Retrofit after database is loaded
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.example.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        // Add floating action button for adding new products
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddEditProductActivity.class);
                startActivity(intent);
            }
        });

        datePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        openPaymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateSelectedOptions();
                Intent intent = new Intent(MainActivity.this, PaymentActivity.class);
                intent.putExtra("total_price", totalPrice);
                intent.putExtra("selected_size", selectedSize);
                intent.putExtra("is_gift_wrap", isGiftWrap);
                intent.putExtra("selected_date", selectedDate); // Add this line
                intent.putExtra("ITEM_NAMES", itemNames);
                intent.putExtra("ITEM_QUANTITIES", itemQuantities);
                intent.putExtra("ITEM_PRICES", itemPrices);
                startActivity(intent);
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        orderET.setText("0");
        orderET2.setText("0");
        orderET3.setText("0");
        orderET4.setText("0");
        orderET5.setText("0");
        orderET6.setText("0");

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Restore state if there's saved instance
        if (savedInstanceState != null) {
            ArrayList<ProductModel> savedProducts = savedInstanceState.getParcelableArrayList("products");
            if (savedProducts != null) {
                productList.clear();
                productList.addAll(savedProducts);
                adapter.notifyDataSetChanged();
            }
        }
    }



    private void updateSelectedOptions() {
        RadioGroup sizeGroup = findViewById(R.id.sizeRadioGroup);
        CheckBox giftWrapCheckbox = findViewById(R.id.giftWrapCheckbox);

        // Get selected size
        int selectedId = sizeGroup.getCheckedRadioButtonId();
        if (selectedId == R.id.sizeS) selectedSize = "S";
        else if (selectedId == R.id.sizeM) selectedSize = "M";
        else if (selectedId == R.id.sizeL) selectedSize = "L";

        // Get gift wrap status
        isGiftWrap = giftWrapCheckbox.isChecked();
    }


    private void deleteProduct(ProductModel product) {
        executor.execute(() -> {
            try {
                db.productDao().delete(product);
                runOnUiThread(() -> {
                    productList.remove(product);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(this, "Product deleted successfully", Toast.LENGTH_SHORT).show();
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error deleting product: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
            }
        });
    }


    private void setupSearchButton() {
        findViewById(R.id.searchButton).setOnClickListener(v -> {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
        });
    }

    private void editProduct(ProductModel product) {
        Intent intent = new Intent(this, AddEditProductActivity.class);
        intent.putExtra("product", product);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProductsFromDatabase();
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                        datePickerButton.setText(selectedDate);
                    }
                }, year, month, day);
        datePickerDialog.show();
    }

    private void loadProductsFromDatabase() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    productList.clear();
                    List<ProductModel> dbProducts = db.productDao().getAllProducts();

                    if (dbProducts == null || dbProducts.isEmpty()) {
                        // Add sample products
                        ProductModel product1 = new ProductModel(1, "Angel Longsleve", "Premium Cotton Material", 150000, R.drawable.longsleve1, "Longsleve");
                        ProductModel product2 = new ProductModel(2, "Four Hoodie", "Comfortable Hoodie Design", 200000, R.drawable.hoodiefour, "Hoodie");
                        ProductModel product3 = new ProductModel(3, "Tribal Longsleve", "Cool Design", 100000, R.drawable.longslvtribal, "Longsleve");
                        ProductModel product4 = new ProductModel(4, "Sunday Hoodie", "Relaxed Fit", 150000, R.drawable.hoodiesunday, "Hoodie");
                        ProductModel product5 = new ProductModel(5, "Evil Tee", "Premium Cotton", 200000, R.drawable.eviltee, "T-Shirt");
                        ProductModel product6 = new ProductModel(6, "Sci-Fi Hoodie", "Limited Edition", 250000, R.drawable.hoodiesci, "Hoodie");

                        db.productDao().insert(product1);
                        db.productDao().insert(product2);
                        db.productDao().insert(product3);
                        db.productDao().insert(product4);
                        db.productDao().insert(product5);
                        db.productDao().insert(product6);

                        productList.add(product1);
                        productList.add(product2);
                        productList.add(product3);
                        productList.add(product4);
                        productList.add(product5);
                        productList.add(product6);
                    } else {
                        productList.addAll(dbProducts);
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (adapter != null) {
                                adapter.notifyDataSetChanged();
                            }
                        }
                    });

                } catch (Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "Error loading products: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    public void plusOrder(View view) {
        EditText targetEditText = null;
        int index = -1;
        int id = view.getId();

        if (id == R.id.plusButton1) {
            targetEditText = orderET;
            index = 0;
        } else if (id == R.id.plusButton2) {
            targetEditText = orderET2;
            index = 1;
        } else if (id == R.id.plusButton3) {
            targetEditText = orderET3;
            index = 2;
        } else if (id == R.id.plusButton4) {
            targetEditText = orderET4;
            index = 3;
        } else if (id == R.id.plusButton5) {
            targetEditText = orderET5;
            index = 4;
        } else if (id == R.id.plusButton6) {
            targetEditText = orderET6;
            index = 5;
        }

        if (targetEditText != null && index != -1) {
            int currentValue = getOrderValue(targetEditText);
            itemQuantities[index] = currentValue + 1;
            targetEditText.setText(String.valueOf(itemQuantities[index]));
            updateTotalPrice();
        }
    }


    public void minusOrder(View view) {
        EditText targetEditText = null;
        int index = -1;
        int id = view.getId();

        if (id == R.id.minusButton1) {
            targetEditText = orderET;
            index = 0;
        } else if (id == R.id.minusButton2) {
            targetEditText = orderET2;
            index = 1;
        } else if (id == R.id.minusButton3) {
            targetEditText = orderET3;
            index = 2;
        } else if (id == R.id.minusButton4) {
            targetEditText = orderET4;
            index = 3;
        } else if (id == R.id.minusButton5) {
            targetEditText = orderET5;
            index = 4;
        } else if (id == R.id.minusButton6) {
            targetEditText = orderET6;
            index = 5;
        }

        if (targetEditText != null && index != -1) {
            int currentValue = getOrderValue(targetEditText);
            if (currentValue > 0) {
                itemQuantities[index] = currentValue - 1;
                targetEditText.setText(String.valueOf(itemQuantities[index]));
                updateTotalPrice();
            }
        }
    }


    private void updateTotalPrice() {
        totalPrice = 0;
        for (int i = 0; i < itemQuantities.length; i++) {
            totalPrice += itemQuantities[i] * itemPrices[i];
        }
        totalPriceTV.setText("Rp. " + totalPrice);
    }

    private int getOrderValue(EditText editText) {
        try {
            return Integer.parseInt(editText.getText().toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }



    private void logout() {
        SharedPreferences sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("isLoggedIn");
        editor.apply();

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

        Toast.makeText(this, "Logout berhasil", Toast.LENGTH_SHORT).show();
    }




    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("products", new ArrayList<>(productList));
    }
}