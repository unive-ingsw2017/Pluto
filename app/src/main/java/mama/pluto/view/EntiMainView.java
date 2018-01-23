package mama.pluto.view;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Anagrafiche;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Comune;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.GeoItem;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Provincia;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Regione;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.jetbrains.annotations.NotNull;

import mama.pluto.R;
import mama.pluto.utils.MetricsUtils;
import mama.pluto.view.selectors.HierarchySelectorView;

/**
 * Created by MMarco on 16/11/2017.
 */

public class EntiMainView extends BaseLayoutView {

    @NotNull
    private final Anagrafiche anagrafiche;
    private final MaterialSearchView searchView;
    private final HierarchySelectorView hierarchySelectorView;

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

        hierarchySelectorView = new HierarchySelectorView(getContext(), anagrafiche);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            hierarchySelectorView.setElevation(MetricsUtils.dpToPixel(getContext(), 4f));
        }
        hierarchySelectorView.setOnSelectedGeoItemChanges(geoItem -> recomputeToolbarText());
        hierarchySelectorView.setOnEnteSelected(ente -> Toast.makeText(getContext(), ente.getNome(), Toast.LENGTH_SHORT).show());
        addView(hierarchySelectorView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        recomputeToolbarText();
    }

    private void recomputeToolbarText() {
        final GeoItem selectedGeoItem = hierarchySelectorView.getSelectedGeoItem();
        if (selectedGeoItem == null) {
            toolbar.setTitle(R.string.seleziona_un_regione);
        } else if (selectedGeoItem instanceof Regione) {
            toolbar.setTitle(R.string.seleziona_una_provincia);
        } else if (selectedGeoItem instanceof Provincia) {
            toolbar.setTitle(R.string.seleziona_un_comune);
        } else if (selectedGeoItem instanceof Comune) {
            toolbar.setTitle(R.string.seleziona_un_ente);
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int width = MeasureSpec.getSize(widthMeasureSpec);
        final int listPadding = Math.max(0, (width - MetricsUtils.dpToPixel(getContext(), 400)) / 2);
        MetricsUtils.applyLateralPadding(hierarchySelectorView, listPadding);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public boolean onBackPressed() {
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
            return true;
        } else {
            return hierarchySelectorView.onBackPressed();
        }
    }
}


