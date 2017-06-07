package com.mastercard.simplifyapp;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.graphics.Color;
import android.os.Build;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.mastercard.simplifyapp.view.RoundedImageView;

public class ItemViewActivity extends AppCompatActivity {

    boolean notEditing = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_view);

        String name = getIntent().getStringExtra("name");
        String description = getIntent().getStringExtra("description");
        int price = getIntent().getIntExtra("price",0);
        int quantity = getIntent().getIntExtra("quantity",0);
        int image = getIntent().getIntExtra("image",0);

        setTitle(name);

        final LinearLayout topLayout = (LinearLayout)findViewById(R.id.layout);
        topLayout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                v.removeOnLayoutChangeListener(this);
                float finalRadius = (float) Math.hypot(v.getWidth(), v.getHeight());
                int cx1 = (topLayout.getLeft() + topLayout.getRight()) / 2;
                int cy1 = (topLayout.getTop() + topLayout.getBottom()) / 2;
                Animator anim = ViewAnimationUtils.createCircularReveal(v, cx1, cy1, 0, finalRadius);
                anim.setDuration(1400);
                anim.setInterpolator(new AccelerateDecelerateInterpolator());
                anim.start();
            }
        });

        TextView heading= (TextView)findViewById(R.id.heading);
        heading.setText(name);

        TextView desc= (TextView)findViewById(R.id.desc);
        desc.setText(description);

        TextView priceView = (TextView)findViewById(R.id.price);
        priceView.setText(price + "");

        RoundedImageView imageView = (RoundedImageView)findViewById(R.id.item_image);
        imageView.setImageResource(image);

        android.support.design.widget.FloatingActionButton editFab = (android.support.design.widget.FloatingActionButton) findViewById(R.id.edit_fab);
        editFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(notEditing) {
                    showEdit();
                    notEditing = false;
                }
                else
                {
                    hideEdit();
                    notEditing = true;
                }
            }

        });
    }

    private void hideEdit() {

        findViewById(R.id.topViewOptions).setVisibility(View.GONE);
        findViewById(R.id.topView).setVisibility(View.VISIBLE);
        findViewById(R.id.topView).addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                v.removeOnLayoutChangeListener(this);
                int cx1 = 0;
                int cy1 = 0;
                // get the hypothenuse so the radius is from one corner to the other
                int radius1 = (int) Math.hypot(right, bottom);
                Animator reveal = ViewAnimationUtils.createCircularReveal(v, cx1, cy1, 0, radius1);
                reveal.setInterpolator(new AccelerateDecelerateInterpolator());
                reveal.setDuration(500);
                reveal.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        //findViewById(R.id.topView).setBackgroundResource(R.color.transparent);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });

                reveal.start();
            }
        });
    }

    private void showEdit() {

        findViewById(R.id.topView).setVisibility(View.GONE);
        findViewById(R.id.topViewOptions).setVisibility(View.VISIBLE);
        findViewById(R.id.topViewOptions).addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                v.removeOnLayoutChangeListener(this);
                int cx = (findViewById(R.id.topViewOptions).getLeft() + findViewById(R.id.topViewOptions).getRight());
                int cy = (findViewById(R.id.topViewOptions).getTop() + findViewById(R.id.topViewOptions).getBottom());
                // get the hypothenuse so the radius is from one corner to the other
                int radius = (int) Math.hypot(right, bottom);
                Animator reveal = ViewAnimationUtils.createCircularReveal(v, cx, cy, 0, radius);
                reveal.setInterpolator(new AccelerateDecelerateInterpolator());
                reveal.setDuration(500);
                reveal.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {

                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });

                reveal.start();
            }
        });
    }

    @Override
    public void onBackPressed() {
        findViewById(R.id.topViewOptions).setVisibility(View.GONE);
        ActivityCompat.finishAfterTransition(this);
    }
}
