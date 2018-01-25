package mama.pluto;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Anagrafiche;

import org.jetbrains.annotations.NotNull;

import mama.pluto.dataAbstraction.AnagraficheImproved;
import mama.pluto.utils.AppSection;
import mama.pluto.utils.BaseActivity;
import mama.pluto.view.EntiMainView;

/**
 * Created by MMarco on 05/12/2017.
 */

public class EntiAppSection extends AppSection<EntiMainView> {
    @NotNull
    private final AnagraficheImproved anagrafiche;

    public EntiAppSection(@NonNull AnagraficheImproved anagrafiche) {
        this.anagrafiche = anagrafiche;
    }

    @Override
    public String getTitle(Context context) {
        return context.getString(R.string.enti);
    }

    @Override
    public Drawable getIcon(Context context) {
        return context.getResources().getDrawable(R.drawable.ic_enti_black_24dp);
    }

    @Override
    protected EntiMainView createView(@NotNull BaseActivity baseActivity) {
        EntiMainView ret = new EntiMainView(baseActivity, anagrafiche);
        baseActivity.setupToolbar(ret.getToolbar());
        return ret;
    }

    @Override
    public boolean onBackPressed() {
        final EntiMainView currentView = getCurrentView();
        return currentView != null && currentView.onBackPressed();
    }
}
