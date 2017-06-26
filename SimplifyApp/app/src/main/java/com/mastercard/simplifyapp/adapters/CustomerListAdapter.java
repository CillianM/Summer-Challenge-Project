package com.mastercard.simplifyapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mastercard.simplifyapp.objects.Customer;
import com.mastercard.simplifyapp.R;
import com.github.pavlospt.roundedletterview.RoundedLetterView;
import com.mastercard.simplifyapp.utility.TextDrawable;

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
        ImageView letterView = (ImageView) view.findViewById(R.id.letter_icon);
        TextDrawable drawable = TextDrawable.builder()
                .buildRect(mNavItems.get(position).getName().toUpperCase().substring(0,1), Color.LTGRAY);
        letterView.setImageDrawable(drawable);
        titleView.setText( mNavItems.get(position).getName() );


        return view;
    }
}
