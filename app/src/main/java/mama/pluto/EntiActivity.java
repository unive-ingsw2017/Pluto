package mama.pluto;

import android.os.Bundle;
import android.support.transition.Fade;
import android.support.transition.TransitionManager;
import android.view.View;
import android.widget.FrameLayout;

import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Comune;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashSet;
import java.util.Set;

import mama.pluto.dataAbstraction.AnagraficheExtended;
import mama.pluto.dataAbstraction.ComuneStat;
import mama.pluto.utils.AppSection;
import mama.pluto.utils.BaseActivity;
import mama.pluto.utils.DataRestrictedState;
import mama.pluto.view.FullscreenErrorView;
import mama.pluto.view.FullscreenInternetUsageWarningView;
import mama.pluto.view.FullscreenLoadingView;
import mama.pluto.view.FullscreenYearSelector;

public class EntiActivity extends BaseActivity {


    private AnagraficheExtended anagrafiche = null;
    private FrameLayout mainContainer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadAnagrafiche();
    }

    private void loadAnagrafiche() {
        if (anagrafiche == null) {
            Integer downloadedYear = InitialDataLoader.getDownloadedYear(this);
            if (downloadedYear == null) {
                setContentView(new FullscreenYearSelector(this, year -> {
                    DataRestrictedState currentState = DataRestrictedState.getCurrentState(this);
                    if (currentState != DataRestrictedState.DATA_UNRESTRICTED) {
                        setContentView(new FullscreenInternetUsageWarningView(this, currentState, 100, () -> startLoading(year)));
                    } else {
                        startLoading(year);
                    }

                }));
            } else {
                startLoading(downloadedYear);
            }
        } else {
            setupContentView();
        }
    }

    private void startLoading(int year) {
        FullscreenLoadingView fullscreenLoadingView = new FullscreenLoadingView(this);
        setContentView(fullscreenLoadingView);
        new InitialDataLoader(this, year) {

            @Override
            protected void onPostExecute(Exception e) {
                if (e == null) {
                    anagrafiche = getAnagrafiche();
                    setupContentView();
                } else {
                    setupErrorView(e);
                }
            }

            @Override
            protected void onProgressUpdate(Progress[] values) {
                fullscreenLoadingView.setProgress(values[0].getProgress());
                fullscreenLoadingView.setMessage(values[0].getMessage());
            }


        }.execute();
    }


    @Override
    public void setContentView(View view) {
        if (mainContainer == null) {
            mainContainer = new FrameLayout(this);
            super.setContentView(mainContainer);
        }
        TransitionManager.beginDelayedTransition(mainContainer, new Fade());
        mainContainer.removeAllViews();
        mainContainer.addView(view, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
    }

    public AppSection entiAppSection;
    public AppSection categorieDiBilancioAppSection;
    public AppSection heatMapAppSection;

    private void ensureAppSections() {
        if (anagrafiche == null) {
            throw new IllegalStateException();
        }
        if (entiAppSection == null) {
            entiAppSection = new EntiAppSection(anagrafiche);
            categorieDiBilancioAppSection = new CategorieDiBilancioAppSection();
            heatMapAppSection = new HeatMapAppSection(anagrafiche);
        }
    }

    @NotNull
    @Override
    protected Set<AppSection> getSections() {
        ensureAppSections();
        Set<AppSection> ret = new LinkedHashSet<>();
        ret.add(entiAppSection);
        ret.add(categorieDiBilancioAppSection);
        ret.add(heatMapAppSection);
        return ret;
    }

    @NotNull
    @Override
    protected AppSection getDefaultAppSection() {
        ensureAppSections();
        return entiAppSection;
    }

    public void setupErrorView(@NotNull Exception error) {
        FullscreenErrorView errorView = new FullscreenErrorView(this);
        errorView.setErrorMessage(error.getLocalizedMessage());
        errorView.setOnRetryListener(this::loadAnagrafiche);
        setContentView(errorView);
    }
}
