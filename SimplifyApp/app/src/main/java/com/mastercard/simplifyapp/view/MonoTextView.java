package com.mastercard.simplifyapp.view;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

public class MonoTextView extends AppCompatTextView {

    public MonoTextView(Context context) {
        super(context);
        applyCustomFont(context);
    }

    public MonoTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont(context);
    }

    public MonoTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        applyCustomFont(context);
    }

    private void applyCustomFont(Context context) {
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/PTM55FT.ttf");
        setTypeface(typeface);
    }
}
