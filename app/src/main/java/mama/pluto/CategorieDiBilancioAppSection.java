package mama.pluto;

import android.content.Context;
import android.graphics.drawable.Drawable;

import org.jetbrains.annotations.NotNull;

import mama.pluto.dataAbstraction.AnagraficheExtended;
import mama.pluto.utils.AppSection;
import mama.pluto.utils.BaseActivity;
import mama.pluto.view.CategoriesMainView;

/**
 * Created by MMarco on 05/12/2017.
 */

public class CategorieDiBilancioAppSection extends AppSection<CategoriesMainView> {
    @NotNull
    private final AnagraficheExtended anagrafiche;

    public CategorieDiBilancioAppSection(AnagraficheExtended anagrafiche) {
        this.anagrafiche = anagrafiche;
    }

    @Override
    public String getTitle(Context context) {
        return context.getString(R.string.categorie_di_bilancio);
    }

    @Override
    public Drawable getIcon(Context context) {
        return context.getResources().getDrawable(R.drawable.ic_categorie_black_24dp);
    }

    @Override
    protected CategoriesMainView createView(@NotNull BaseActivity baseActivity) {
        CategoriesMainView ret = new CategoriesMainView(baseActivity, anagrafiche);
        baseActivity.setupToolbar(ret.getToolbar());
        return ret;
    }

    @Override
    public boolean onBackPressed() {
        return getCurrentView() != null && getCurrentView().onBackPressed();
    }
}
