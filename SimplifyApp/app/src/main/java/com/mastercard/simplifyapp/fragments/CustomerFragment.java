package com.mastercard.simplifyapp.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
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
                if (scrollState == SCROLL_STATE_IDLE){
                    menu.show(true);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (visibleItemCount > totalItemCount && menu.isShown())
                    menu.hide(true);
            }
        });

        populateCustomerList();

    }

    private void removeItem(int position) {
        customers.remove(position);

        CustomerListAdapter adapter = new CustomerListAdapter(getActivity(), customers);

        customerList.setAdapter(adapter);
    }

    private void populateCustomerList() {

        CustomerHandler handler = new CustomerHandler(getActivity().getBaseContext());
        handler.open();
        int length = handler.returnAmount();


        if(length < 1)
        {
            handler.insertData("Cillian Mc Neill");
            handler.insertData("Sarah Kingston");
            handler.insertData("Mark Scully");
            handler.insertData("Mary O'Brien");
            handler.insertData("Rachel Byrne");
        }

        customers = new ArrayList<>();
        Cursor c1 = handler.returnData();
        if (c1.moveToFirst()) {
            do {
                customers.add(new Customer(c1.getString(1)));
            }
            while (c1.moveToNext());
        }

        handler.close();

        adapter = new CustomerListAdapter(getActivity(), customers);

        customerList.setAdapter(adapter);
    }

    private void addItem() {
        customers.add(new Customer("New Name"));
        adapter.notifyDataSetChanged();
    }

}
