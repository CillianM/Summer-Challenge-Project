package com.mastercard.simplifyapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.mastercard.simplifyapp.R;
import com.mastercard.simplifyapp.objects.StoreItem;
import com.mastercard.simplifyapp.utility.ColorGenerator;
import com.mastercard.simplifyapp.utility.TextDrawable;

import java.util.ArrayList;

/**
 * Created by Cillian on 10/05/2017.
 */

public class StoreListAdapter extends BaseAdapter implements Filterable {

    Context mContext;
    ArrayList<StoreItem> mNavItems;
    ArrayList<StoreItem> originalList;

    public StoreListAdapter(Context context, ArrayList<StoreItem> navItems) {
        mContext = context;
        mNavItems = navItems;
        originalList = new ArrayList<>(mNavItems);
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
        TextView priceView = (TextView) view.findViewById(R.id.price);
        ImageView iconView = (ImageView) view.findViewById(R.id.icon);
        ColorGenerator generator = ColorGenerator.MATERIAL;
        int color = generator.getRandomColor();
        TextDrawable drawable = TextDrawable.builder()
                .buildRect(mNavItems.get(position).getName().toUpperCase().substring(0,1), Color.LTGRAY);
        iconView.setImageDrawable(drawable);
        titleView.setText( mNavItems.get(position).getName() );
        priceView.setText("€" + mNavItems.get(position).getPriceString() );

        return view;
    }

    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults toReturn = new FilterResults();
                final ArrayList<StoreItem> items = new ArrayList<>();
                if(constraint != null)
                {
                    if(originalList.size() > 0)
                    {
                        for(StoreItem item: originalList)
                        {
                            if(item.getName().toLowerCase().contains(constraint.toString()))
                                items.add(item);
                        }
                    }
                    toReturn.values = items;
                    return toReturn;
                }
                return toReturn;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mNavItems = (ArrayList<StoreItem>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public void notifyDataSetChanged(){
        super.notifyDataSetChanged();
    }
}
