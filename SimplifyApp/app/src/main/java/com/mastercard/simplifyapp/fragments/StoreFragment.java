package com.mastercard.simplifyapp.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.mastercard.simplifyapp.R;
import com.mastercard.simplifyapp.objects.StoreItem;
import com.mastercard.simplifyapp.adapters.StoreListAdapter;

import java.util.ArrayList;

import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;

/**
 * Created by Cillian on 10/05/2017.
 */

public class StoreFragment extends Fragment {

    int MY_SCAN_REQUEST_CODE = 1;
    ListView savedItems;

    public StoreFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_store, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

            savedItems = (ListView) getView().findViewById(R.id.listView);
            assert savedItems != null;
            savedItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    scanPress();

                }

            });

            savedItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                //on item click create a url and open it in the browser
                public boolean onItemLongClick(AdapterView<?> l, View v, int position, long id) {
                    return true;
                }
            });

            populateStoreList();

    }

    private void scanPress() {
        Intent scanIntent = new Intent(getActivity().getApplicationContext(), CardIOActivity.class);

        // customize these values to suit your needs.
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, false); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false); // default: false

        // MY_SCAN_REQUEST_CODE is arbitrary and is only used within this activity.
        startActivityForResult(scanIntent, MY_SCAN_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MY_SCAN_REQUEST_CODE) {
            String resultDisplayStr;
            if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
                CreditCard scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);

                // Never log a raw card number. Avoid displaying it, but if necessary use getFormattedCardNumber()
                resultDisplayStr = "Card Number: " + scanResult.getRedactedCardNumber() + "\n";

                // Do something with the raw number, e.g.:
                // myService.setCardNumber( scanResult.cardNumber );

                if (scanResult.isExpiryValid()) {
                    resultDisplayStr += "Expiration Date: " + scanResult.expiryMonth + "/" + scanResult.expiryYear + "\n";
                }

                if (scanResult.cvv != null) {
                    // Never log or display a CVV
                    resultDisplayStr += "CVV has " + scanResult.cvv.length() + " digits.\n";
                }

                if (scanResult.postalCode != null) {
                    resultDisplayStr += "Postal Code: " + scanResult.postalCode + "\n";
                }
            }
            else {
                resultDisplayStr = "Scan was canceled.";
            }
            // do something with resultDisplayStr, maybe display it in a textView
            // resultTextView.setText(resultDisplayStr);
            Toast.makeText(getActivity().getApplicationContext(), resultDisplayStr, Toast.LENGTH_SHORT).show();
        }
        // else handle other activity results
    }

    private void populateStoreList() {

        ArrayList<StoreItem> storeItems = new ArrayList<>();
        storeItems.add(new StoreItem("Item One","This is Item one", 10));
        storeItems.add(new StoreItem("Item Two","This is Item two", 5));
        storeItems.add(new StoreItem("Item Three","This is Item three", 17));
        storeItems.add(new StoreItem("Item Four","This is Item four", 20));

        StoreListAdapter adapter = new StoreListAdapter(getActivity(), storeItems);

        savedItems.setAdapter(adapter);

        getView().findViewById(R.id.listView).setVisibility(View.VISIBLE);
        getView().findViewById(R.id.nothingSavedText).setVisibility(View.GONE);
    }
}
