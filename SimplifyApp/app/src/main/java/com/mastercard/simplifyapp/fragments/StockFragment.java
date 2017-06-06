package com.mastercard.simplifyapp.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.mastercard.simplifyapp.ItemViewActivity;
import com.mastercard.simplifyapp.R;
import com.mastercard.simplifyapp.StoreItem;
import com.mastercard.simplifyapp.adapters.StoreListAdapter;
import com.mastercard.simplifyapp.widgets.SimplifyTextView;

import java.util.ArrayList;

import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;

/**
 * Created by e069278 on 23/05/2017.
 */

public class StockFragment extends Fragment{

    int MY_SCAN_REQUEST_CODE = 1;
    ArrayList<StoreItem> storeItems;
    ListView itemsList;
    private FloatingActionButton add,edit,count;

    public StockFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stock, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        add = (FloatingActionButton) getView().findViewById(R.id.add_item);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem();
            }

        });
        edit = (FloatingActionButton) getView().findViewById(R.id.edit_item);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem();
            }

        });
        count = (FloatingActionButton) getView().findViewById(R.id.count_item);
        count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem();
            }

        });
        itemsList = (ListView) getView().findViewById(R.id.checkout_items);
        assert storeItems != null;
        itemsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ItemViewActivity.class);
                intent.putExtra("name", storeItems.get(position).getName());
                intent.putExtra("description", storeItems.get(position).getDescription());
                intent.putExtra("price", storeItems.get(position).getPrice());
                intent.putExtra("quantity", storeItems.get(position).getQuantity());
                intent.putExtra("image", R.drawable.stock_icon);
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
        storeItems.add(new StoreItem("Item One","This is Item one", 10));
        storeItems.add(new StoreItem("Item Two","This is Item two", 5));
        storeItems.add(new StoreItem("Item Three","This is Item three", 17));
        storeItems.add(new StoreItem("Item Four","This is Item four", 20));
        storeItems.add(new StoreItem("Item Five","This is Item five", 10));
        storeItems.add(new StoreItem("Item Six","This is Item six", 5));
        storeItems.add(new StoreItem("Item Seven","This is Item seven", 17));
        storeItems.add(new StoreItem("Item Eight","This is Item eight", 20));

        StoreListAdapter adapter = new StoreListAdapter(getActivity(), storeItems);

        itemsList.setAdapter(adapter);
    }

    public void addItem() {
        storeItems.add(new StoreItem("New Item","This is a new item that has been added", 1));
        StoreListAdapter adapter = new StoreListAdapter(getActivity(), storeItems);
        itemsList.setAdapter(adapter);
    }

    private void removeItem(int index) {
        storeItems.remove(index);
        StoreListAdapter adapter = new StoreListAdapter(getActivity(), storeItems);
        itemsList.setAdapter(adapter);
    }
}
