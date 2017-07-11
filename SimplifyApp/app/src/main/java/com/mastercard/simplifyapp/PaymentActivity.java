package com.mastercard.simplifyapp;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.mastercard.simplifyapp.utility.CardUtils;
import com.mastercard.simplifyapp.view.MonoTextView;
import com.mastercard.simplifyapp.view.WrapContentHeightViewPager;
import com.pro100svitlo.creditCardNfcReader.CardNfcAsyncTask;
import com.pro100svitlo.creditCardNfcReader.utils.CardNfcUtils;
import com.simplify.android.sdk.Card;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;

public class PaymentActivity extends AppCompatActivity implements OnTaskCompleted,CardNfcAsyncTask.CardNfcInterface {

    private static final int SCAN_REQUEST_CODE = 0;
    Bitmap qrCode;
    BitmapCreator creator;
    String qrContent;
    NfcAdapter mNfcAdapter;
    CardNfcUtils mCardNfcUtils;
    CardNfcAsyncTask mCardNfcAsyncTask;
    boolean mIntentFromCreate;
    Transaction currentTransaction;
    Button customerButton;
    String customerPurchasing;

    private CardView grayCard;
    private CardView blueCard;
    private MonoTextView numberLabel;
    private MonoTextView expiryLabel;
    private MonoTextView cvvLabel;

    private MonoTextView numberText;
    private MonoTextView expiryText;
    private MonoTextView cvvText;

    private TextInputEditText numberEditText;
    private TextInputEditText expiryEditText;
    private TextInputEditText cvvEditText;
    WrapContentHeightViewPager pager;

    private boolean showingGray = true;
    private AnimatorSet inSet;
    private AnimatorSet outSet;

