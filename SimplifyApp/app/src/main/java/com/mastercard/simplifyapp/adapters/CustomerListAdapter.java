package com.mastercard.simplifyapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mastercard.simplifyapp.Customer;
import com.mastercard.simplifyapp.R;
import com.github.pavlospt.roundedletterview.RoundedLetterView;

import java.util.ArrayList;

/**
 * Created by Cillian on 10/05/2017.
 */

public class CustomerListAdapter extends BaseAdapter {

    Context mContext;
    ArrayList<Customer> mNavItems;

    public CustomerListAdapter(Context context, ArrayList<Customer> navItems) {
        mContext = context;
        mNavItems = navItems;
    }

    @Override
    public int getCount() {
        return mNavItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mNavItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.customer_listing, null);
        }
        else {
            view = convertView;
        }

        TextView titleView = (TextView) view.findViewById(R.id.customer_name);
        RoundedLetterView letterView = (RoundedLetterView) view.findViewById(R.id.letter_icon);


        titleView.setText( mNavItems.get(position).getName() );
        letterView.setTitleText(mNavItems.get(position).getName().substring(0,1).toUpperCase());


        return view;
    }
}
