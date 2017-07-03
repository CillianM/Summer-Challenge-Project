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
                populateStock();
                populateCustomers();
                Intent i = new Intent(CoverActivity.this, LoginActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        }, timeOut);
    }

    void populateStock() {
        StockHandler handler = new StockHandler(getApplicationContext());
        handler.open();
        int length = handler.returnAmount();
        if (length < 1) {
            handler.insertData("Coffee", "This is Item one", 2.99, 100);
            handler.insertData("Tea", "This is Item two", 1.99, 100);
            handler.insertData("Scone", "This is Item three", 1.99, 100);
            handler.insertData("Muffin", "This is Item four", 1.99, 100);
            handler.insertData("Cake Slice", "This is Item five", 3.99, 100);
            handler.insertData("Orange Juice", "This is Item six", 2.00, 100);
            handler.insertData("Bottled Water", "This is Item seven", 1.50, 100);
            handler.insertData("Sandwich", "This is Item eight", 4.99, 100);
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
