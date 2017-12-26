package mama.pluto;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;

import mama.pluto.utils.AppSection;
import mama.pluto.view.EntiMainView;

/**
 * Created by MMarco on 05/12/2017.
 */

public class EntiAppSection extends AppSection<EntiMainView> {

    @Override
    public String getTitle(Context context) {
        return context.getString(R.string.enti);
    }

    @Override
    public Drawable getIcon(Context context) {
        return context.getResources().getDrawable(R.drawable.ic_enti_black_24dp);
    }

    @Override
    public EntiMainView createView(Context context) {
        return new EntiMainView(context);
    }

    @Override
    public boolean onBackPressed() {
        final EntiMainView currentView = getCurrentView();
        return currentView != null && currentView.onBackPressed();
    }
}
