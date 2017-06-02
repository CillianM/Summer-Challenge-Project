package com.mastercard.simplifyapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.mastercard.simplifyapp.widgets.SimplifyEditText;
import com.mastercard.simplifyapp.widgets.SimplifyTextView;
import com.simplify.android.sdk.Card;

import java.util.ArrayList;
import java.util.Random;

import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;

public class ConfirmDetailsActivity extends AppCompatActivity {

    private static final int MY_SCAN_REQUEST_CODE = 1;
    private SimplifyTextView message;


    private Card card = new Card();

    ArrayList<Customer> list = new ArrayList<Customer>() {{
        add(new Customer("Firdaus Liborius","example@gmail.com"));
        add(new Customer("Cas Brendan","example@gmail.com"));
        add(new Customer("Hyginos Givi","example@gmail.com"));
        add(new Customer("Paderau Servaas","example@gmail.com"));
        add(new Customer("Haimo Dmitar","example@gmail.com"));
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_details);
        FloatingActionButton scanCard = (FloatingActionButton) findViewById(R.id.scan_card);
        scanCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onScanPress();
            }

        });
        card.setExpMonth("1");
        card.setExpYear("2000");
        card.setNumber("123456543210");
        card.setCvc("123");
        Intent intent = getIntent();
        boolean bool = intent.getBooleanExtra("valid",true);
        if(bool)
            getCustomer();
    }

    public void getCustomer() {
        Random rn = new Random();
        int range = list.size() + 1;
        int randomNum = rn.nextInt(range);
        if(randomNum == list.size())
        {
            randomNum -= 1;
        }
        Customer customer = list.get(randomNum);
        SimplifyEditText nameView = (SimplifyEditText) findViewById(R.id.user_name);
        nameView.setText(customer.getName());
        SimplifyEditText emailView = (SimplifyEditText) findViewById(R.id.user_email);
        emailView.setText(customer.getEmail());
        SimplifyEditText cardNumberView = (SimplifyEditText) findViewById(R.id.card_number);
        cardNumberView.setText(card.getNumber());
        SimplifyEditText cvvView = (SimplifyEditText) findViewById(R.id.card_cvv);
        cvvView.setText(card.getCvc());
        SimplifyEditText expiryView = (SimplifyEditText) findViewById(R.id.card_expiry);
        expiryView.setText(card.getExpMonth() + "/" + card.getExpYear());

    }

    public void onScanPress() {
        Intent scanIntent = new Intent(this, CardIOActivity.class);

        // customize these values to suit your needs.
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true);
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, true);
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false);
        scanIntent.putExtra(CardIOActivity.EXTRA_HIDE_CARDIO_LOGO,true);

        startActivityForResult(scanIntent, MY_SCAN_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MY_SCAN_REQUEST_CODE) {
            String resultDisplayStr;
            if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
                CreditCard scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);

                resultDisplayStr = "Card Number: " + scanResult.getRedactedCardNumber() + "\n";
                SimplifyEditText cardNumberView = (SimplifyEditText) findViewById(R.id.card_number);
                cardNumberView.setText(scanResult.cardNumber);

                // Do something with the raw number, e.g.:
                // myService.setCardNumber( scanResult.cardNumber );

                if (scanResult.isExpiryValid()) {
                    SimplifyEditText expiryView = (SimplifyEditText) findViewById(R.id.card_expiry);
                    expiryView.setText(scanResult.expiryMonth + "/" + scanResult.expiryYear);
                    resultDisplayStr += "Expiration Date: " + scanResult.expiryMonth + "/" + scanResult.expiryYear + "\n";
                }

                if (scanResult.cvv != null) {
                    // Never log or display a CVV
                    SimplifyEditText cvvView = (SimplifyEditText) findViewById(R.id.card_cvv);
                    cvvView.setText(scanResult.cvv);
                    resultDisplayStr += "CVV has " + scanResult.cvv.length() + " digits.\n";
                }

                if (scanResult.postalCode != null) {
                    resultDisplayStr += "Postal Code: " + scanResult.postalCode + "\n";
                }
            }
            else {
                resultDisplayStr = "Scan was canceled.";
            }
        }
    }
}
