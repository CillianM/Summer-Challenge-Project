package com.mastercard.simplifyapp.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.mastercard.simplifyapp.PaymentActivity;
import com.mastercard.simplifyapp.R;
import com.mastercard.simplifyapp.adapters.StoreListAdapter;
import com.mastercard.simplifyapp.handlers.StockHandler;
import com.mastercard.simplifyapp.interfaces.UpdateableFragment;
import com.mastercard.simplifyapp.objects.StoreItem;
import com.mastercard.simplifyapp.objects.Transaction;
import com.mastercard.simplifyapp.objects.UpdateData;

import java.util.ArrayList;

import static com.mastercard.simplifyapp.utility.DbUtils.round;

/**
 * Created by e069278 on 23/05/2017.
 */

public class CheckoutFragment extends Fragment implements SearchView.OnQueryTextListener {

    ListView savedItems;
    FloatingActionMenu menu;
    FloatingActionButton scanBarcode, checkout;
    ArrayList<StoreItem> checkoutItems;
    ArrayList<StoreItem> storeItems;
    double total;
    ViewPager mPager;
    ScreenSlidePagerAdapter mPagerAdapter;
    SearchView searchView;
    StoreListAdapter adapter;


    public CheckoutFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_checkout, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        menu = (FloatingActionMenu) getView().findViewById(R.id.menu);
        checkout = (FloatingActionButton) getView().findViewById(R.id.confirm_transaction);
        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commitTransaction();
            }

        });
        scanBarcode = (FloatingActionButton) getView().findViewById(R.id.scan_item);
        scanBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }

        });
        savedItems = (ListView) getView().findViewById(R.id.checkout_items);
        savedItems.setTextFilterEnabled(false);
        savedItems.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                    menu.hideMenu(true);
                } else {
                    menu.showMenu(true);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

        assert savedItems != null;
        savedItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                addCheckoutItem((StoreItem) adapter.getItem(position));
            }

        });

        savedItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            //on item click create a url and open it in the browser
            public boolean onItemLongClick(AdapterView<?> l, View v, int position, long id) {
                return true;
            }
        });

        searchView = (SearchView) getView().findViewById(R.id.search_stock);
        setupSearch();

        mPager = (ViewPager) getView().findViewById(R.id.basket_info);
        mPagerAdapter = new ScreenSlidePagerAdapter(getChildFragmentManager());
        mPagerAdapter.setParent(this);
        mPager.setAdapter(mPagerAdapter);
        populateStoreList();
        menu.showMenu(true);

    }

    private void addCheckoutItem(StoreItem storeItem) {
        checkoutItems.add(storeItem);
        calculateTotal();
        mPagerAdapter.notifyDataSetChanged();
        menu.showMenu(true);
    }

    private void setupSearch() {
        searchView.setIconifiedByDefault(true);
        searchView.setOnQueryTextListener(this);
        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryHint("Search Here");
    }

    private void commitTransaction() {
        if (total > 0) {
            Intent i = new Intent(this.getActivity(), PaymentActivity.class);
            i.putExtra("transaction", new Transaction(total, "N/A", getCheckoutIds(), "Unknown"));
            startActivity(i);
        } else {
            Toast.makeText(this.getActivity().getApplicationContext(), "Add something to the basket!", Toast.LENGTH_SHORT).show();
        }
    }

    private String getCheckoutIds() {
        String items = "";
        for (StoreItem item : checkoutItems) {
            items += item.getId() + ",";
        }
        return items.substring(0, items.length() - 1);
    }


    private void calculateTotal() {
        total = 0;
        for (StoreItem item : checkoutItems) {
            total += item.getPrice();
        }

        total = round(total, 2);
        mPagerAdapter.notifyDataSetChanged();
    }

    void populateStoreList() {
        StockHandler handler = new StockHandler(getActivity().getBaseContext());
        handler.open();

        storeItems = new ArrayList<>();
        Cursor c1 = handler.returnData();
        if (c1.moveToFirst()) {
            do {
                storeItems.add(new StoreItem(c1.getString(0), c1.getString(1), c1.getString(2), c1.getString(3), c1.getFloat(4)));
            }
            while (c1.moveToNext());
        }

        handler.close();

        adapter = new StoreListAdapter(getActivity(), storeItems);
        checkoutItems = new ArrayList<>();
        calculateTotal();
        savedItems.setAdapter(adapter);
        mPagerAdapter.notifyDataSetChanged();
    }


    private ArrayList<String> toStringArray(ArrayList<StoreItem> items) {
        ArrayList<String> strings = new ArrayList<>();
        for (StoreItem item : items) {
            strings.add(item.getName());
        }
        return strings;
    }

    public void notifyChildren(ArrayList<StoreItem> items) {
        total = 0;
        for (StoreItem item : items) {
            total += item.getPrice();
        }
        total = round(total, 2);

        mPagerAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        adapter.getFilter().filter(newText);
        return true;
    }

    //Set up fragments for upper part of the screen
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        CheckoutFragment parent;

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void setParent(CheckoutFragment parent) {
            this.parent = parent;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment;

            if (position == 0) {
                fragment = new CheckoutTotalFragment();

                Bundle args = new Bundle();
                args.putDouble("total", total);
                fragment.setArguments(args);
                return fragment;
            } else {
                fragment = new CheckoutBasketFragment();
                ((CheckoutBasketFragment) fragment).setParent(parent);
                Bundle args = new Bundle();
                args.putStringArrayList("items", toStringArray(checkoutItems));
                fragment.setArguments(args);
                return fragment;
            }
        }

        @Override
        public int getItemPosition(Object object) {
            UpdateData updateData = new UpdateData(checkoutItems, total);
            if (object instanceof UpdateableFragment)
                ((UpdateableFragment) object).update(updateData);
            //return POSITION_NONE;
            return super.getItemPosition(object);
        }


        @Override
        public int getCount() {
            return 2;
        }
    }
}
