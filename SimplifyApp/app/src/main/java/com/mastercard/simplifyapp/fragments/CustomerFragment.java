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
import com.simplify.android.sdk.Customer;

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
        Customer customer1 = new Customer();
        customer1.setName("Firdaus Liborius");
        Customer customer2 = new Customer();
        customer2.setName("Cas Brendan");
        Customer customer3 = new Customer();
        customer3.setName("Hyginos Givi");
        Customer customer4 = new Customer();
        customer4.setName("Paderau Servaas");
        Customer customer5 = new Customer();
        customer5.setName("Haimo Dmitar");
        customers.add(customer1);
        customers.add(customer2);
        customers.add(customer3);
        customers.add(customer4);
        customers.add(customer5);

        CustomerListAdapter adapter = new CustomerListAdapter(getActivity(), customers);

        customerList.setAdapter(adapter);
    }

    private void addItem() {
        Customer customer = new Customer();
        customer.setName("New Name");
        customers.add(customer);

        CustomerListAdapter adapter = new CustomerListAdapter(getActivity(), customers);

        customerList.setAdapter(adapter);
    }

}
