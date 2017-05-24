package com.mastercard.simplifyapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.mastercard.simplifyapp.R;
import com.mastercard.simplifyapp.StoreItem;

import java.util.ArrayList;

/**
 * Created by Cillian on 10/05/2017.
 */

public class StoreListAdapter extends BaseAdapter {

    Context mContext;
    ArrayList<StoreItem> mNavItems;

    public StoreListAdapter(Context context, ArrayList<StoreItem> navItems) {
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
            view = inflater.inflate(R.layout.store_item, null);
        }
        else {
            view = convertView;
        }

        TextView titleView = (TextView) view.findViewById(R.id.title);
        TextView descriptionView = (TextView) view.findViewById(R.id.description);
        TextView priceView = (TextView) view.findViewById(R.id.price);
        ImageView draweeView = (ImageView) view.findViewById(R.id.icon);


        titleView.setText( mNavItems.get(position).getName() );
        descriptionView.setText( mNavItems.get(position).getDescription() );
        priceView.setText( mNavItems.get(position).getPriceString() );

        return view;
    }
}
