package com.mastercard.simplifyapp.objects;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by e069278 on 26/06/2017.
 */
@SuppressWarnings("serial") //hide compiler warnings
public class Transaction implements Serializable {
    private String methodOfPay;
    private String id;
    private double transactionAmount;
    private String customerId;
    private String date;
    private String items;


    public Transaction() {
        super();
    }

    public Transaction(String id, double transactionAmount, String customerId, String items, String methodOfPay, String date) {
        this.id = id;
        this.transactionAmount = transactionAmount;
        this.items = items;
        this.customerId = customerId;
        this.date = date;
        this.methodOfPay = methodOfPay;
    }

    public Transaction(double transactionAmount, String customerId, String items, String methodOfPay) {
        this.transactionAmount = transactionAmount;
        this.customerId = customerId;
        this.items = items;
        this.date = new Date().toString();
        this.methodOfPay = methodOfPay;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
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

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerName) {
        this.customerId = customerName;
    }

    public String getMethodOfPay() {
        return methodOfPay;
    }

    public void setMethodOfPay(String methodOfPay) {
        this.methodOfPay = methodOfPay;
    }
}
