package com.mastercard.simplifyapp.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.mastercard.simplifyapp.ItemViewActivity;
import com.mastercard.simplifyapp.R;
import com.mastercard.simplifyapp.adapters.StoreListAdapter;
import com.mastercard.simplifyapp.handlers.StockHandler;
import com.mastercard.simplifyapp.objects.StoreItem;

import java.util.ArrayList;

/**
 * Created by e069278 on 23/05/2017.
 */

public class StockFragment extends Fragment{

    int MY_SCAN_REQUEST_CODE = 1;
    ArrayList<StoreItem> storeItems;
    ListView itemsList;
    private FloatingActionButton add,edit,count;
    EditText name,description,price,quantity;
    StoreListAdapter adapter;

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

        itemsList.setOnScrollListener(new AbsListView.OnScrollListener(){

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE){
                    menu.showMenu(true);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (visibleItemCount > totalItemCount && menu.isShown())
                    menu.hideMenu(true);
            }
        });

        populateStoreList();

    }

    void populateStoreList()
    {
        StockHandler handler = new StockHandler(getActivity().getBaseContext());
        handler.open();
        int length = handler.returnAmount();


        if(length < 1)
        {
            handler.insertData("Coffee","This is Item one", 2.99,100);
            handler.insertData("Tea","This is Item two", 1.99,100);
            handler.insertData("Scone","This is Item three", 1.99,100);
            handler.insertData("Muffin","This is Item four", 1.99,100);
            handler.insertData("Cake Slice","This is Item five", 3.99,100);
            handler.insertData("Orange Juice","This is Item six", 2.00,100);
            handler.insertData("Bottled Water","This is Item seven", 1.50,100);
            handler.insertData("Sandwich","This is Item eight", 4.99,100);
        }

        storeItems = new ArrayList<>();
        Cursor c1 = handler.returnData();
        if (c1.moveToFirst()) {
            do {
                storeItems.add(new StoreItem(c1.getString(0),c1.getString(1),c1.getString(2),c1.getFloat(3)));
            }
            while (c1.moveToNext());
        }

        handler.close();

        adapter = new StoreListAdapter(getActivity(), storeItems);

        itemsList.setAdapter(adapter);
    }

    public void addItem() {
//        storeItems.add(new StoreItem(generateUUID().toString(),"New Item","This is a new item that has been added", 1));
//        StoreListAdapter adapter = new StoreListAdapter(getActivity(), storeItems);
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
                    handler.insertData(nameText,descriptionText,priceNum,quantitiyNum);
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
        StockHandler handler = new StockHandler(getActivity().getBaseContext());
        handler.open();
        handler.deleteStock(storeItems.get(index).getName());
        handler.close();
        storeItems.remove(index);
        adapter.notifyDataSetChanged();
    }
}
