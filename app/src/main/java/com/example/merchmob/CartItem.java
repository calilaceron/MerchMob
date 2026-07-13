package com.example.merchmob;

import io.realm.RealmObject;

public class CartItem extends RealmObject {
    String productUUID;
    int quantitySelected;
    public String getProductUUID() {
        return productUUID;
    }

    public void setProductUUID(String productUUID) {
        this.productUUID = productUUID;
    }

    public int getQuantitySelected() {
        return quantitySelected;
    }

    public void setQuantitySelected(int quantitySelected) {
        this.quantitySelected = quantitySelected;
    }

    @Override
    public String toString() {
        return "CartItem{" +
                "productUUID='" + productUUID + '\'' +
                ", quantitySelected=" + quantitySelected +
                '}';
    }
}
