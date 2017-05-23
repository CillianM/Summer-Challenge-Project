package com.mastercard.simplifyapp.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;

import com.mastercard.simplifyapp.utility.FontUtils;

public class SimplifyButton extends android.support.v7.widget.AppCompatButton {

    /**
     * Creates the text view.
     * @param context The android context.
     */
    public SimplifyButton(Context context) {
        super(context);
        setFont(context, null);
    }

    /**
     * Creates the text view.
     * @param context The android context.
     * @param attrs Attributes for the TextView
     */
    public SimplifyButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFont(context, attrs);
    }

    /**
     * Creates the text view.
     * @param context The android context.
     * @param attrs Attributes for the TextView
     * @param defStyle The style for the TextView
     */
    public SimplifyButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setFont(context, attrs);
    }

    /**
     * Sets the font for the TextView based on attributes
     * @param context The android context.
     * @param attrs TextView attributes
     */
    private void setFont(Context context, AttributeSet attrs){
        TypedArray styleValues = context.obtainStyledAttributes(attrs, new int[]{android.R.attr.fontFamily});
        final String fontFamily = styleValues.getString(0);

        setFontFamily(fontFamily);
        styleValues.recycle();
    }

    /**
     * Sets the font family for use
     * @param family The type of styling to use for this TextView.
     */
    public void setFontFamily(String family){
        if((family != null) && (!family.startsWith("sans"))) {
            setTypeface(FontUtils.getFontForFontFamily(getContext(), family));
        }
    }


    @Override
    protected int[] onCreateDrawableState(int extraSpace) {

        final int[] drawableState = super.onCreateDrawableState(extraSpace + 2);
        return drawableState;
    }

    /**
     * Provide information on whether the view is pending.
     * @param info the node info for this view.
     */
    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(SimplifyButton.class.getName());
    }

    /**
     * Sets the appearance of the text. Used to support programmatic font-family
     * changes.
     *
     * @param context Application context
     * @param resid ID for the desired style
     */
    @Override
    public void setTextAppearance(Context context, int resid) {
        super.setTextAppearance(context, resid);
        TypedArray appearance = context.obtainStyledAttributes(resid, new int[]{android.R.attr.fontFamily});
        String familyName = appearance.getString(0);
        setFontFamily(familyName);
        appearance.recycle();
    }
}

