package com.adibayu.utsproject;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class SearchResponse {
    @SerializedName("items")
    private List<SearchItem> items;

    public List<SearchItem> getItems() {
        return items;
    }

    public static class SearchItem {
        @SerializedName("title")
        private String title;

        @SerializedName("link")
        private String link;

        @SerializedName("snippet")
        private String description;

        @SerializedName("pagemap")
        private PageMap pagemap;

        public String getTitle() {
            return title;
        }

        public String getLink() {
            return link;
        }

        public String getDescription() {
            return description;
        }

        public PageMap getPagemap() {
            return pagemap;
        }
    }

    public static class PageMap {
        @SerializedName("product")
        private List<Product> products;

        @SerializedName("metatags")
        private List<MetaTags> metatags;

        public List<Product> getProducts() {
            return products;
        }
    }

    public static class Product {
        @SerializedName("name")
        private String name;

        @SerializedName("price")
        private String price;

        @SerializedName("image")
        private String image;

        public String getName() {
            return name;
        }

        public String getPrice() {
            return price;
        }

        public String getImage() {
            return image;
        }
    }

    public static class MetaTags {
        // Add any needed meta tags fields
    }
}