package com.mastercard.simplifyapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.braintreepayments.cardform.view.CardForm;
import com.github.clans.fab.FloatingActionButton;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.mastercard.mpqr.pushpayment.model.AdditionalData;
import com.mastercard.mpqr.pushpayment.model.PushPaymentData;
import com.mastercard.simplifyapp.fragments.ProcessCashFragment;
import com.mastercard.simplifyapp.interfaces.OnTaskCompleted;
import com.mastercard.simplifyapp.objects.Transaction;
import com.pro100svitlo.creditCardNfcReader.CardNfcAsyncTask;
import com.pro100svitlo.creditCardNfcReader.utils.CardNfcUtils;
import com.simplify.android.sdk.Card;
import com.simplify.android.sdk.CardToken;
import com.simplify.android.sdk.Simplify;

public class PaymentActivity extends AppCompatActivity implements OnTaskCompleted,CardNfcAsyncTask.CardNfcInterface {

    Bitmap qrCode;
    BitmapCreator creator;
    String qrContent;
    CardForm cardForm;
    Simplify simplify;
    CardToken token;
    NfcAdapter mNfcAdapter;
    CardNfcUtils mCardNfcUtils;
    CardNfcAsyncTask mCardNfcAsyncTask;
    boolean mIntentFromCreate;
    Transaction currentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        simplify = new Simplify();
        simplify.setApiKey("sbpb_NGY0ZTFmODctZmZjNi00YmFiLThjZjktZTBiMGNhNmVhMjI2");


        Intent intent = getIntent();
        currentTransaction = (Transaction)intent.getSerializableExtra("transaction");

        cardForm = (CardForm) findViewById(R.id.card_form);
        cardForm.cardRequired(true)
                .expirationRequired(true)
                .cvvRequired(true)
                .postalCodeRequired(false)
                .mobileNumberRequired(false)
                .actionLabel("Purchase")
                .setup(this);

        AdditionalData additionalData = new AdditionalData();
        additionalData.setTerminalId("45784312");
        additionalData.setReferenceId("Test Ref");
        additionalData.setStoreId("A6008");
        additionalData.setPurpose(currentTransaction.getItems());
        additionalData.setLoyaltyNumber("000");

