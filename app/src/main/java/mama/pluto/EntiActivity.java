package mama.pluto;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.transition.Fade;
import android.support.transition.TransitionManager;
import android.view.View;
import android.widget.FrameLayout;

import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Anagrafiche;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import mama.pluto.database.Database;
import mama.pluto.utils.AppSection;
import mama.pluto.utils.BaseActivity;
import mama.pluto.view.FullscreenErrorView;
import mama.pluto.view.FullscreenLoadingView;

public class EntiActivity extends BaseActivity {

    private Anagrafiche anagrafiche = null;
    private FrameLayout mainContainer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadAnagrafiche();
    }

    private void loadAnagrafiche() {
        if (anagrafiche == null) {
            FullscreenLoadingView fullscreenLoadingView = new FullscreenLoadingView(this);
            setContentView(fullscreenLoadingView);
            new AsyncTask<Void, Float, Exception>() {
                @Override
                protected Exception doInBackground(Void[] objects) {
                    try {
                        Database.getInstance(EntiActivity.this).saveAnagrafiche(Anagrafiche.downloadAnagrafiche((progress ->
                            publishProgress(progress)
                        )));
                        return null;
                    } catch (IOException e) {
                        return e;
                    }
                }

                @Override
                protected void onPostExecute(Exception o) {
                    if (o == null) {
                        setupContentView();
                    } else {
                        setupErrorView(o);
                    }
                }

                @Override
                protected void onProgressUpdate(Float[] values) {
                    fullscreenLoadingView.setProgress(values[0]);
                }
            }.execute();
        } else {
            setupContentView();
        }
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
            heatMapAppSection = new HeatMapAppSection();
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
