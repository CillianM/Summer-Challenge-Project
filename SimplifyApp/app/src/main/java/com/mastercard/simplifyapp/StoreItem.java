package com.mastercard.simplifyapp;

/**
 * Created by Cillian on 10/05/2017.
 */

public class StoreItem {

    public StoreItem(String name, String description, int price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    private String name;
    private String description;
    private int price;


    public String getPriceString() {
        return Integer.toString(price);
    }
}
