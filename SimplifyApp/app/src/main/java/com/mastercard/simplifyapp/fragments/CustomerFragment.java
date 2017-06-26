package com.mastercard.simplifyapp.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.github.clans.fab.FloatingActionButton;
import com.mastercard.simplifyapp.adapters.CustomerListAdapter;
import com.mastercard.simplifyapp.R;
import com.mastercard.simplifyapp.objects.Customer;

import java.util.ArrayList;

/**
 * Created by Cillian on 10/05/2017.
 */

public class CustomerFragment extends Fragment {


    FloatingActionButton addButton;
    ListView customerList;
    ArrayList<Customer> customers;
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

        populateCustomerList();

    }

    private void removeItem(int position) {
        customers.remove(position);

        CustomerListAdapter adapter = new CustomerListAdapter(getActivity(), customers);

        customerList.setAdapter(adapter);
    }

    private void populateCustomerList() {
        customers = new ArrayList<>();
        customers.add(new Customer("Firdaus Liborius"));
        customers.add(new Customer("Cas Brendan"));
        customers.add(new Customer("Hyginos Givi"));
        customers.add(new Customer("Paderau Servaas"));
        customers.add(new Customer("Haimo Dmitar"));

        CustomerListAdapter adapter = new CustomerListAdapter(getActivity(), customers);

        customerList.setAdapter(adapter);
    }

    private void addItem() {
        customers.add(new Customer("New Name"));

        CustomerListAdapter adapter = new CustomerListAdapter(getActivity(), customers);

        customerList.setAdapter(adapter);
    }

}
