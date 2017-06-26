package com.mastercard.simplifyapp.objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by e069278 on 26/06/2017.
 */
@SuppressWarnings("serial") //hide compiler warnings
public class Transaction implements Serializable {
    String customerName;
    String merchantName;
    String merchantCategoryCode;
    String countryCode;
    double transactionAmount;
    String currencyCode;
    Date date;
    String items;


    public Transaction() {
        super();
    }

    public Transaction(String customerName, String merchantName, String merchantCategoryCode, String countryCode, double transactionAmount, String currencyCode,String items) {
        this.customerName = customerName;
        this.merchantName = merchantName;
        this.merchantCategoryCode = merchantCategoryCode;
        this.countryCode = countryCode;
        this.transactionAmount = transactionAmount;
        this.currencyCode = currencyCode;
        this.date = new Date();
        this.items = items;
    }

    public Transaction(String customerName, String merchantName, double transactionAmount, String items) {
        this.customerName = customerName;
        this.merchantName = merchantName;
        this.transactionAmount = transactionAmount;
        this.items = items;

        merchantCategoryCode = "5204";
        countryCode = "IE";
        currencyCode = "EUR";
        date = new Date();
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getMerchantCategoryCode() {
        return merchantCategoryCode;
    }

    public void setMerchantCategoryCode(String merchantCategoryCode) {
        this.merchantCategoryCode = merchantCategoryCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public double getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(double transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
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
}
