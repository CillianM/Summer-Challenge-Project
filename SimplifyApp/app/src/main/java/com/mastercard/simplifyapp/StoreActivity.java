package com.mastercard.simplifyapp;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.mastercard.simplifyapp.adapters.DrawerListAdapter;
import com.mastercard.simplifyapp.fragments.CheckoutFragment;
import com.mastercard.simplifyapp.fragments.CustomerFragment;
import com.mastercard.simplifyapp.fragments.SettingsFragment;
import com.mastercard.simplifyapp.fragments.StockFragment;
import com.mastercard.simplifyapp.fragments.TransactionsFragment;
import com.mastercard.simplifyapp.objects.NavItem;

import java.util.ArrayList;

public class StoreActivity extends AppCompatActivity {

    ListView mDrawerList;
    RelativeLayout mDrawerPane;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    int count = 0;
    private static String TAG = StoreActivity.class.getSimpleName();

    // Arraylist of custom nav items for side bar
    ArrayList<NavItem> mNavItems = new ArrayList<NavItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        mNavItems.add(new NavItem("Checkout", "Check Out Customers Items", R.drawable.ic_shopping_basket_black_24dp));
        mNavItems.add(new NavItem("Transactions", "View Transactions", R.drawable.ic_receipt_black_24dp));
        mNavItems.add(new NavItem("Stock", "Stock Control", R.drawable.ic_store_black_24dp));
        mNavItems.add(new NavItem("Customers", "Search For Customer Information", R.drawable.ic_perm_identity_black_24dp));


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // DrawerLayout
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        // Populate the Navigtion Drawer with options
        mDrawerPane = (RelativeLayout) findViewById(R.id.drawerPane);
        mDrawerList = (ListView) findViewById(R.id.navList);
        DrawerListAdapter adapter = new DrawerListAdapter(this, mNavItems);
        mDrawerList.setAdapter(adapter);

        // Drawer Item click listeners
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItemFromDrawer(position);
            }
        });

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                Log.d(TAG, "onDrawerClosed: " + getTitle());

                invalidateOptionsMenu();
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        selectItemFromDrawer(0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle
        // If it returns true, then it has handled
        // the nav drawer indicator touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(count == 0)
        {
            count++;
            Toast.makeText(this,"Press Back Again To Exit To Login",Toast.LENGTH_LONG).show();
        }
        else
        {
            finish();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }



    private void selectItemFromDrawer(int position) {
        Fragment fragment = null; //initialize empty fragment
        count = 0;
        if(position == 0) {
            fragment = new CheckoutFragment();
        }
        else if(position == 1) {
            fragment = new TransactionsFragment();
        }
        else if(position == 2) {
            fragment = new StockFragment();
        }
        else if(position == 3) {
            fragment = new CustomerFragment();
        }

        //Replace current fragment
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.mainContent, fragment,"FRAGMENT")
                .commit();

        mDrawerList.setItemChecked(position, true);
        setTitle(mNavItems.get(position).mTitle);

        // Close the drawer
        mDrawerLayout.closeDrawer(mDrawerPane);
    }

    public void openSettings(View view) {
        Fragment fragment = new SettingsFragment();

        //Replace current fragment
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.mainContent, fragment,"FRAGMENT")
                .commit();
        setTitle("Settings");

        // Close the drawer
        mDrawerLayout.closeDrawer(mDrawerPane);
    }
}
