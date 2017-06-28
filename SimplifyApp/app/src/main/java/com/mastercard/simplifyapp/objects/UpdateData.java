package com.mastercard.simplifyapp.objects;

import java.util.ArrayList;

/**
 * Created by e069278 on 28/06/2017.
 */

public class UpdateData {
    ArrayList<StoreItem> items;
    double total;

    public UpdateData(ArrayList<StoreItem> items, double total) {
        this.items = items;
        this.total=total;
    }

    public ArrayList<StoreItem> getItems() {
        return items;
    }

    public void setItems(ArrayList<StoreItem> items) {
        this.items = items;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
