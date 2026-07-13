package com.example.merchmob;

import java.math.BigDecimal;
import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Product extends RealmObject {
    @PrimaryKey
    private String productUUID = UUID.randomUUID().toString();
    String sellerUUID;
    String itemName;
    BigDecimal price;
    String productDescription;
    int stock;
    String productImageName;

    public String getProductImageName() {
        return productImageName;
    }

    public void setProductImageName(String productImageName) {
        this.productImageName = productImageName;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getSellerUUID() {
        return sellerUUID;
    }

    public void setSellerUUID(String sellerUUID) {
        this.sellerUUID = sellerUUID;
    }

    public String getProductUUID() {
        return productUUID;
    }

    public void setProductUUID(String productUUID) {
        this.productUUID = productUUID;
    }

    @Override
    public String toString() {
        return "Product{" +
                "productUUID='" + productUUID + '\'' +
                ", sellerUUID='" + sellerUUID + '\'' +
                ", itemName='" + itemName + '\'' +
                ", price=" + price +
                ", productDescription='" + productDescription + '\'' +
                ", stock=" + stock +
                ", productImageName='" + productImageName + '\'' +
                '}';
    }
}
