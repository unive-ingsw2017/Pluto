package mama.pluto.view;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Comune;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Ente;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.GeoItem;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Provincia;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Regione;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import mama.pluto.R;
import mama.pluto.dataAbstraction.AnagraficheExtended;
import mama.pluto.utils.EntiSearchAdapter;
import mama.pluto.utils.MetricsUtils;
import mama.pluto.utils.StringUtils;
import mama.pluto.view.selectors.HierarchySelectorView;

/**
 * Created by MMarco on 16/11/2017.
 */

public class EntiMainView extends BaseLayoutView {

    @NotNull
    private final AnagraficheExtended anagrafiche;
    private final MaterialSearchView searchView;
    private final FrameLayout content;
    private final HierarchySelectorView hierarchySelectorView;
    private final RecyclerView searchResults;
    private final EntiSearchAdapter searchAdapter;
    private final EnteView enteView;
    private final EnteComparatorView enteComparatorView;
    private final MenuItem searchMenuItem;
    private Ente selectedEnte;
    private Ente comparingEnte;

    public EntiMainView(@NotNull Context context, @NotNull AnagraficheExtended anagrafiche) {
        super(context);
        this.anagrafiche = anagrafiche;
        toolbar.inflateMenu(R.menu.enti_main_menu);

        searchView = new MaterialSearchView(getContext());
        searchView.setHint(context.getString(R.string.cerca));
        toolbarWrapper.addView(searchView, Toolbar.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.WRAP_CONTENT);
        searchMenuItem = toolbar.getMenu().findItem(R.id.app_bar_search);
        searchView.setMenuItem(searchMenuItem);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchAdapter.setSearchQuery(query.trim());
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchAdapter.setSearchQuery(newText.trim());
                return false;
            }
        });
        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                setContent(searchResults);
            }

            @Override
            public void onSearchViewClosed() {
                setContent(hierarchySelectorView);
                searchAdapter.setSearchQuery(null);
            }
        });
        content = new FrameLayout(context);
        addView(content, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);


        hierarchySelectorView = new HierarchySelectorView(getContext(), anagrafiche);
        hierarchySelectorView.setGlobalHeaderCreator(new HierarchySelectorView.TutorialHeaderCreator());
        hierarchySelectorView.setHeaderCreator(new HierarchySelectorView.SummaryHeaderCreator());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            hierarchySelectorView.setElevation(MetricsUtils.dpToPixel(getContext(), 4f));
        }
        hierarchySelectorView.setOnSelectedGeoItemChanges(geoItem -> recomputeToolbarText());
        hierarchySelectorView.setOnEnteSelected(this::setSelectedEnte);
        content.addView(hierarchySelectorView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);


        searchResults = new RecyclerView(context);
        searchResults.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        content.addView(searchResults, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        searchAdapter = new EntiSearchAdapter(anagrafiche);
        searchAdapter.setOnEnteSelected(this::setSelectedEnte);
        searchResults.setAdapter(searchAdapter);

        enteView = new EnteView(context, anagrafiche);
        enteView.setOnEnteSelectedForComparison(this::setComparingEnte);
        content.addView(enteView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        enteComparatorView = new EnteComparatorView(context, anagrafiche);
        content.addView(enteComparatorView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        recomputeContent();
        recomputeToolbarText();
    }

    private void setSelectedEnte(@Nullable Ente ente) {
        selectedEnte = ente;
        if (ente != null) {
            enteView.setEnte(ente, null);
        } else {
            comparingEnte = null;
        }
        recomputeContent();
        recomputeToolbarText();
    }

    public void setComparingEnte(Ente comparingEnte) {
        this.comparingEnte = comparingEnte;
        if (comparingEnte != null && selectedEnte != null) {
            enteComparatorView.setEnti(selectedEnte, comparingEnte);
        } else {
            setSelectedEnte(selectedEnte);
        }
        recomputeContent();
        recomputeToolbarText();
    }

    private void setContent(@NotNull View v) {
        for (int i = 0; i < content.getChildCount(); i++) {
            View view = content.getChildAt(i);
            view.setVisibility(view == v ? View.VISIBLE : View.GONE);
        }
    }

    private void recomputeContent() {
        if (selectedEnte != null && comparingEnte != null) {
            setContent(enteComparatorView);
        } else if (selectedEnte != null) {
            setContent(enteView);
        } else {
            setContent(hierarchySelectorView);
        }
    }

    private void recomputeToolbarText() {
        if (selectedEnte != null && comparingEnte != null) {
            searchMenuItem.setVisible(false);
            toolbar.setTitle(R.string.comparison);
        } else if (selectedEnte != null) {
            searchMenuItem.setVisible(false);
            toolbar.setTitle(StringUtils.toNormalCase(selectedEnte.getNome()));
        } else {
            searchMenuItem.setVisible(true);
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
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int width = MeasureSpec.getSize(widthMeasureSpec);
        final int listPadding = Math.max(0, (width - MetricsUtils.dpToPixel(getContext(), 400)) / 2);
        MetricsUtils.applyLateralPadding(hierarchySelectorView, listPadding);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public boolean onBackPressed() {
        if (comparingEnte != null) {
            setComparingEnte(null);
            return true;
        } else if (selectedEnte != null) {
            setSelectedEnte(null);
            return true;
        } else if (searchView.isSearchOpen()) {
            searchView.closeSearch();
            return true;
        } else {
            return hierarchySelectorView.onBackPressed();
        }
    }
}


