package com.mastercard.simplifyapp.fragments;

import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.mastercard.simplifyapp.R;
import com.mastercard.simplifyapp.adapters.CategoryListAdapter;
import com.mastercard.simplifyapp.handlers.CategoryHandler;
import com.mastercard.simplifyapp.handlers.StockHandler;
import com.mastercard.simplifyapp.objects.ItemCategory;
import com.mastercard.simplifyapp.objects.StoreItem;

import java.util.ArrayList;

/**
 * Created by e069278 on 23/05/2017.
 */

public class StockFragment extends Fragment{

    int MY_SCAN_REQUEST_CODE = 1;
    ArrayList<StoreItem> storeItems;
    ArrayList<ItemCategory> groupItems;
    ExpandableListView itemsList;
    private FloatingActionButton add,edit,count;
    EditText name,description,price,quantity;
    CategoryListAdapter adapter;

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
        final FloatingActionMenu menu = (FloatingActionMenu) getView().findViewById(R.id.menu);
        add = (FloatingActionButton) getView().findViewById(R.id.add_item);
        add.setOnClickListener(new View.OnClickListener() {
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
        itemsList = (ExpandableListView) getView().findViewById(R.id.checkout_items);
        assert storeItems != null;
        itemsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


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

        itemsList.setOnScrollListener(new AbsListView.OnScrollListener(){

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                    menu.hideMenu(true);
                } else {
                    menu.showMenu(true);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });

        populateStoreList();

    }

    void populateStoreList()
    {
        CategoryHandler categoryHandler = new CategoryHandler(getActivity().getBaseContext());
        categoryHandler.open();

        groupItems = new ArrayList<>();
        Cursor c = categoryHandler.returnData();
        if (c.moveToFirst()) {
            do {
                groupItems.add(new ItemCategory(c.getString(0), c.getString(1)));
            }
            while (c.moveToNext());
        }

        StockHandler handler = new StockHandler(getActivity().getBaseContext());
        handler.open();

        storeItems = new ArrayList<>();
        Cursor c1 = handler.returnData();
        if (c1.moveToFirst()) {
            do {
                storeItems.add(new StoreItem(c1.getString(0), c1.getString(1), c1.getString(2), c1.getString(3), c1.getFloat(4)));
            }
            while (c1.moveToNext());
        }
        handler.close();

        setGroups();

        adapter = new CategoryListAdapter(groupItems);
        adapter.setInflater((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE), getActivity());
        itemsList.setAdapter(adapter);
    }

    private void setGroups() {
        for (ItemCategory cat : groupItems) {
            ArrayList<StoreItem> itemList = new ArrayList<>();
            for (StoreItem item : storeItems) {
                if (item.getCategoryId().matches(cat.getId())) {
                    itemList.add(item);
                }
            }
            cat.setItems(itemList);
        }
    }

    public void addItem() {
//        transactions.add(new StoreItem(generateUUID().toString(),"New Item","This is a new item that has been added", 1));
//        StoreListAdapter adapter = new StoreListAdapter(getActivity(), transactions);
//        itemsList.setAdapter(adapter);
        // custom dialog
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_add_stock);
        dialog.setTitle("Title...");

        TextView text = (TextView) dialog.findViewById(R.id.text);

        name = (EditText) dialog.findViewById(R.id.name);
        description = (EditText) dialog.findViewById(R.id.description);
        price = (EditText) dialog.findViewById(R.id.price);
        quantity = (EditText) dialog.findViewById(R.id.quantity);

        Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameText = name.getText().toString();
                String descriptionText = description.getText().toString();
                String quantityText = quantity.getText().toString();
                try{
                    int quantitiyNum = Integer.parseInt(quantityText);
                    double priceNum = Double.parseDouble(price.getText().toString());

                    StockHandler handler = new StockHandler(getActivity().getBaseContext());
                    handler.open();
                    handler.insertData("6", nameText, descriptionText, priceNum, quantitiyNum);
                    handler.close();
                    Toast.makeText(getActivity().getApplicationContext(),"Item saved",Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    storeItems.add(new StoreItem(nameText,descriptionText,priceNum,quantitiyNum));
                    adapter.notifyDataSetChanged();
                }
                catch (NumberFormatException e)
                {
                    Toast.makeText(getActivity().getApplicationContext(),"Quantity must be a number and Price must be of the form X.XX",Toast.LENGTH_SHORT).show();
                }

            }
        });

        dialog.show();
    }


    private void removeItem(int index) {

    }
}
