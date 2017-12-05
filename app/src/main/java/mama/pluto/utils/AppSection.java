package mama.pluto.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.MenuItem;
import android.view.View;

/**
 * Created by MMarco on 05/12/2017.
 */

public abstract class AppSection {
    private View createdView;


    public void setupItem(Context context, MenuItem menuItem) {
        menuItem.setIcon(getIcon(context));
    }

    public abstract String getTitle(Context context);

    public abstract Drawable getIcon(Context context);

    public View getView(Context context) {
        if (createdView == null) {
            createdView = createView(context);
        }
        return createdView;
    }

    protected abstract View createView(Context context);

}
