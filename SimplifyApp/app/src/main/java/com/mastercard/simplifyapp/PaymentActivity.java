package com.mastercard.simplifyapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.mastercard.simplifyapp.adapters.CustomerListAdapter;
import com.mastercard.simplifyapp.handlers.CustomerHandler;
import com.mastercard.simplifyapp.handlers.TransactionHandler;
import com.mastercard.simplifyapp.interfaces.OnTaskCompleted;
import com.mastercard.simplifyapp.objects.Customer;
import com.mastercard.simplifyapp.objects.Transaction;
import com.pro100svitlo.creditCardNfcReader.CardNfcAsyncTask;
import com.pro100svitlo.creditCardNfcReader.utils.CardNfcUtils;
import com.simplify.android.sdk.Card;
import com.simplify.android.sdk.CardToken;
import com.simplify.android.sdk.Simplify;

import java.util.ArrayList;
import java.util.Date;

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
    Button customerButton;
    String customerPurchasing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        customerPurchasing = "Unknown";
        Intent intent = getIntent();
        currentTransaction = (Transaction)intent.getSerializableExtra("transaction");
        customerButton = (Button) findViewById(R.id.customer_button);
        customerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCustomer();
            }
        });
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
        additionalData.setPurpose("MEPOS Sale");
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
        data.setTransactionAmount(currentTransaction.getTransactionAmount());
        data.setTransactionCurrencyCode("978");
        data.setAdditionalData(additionalData);
        try {
            //validate the payload before generate the qrcode string
            data.validate();
            qrContent = data.generatePushPaymentString();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),"Error Occured",Toast.LENGTH_SHORT).show();
            findViewById(R.id.qr_image).setVisibility(View.GONE);
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

    private void addCustomer() {
        final Dialog dialog = new Dialog(PaymentActivity.this);
        dialog.setContentView(R.layout.dialog_choose_customer);
        dialog.setTitle("Title...");

        ListView customerList = (ListView) dialog.findViewById(R.id.customer_list_dialog);
        final ArrayList<Customer> customers = getCustomerList();
        CustomerListAdapter adapter = new CustomerListAdapter(getApplicationContext(), customers);
        customerList.setAdapter(adapter);

        customerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                customerPurchasing = customers.get(position).getId();
                getCustomerCard(customers.get(position).getId());
                customerButton.setText(customers.get(position).getName());
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void getCustomerCard(String id) {
        CustomerHandler handler = new CustomerHandler(getApplicationContext());
        handler.open();
        Card card = handler.getCustomerCard(id);
        handler.close();
        if (card != null) {
            cardForm.getCardEditText().setText(card.getNumber());
            cardForm.getExpirationDateEditText().setText(card.getExpMonth() + card.getExpYear());
            cardForm.getCvvEditText().setText(card.getCvc());
        }
    }

    private ArrayList<Customer> getCustomerList() {
        ArrayList<Customer> customers = new ArrayList<>();
        CustomerHandler handler = new CustomerHandler(getApplicationContext());
        handler.open();
        Cursor c = handler.returnData();
        while (c.moveToNext()) {
            Customer customer = new Customer(c.getString(1));
            customer.setId(c.getString(0));
            customers.add(customer);

        }
        handler.close();

        return customers;

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
                final Activity currentActivity = this;
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                currentTransaction.setDate(new Date().toString());

                                currentTransaction.setCustomerId(customerPurchasing);
                                currentTransaction.setMethodOfPay("Cash");
                                TransactionHandler handler = new TransactionHandler(getApplicationContext());
                                handler.open();
                                handler.insertData(currentTransaction.getTransactionAmount(), currentTransaction.getCustomerId(), currentTransaction.getItems(), currentTransaction.getMethodOfPay());
                                handler.close();
                                Toast.makeText(getApplicationContext(),"Transaction Saved",Toast.LENGTH_SHORT).show();


                                Toast.makeText(getApplicationContext(), "Transaction Saved", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(currentActivity,StoreActivity.class);
                                startActivity(intent);
                                finish();

                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Charge " + currentTransaction.getTransactionAmount() + " as cash?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
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

        cardForm.validate();
        if (cardForm.isValid()) {
            Card card = new Card();
            card.setCvc(cardForm.getCvv());
            card.setNumber(cardForm.getCardNumber());
            card.setExpMonth(cardForm.getExpirationMonth());
            card.setExpYear(cardForm.getExpirationYear());
            card.setAddressZip(cardForm.getCountryCode());

            if (!customerPurchasing.equals("Unknown")) {
                CustomerHandler handler = new CustomerHandler(getApplicationContext());
                handler.open();
                handler.updateCustomerCard(customerPurchasing, cardForm.getCardNumber(), cardForm.getCvv(), cardForm.getExpirationMonth() + "/" + cardForm.getExpirationYear());
                handler.close();
            }

            currentTransaction.setDate(new Date().toString());
            currentTransaction.setCustomerId(customerPurchasing);
            currentTransaction.setMethodOfPay("Card");
            TransactionHandler handler = new TransactionHandler(getApplicationContext());
            handler.open();
            handler.insertData(currentTransaction.getTransactionAmount(), currentTransaction.getCustomerId(), currentTransaction.getItems(), currentTransaction.getMethodOfPay());
            handler.close();
            Toast.makeText(getApplicationContext(), "Transaction Saved", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, StoreActivity.class);
            startActivity(intent);
            finish();
        }
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
