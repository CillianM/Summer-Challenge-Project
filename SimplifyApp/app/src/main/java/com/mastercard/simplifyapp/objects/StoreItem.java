package com.mastercard.simplifyapp.objects;

/**
 * Created by Cillian on 10/05/2017.
 */

public class StoreItem{
    private String name;
    private String description;
    private double price;
    private int quantity;


    public StoreItem(String name, String description, int price) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = 1;
    }
    public StoreItem(String name, String description, int price, int quantity) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }




    public String getPriceString() {
        return String.format("%.2f",price);
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

}
