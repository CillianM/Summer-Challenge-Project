package com.mastercard.simplifyapp;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

public class UserConfigActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_config);

        LottieAnimationView animationView = (LottieAnimationView) findViewById(R.id.animation_view);


        // Any class that conforms to the ColorFilter interface
        final ColorFilter colorFilter = new PorterDuffColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY);


        // Adding a color filter to the whole view
        animationView.addColorFilter(colorFilter);

        animationView.addColorFilterToLayer("UserIcon contornos",colorFilter);
        animationView.playAnimation();

    }


    public void merchantView(View view) {
        final RelativeLayout root = (RelativeLayout) findViewById(R.id.root_layout);
        final ImageView imageView = (ImageView) findViewById(R.id.store_icon);
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int statusBarOffset = dm.heightPixels - root.getMeasuredHeight();
        final int originalPos[] = new int[2];
        imageView.getLocationOnScreen(originalPos);
        final int xDest = (dm.widthPixels / 2) - (imageView.getMeasuredWidth() / 2);
        final int yDest = dm.heightPixels / 2 - (imageView.getMeasuredHeight() / 2)- statusBarOffset;

        TranslateAnimation anim = new TranslateAnimation(0, xDest
                - originalPos[0], 0, yDest - originalPos[1]);
        anim.setDuration(1000);
        anim.setFillAfter(true);
        anim.setZAdjustment(1);
        anim.setAnimationListener(new Animation.AnimationListener() {

            public void onAnimationStart(Animation animation) {
                findViewById(R.id.animation_view).setVisibility(View.INVISIBLE);
                findViewById(R.id.customer_icon).setVisibility(View.INVISIBLE);
                findViewById(R.id.customer_icon_text).setVisibility(View.INVISIBLE);
                findViewById(R.id.question).setVisibility(View.INVISIBLE);
                findViewById(R.id.store_icon_text).setVisibility(View.INVISIBLE);

            }

            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub

            }

            public void onAnimationEnd(Animation animation) {
                Intent i = new Intent(UserConfigActivity.this, LoginActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });
        imageView.startAnimation(anim);

    }

    public void customerView(View view) {
        final RelativeLayout root = (RelativeLayout) findViewById(R.id.root_layout);
        final ImageView imageView = (ImageView) findViewById(R.id.customer_icon);
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int statusBarOffset = dm.heightPixels - root.getMeasuredHeight();
        final int originalPos[] = new int[2];
        imageView.getLocationOnScreen(originalPos);
        final int xDest = (dm.widthPixels / 2) - (imageView.getMeasuredWidth() / 2);
        final int yDest = dm.heightPixels / 2 - (imageView.getMeasuredHeight() / 2)- statusBarOffset;

        TranslateAnimation anim = new TranslateAnimation(0, xDest
                - originalPos[0], 0, yDest - originalPos[1]);
        anim.setDuration(1000);
        anim.setFillAfter(true);
        anim.setZAdjustment(1);
        anim.setAnimationListener(new Animation.AnimationListener() {

            public void onAnimationStart(Animation animation) {
                findViewById(R.id.animation_view).setVisibility(View.INVISIBLE);
                findViewById(R.id.store_icon).setVisibility(View.INVISIBLE);
                findViewById(R.id.store_icon_text).setVisibility(View.INVISIBLE);
                findViewById(R.id.question).setVisibility(View.INVISIBLE);
                findViewById(R.id.customer_icon_text).setVisibility(View.INVISIBLE);

            }

            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub

            }

            public void onAnimationEnd(Animation animation) {
                Intent i = new Intent(UserConfigActivity.this, UserRegistrationActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });
        imageView.startAnimation(anim);
    }
}
