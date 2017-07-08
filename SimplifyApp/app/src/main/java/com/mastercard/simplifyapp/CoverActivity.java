package com.mastercard.simplifyapp;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.mastercard.simplifyapp.handlers.CategoryHandler;
import com.mastercard.simplifyapp.handlers.CustomerHandler;
import com.mastercard.simplifyapp.handlers.StockHandler;

public class CoverActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cover);

        findViewById(R.id.mastercard_logo).addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                v.removeOnLayoutChangeListener(this);

                float finalRadius = (float) Math.hypot(v.getWidth(), v.getHeight());
                int cx1 = (findViewById(R.id.mastercard_logo).getLeft() + findViewById(R.id.mastercard_logo).getRight()) / 2;
                int cy1 = (findViewById(R.id.mastercard_logo).getTop() + findViewById(R.id.mastercard_logo).getBottom()) / 2;
                Animator anim = ViewAnimationUtils.createCircularReveal(v, cx1, cy1, 0, finalRadius);
                anim.setDuration(1400);
                anim.setInterpolator(new AccelerateDecelerateInterpolator());

                anim.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        findViewById(R.id.mastercard_logo).setBackgroundResource(R.color.transparent);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                anim.start();


            }
        });

        Window window = this.getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        //Wait the timeout then move on to home activity
        int timeOut = 3000;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                populateCategory();
                populateStock();
                populateCustomers();
                Intent i = new Intent(CoverActivity.this, LoginActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        }, timeOut);
    }

    void populateCategory() {
        CategoryHandler handler = new CategoryHandler(getApplicationContext());
        handler.open();
        int length = handler.returnAmount();
        if (length < 1) {
            handler.insertData("1", "Fruit");
            handler.insertData("2", "Baked Goods");
            handler.insertData("3", "Sweets");
            handler.insertData("4", "Cold Drinks");
            handler.insertData("5", "Hot Drinks");
            handler.insertData("6", "Misc");
        }
        handler.close();
    }

    void populateStock() {
        StockHandler handler = new StockHandler(getApplicationContext());
        handler.open();
        int length = handler.returnAmount();
        if (length < 1) {
            handler.insertData("5", "Coffee", "Plain Coffee", 1.49, 100);
            handler.insertData("5", "Tea", "Plain Tea", 1.49, 100);
            handler.insertData("4", "Coca Cola", "Original Coca cola", 1.49, 100);
            handler.insertData("4", "Fanta", "Orange Fanta", 1.49, 100);
            handler.insertData("4", "Diet Coke", "Diet version of coca cola", 1.49, 100);
            handler.insertData("3", "Chocolate Bar", "Cadbury", 1.29, 100);
            handler.insertData("3", "Crisps", "King Crisps", 0.99, 100);
            handler.insertData("2", "Muffin", "Chocolate muffin", 1.99, 100);
            handler.insertData("2", "Scone", "Plain scone", 1.99, 100);
            handler.insertData("2", "Cake Slice", "Cheesecake", 1.99, 100);
            handler.insertData("1", "Apple", "Red Apple", 0.89, 100);
            handler.insertData("1", "Orange", "Simple orange", 0.89, 100);
            handler.insertData("1", "Banana", "Simple banana", 0.89, 100);
            handler.insertData("4", "Bottled Water", "Plain bottled water", 1.50, 100);
            handler.insertData("6", "Lotto Ticket", "Ticket for this weeks lotto", 4.99, 100);
        }
    }

    void populateCustomers() {
        CustomerHandler handler = new CustomerHandler(getApplicationContext());
        handler.open();
        int length = handler.returnAmount();


        if (length < 1) {
            handler.insertData("Cillian Mc Neill");
            handler.insertData("Sarah Kingston");
            handler.insertData("Mark Scully");
            handler.insertData("Mary O'Brien");
            handler.insertData("Rachel Byrne");
        }
    }

}
