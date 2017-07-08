package com.mastercard.simplifyapp;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.mastercard.simplifyapp.handlers.CategoryHandler;
import com.mastercard.simplifyapp.handlers.StockHandler;
import com.mastercard.simplifyapp.objects.ItemCategory;
import com.mastercard.simplifyapp.objects.StoreItem;
import com.mastercard.simplifyapp.objects.Transaction;

import java.util.ArrayList;

public class ItemViewActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener {

    private CollapsingToolbarLayout collapsingToolbarLayout = null;

    private Drawable drawable = null;
    ArrayList<Transaction> transactions;
    String itemId;
    int itemcount = 12;
    private ArrayList<ItemCategory> groupItems;
    FloatingActionButton mFab;

    private static final int PERCENTAGE_TO_SHOW_IMAGE = 20;
    private int mMaxScrollSize;
    private boolean mIsImageHidden;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_view);

        String id = getIntent().getStringExtra("id");
        StoreItem item = getStoreItem(id);
        itemId = id;

        mFab = (FloatingActionButton) findViewById(R.id.add_item);

        AppBarLayout appbar = (AppBarLayout) findViewById(R.id.activity_main_appbar);
        appbar.addOnOffsetChangedListener(this);

        CombinedChart combinedChart = (CombinedChart) findViewById(R.id.chart);
        combinedChart.getDescription().setEnabled(false);
        combinedChart.setDrawGridBackground(false);
        combinedChart.setDrawBarShadow(false);
        combinedChart.setHighlightFullBarEnabled(false);

        YAxis rightAxis = combinedChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis leftAxis = combinedChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        XAxis xAxis = combinedChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
        xAxis.setAxisMinimum(0f);
        xAxis.setGranularity(1f);

        CombinedData data = new CombinedData();
        data.setData(getBarData());
        data.setData(getLineData(item.getPrice()));
        combinedChart.setData(data);

        Window window = this.getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(item.getName());


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
        getCategories();
    }

    private void getCategories() {
        CategoryHandler categoryHandler = new CategoryHandler(getBaseContext());
        categoryHandler.open();

        groupItems = new ArrayList<>();
        Cursor c = categoryHandler.returnData();
        if (c.moveToFirst()) {
            do {
                groupItems.add(new ItemCategory(c.getString(0), c.getString(1)));
            }
            while (c.moveToNext());
        }

        categoryHandler.close();
    }

    private double getRandom(double max, double min) {
        return (Math.random() * max + min);
    }

    private int getRandom(int max, int min) {
        return (int) (Math.random() * max + min);
    }

    private LineData getLineData(double price) {

        LineData d = new LineData();

        ArrayList<Entry> entries = new ArrayList<Entry>();

        for (int index = 0; index < itemcount; index++)
            entries.add(new Entry(index + 0.5f, (float) getRandom(price + 1, price - 0.5)));

        LineDataSet set = new LineDataSet(entries, "Quantity");
        set.setColor(getResources().getColor(R.color.colorAccent));
        set.setLineWidth(2.5f);
        set.setCircleColor(getResources().getColor(R.color.colorPrimaryDark));
        set.setCircleRadius(5f);
        set.setFillColor(getResources().getColor(R.color.colorPrimaryDark));
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setDrawValues(true);
        set.setValueTextSize(10f);
        set.setValueTextColor(getResources().getColor(R.color.colorPrimaryDark));

        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        d.addDataSet(set);

        return d;
    }

    private BarData getBarData() {
        ArrayList<BarEntry> entries1 = new ArrayList<>();

        for (int index = 0; index < itemcount; index++) {
            entries1.add(new BarEntry(index, getRandom(25, 25)));
        }

        BarDataSet set1 = new BarDataSet(entries1, "Price");
        set1.setColor(getResources().getColor(R.color.colorPrimary));
        set1.setValueTextColor(getResources().getColor(R.color.colorPrimaryDark));
        set1.setValueTextSize(10f);
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);

        float groupSpace = 0.06f;
        float barSpace = 0.02f; // x2 dataset
        float barWidth = 0.45f; // x2 dataset
        // (0.45 + 0.02) * 2 + 0.06 = 1.00 -> interval per "group"

        BarData d = new BarData(set1);
        d.setBarWidth(barWidth);
        return d;
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

        if (drawable != null) {
            Bitmap bitmap = drawableToBitmap(drawable);
            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {

                @Override
                public void onGenerated(Palette palette) {
                    collapsingToolbarLayout.setExpandedTitleColor(palette.getMutedColor(getResources().getColor(R.color.colorPrimaryDark)));
                }
            });
        }
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

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (mMaxScrollSize == 0)
            mMaxScrollSize = appBarLayout.getTotalScrollRange();

        int currentScrollPercentage = (Math.abs(verticalOffset)) * 100
                / mMaxScrollSize;

        if (currentScrollPercentage >= PERCENTAGE_TO_SHOW_IMAGE) {
            if (!mIsImageHidden) {
                mIsImageHidden = true;

                ViewCompat.animate(mFab).scaleY(0).scaleX(0).start();
                /**
                 * Realize your any behavior for FAB here!
                 **/
            }
        }

        if (currentScrollPercentage < PERCENTAGE_TO_SHOW_IMAGE) {
            if (mIsImageHidden) {
                mIsImageHidden = false;
                ViewCompat.animate(mFab).scaleY(1).scaleX(1).start();
                /**
                 * Realize your any behavior for FAB here!
                 **/
            }
        }
    }
}
