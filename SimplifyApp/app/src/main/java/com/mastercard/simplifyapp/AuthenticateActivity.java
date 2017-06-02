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
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.mastercard.simplifyapp.adapters.DrawerListAdapter;
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

    private FingerprintManager fingerprintManager;
    private KeyguardManager keyguardManager;
    private KeyStore keyStore;
    private KeyGenerator keyGenerator;
    private Cipher cipher;
    private FingerprintManager.CryptoObject cryptoObject;
    private static final String KEY_NAME = "example_key";

    ArrayList<NavItem> mNavItems;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticate);

        mNavItems = new ArrayList<>();
        mNavItems.add(new NavItem("Fingerprint", "Use fingerprint to authenticate user", R.drawable.fingerprint));
        mNavItems.add(new NavItem("Facial Recognition", "Use face to authenticate user", R.drawable.face));
        mNavItems.add(new NavItem("Password", "Enter password set for user", R.drawable.textbox_password));
        mNavItems.add(new NavItem("Pattern", "Enter pattern set for user", R.drawable.lock_pattern));

        ListView optionsList = (ListView) findViewById(R.id.authenticate_options);
        DrawerListAdapter adapter = new DrawerListAdapter(this, mNavItems);
        optionsList.setAdapter(adapter);

        optionsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectAuth(position);
            }
        });



    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void selectAuth(int position) {
        if(position == 0)
        {
            startFingerprintAuth();
        }
        else
        {
            Intent intent = new Intent(getApplicationContext(),ConfirmDetailsActivity.class);
            intent.putExtra("valid",true);
            startActivity(intent);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void startFingerprintAuth() {
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
            final Activity activity = this;
            cryptoObject = new FingerprintManager.CryptoObject(cipher);
            FingerprintHandler helper = new FingerprintHandler(getApplicationContext(), new FingerprintHandler.FingerprintHelperListener() {
                @Override
                public void authenticationFailed(String error) {
                    Toast.makeText(activity,
                            "Fingerprint authentication failed",
                            Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(),ConfirmDetailsActivity.class);
                    intent.putExtra("valid",false);
                    startActivity(intent);
                }

                @Override
                public void authenticationSucceeded(FingerprintManager.AuthenticationResult result) {
                    Toast.makeText(activity,
                            "Fingerprint authentication succeeded",
                            Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(),ConfirmDetailsActivity.class);
                    intent.putExtra("valid",true);
                    startActivity(intent);
                }
            });
            helper.setActivity(this);
            helper.startAuth(fingerprintManager, cryptoObject);
            Toast.makeText(activity,
                    "Scan fingerprint now",
                    Toast.LENGTH_SHORT).show();
        }
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

}
