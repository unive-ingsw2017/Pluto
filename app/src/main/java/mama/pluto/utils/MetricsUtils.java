package mama.pluto.utils;

import android.content.Context;
import org.jetbrains.annotations.NotNull;
import android.view.View;

/**
 * Created by MMarco on 16/11/2017.
 */

public class MetricsUtils {
    private MetricsUtils() {
        throw new IllegalStateException();
    }

    public static int pixelToDp(@NotNull Context context, int pixel) {
        return Math.round(pixelToDp(context, (float) pixel));
    }

    public static float pixelToDp(@NotNull Context context, float pixel) {
        return pixel / context.getResources().getDisplayMetrics().density;
    }

    public static int dpToPixel(@NotNull Context context, int dp) {
        return Math.round(dpToPixel(context, (float) dp));
    }

    public static float dpToPixel(@NotNull Context context, float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

    public static void applyLateralPadding(View v, int lateralPadding) {
        v.setPadding(lateralPadding, v.getPaddingTop(), lateralPadding, v.getPaddingBottom());
    }
}
