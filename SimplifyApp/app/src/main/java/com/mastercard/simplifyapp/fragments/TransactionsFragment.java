package com.mastercard.simplifyapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.github.clans.fab.FloatingActionButton;
import com.mastercard.simplifyapp.R;
import com.mastercard.simplifyapp.TransactionViewActivity;
import com.mastercard.simplifyapp.adapters.TransactionListAdapter;
import com.mastercard.simplifyapp.objects.Transaction;

import java.util.ArrayList;

/**
 * Created by e069278 on 26/06/2017.
 */

public class TransactionsFragment extends Fragment {
    int MY_SCAN_REQUEST_CODE = 1;
    ArrayList<Transaction> storeItems;
    ListView itemsList;
    private FloatingActionButton add, edit, count;

    public TransactionsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_transactions, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        itemsList = (ListView) getView().findViewById(R.id.transaction_items);
        assert storeItems != null;
        itemsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), TransactionViewActivity.class);
                intent.putExtra("transaction", storeItems.get(position));
                // Get the transition name from the string
                String transitionName = "reveal";

                ActivityOptionsCompat options =

                        ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                                view,   // Starting view
                                transitionName    // The String
                        );

                ActivityCompat.startActivity(getActivity(), intent, options.toBundle());

            }

        });

        itemsList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            //on item click create a url and open it in the browser
            public boolean onItemLongClick(AdapterView<?> l, View v, int position, long id) {
                removeItem(position);
                return true;
            }
        });
        itemsList.setTransitionName("reveal");

        populateStoreList();

    }


    private void populateStoreList() {

        storeItems = new ArrayList<>();
        storeItems.add(new Transaction("Sean","Test Merchant",2.99,"1,2,3,4,5"));
        storeItems.add(new Transaction("Sean","Test Merchant",2.50,"1,2,3,4,5"));
        storeItems.add(new Transaction("Sean","Test Merchant",3.99,"1,2,3,4,5"));
        storeItems.add(new Transaction("Sean","Test Merchant",4.99,"1,2,3,4,5"));
        storeItems.add(new Transaction("Sean","Test Merchant",12.99,"1,2,3,4,5"));
        storeItems.add(new Transaction("Sean","Test Merchant",11.99,"1,2,3,4,5"));
        storeItems.add(new Transaction("Sean","Test Merchant",2.78,"1,2,3,4,5"));

        TransactionListAdapter adapter = new TransactionListAdapter(getActivity(), storeItems);

        itemsList.setAdapter(adapter);
    }


    private void removeItem(int index) {
        storeItems.remove(index);
        TransactionListAdapter adapter = new TransactionListAdapter(getActivity(), storeItems);
        itemsList.setAdapter(adapter);
    }
}

