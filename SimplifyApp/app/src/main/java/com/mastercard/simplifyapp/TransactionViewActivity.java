package com.mastercard.simplifyapp;

import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.TextView;

import com.mastercard.simplifyapp.adapters.StoreListAdapter;
import com.mastercard.simplifyapp.handlers.CustomerHandler;
import com.mastercard.simplifyapp.handlers.StockHandler;
import com.mastercard.simplifyapp.objects.Customer;
import com.mastercard.simplifyapp.objects.StoreItem;
import com.mastercard.simplifyapp.objects.Transaction;

import java.util.ArrayList;

public class TransactionViewActivity extends AppCompatActivity {

    boolean notEditing = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_view);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Transaction transaction = (Transaction) getIntent().getSerializableExtra("transaction");

        Customer c = new Customer("Unknown");
        if (!transaction.getCustomerId().equals("Unknown")) {
            CustomerHandler handler = new CustomerHandler(getApplicationContext());
            handler.open();
            c = handler.getCustomer(transaction.getCustomerId());
            if (c == null)
                c = new Customer("Unknown");
            handler.close();
        }

        setTitle(transaction.getDate());


        TextView heading = (TextView) findViewById(R.id.customer_name);
        heading.setText("Customer: " + c.getName());

        TextView method = (TextView) findViewById(R.id.method);
        method.setText("Method Of Payment: " + transaction.getMethodOfPay());

        TextView total = (TextView) findViewById(R.id.total);
        total.setText("Total: €" + String.format("%.2f", transaction.getTransactionAmount()));

        ListView listView = (ListView) findViewById(R.id.sale_items);
        ArrayList<StoreItem> items = getItems(transaction.getItems());
        StoreListAdapter adapter = new StoreListAdapter(getApplicationContext(), items);
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private ArrayList<StoreItem> getItems(String items) {
        String[] itemIds = items.split(",");
        ArrayList<StoreItem> storeItems = new ArrayList<>();
        StockHandler handler = new StockHandler(getApplicationContext());
        handler.open();
        for (String id : itemIds) {
            storeItems.add(handler.getStoreItem(id));
        }
        handler.close();
        return storeItems;
    }

    @Override
    public void onBackPressed() {
        ActivityCompat.finishAfterTransition(this);
    }
}
