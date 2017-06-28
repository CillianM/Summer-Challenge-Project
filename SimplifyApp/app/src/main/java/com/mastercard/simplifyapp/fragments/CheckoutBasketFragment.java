package com.mastercard.simplifyapp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.mastercard.simplifyapp.R;
import com.mastercard.simplifyapp.interfaces.DataChangeListener;
import com.mastercard.simplifyapp.interfaces.UpdateableFragment;
import com.mastercard.simplifyapp.objects.StoreItem;
import com.mastercard.simplifyapp.objects.UpdateData;

import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;

/**
 * Created by e069278 on 28/06/2017.
 */

public class CheckoutBasketFragment extends Fragment implements UpdateableFragment, DataChangeListener {

    ArrayAdapter<String> adapter;
    ArrayList<String> items;
    ArrayList<StoreItem> storeItems;
    ListView itemList;
    CheckoutFragment parent;
    public CheckoutBasketFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_checkout_basket, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        items = bundle.getStringArrayList("items");
        itemList = (ListView) view.findViewById(R.id.basket_items);
        itemList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {
                items.remove(pos);
                if(CollectionUtils.isNotEmpty(storeItems)) {
                    storeItems.remove(pos);
                    onDataChanged();
                }
                adapter.notifyDataSetChanged();
                return true;
            }
        });
        adapter = new ArrayAdapter<>(this.getActivity().getApplicationContext(),
                android.R.layout.simple_list_item_1, items);
        itemList.setAdapter(adapter);

    }

    private ArrayList<String> toStringArray(ArrayList<StoreItem> items)
    {
        ArrayList<String> strings = new ArrayList<>();
        for(StoreItem item: items)
        {
            strings.add(item.getName());
        }
        return strings;
    }

    @Override
    public void update(UpdateData object) {
        storeItems = object.getItems();
        items = toStringArray(object.getItems());
        adapter = new ArrayAdapter<>(this.getActivity().getApplicationContext(),
                android.R.layout.simple_list_item_1, items);
        itemList.setAdapter(adapter);
    }

    @Override
    public void onDataChanged() {
        parent.notifyChildren(storeItems);
    }

    public void setParent(CheckoutFragment parent) {
        this.parent = parent;
    }
}
