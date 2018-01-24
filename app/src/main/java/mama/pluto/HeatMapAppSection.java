package mama.pluto;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.View;

import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Anagrafiche;

import org.jetbrains.annotations.NotNull;

import mama.pluto.utils.AppSection;
import mama.pluto.utils.BaseActivity;
import mama.pluto.view.HeatMapMainView;

public class HeatMapAppSection extends AppSection {
    @NotNull
    private final Anagrafiche anagrafiche;

    public HeatMapAppSection(@NonNull Anagrafiche anagrafiche) {
        this.anagrafiche = anagrafiche;
    }

    @Override
    public String getTitle(@NotNull Context context) {
        return context.getString(R.string.heat_map);
    }

    @Override
    public Drawable getIcon(@NotNull Context context) {
        return context.getResources().getDrawable(R.drawable.ic_heatmap_black_24dp);
    }


    @Override
    protected View createView(@NotNull BaseActivity baseActivity) {
        HeatMapMainView heatMapMainView = new HeatMapMainView(baseActivity, anagrafiche);
        baseActivity.setupToolbar(heatMapMainView.getToolbar());
        return heatMapMainView;
    }
}
