package com.mastercard.simplifyapp;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.mastercard.simplifyapp.adapters.TransactionListAdapter;
import com.mastercard.simplifyapp.handlers.StockHandler;
import com.mastercard.simplifyapp.handlers.TransactionHandler;
import com.mastercard.simplifyapp.objects.StoreItem;
import com.mastercard.simplifyapp.objects.Transaction;

import java.util.ArrayList;

import static com.mastercard.simplifyapp.utility.DbUtils.epochToDate;

public class ItemViewActivity extends AppCompatActivity {

    private CollapsingToolbarLayout collapsingToolbarLayout = null;

    private Drawable drawable = null;
    ArrayList<Transaction> transactions;
    ListView itemsList;
    String itemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_view);
        itemsList = (ListView) findViewById(R.id.item_history);
        itemsList.setNestedScrollingEnabled(true);

        Window window = this.getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        String id = getIntent().getStringExtra("id");
        StoreItem item = getStoreItem(id);
        itemId = id;

//        ImageView imageView = (ImageView) findViewById(R.id.item_image);
//        ColorGenerator generator = ColorGenerator.MATERIAL;
//        int color = generator.getRandomColor();
//        TextDrawable drawable = TextDrawable.builder()
//                .buildRound(item.getName().toUpperCase().substring(0, 1), Color.LTGRAY);
//        imageView.setImageDrawable(drawable);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(item.getName());


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


        TextView desc= (TextView)findViewById(R.id.desc);
        desc.setText(item.getDescription());

        TextView priceView = (TextView)findViewById(R.id.price);
        priceView.setText("€" + item.getPriceString());

        ImageView imageView = (ImageView) findViewById(R.id.item_image);
        if (item.getName().equals("Muffin")) {
            drawable = getDrawable(R.drawable.muffins);
            imageView.setImageDrawable(drawable);
        } else if (item.getName().equals("Coca Cola")) {
            drawable = getDrawable(R.drawable.cocacola);
            imageView.setImageDrawable(drawable);
        } else if (item.getName().equals("Apple")) {
            drawable = getDrawable(R.drawable.apple);
            imageView.setImageDrawable(drawable);
        } else if (item.getName().equals("Orange")) {
            drawable = getDrawable(R.drawable.orange);
            imageView.setImageDrawable(drawable);
        } else if (item.getName().equals("Banana")) {
            drawable = getDrawable(R.drawable.banana);
            imageView.setImageDrawable(drawable);
        } else if (item.getName().equals("Scone")) {
            drawable = getDrawable(R.drawable.scones);
            imageView.setImageDrawable(drawable);
        } else if (item.getName().equals("Cake Slice")) {
            drawable = getDrawable(R.drawable.cake_slice);
            imageView.setImageDrawable(drawable);
        } else if (item.getName().equals("Chocolate Bar")) {
            drawable = getDrawable(R.drawable.chocolate);
            imageView.setImageDrawable(drawable);
        } else if (item.getName().equals("Crisps")) {
            drawable = getDrawable(R.drawable.crisps);
            imageView.setImageDrawable(drawable);
        } else if (item.getName().equals("Fanta")) {
            drawable = getDrawable(R.drawable.fanta);
            imageView.setImageDrawable(drawable);
        } else if (item.getName().equals("Diet Coke")) {
            drawable = getDrawable(R.drawable.diet_coke);
            imageView.setImageDrawable(drawable);
        } else if (item.getName().equals("Bottled Water")) {
            drawable = getDrawable(R.drawable.water);
            imageView.setImageDrawable(drawable);
        } else if (item.getName().equals("Coffee")) {
            drawable = getDrawable(R.drawable.coffee);
            imageView.setImageDrawable(drawable);
        } else if (item.getName().equals("Tea")) {
            drawable = getDrawable(R.drawable.tea);
            imageView.setImageDrawable(drawable);
        } else if (item.getName().equals("Lotto Ticket")) {
            drawable = getDrawable(R.drawable.lotto);
            imageView.setImageDrawable(drawable);
        }
        dynamicToolbarColor();
        toolbarTextAppernce();
        populateStoreList();
    }

    void populateStoreList() {
        TransactionHandler handler = new TransactionHandler(getBaseContext());
        handler.open();
        if (handler.returnAmount() > 0) {

            transactions = new ArrayList<>();
            Cursor c1 = handler.returnData();
            if (c1.moveToFirst()) {
                do {
                    if (c1.getString(3).contains(itemId))
                        transactions.add(new Transaction(c1.getString(0), c1.getFloat(1), c1.getString(2), c1.getString(3), c1.getString(4), epochToDate(Long.parseLong(c1.getString(5)))));
                }
                while (c1.moveToNext());
            }

            handler.close();

            TransactionListAdapter adapter = new TransactionListAdapter(this, transactions);

            itemsList.setAdapter(adapter);
        }
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    private void dynamicToolbarColor() {

        Bitmap bitmap = drawableToBitmap(drawable);
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {

            @Override
            public void onGenerated(Palette palette) {
                collapsingToolbarLayout.setExpandedTitleColor(palette.getMutedColor(getResources().getColor(R.color.colorPrimaryDark)));
            }
        });
    }

    private void toolbarTextAppernce() {
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.collapsedappbar);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.expandedappbar);
    }

    @Override
    public boolean onSupportNavigateUp() {
        ActivityCompat.finishAfterTransition(this);
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
