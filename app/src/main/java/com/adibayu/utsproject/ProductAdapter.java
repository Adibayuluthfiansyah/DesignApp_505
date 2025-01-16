package com.adibayu.utsproject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    private List<ProductModel> products;
    private OnProductActionListener listener;

    public interface OnProductActionListener {
        void onDeleteProduct(ProductModel product);
        void onEditProduct(ProductModel product);
    }

    public ProductAdapter(List<ProductModel> products, OnProductActionListener listener) {
        this.products = products;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_item_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ProductModel product = products.get(position);
        holder.nameTextView.setText(product.getName());
        holder.descriptionTextView.setText(product.getDescription());
        holder.priceTextView.setText("Rp. " + product.getPrice());

        // Handle image loading
        String imagePath = product.getImageFileName();
        if (imagePath != null && !imagePath.isEmpty()) {
            // Load custom image from storage
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            if (bitmap != null) {
                holder.imageView.setImageBitmap(bitmap);
            } else {
                // Fallback to resource image if loading fails
                handleResourceImage(holder.imageView, product);
            }
        } else {
            // Use resource image
            handleResourceImage(holder.imageView, product);
        }

        // Set click listeners for edit and delete buttons
        holder.editButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditProduct(product);
            }
        });

        holder.deleteButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteProduct(product);
            }
        });
    }

    private void handleResourceImage(ImageView imageView, ProductModel product) {
        int imageResource = product.getImageResource();
        if (imageResource != 0) {
            imageView.setImageResource(imageResource);
        } else {
            imageView.setImageResource(R.drawable.eviltee);
        }
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public void updateProducts(List<ProductModel> newProducts) {
        this.products = newProducts;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView nameTextView;
        TextView descriptionTextView;
        TextView priceTextView;
        Button editButton;
        Button deleteButton;

        ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.productImage);
            nameTextView = view.findViewById(R.id.productName);
            descriptionTextView = view.findViewById(R.id.productDescription);
            priceTextView = view.findViewById(R.id.productPrice);
            editButton = view.findViewById(R.id.editButton);
            deleteButton = view.findViewById(R.id.deleteButton);
        }
    }
}