        PushPaymentData data = new PushPaymentData();
        data.setPayloadFormatIndicator("01");
        data.setPointOfInitiationMethod("12");
        data.setMerchantIdentifierMastercard04("460067893452143");
        data.setPayloadFormatIndicator("01");
        data.setCountryCode("IE");
        data.setMerchantCategoryCode("5204");
        data.setMerchantCity("Dublin");
        data.setMerchantName("Test Merchant");
        data.setTransactionAmount(19.99);
        data.setTransactionCurrencyCode("978");
        data.setAdditionalData(additionalData);
        try {
            //validate the payload before generate the qrcode string
            data.validate();
            qrContent = data.generatePushPaymentString();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),"Error Occured",Toast.LENGTH_SHORT).show();
        }
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null){
            //do something if there are no nfc module on device
        } else {
            //do something if there are nfc module on device

            mCardNfcUtils = new CardNfcUtils(this);
            //next few lines here needed in case you will scan credit card when app is closed
            mIntentFromCreate = true;
            onNewIntent(getIntent());
        }

        FloatingActionButton submit = (FloatingActionButton) findViewById(R.id.confirm_transaction);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyTransaction();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_viewer, menu);
        return true;
    }

    //allow user to share current series as a link
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent sendIntent;
        switch (item.getItemId()) {
            case R.id.action_cash:
                FragmentManager fm = getSupportFragmentManager();
                ProcessCashFragment editNameDialogFragment = ProcessCashFragment.newInstance("Process Cash",currentTransaction.getTransactionAmount());
                editNameDialogFragment.show(fm, "fragment_edit_name");
                break;

            default:
                break;
        }

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIntentFromCreate = false;
        if (mNfcAdapter != null && !mNfcAdapter.isEnabled()){
            //show some turn on nfc dialog here. take a look in the samle ;-)
        } else if (mNfcAdapter != null){
            mCardNfcUtils.enableDispatch();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mNfcAdapter != null) {
            mCardNfcUtils.disableDispatch();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (mNfcAdapter != null && mNfcAdapter.isEnabled()) {
            //this - interface for callbacks
            //intent = intent :)
            //mIntentFromCreate - boolean flag, for understanding if onNewIntent() was called from onCreate or not
            mCardNfcAsyncTask = new CardNfcAsyncTask.Builder(this, intent, mIntentFromCreate).build();
        }
    }

    @Override
    public void startNfcReadCard() {
        findViewById(R.id.confirm_transaction).setVisibility(View.VISIBLE);
    }

    @Override
    public void cardIsReadyToRead() {
        cardForm.getCardEditText().setText(mCardNfcAsyncTask.getCardNumber());
        String expirationDate = mCardNfcAsyncTask.getCardExpireDate();
        expirationDate = expirationDate.substring(0,expirationDate.indexOf("/")) + "20" + expirationDate.substring(expirationDate.indexOf("/") +1);
        cardForm.getExpirationDateEditText().setText(expirationDate);
        String cardType = mCardNfcAsyncTask.getCardType();

    }

    @Override
    public void doNotMoveCardSoFast() {
        Toast.makeText(this,"Don't move the card so fast",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void unknownEmvCard() {
        Toast.makeText(this,"Unknown Card",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void cardWithLockedNfc() {
        Toast.makeText(this,"NFC Locked",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void finishNfcReadCard() {
        //notify user that scannig finished
    }

    public void qrView(View view) {
        final ImageView logoView = (ImageView) findViewById(R.id.qr_image);
        final ProgressBar progressView = (ProgressBar) findViewById(R.id.qr_code_progress);
        logoView.setVisibility(View.GONE);
        progressView.setVisibility(View.VISIBLE);
        creator = new BitmapCreator(qrContent, this);
        creator.execute();
    }


    void verifyTransaction() {

        Card card = new Card();
        card.setCvc(cardForm.getCvv());
        card.setNumber(cardForm.getCardNumber());
        card.setExpMonth(cardForm.getExpirationMonth());
        card.setExpYear(cardForm.getExpirationYear());
        card.setAddressZip(cardForm.getCountryCode());

        // tokenize the card
        simplify.createCardToken(card, new CardToken.Callback() {
            @Override
            public void onSuccess(CardToken cardToken) {
               token = cardToken;
            }
            @Override
            public void onError(Throwable throwable) {
                // ...
            }
        });
    }

    @Override
    public void onTaskCompleted() {
        qrCode = creator.getBitmap();
        ImageView qrCodeView = (ImageView) findViewById(R.id.qr_code);
        qrCodeView.setImageBitmap(qrCode);
        findViewById(R.id.qr_code_progress).setVisibility(View.GONE);
        findViewById(R.id.qr_code).setVisibility(View.VISIBLE);
    }

    @Override
    public void onTaskInProgress(String info) {

    }

    @Override
    public void onErrorOccuring() {

    }


    class BitmapCreator extends AsyncTask<String, Void, String> {

        String content;
        OnTaskCompleted listener;
        Bitmap bmp;

        BitmapCreator(String content, OnTaskCompleted listener) {
            this.content = content;
            this.listener = listener;
            this.bmp = null;
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                bmp = constructBitmap(content);
            } catch (WriterException e) {
                e.printStackTrace();
                bmp = null;
            }

            return  "done";
        }

        @Override
        protected void onPostExecute(String result) {
            listener.onTaskCompleted();
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values)
        {

        }

        public Bitmap getBitmap(){return bmp;}

        Bitmap constructBitmap(String str) throws WriterException {
            Bitmap bmp = null;

            QRCodeWriter writer = new QRCodeWriter();
            try {
                BitMatrix bitMatrix = writer.encode(str, BarcodeFormat.QR_CODE, 512, 512);
                int width = bitMatrix.getWidth();
                int height = bitMatrix.getHeight();
                bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                    }
                }

            } catch (WriterException e) {
                e.printStackTrace();
            }
            return bmp;
        }

    }
}
