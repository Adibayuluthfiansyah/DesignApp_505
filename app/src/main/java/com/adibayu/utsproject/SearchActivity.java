package com.adibayu.utsproject;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import okhttp3.ResponseBody;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private static final String TAG = "SearchActivity";
    private static final String API_KEY = "AIzaSyAU-Ev8gzB-mJvH_jjoh_rHzypZwA8y9OI";
    private static final String SEARCH_ENGINE_ID = "f208e1c363be44825";
    private static final String GOOGLE_BASE_URL = "https://www.googleapis.com/";

    private EditText searchEditText;
    private ImageButton searchButton;
    private RecyclerView searchRecyclerView;
    private ProgressBar searchProgressBar;
    private ProductAdapter searchAdapter;
    private ApiService googleApiService;
    private List<ProductModel> searchResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        initViews();

        String searchQuery = getIntent().getStringExtra("search_query");
        Log.d(TAG, "Query received: " + searchQuery);

        if (searchQuery != null && !searchQuery.isEmpty()) {
            searchEditText.setText(searchQuery);
            performGoogleSearch(searchQuery);
        }

        searchButton.setOnClickListener(v -> {
            String query = searchEditText.getText().toString().trim();
            if (!query.isEmpty()) {
                performGoogleSearch(query);
            } else {
                Toast.makeText(this, "Masukkan kata kunci pencarian", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initViews() {
        searchEditText = findViewById(R.id.searchEditText);
        searchButton = findViewById(R.id.searchButton);
        searchRecyclerView = findViewById(R.id.searchRecyclerView);
        searchProgressBar = findViewById(R.id.searchProgressBar);

        searchResults = new ArrayList<>();
        searchAdapter = new ProductAdapter(searchResults, null);
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchRecyclerView.setAdapter(searchAdapter);
    }

    private int parsePrice(String priceStr) {
        if (priceStr == null) return 0; // Jika harga kosong, kembalikan 0
        try {
            // Hapus semua karakter non-angka dan titik
            String cleanPrice = priceStr.replaceAll("[^0-9.]", "");
            // Ubah string menjadi angka desimal
            double price = Double.parseDouble(cleanPrice);
            // Kembalikan harga dalam bentuk integer
            return (int) price;
        } catch (NumberFormatException e) {
            // Tangani error jika parsing gagal
            Log.e("SearchActivity", "Error parsing price: " + priceStr, e);
            return 0;
        }
    }


    private void performGoogleSearch(String query) {
        searchProgressBar.setVisibility(View.VISIBLE);
        searchResults.clear();
        searchAdapter.notifyDataSetChanged();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GOOGLE_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        googleApiService = retrofit.create(ApiService.class);

        Call<SearchResponse> call = googleApiService.searchGoogleProducts(
                API_KEY,
                SEARCH_ENGINE_ID,
                query + " apparel clothing",
                "items(title,link,snippet,pagemap)",
                "image",
                10
        );

        call.enqueue(new Callback<SearchResponse>() {
            @Override
            public void onResponse(@NonNull Call<SearchResponse> call, @NonNull Response<SearchResponse> response) {
                searchProgressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    List<SearchResponse.SearchItem> items = response.body().getItems();
                    if (items != null) {
                        for (SearchResponse.SearchItem item : items) {
                            String title = item.getTitle();
                            String description = item.getDescription(); // Ganti getSnippet() dengan getDescription()
                            String price = null;

                            if (item.getPagemap() != null && item.getPagemap().getProducts() != null) {
                                SearchResponse.Product product = item.getPagemap().getProducts().get(0);
                                price = product.getPrice();
                            }

                            searchResults.add(new ProductModel(
                                    0,
                                    title,
                                    description != null ? description : "No description available",
                                    price != null ? parsePrice(price) : 0,
                                    R.drawable.longsleve1,
                                    "Apparel"
                            ));
                        }
                        searchAdapter.notifyDataSetChanged();
                    }
                } else {
                    Toast.makeText(SearchActivity.this, "Pencarian tidak ditemukan.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<SearchResponse> call, @NonNull Throwable t) {
                searchProgressBar.setVisibility(View.GONE);
                Toast.makeText(SearchActivity.this, "Kesalahan: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}