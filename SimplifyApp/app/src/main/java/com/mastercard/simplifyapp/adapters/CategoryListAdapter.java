package com.mastercard.simplifyapp.adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mastercard.simplifyapp.ItemViewActivity;
import com.mastercard.simplifyapp.R;
import com.mastercard.simplifyapp.handlers.StockHandler;
import com.mastercard.simplifyapp.objects.ItemCategory;
import com.mastercard.simplifyapp.objects.StoreItem;
import com.mastercard.simplifyapp.utility.ColorGenerator;
import com.mastercard.simplifyapp.utility.TextDrawable;

import java.util.ArrayList;

@SuppressWarnings("unchecked")
public class CategoryListAdapter extends BaseExpandableListAdapter {

    public ArrayList<ItemCategory> categories;
    public LayoutInflater minflater;
    public Activity activity;


    public CategoryListAdapter(ArrayList<ItemCategory> categories) {
        this.categories = categories;
    }

    public void setInflater(LayoutInflater mInflater, Activity act) {
        this.minflater = mInflater;
        activity = act;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, final ViewGroup parent) {
        final ArrayList<StoreItem> items = categories.get(groupPosition).getItems();
        TextView text = null;
        if (convertView == null) {
            convertView = minflater.inflate(R.layout.store_item, null);
        }
        TextView titleView = (TextView) convertView.findViewById(R.id.title);
        TextView priceView = (TextView) convertView.findViewById(R.id.price);
        ImageView iconView = (ImageView) convertView.findViewById(R.id.icon);
        ColorGenerator generator = ColorGenerator.MATERIAL;
        int color = generator.getRandomColor();
        TextDrawable drawable = TextDrawable.builder()
                .buildRect(items.get(childPosition).getName().toUpperCase().substring(0, 1), Color.LTGRAY);
        iconView.setImageDrawable(drawable);
        titleView.setText(items.get(childPosition).getName());
        priceView.setText("€" + items.get(childPosition).getPriceString());
        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                StockHandler handler = new StockHandler(activity.getBaseContext());
                handler.open();
                handler.deleteStock(items.get(childPosition).getName());
                handler.close();
                items.remove(childPosition);
                notifyDataSetChanged();
                return false;
            }
        });
        convertView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, ItemViewActivity.class);
                intent.putExtra("id", items.get(childPosition).getId());
                // Get the transition name from the string
                String transitionName = "reveal";

                ActivityOptionsCompat options =

                        ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
                                parent,   // Starting view
                                transitionName    // The String
                        );

                ActivityCompat.startActivity(activity, intent, options.toBundle());
            }
        });
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return categories.get(groupPosition).getItems().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return null;
    }

    @Override
    public int getGroupCount() {
        return categories.size();
    }

    @Override
    public void onGroupCollapsed(int groupPosition) {
        super.onGroupCollapsed(groupPosition);
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        super.onGroupExpanded(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = minflater.inflate(R.layout.category_item, null);
        }
        String name = categories.get(groupPosition).getName();

        TextView titleView = (TextView) convertView.findViewById(R.id.title);
        TextView countView = (TextView) convertView.findViewById(R.id.item_count);
        String count = categories.get(groupPosition).getItems().size() + " Items";
        countView.setText(count);
        titleView.setText(name);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

}
