package com.mastercard.simplifyapp.objects;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by e069278 on 26/06/2017.
 */
@SuppressWarnings("serial") //hide compiler warnings
public class Transaction implements Serializable {
    private String id;
    private double transactionAmount;
    private String customerName;
    private Date date;
    private String items;


    public Transaction() {
        super();
    }

    public Transaction(String id, double transactionAmount,String customerName, String items) {
        this.id = id;
        this.transactionAmount = transactionAmount;
        this.items = items;
        this.customerName = customerName;
        this.date = new Date();
    }

    public Transaction(double transactionAmount, String customerName, String items) {
        this.transactionAmount = transactionAmount;
        this.customerName =customerName;
        this.items = items;
        this.date = new Date();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(double transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getItems() {
        return items;
    }

    public void setItems(String items) {
        this.items = items;
    }

    public static String[] getItemsList(String items)
    {
        return items.split(",");
    }

    public static String getItemsString(String[] items)
    {
        String itemsString = "";
        for (String item : items) {
            itemsString += item + ",";
        }
        return itemsString.substring(0,items.length -1);
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
}
