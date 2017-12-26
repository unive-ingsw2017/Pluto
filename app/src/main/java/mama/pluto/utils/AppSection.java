package mama.pluto.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import org.jetbrains.annotations.Nullable;
import android.view.MenuItem;
import android.view.View;

/**
 * Created by MMarco on 05/12/2017.
 */

public abstract class AppSection<V extends View> {
    @Nullable
    private V createdView;


    public void setupItem(Context context, MenuItem menuItem) {
        menuItem.setIcon(getIcon(context));
    }

    public abstract String getTitle(Context context);

    public abstract Drawable getIcon(Context context);

    public V getView(Context context) {
        if (createdView == null) {
            createdView = createView(context);
        }
        return createdView;
    }

    @Nullable
    protected V getCurrentView() {
        return createdView;
    }

    protected abstract V createView(Context context);

    public boolean onBackPressed() {
        return false;
    }
}
