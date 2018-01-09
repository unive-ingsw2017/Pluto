package mama.pluto;

import android.os.AsyncTask;
import android.os.Bundle;

import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Anagrafiche;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import mama.pluto.database.Database;
import mama.pluto.utils.AppSection;
import mama.pluto.utils.BaseActivity;
import mama.pluto.view.FullscreenLoadingView;

public class EntiActivity extends BaseActivity {

    public final AppSection entiAppSection = new EntiAppSection();
    public final AppSection categorieDiBilancioAppSection = new CategorieDiBilancioAppSection();
    public final AppSection heatMapAppSection = new HeatMapAppSection();

    private boolean datiLoaded = false;//TODO: replace with an actual struture to hold the data

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!datiLoaded) {
            FullscreenLoadingView fullscreenLoadingView = new FullscreenLoadingView(this);
            setContentView(fullscreenLoadingView);
            new AsyncTask<Void, Integer, Exception>() {
                @Override
                protected Exception doInBackground(Void[] objects) {
                    try {
                        Database.getInstance(EntiActivity.this).saveAnagrafiche(Anagrafiche.downloadAnagrafiche());
                        return null;
                    } catch (IOException e) {
                        return e;
                    }
                }

                @Override
                protected void onPostExecute(Exception o) {
                    datiLoaded = o == null;
                    setupContentView();
                }

                @Override
                protected void onProgressUpdate(Integer[] values) {
                    fullscreenLoadingView.setProgress(values[0] / 100f);
                }
            }.execute();
        } else {
            setupContentView();
        }
    }

    @NotNull
    @Override
    protected Set<AppSection> getSections() {
        Set<AppSection> ret = new LinkedHashSet<>();
        ret.add(entiAppSection);
        ret.add(categorieDiBilancioAppSection);
        ret.add(heatMapAppSection);
        return ret;
    }

    @NotNull
    @Override
    protected AppSection getDefaultAppSection() {
        return entiAppSection;
    }
}
