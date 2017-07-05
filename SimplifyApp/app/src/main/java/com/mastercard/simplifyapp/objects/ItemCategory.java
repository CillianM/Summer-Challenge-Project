package com.mastercard.simplifyapp.objects;

import java.util.ArrayList;

/**
 * Created by Cillian on 05/07/2017.
 */

public class ItemCategory {

    private String id;
    private String name;
    private ArrayList<StoreItem> items;

    public ItemCategory(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<StoreItem> getItems() {
        return items;
    }

    public void setItems(ArrayList<StoreItem> items) {
        this.items = items;
    }
}
