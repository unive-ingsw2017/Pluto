package mama.pluto.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.MenuItem;
import android.view.View;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import mama.pluto.view.EntiMainView;

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

    public V getView(@NotNull BaseActivity baseActivity) {
        if (createdView == null) {
            createdView = createView(baseActivity);
        }
        return createdView;
    }

    @Nullable
    protected V getCurrentView() {
        return createdView;
    }

    protected abstract V createView(@NotNull BaseActivity baseActivity);

    public boolean onBackPressed() {
        return false;
    }
}
