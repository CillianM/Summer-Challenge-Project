package com.mastercard.simplifyapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mastercard.simplifyapp.R;
import com.mastercard.simplifyapp.objects.StoreItem;
import com.mastercard.simplifyapp.objects.Transaction;
import com.mastercard.simplifyapp.utility.ColorGenerator;
import com.mastercard.simplifyapp.utility.TextDrawable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by e069278 on 26/06/2017.
 */

public class TransactionListAdapter extends BaseAdapter {

    Context mContext;
    ArrayList<Transaction> mNavItems;

    public TransactionListAdapter(Context context, ArrayList<Transaction> navItems) {
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
            view = inflater.inflate(R.layout.transaction_item, null);
        }
        else {
            view = convertView;
        }

        TextView titleView = (TextView) view.findViewById(R.id.title);
        TextView dateView = (TextView) view.findViewById(R.id.date);
        TextView priceView = (TextView) view.findViewById(R.id.price);
        ImageView iconView = (ImageView) view.findViewById(R.id.icon);
        ColorGenerator generator = ColorGenerator.MATERIAL;
        int color = generator.getRandomColor();
        TextDrawable drawable = TextDrawable.builder()
                .buildRect(mNavItems.get(position).getCustomerName().toUpperCase().substring(0,1), Color.LTGRAY);
        iconView.setImageDrawable(drawable);
        titleView.setText( mNavItems.get(position).getCustomerName() );
        priceView.setText("€" + mNavItems.get(position).getTransactionAmount() );
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        String reportDate = df.format(mNavItems.get(position).getDate());
        dateView.setText(reportDate);


        return view;
    }
}
