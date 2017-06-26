package com.mastercard.simplifyapp.objects;

import java.io.Serializable;

/**
 * Created by e069278 on 26/06/2017.
 */
@SuppressWarnings("serial") //hide compiler warnings
public class Transaction implements Serializable {
    String merchantName;
    String merchantCategoryCode;
    String countryCode;
    double transactionAmount;
    String currencyCode;


    public Transaction() {
        super();
    }


}
