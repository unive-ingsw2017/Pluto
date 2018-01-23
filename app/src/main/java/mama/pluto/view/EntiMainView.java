package mama.pluto.view;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Anagrafiche;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.jetbrains.annotations.NotNull;

import mama.pluto.R;
import mama.pluto.utils.MetricsUtils;
import mama.pluto.view.selectors.HierarcySelectorView;

/**
 * Created by MMarco on 16/11/2017.
 */

public class EntiMainView extends BaseLayoutView {

    @NotNull
    private final Anagrafiche anagrafiche;
    private final MaterialSearchView searchView;
    private final HierarcySelectorView hierarcySelectorView;

    public EntiMainView(@NotNull Context context, @NotNull Anagrafiche anagrafiche) {
        super(context);
        this.anagrafiche = anagrafiche;
        toolbar.inflateMenu(R.menu.enti_main_menu);

        searchView = new MaterialSearchView(getContext());
        searchView.setHint(context.getString(R.string.cerca));
        toolbarWrapper.addView(searchView, Toolbar.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.WRAP_CONTENT);
        searchView.setMenuItem(toolbar.getMenu().findItem(R.id.app_bar_search));
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(context, query + "!!!", Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        hierarcySelectorView = new HierarcySelectorView(getContext(), anagrafiche);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            hierarcySelectorView.setElevation(MetricsUtils.dpToPixel(getContext(), 4f));
        }
        hierarcySelectorView.setOnHierarcyLevelSelector(hierarcyLevel -> recomputeToolbarText());
        addView(hierarcySelectorView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        recomputeToolbarText();
    }

    private void recomputeToolbarText() {
        switch (hierarcySelectorView.getHierarchyLevel()) {
            case REGIONE:
                toolbar.setTitle(R.string.seleziona_un_regione);
                break;
            case PROVINCIA:
                toolbar.setTitle(R.string.seleziona_una_provincia);
                break;
            case COMUNE:
                toolbar.setTitle(R.string.seleziona_un_comune);
                break;
            case ENTE:
                toolbar.setTitle(R.string.seleziona_un_ente);
                break;
            default:
                throw new IllegalStateException();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int width = MeasureSpec.getSize(widthMeasureSpec);
        final int listPadding = Math.max(0, (width - MetricsUtils.dpToPixel(getContext(), 400)) / 2);
        MetricsUtils.applyLateralPadding(hierarcySelectorView, listPadding);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public boolean onBackPressed() {
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
            return true;
        } else {
            return hierarcySelectorView.onBackPressed();
        }
    }
}


