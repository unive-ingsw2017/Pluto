package mama.pluto;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;

import org.jetbrains.annotations.NotNull;

import mama.pluto.utils.AppSection;
import mama.pluto.utils.BaseActivity;

/**
 * Created by MMarco on 05/12/2017.
 */

public class CategorieDiBilancioAppSection extends AppSection {

    @Override
    public String getTitle(Context context) {
        return context.getString(R.string.categorie_di_bilancio);
    }

    @Override
    public Drawable getIcon(Context context) {
        return context.getResources().getDrawable(R.drawable.ic_categorie_black_24dp);
    }

    @Override
    protected View createView(@NotNull BaseActivity baseActivity) {
        return new View(baseActivity);
    }
}
