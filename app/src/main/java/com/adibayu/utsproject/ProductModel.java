package com.adibayu.utsproject;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

@Entity(tableName = "products")
public class ProductModel implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private String description;
    private int price;
    private int imageResource;
    private String category;

    @ColumnInfo(name = "image_filename")
    private String imageFileName;

    // Constructor for standard use
    public ProductModel(int id, String name, String description, int price, int imageResource, String category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageResource = imageResource;
        this.category = category;
        this.imageFileName = null; // Default value
    }

    // Constructor for API results
    public ProductModel(ApiProductModel apiModel) {
        this.id = apiModel.getId();
        this.name = apiModel.getName();
        this.description = apiModel.getDescription();
        this.price = (int)(apiModel.getPrice() * 15000); // Convert USD to IDR
        this.imageResource = R.drawable.hoodiesci; // Default image
        this.category = apiModel.getCategory();
        this.imageFileName = null;
    }

    protected ProductModel(Parcel in) {
        id = in.readInt();
        name = in.readString();
        description = in.readString();
        price = in.readInt();
        imageResource = in.readInt();
        category = in.readString();
        imageFileName = in.readString();
    }

    public static final Creator<ProductModel> CREATOR = new Creator<ProductModel>() {
        @Override
        public ProductModel createFromParcel(Parcel in) {
            return new ProductModel(in);
        }

        @Override
        public ProductModel[] newArray(int size) {
            return new ProductModel[size];
        }
    };

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getPrice() {
        return price;
    }

    public int getImageResource() {
        return imageResource;
    }

    public String getCategory() {
        return category;
    }

    public String getImageFileName() {
        return imageFileName;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeInt(price);
        dest.writeInt(imageResource);
        dest.writeString(category);
        dest.writeString(imageFileName);
    }
}