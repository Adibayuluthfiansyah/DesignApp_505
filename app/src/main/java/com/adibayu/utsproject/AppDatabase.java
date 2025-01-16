package com.adibayu.utsproject;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {ProductModel.class}, version = 3, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ProductDao productDao();

    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Create the new table with all required columns
            database.execSQL("CREATE TABLE IF NOT EXISTS products_new " +
                    "(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "name TEXT, " +
                    "description TEXT, " +
                    "price INTEGER NOT NULL, " +
                    "imageResource INTEGER NOT NULL, " +
                    "category TEXT)");

            // Copy data from old table if it exists
            database.execSQL("INSERT OR IGNORE INTO products_new " +
                    "SELECT id, name, description, price, imageResource, category " +
                    "FROM products");

            // Remove old table
            database.execSQL("DROP TABLE IF EXISTS products");

            // Rename new table to products
            database.execSQL("ALTER TABLE products_new RENAME TO products");
        }
    };

    public static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Add new column for image filename
            database.execSQL("ALTER TABLE products ADD COLUMN image_filename TEXT");
        }
    };
}