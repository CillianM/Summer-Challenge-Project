package com.mastercard.simplifyapp.fragments;

import android.app.Fragment;
import android.os.Bundle;
import com.github.clans.fab.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.mastercard.simplifyapp.R;
import com.mastercard.simplifyapp.StoreItem;
import com.mastercard.simplifyapp.adapters.StoreListAdapter;
import com.mastercard.simplifyapp.widgets.SimplifyTextView;

import java.util.ArrayList;

/**
 * Created by e069278 on 23/05/2017.
 */

public class CheckoutFragment extends Fragment {

    int MY_SCAN_REQUEST_CODE = 1;
    ListView savedItems;
    SimplifyTextView priceView;
    FloatingActionButton scanBarcode,takePicture;
    ArrayList<StoreItem> storeItems;
    public CheckoutFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_checkout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        scanBarcode = (FloatingActionButton) getView().findViewById(R.id.scan_item);
        scanBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem();
            }

        });
        takePicture = (FloatingActionButton) getView().findViewById(R.id.photo_item);
        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem();
            }

        });
        savedItems = (ListView) getView().findViewById(R.id.checkout_items);
        priceView = (SimplifyTextView) getView().findViewById(R.id.total);
        assert savedItems != null;
        savedItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }

        });

        savedItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            //on item click create a url and open it in the browser
            public boolean onItemLongClick(AdapterView<?> l, View v, int position, long id) {
                removeItem(position);
                return true;
            }
        });

        populateStoreList();

    }

    private void calculateTotal() {
        int total = 0;
        for(StoreItem item: storeItems)
        {
            total += item.getPrice();
        }
        String currency = getResources().getString(R.string.euro);
        String price = currency + total;
        priceView.setText(price);
    }

    private void populateStoreList() {

        storeItems = new ArrayList<>();
        storeItems.add(new StoreItem("Item One","This is Item one", 10));
        storeItems.add(new StoreItem("Item Two","This is Item two", 5));
        storeItems.add(new StoreItem("Item Three","This is Item three", 17));
        storeItems.add(new StoreItem("Item Four","This is Item four", 20));

        StoreListAdapter adapter = new StoreListAdapter(getActivity(), storeItems);

        savedItems.setAdapter(adapter);
        calculateTotal();
    }

    public void addItem() {
        storeItems.add(new StoreItem("New Item","This is a new item that has been added", 1));
        StoreListAdapter adapter = new StoreListAdapter(getActivity(), storeItems);
        savedItems.setAdapter(adapter);
        calculateTotal();
    }

    private void removeItem(int index) {
        storeItems.remove(index);
        StoreListAdapter adapter = new StoreListAdapter(getActivity(), storeItems);
        savedItems.setAdapter(adapter);
        calculateTotal();
    }
}
