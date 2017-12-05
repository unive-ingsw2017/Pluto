package mama.pluto.utils;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by MMarco on 05/12/2017.
 */

public class ViewUtils {
    private ViewUtils() {
        throw new IllegalStateException();
    }

    private static final AtomicInteger VIEW_ID_TRACKER = new AtomicInteger(1);

    public static int generateViewId() {
        while (true) {
            final int result = VIEW_ID_TRACKER.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) {
                newValue = 1; // Roll over to 1, not 0.
            }
            if (VIEW_ID_TRACKER.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }

}
