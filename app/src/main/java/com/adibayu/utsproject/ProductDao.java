package com.adibayu.utsproject;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface ProductDao {
    @Query("SELECT * FROM products")
    List<ProductModel> getAllProducts();

    @Query("SELECT * FROM products WHERE id = :id")
    ProductModel getProduct(int id);

    @Insert
    void insert(ProductModel product);

    @Update
    void update(ProductModel product);

    @Delete
    void delete(ProductModel product);
}