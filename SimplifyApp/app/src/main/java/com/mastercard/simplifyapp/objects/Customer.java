package com.mastercard.simplifyapp.objects;

import com.simplify.android.sdk.Card;

/**
 * Created by e069278 on 24/05/2017.
 */

public class Customer {

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Card getCardInformation() {
        return cardInformation;
    }

    public void setCardInformation(Card cardInformation) {
        this.cardInformation = cardInformation;
    }

    String name;
    String email;
    Card cardInformation;

    public Customer(String name, String email)
    {
        this.name = name;
        this.email = email;
    }
    public Customer(String name)
    {
        this.name = name;
    }
    public Customer(String name, String email, Card cardInformation)
    {
        this.name = name;
        this.email = email;
        this.cardInformation = cardInformation;
    }

}