    Card currentCard = new Card();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setupCardForm();

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SCAN_REQUEST_CODE) {
            if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
                CreditCard scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);
                currentCard.setNumber(scanResult.getFormattedCardNumber());
                currentCard.setExpMonth(scanResult.expiryMonth + "");
                currentCard.setExpYear(scanResult.expiryYear + "");
                currentCard.setCvc("000");
                enableEdit();
                setCardType(currentCard.getNumber());
                numberText.setText(currentCard.getNumber());
                cvvText.setText("000");
                String expirationDate = scanResult.expiryMonth + "/" + scanResult.expiryYear;
                expiryText.setText(expirationDate);
            } else {
                Toast.makeText(getApplicationContext(), "Scan Was Cancelled", Toast.LENGTH_SHORT).show();
            }
            // do something with resultDisplayStr, maybe display it in a textView
            // resultTextView.setText(resultDisplayStr);
        }
        // else handle other activity results
    }

    private void flipToGray() {
        if (!showingGray && !outSet.isRunning() && !inSet.isRunning()) {
            showingGray = true;

            blueCard.setCardElevation(0);
            grayCard.setCardElevation(0);

            outSet.setTarget(blueCard);
            outSet.start();

            inSet.setTarget(grayCard);
            inSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    grayCard.setCardElevation(convertDpToPixel(12, PaymentActivity.this));
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            inSet.start();
        }
    }

    private void flipToBlue() {
        if (showingGray && !outSet.isRunning() && !inSet.isRunning()) {
            showingGray = false;

            grayCard.setCardElevation(0);
            blueCard.setCardElevation(0);

            outSet.setTarget(grayCard);
            outSet.start();

            inSet.setTarget(blueCard);
            inSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    blueCard.setCardElevation(convertDpToPixel(12, PaymentActivity.this));
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            inSet.start();
        }
    }

    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    public static float convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return dp;
    }

    private void setupCardForm() {
        grayCard = (CardView) findViewById(R.id.card_gray);
        blueCard = (CardView) findViewById(R.id.card_blue);
        numberLabel = (MonoTextView) findViewById(R.id.label_card_number);
        expiryLabel = (MonoTextView) findViewById(R.id.label_expired_date);
        cvvLabel = (MonoTextView) findViewById(R.id.label_cvv_code);

        numberText = (MonoTextView) findViewById(R.id.text_card_number);
        expiryText = (MonoTextView) findViewById(R.id.text_expired_date);
        cvvText = (MonoTextView) findViewById(R.id.text_cvv_code);

        numberEditText = (TextInputEditText) findViewById(R.id.input_edit_card_number);
        expiryEditText = (TextInputEditText) findViewById(R.id.input_edit_expired_date);
        cvvEditText = (TextInputEditText) findViewById(R.id.input_edit_cvv_code);

        inSet = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.card_flip_in);
        outSet = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.card_flip_out);

        numberEditText.addTextChangedListener(new TextWatcher() {

            private boolean lock;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    flipToBlue();
                }
                numberText.setText(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
                setCardType(s.toString());
                if (lock || s.toString().length() > 16) {
                    return;
                }
                lock = true;
                for (int i = 4; i < s.length(); i += 5) {
                    if (s.toString().charAt(i) != ' ') {
                        s.insert(i, " ");
                    }
                }
                lock = false;
            }
        });

        expiryEditText.addTextChangedListener(new TextWatcher() {

            private boolean lock;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                expiryText.setText(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (lock || s.length() > 4) {
                    return;
                }
                lock = true;
                if (s.length() > 2 && s.toString().charAt(2) != '/') {
                    s.insert(2, "/");
                }
                lock = false;
            }
        });

        cvvEditText.addTextChangedListener(new TextWatcher() {

            private boolean lock;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                cvvText.setText(s);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        PagerAdapter adapter = new MyPagerAdapter();
        pager = (WrapContentHeightViewPager) findViewById(R.id.view_pager);
        pager.setAdapter(adapter);
        pager.setClipToPadding(false);
        pager.setPadding(width / 4, 0, width / 4, 0);
        pager.setPageMargin(width / 14);
        pager.setPagingEnabled(false);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        numberEditText.setFocusableInTouchMode(true);
                        expiryEditText.setFocusable(false);
                        cvvEditText.setFocusable(false);
                        numberEditText.requestFocus();
                        return;
                    case 1:
                        numberEditText.setFocusable(false);
                        expiryEditText.setFocusableInTouchMode(true);
                        cvvEditText.setFocusable(false);
                        expiryEditText.requestFocus();
                        return;
                    case 2:
                        numberEditText.setFocusable(false);
                        expiryEditText.setFocusable(false);
                        cvvEditText.setFocusableInTouchMode(true);
                        cvvEditText.requestFocus();
                        return;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        final Activity activity = this;
        TextView.OnEditorActionListener onEditorActionListener = new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    pager.setCurrentItem(pager.getCurrentItem() + 1);
                    handled = true;
                }
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String expirationDate = expiryEditText.getText().toString();
                    currentCard.setCvc("000");
                    currentCard.setNumber(numberEditText.getText().toString().replace(" ", ""));
                    currentCard.setExpMonth(expirationDate.substring(0, expirationDate.indexOf("/")));
                    currentCard.setExpYear(expirationDate.substring(expirationDate.indexOf("/") + 1));
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(cvvEditText.getWindowToken(), 0);
                    handled = true;
                }
                return handled;
            }
        };

        numberEditText.setOnEditorActionListener(onEditorActionListener);
        expiryEditText.setOnEditorActionListener(onEditorActionListener);
        cvvEditText.setOnEditorActionListener(onEditorActionListener);

    }

    public void onScanPress() {
        Intent scanIntent = new Intent(this, CardIOActivity.class);

        // customize these values to suit your needs.
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, false); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_HIDE_CARDIO_LOGO, true);

        // MY_SCAN_REQUEST_CODE is arbitrary and is only used within this activity.
        startActivityForResult(scanIntent, SCAN_REQUEST_CODE);
    }

    public void enableEdit() {
        AnimatorSet flipIn = (AnimatorSet) AnimatorInflater.loadAnimator(getApplicationContext(), R.animator.card_flip_in);
        AnimatorSet flipOut = (AnimatorSet) AnimatorInflater.loadAnimator(getApplicationContext(), R.animator.card_flip_out);
        flipIn.setTarget(blueCard);
        flipOut.setTarget(grayCard);
        flipOut.start();
        flipIn.start();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
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
            enableEdit();
            setCardType(card.getNumber());
            currentCard.setNumber(card.getNumber());
            currentCard.setCvc(card.getCvc());
            currentCard.setExpYear(card.getExpYear());
            currentCard.setExpMonth(card.getExpMonth());
            numberText.setText(card.getNumber());
            expiryText.setText(card.getExpMonth() + "/" + card.getExpYear());
            cvvText.setText(card.getCvc());
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
        inflater.inflate(R.menu.payment_menu, menu);
        return true;
    }

    //allow user to share current series as a link
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent sendIntent;
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.process_cash:
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

            case R.id.scan_card:
                onScanPress();
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
        enableEdit();
        numberText.setText(mCardNfcAsyncTask.getCardNumber());
        cvvText.setText("000");
        String expirationDate = mCardNfcAsyncTask.getCardExpireDate();
        expirationDate = expirationDate.substring(0, expirationDate.indexOf("/")) + expirationDate.substring(expirationDate.indexOf("/"));
        expiryText.setText(expirationDate);
        currentCard.setCvc("000");
        currentCard.setNumber(mCardNfcAsyncTask.getCardNumber());
        currentCard.setExpMonth(expirationDate.substring(0, expirationDate.indexOf("/")));
        currentCard.setExpYear(expirationDate.substring(expirationDate.indexOf("/") + 1));
        setCardType(currentCard.getNumber());


    }

    private void setCardType(String number) {
        ImageView cardIcon = (ImageView) findViewById(R.id.icon_card_blue);
        if (number.startsWith("4"))
            cardIcon.setImageDrawable(getDrawable(R.drawable.simplify_visa));
        else if (number.startsWith("5"))
            cardIcon.setImageDrawable(getDrawable(R.drawable.simplify_mastercard));
        else if (number.startsWith("6"))
            cardIcon.setImageDrawable(getDrawable(R.drawable.simplify_discover));
        else if (number.startsWith("37"))
            cardIcon.setImageDrawable(getDrawable(R.drawable.simplify_amex));
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

    private boolean isValid() {
        if (!CardUtils.isValid(Long.parseLong(currentCard.getNumber()))) {
            return false;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/yy");
        simpleDateFormat.setLenient(false);
        Date expiry = null;
        try {
            expiry = simpleDateFormat.parse(currentCard.getExpMonth() + "/" + currentCard.getExpYear());
        } catch (ParseException e) {
            return false;
        }
        boolean expired = expiry.before(new Date());
        return !expired;
    }


    void verifyTransaction() {

        if (isValid()) {

            if (!customerPurchasing.equals("Unknown")) {
                CustomerHandler handler = new CustomerHandler(getApplicationContext());
                handler.open();
                handler.updateCustomerCard(customerPurchasing, currentCard.getNumber(), currentCard.getCvc(), currentCard.getExpMonth() + "/" + currentCard.getExpYear());
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

    private class MyPagerAdapter extends PagerAdapter {

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            int resId = 0;
            switch (position) {
                case 0:
                    resId = R.id.input_layout_card_number;
                    break;
                case 1:
                    resId = R.id.input_layout_expired_date;
                    break;
                case 2:
                    resId = R.id.input_layout_cvv_code;
                    break;
                case 3:
                    resId = R.id.space;
                    break;

            }
            return findViewById(resId);
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
        }


        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }
}
