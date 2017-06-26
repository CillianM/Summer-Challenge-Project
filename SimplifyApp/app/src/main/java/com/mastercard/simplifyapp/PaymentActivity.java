package com.mastercard.simplifyapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.braintreepayments.cardform.view.CardForm;
import com.mastercard.mpqr.pushpayment.model.AdditionalData;
import com.mastercard.mpqr.pushpayment.model.PushPaymentData;
import com.mastercard.simplifyapp.objects.Transaction;

public class PaymentActivity extends AppCompatActivity {

    Bitmap qrCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        Intent intent = getIntent();
        Transaction currentTransaction = (Transaction)intent.getSerializableExtra("transaction");

        CardForm cardForm = (CardForm) findViewById(R.id.card_form);
        cardForm.cardRequired(true)
                .expirationRequired(true)
                .cvvRequired(true)
                .postalCodeRequired(true)
                .mobileNumberRequired(false)
                .actionLabel("Purchase")
                .setup(this);
        cardForm.setVisibility(View.GONE);

        AdditionalData additionalData = new AdditionalData();
        additionalData.setTerminalId("45784312");
        additionalData.setReferenceId("Test Ref");
        additionalData.setStoreId("A6008");
        additionalData.setLoyaltyNumber("000");

        PushPaymentData data = new PushPaymentData();
        data.setPayloadFormatIndicator("01");
        data.setCountryCode("IE");
        data.setMerchantCategoryCode("5204");
        data.setMerchantCity("Dublin");
        data.setMerchantName("Test Merchant");
        data.setTransactionAmount(19.99);
        data.setTransactionCurrencyCode("eur");
        data.setAdditionalData(additionalData);
        try {
            //validate the payload before generate the qrcode string
            data.validate();
            String qrContent = data.generatePushPaymentString();
            //qrCode = encodeAsBitmap(qrContent);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),"Error Occured",Toast.LENGTH_SHORT).show();
        }
    }

    public void qrView(View view) {
        final RelativeLayout root = (RelativeLayout) findViewById(R.id.root_layout);
        final ImageView imageView = (ImageView) findViewById(R.id.qr_image);
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int statusBarOffset = dm.heightPixels - root.getMeasuredHeight();
        final int originalPos[] = new int[2];
        imageView.getLocationOnScreen(originalPos);
        final int xDest = (dm.widthPixels / 2) - (imageView.getMeasuredWidth() / 2);
        final int yDest = dm.heightPixels / 2 - (imageView.getMeasuredHeight() / 2)- statusBarOffset;

        TranslateAnimation anim = new TranslateAnimation(0, xDest
                - originalPos[0], 0, yDest - originalPos[1]);
        anim.setDuration(1000);
        anim.setAnimationListener(new Animation.AnimationListener() {

            public void onAnimationStart(Animation animation) {
                findViewById(R.id.card_image).setVisibility(View.INVISIBLE);

            }

            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub

            }

            public void onAnimationEnd(Animation animation) {
                findViewById(R.id.card_image).setVisibility(View.GONE);
                findViewById(R.id.qr_image).setVisibility(View.GONE);
                findViewById(R.id.qr_code_layout).setVisibility(View.VISIBLE);
                ImageView qrCodeView = (ImageView) findViewById(R.id.qr_code);
                qrCodeView.setImageBitmap(qrCode);
            }
        });
        imageView.startAnimation(anim);

    }

    public void cardView(View view) {
        final RelativeLayout root = (RelativeLayout) findViewById(R.id.root_layout);
        final ImageView imageView = (ImageView) findViewById(R.id.card_image);
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int statusBarOffset = dm.heightPixels - root.getMeasuredHeight();
        final int originalPos[] = new int[2];
        imageView.getLocationOnScreen(originalPos);
        final int xDest = (dm.widthPixels / 2) - (imageView.getMeasuredWidth() / 2);
        final int yDest = dm.heightPixels / 2 - (imageView.getMeasuredHeight() / 2)- statusBarOffset;

        TranslateAnimation anim = new TranslateAnimation(0, xDest
                - originalPos[0], 0, yDest - originalPos[1]);
        anim.setDuration(600);
        anim.setAnimationListener(new Animation.AnimationListener() {

            public void onAnimationStart(Animation animation) {
                findViewById(R.id.qr_image).setVisibility(View.INVISIBLE);

            }

            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub

            }

            public void onAnimationEnd(Animation animation) {
                findViewById(R.id.card_image).setVisibility(View.GONE);
                findViewById(R.id.qr_image).setVisibility(View.GONE);
                findViewById(R.id.card_form).setVisibility(View.VISIBLE);
            }
        });
        imageView.startAnimation(anim);
    }

    /*Bitmap encodeAsBitmap(String str) throws WriterException {
    const int black = 0xFF000000;
    const int white = 0xFFFFFFFF;
        BitMatrix result;
        Bitmap bitmap=null;
        try {
            result = new MultiFormatWriter().encode(str,
                    BarcodeFormat.QR_CODE, WIDTH, WIDTH, null);

            int w = result.getWidth();
            int h = result.getHeight();
            int[] pixels = new int[w * h];
            for (int y = 0; y < h; y++) {
                int offset = y * w;
                for (int x = 0; x < w; x++) {
                    pixels[offset + x] = result.get(x, y) ? black : white;
                }
            }
            bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, 500, 0, 0, w, h);
        } catch (Exception iae) {
            iae.printStackTrace();
            return null;
        }
        return bitmap;
    }*/
}
