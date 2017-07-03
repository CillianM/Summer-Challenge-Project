package com.mastercard.simplifyapp.fragments;

import android.app.Dialog;
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
import android.widget.ListView;

import com.github.clans.fab.FloatingActionButton;
import com.mastercard.simplifyapp.R;
import com.mastercard.simplifyapp.adapters.CustomerListAdapter;
import com.mastercard.simplifyapp.handlers.CustomerHandler;
import com.mastercard.simplifyapp.objects.Customer;

import java.util.ArrayList;

/**
 * Created by Cillian on 10/05/2017.
 */

public class CustomerFragment extends Fragment {


    FloatingActionButton addButton;
    ListView customerList;
    ArrayList<Customer> customers;
    CustomerListAdapter adapter;
    public CustomerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_customer, container, false);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        final FloatingActionButton menu = (FloatingActionButton) getView().findViewById(R.id.add_item);
        addButton = (FloatingActionButton) getView().findViewById(R.id.add_item);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem();
            }



        });
        customerList = (ListView) getView().findViewById(R.id.customer_list);
        customerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }

        });

        customerList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            //on item click create a url and open it in the browser
            public boolean onItemLongClick(AdapterView<?> l, View v, int position, long id) {
                removeItem(position);
                return true;
            }
        });
        customerList.setOnScrollListener(new AbsListView.OnScrollListener(){

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                    menu.hide(true);
                } else {
                    menu.show(true);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });

        populateCustomerList();

    }

    private void removeItem(int position) {
        CustomerHandler handler = new CustomerHandler(getActivity().getApplicationContext());
        handler.open();
        handler.deleteCustomer(customers.get(position).getId());
        handler.close();
        customers.remove(position);
        adapter.notifyDataSetChanged();
    }

    private void populateCustomerList() {

        CustomerHandler handler = new CustomerHandler(getActivity().getBaseContext());
        handler.open();

        customers = new ArrayList<>();
        Cursor c1 = handler.returnData();
        if (c1.moveToFirst()) {
            do {
                Customer c = new Customer(c1.getString(1));
                c.setId(c1.getString(0));
                customers.add(c);
            }
            while (c1.moveToNext());
        }

        handler.close();

        adapter = new CustomerListAdapter(getActivity(), customers);

        customerList.setAdapter(adapter);
    }

    private void addItem() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_add_customer);
        dialog.setTitle("Title...");

        final EditText name = (EditText) dialog.findViewById(R.id.name);

        Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameText = name.getText().toString();
                CustomerHandler handler = new CustomerHandler(getActivity().getApplicationContext());
                handler.open();
                handler.insertData(nameText);
                handler.close();
                customers.add(new Customer(nameText));
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });

        dialog.show();



    }

}
