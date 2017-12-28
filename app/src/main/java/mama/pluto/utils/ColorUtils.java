package mama.pluto.utils;

import android.content.Context;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;

public class ColorUtils {
    @ColorInt
    public static int getColor(@NonNull Context context, @ColorRes int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.getColor(color);
        } else {
            //noinspection deprecation
            return context.getResources().getColor(color);
        }
    }
}
