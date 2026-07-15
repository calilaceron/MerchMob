package com.example.merchmob;

import java.util.Arrays;
import java.util.UUID;

import io.realm.RealmObject;
import io.realm.RealmList;
import io.realm.annotations.PrimaryKey;

public class User extends RealmObject {
    @PrimaryKey
    private String userUUID = UUID.randomUUID().toString();
    String username;
    String password;
    String userDescription;
    String role;
    String userImageName;

    int productsBought;
    int productsSold;
    RealmList<CartItem> userCart;

    public String getUserUUID() {
        return userUUID;
    }

    public void setUserUUID(String userUUID) {
        this.userUUID = userUUID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserDescription() {
        return userDescription;
    }

    public void setUserDescription(String userDescription) {
        this.userDescription = userDescription;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUserImageName() {
        return userImageName;
    }

    public void setUserImageName(String userImageName) {
        this.userImageName = userImageName;
    }

    public RealmList<CartItem> getUserCart() {
        return userCart;
    }

    public void setUserCart(RealmList<CartItem> userCart) {
        this.userCart = userCart;
    }
    public int getProductsBought() {
        return productsBought;
    }

    public void setProductsBought(int productsBought) {
        this.productsBought = productsBought;
    }

    public int getProductsSold() {
        return productsSold;
    }

    public void setProductsSold(int productsSold) {
        this.productsSold = productsSold;
    }

    @Override
    public String toString() {
        return "User{" +
                "userUUID='" + userUUID + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", userDescription='" + userDescription + '\'' +
                ", role='" + role + '\'' +
                ", userImageName='" + userImageName + '\'' +
                ", productsBought=" + productsBought +
                ", productsSold=" + productsSold +
                ", userCart=" + userCart +
                '}';
    }
}
