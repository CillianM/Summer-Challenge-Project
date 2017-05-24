package com.mastercard.simplifyapp;

import android.Manifest;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Handler;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.mastercard.simplifyapp.utility.FingerprintHandler;
import com.mastercard.simplifyapp.widgets.SimplifyEditText;
import com.mastercard.simplifyapp.widgets.SimplifyTextView;
import com.simplify.android.sdk.Card;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;

public class AuthenticateActivity extends AppCompatActivity {
    private static final int MY_SCAN_REQUEST_CODE = 1;
    private SimplifyTextView message;
    private FingerprintManager fingerprintManager;
    private KeyguardManager keyguardManager;
    private KeyStore keyStore;
    private KeyGenerator keyGenerator;
    private Cipher cipher;
    private FingerprintManager.CryptoObject cryptoObject;
    private static final String KEY_NAME = "example_key";
    private Card card = new Card();

    ArrayList<Customer> list = new ArrayList<Customer>() {{
        add(new Customer("Firdaus Liborius","example@gmail.com"));
        add(new Customer("Cas Brendan","example@gmail.com"));
        add(new Customer("Hyginos Givi","example@gmail.com"));
        add(new Customer("Paderau Servaas","example@gmail.com"));
        add(new Customer("Haimo Dmitar","example@gmail.com"));
    }};


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticate);
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


        keyguardManager =
                (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        fingerprintManager =
                (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);

        if (!keyguardManager.isKeyguardSecure()) {

            Toast.makeText(this,
                    "Lock screen security not enabled in Settings",
                    Toast.LENGTH_LONG).show();
            return;
        }

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.USE_FINGERPRINT) !=
                PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this,
                    "Fingerprint authentication permission not enabled",
                    Toast.LENGTH_LONG).show();

            return;
        }

        if (!fingerprintManager.hasEnrolledFingerprints()) {

            // This happens when no fingerprints are registered.
            Toast.makeText(this,
                    "Register at least one fingerprint in Settings",
                    Toast.LENGTH_LONG).show();
            return;
        }

        generateKey();
        if (cipherInit()) {
            cryptoObject = new FingerprintManager.CryptoObject(cipher);
            FingerprintHandler helper = new FingerprintHandler(getApplicationContext(), new FingerprintHandler.FingerprintHelperListener() {
                @Override
                public void authenticationFailed(String error) {
                    getCustomerData(false);
                }

                @Override
                public void authenticationSucceeded(FingerprintManager.AuthenticationResult result) {
                    getCustomerData(true);
                }
            });
            helper.setActivity(this);
            helper.startAuth(fingerprintManager, cryptoObject);
        }
    }

    public void getCustomerData(final boolean successful) {
        final ProgressBar fingerprintProgress = (ProgressBar) findViewById(R.id.fingerprint_progress);
        final ImageView fingerprintError = (ImageView) findViewById(R.id.fingerprint_icon_error);
        final ImageView fingerprintSuccess = (ImageView) findViewById(R.id.fingerprint_icon_success);
        fingerprintProgress.setVisibility(View.VISIBLE);
        int timeOut = 3000;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(successful){
                    Toast.makeText(getApplicationContext(),"Authentication succeeded.",Toast.LENGTH_LONG).show();
                    fingerprintProgress.setVisibility(View.GONE);
                    fingerprintSuccess.setVisibility(View.VISIBLE);
                    hideFingerPrint("Fingerprint Scanned Successfully",true);
                }
                else {
                    fingerprintProgress.setVisibility(View.GONE);
                    fingerprintError.setVisibility(View.VISIBLE);
                    hideFingerPrint("Error Scanning Fingerprint",false);
                }
            }
        }, timeOut);
    }

    public void hideFingerPrint(String messageToDisplay, final boolean successful) {
        message=  (SimplifyTextView) this.findViewById(R.id.fingerprint_message);
        message.setText(messageToDisplay);
        int timeOut = 1500;
        final Activity activity = this;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                RelativeLayout fingerprintLayout= (RelativeLayout) activity.findViewById(R.id.fingerprint_layout);
                RelativeLayout formLayout = (RelativeLayout) activity.findViewById(R.id.cardform_layout);
                fingerprintLayout.setVisibility(View.GONE);
                formLayout.setVisibility(View.VISIBLE);
                if(successful)
                {
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
            }
        }, timeOut);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean cipherInit() {
        try {
            cipher = Cipher.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES + "/"
                            + KeyProperties.BLOCK_MODE_CBC + "/"
                            + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException |
                NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get Cipher", e);
        }

        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME,
                    null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (KeyStoreException | CertificateException
                | UnrecoverableKeyException | IOException
                | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void generateKey() {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            keyGenerator = KeyGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES,
                    "AndroidKeyStore");
        } catch (NoSuchAlgorithmException |
                NoSuchProviderException e) {
            throw new RuntimeException(
                    "Failed to get KeyGenerator instance", e);
        }

        try {
            keyStore.load(null);
            keyGenerator.init(new
                    KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(
                            KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
            keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException |
                InvalidAlgorithmParameterException
                | CertificateException | IOException e) {
            throw new RuntimeException(e);
        }
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
