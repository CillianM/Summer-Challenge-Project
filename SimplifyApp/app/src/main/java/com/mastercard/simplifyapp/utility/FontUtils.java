/**
 * @author &copy;2016 MasterCard. Proprietary. All rights reserved.
 * @version 1.0.0
 */
package com.mastercard.simplifyapp.utility;

import android.content.Context;
import android.graphics.Typeface;

import java.util.HashMap;

/**
 * Provides fonts for different font families
 */
public class FontUtils {

    private static final int FONT_BOLD = 0;
    private static final int FONT_LIGHT = 1;
    private static final int FONT_REGULAR = 2;
    private static final int FONT_THIN = 3;
    private static final int FONT_BOOK = 4;
    private static final int FONT_ITALIC = 5;
    private static final int FONT_MEDIUM = 6;
    private static final HashMap<Integer, Typeface> mFontMap = new HashMap<Integer, Typeface>(){
        {
            put(FONT_BOLD, null);
            put(FONT_LIGHT, null);
            put(FONT_REGULAR, null);
            put(FONT_THIN, null);
            put(FONT_BOOK, null);
            put(FONT_ITALIC, null);
            put(FONT_MEDIUM, null);
        }
    };

    private FontUtils() {
        // Empty constructor
    }

    // Mapping of font family values to fonts
    private static HashMap<String, Integer> mFontFamilies = new HashMap<String, Integer>()
    {
        {
            put("book", FontUtils.FONT_BOOK);
            put("light", FontUtils.FONT_LIGHT);
            put("thin", FontUtils.FONT_THIN);
            put("regular", FontUtils.FONT_REGULAR);
            put("bold", FontUtils.FONT_BOLD);
            put("italic", FontUtils.FONT_ITALIC);
            put("medium", FontUtils.FONT_MEDIUM);
        }
    };

    public static Typeface getFontForFontFamily(Context context, String fontFamily) {
        if(mFontFamilies.containsKey(fontFamily)) {
            return FontUtils.getFont(context, mFontFamilies.get(fontFamily));
        } else {
            return FontUtils.getFont(context, FontUtils.FONT_REGULAR);
        }
    }

    /**
     * Gets the font as specified
     * @param context The context to use when retrieving the font.
     * @param font The font to get.
     * @return The requested font or null if it couldn't be loaded.
     */
    private static Typeface getFont(Context context, int font)
    {
        Typeface typeface = mFontMap.get(font);
        if(typeface == null) {
            typeface = loadFont(context, font);
            mFontMap.put(font, typeface);
        }

        return typeface;
    }


    /**
     * Loads the specified font.
     * @param context The context to use when loading the font.
     * @param font The specified font.
     * @return The typeface associated with the specified or null Regular if none was found.
     */
    private static Typeface  loadFont(Context context, int font){

        Typeface typeface;
        switch (font){

            case FONT_BOLD:
                typeface = Typeface.createFromAsset(context.getAssets(),  "fonts/MarkForMC_Bold.ttf");
                break;
            case FONT_LIGHT:
                typeface = Typeface.createFromAsset(context.getAssets(), "fonts/MarkForMC_Lt.ttf");
                break;
            case FONT_BOOK:
                typeface = Typeface.createFromAsset(context.getAssets(), "fonts/MarkForMC_Book.ttf");
                break;
            case FONT_MEDIUM:
                typeface = Typeface.createFromAsset(context.getAssets(), "fonts/MarkForMC_Med.ttf");
                break;
            case FONT_REGULAR:
            default:
                typeface = Typeface.createFromAsset(context.getAssets(), "fonts/MarkForMC.ttf");
                break;
        }

        return typeface;
    }

}
