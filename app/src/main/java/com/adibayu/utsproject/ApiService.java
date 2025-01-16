package com.adibayu.utsproject;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    // Existing endpoints
    @GET("products/category/clothing")
    Call<List<ApiProductModel>> getClothingProducts();

    @GET("products/search")
    Call<List<ApiProductModel>> searchProducts(@Query("q") String query);

    // Google Custom Search endpoint
    @GET("customsearch/v1")
    Call<SearchResponse> searchGoogleProducts(
            @Query("key") String apiKey,
            @Query("cx") String searchEngineId,
            @Query("q") String query,
            @Query("fields") String fields,
            @Query("searchType") String searchType,
            @Query("num") int resultCount
    );
}