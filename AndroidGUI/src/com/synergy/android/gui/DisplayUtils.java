package com.synergy.android.gui;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class DisplayUtils {
    private static WindowManager windowManager;
    private static DisplayMetrics displayMetrics;
    
    /**
     * Retrieves {@link WindowManager} from context.
     * 
     * @param context the context
     * @return        the window manager
     */
    public static WindowManager getWindowManager(Context context) {
        if (windowManager == null) {
            windowManager = (WindowManager) context.getSystemService(
                    Context.WINDOW_SERVICE);
        }
        return windowManager;
    }
    
    /**
     * Retrieves {@link DisplayMetrics} from context
     * 
     * @param context the context
     * @return        the display metrics
     */
    public static DisplayMetrics getDisplayMetrics(Context context) {
        if (displayMetrics == null) {
            displayMetrics = new DisplayMetrics();
            getWindowManager(context).getDefaultDisplay().getMetrics(displayMetrics);
        }
        return displayMetrics;
    }
    
    /**
     * Converts density-independent pixel to screen pixel.
     * 
     * @param context the context
     * @param dp      the density-independent pixel
     * @return        the screen pixel
     */
    public static float toPixels(Context context, int dp) {
        return dp * (getDisplayMetrics(context).densityDpi / 160.0f);
    }
}
