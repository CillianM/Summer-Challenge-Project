package com.mastercard.simplifyapp;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mastercard.simplifyapp.handlers.StockHandler;
import com.mastercard.simplifyapp.objects.StoreItem;
import com.mastercard.simplifyapp.utility.ColorGenerator;
import com.mastercard.simplifyapp.utility.TextDrawable;

public class ItemViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_view);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String id = getIntent().getStringExtra("id");
        StoreItem item = getStoreItem(id);

        ImageView imageView = (ImageView) findViewById(R.id.item_image);
        ColorGenerator generator = ColorGenerator.MATERIAL;
        int color = generator.getRandomColor();
        TextDrawable drawable = TextDrawable.builder()
                .buildRound(item.getName().toUpperCase().substring(0, 1), Color.LTGRAY);
        imageView.setImageDrawable(drawable);

        setTitle(item.getName());

//        final LinearLayout topLayout = (LinearLayout)findViewById(R.id.layout);
//        topLayout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
//            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//            @Override
//            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
//                v.removeOnLayoutChangeListener(this);
//                float finalRadius = (float) Math.hypot(v.getWidth(), v.getHeight());
//                int cx1 = (topLayout.getLeft() + topLayout.getRight()) / 2;
//                int cy1 = (topLayout.getTop() + topLayout.getBottom()) / 2;
//                Animator anim = ViewAnimationUtils.createCircularReveal(v, cx1, cy1, 0, finalRadius);
//                anim.setDuration(1400);
//                anim.setInterpolator(new AccelerateDecelerateInterpolator());
//                anim.start();
//            }
//        });

        TextView heading= (TextView)findViewById(R.id.heading);
        heading.setText(item.getName());

        TextView desc= (TextView)findViewById(R.id.desc);
        desc.setText(item.getDescription());

        TextView priceView = (TextView)findViewById(R.id.price);
        priceView.setText(item.getPriceString());



        android.support.design.widget.FloatingActionButton editFab = (android.support.design.widget.FloatingActionButton) findViewById(R.id.edit_fab);
        editFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }

        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private StoreItem getStoreItem(String id) {
        StockHandler handler = new StockHandler(getApplicationContext());
        handler.open();
        StoreItem item = handler.getStoreItem(id);
        handler.close();
        return item;
    }

    @Override
    public void onBackPressed() {
        ActivityCompat.finishAfterTransition(this);
    }
}
