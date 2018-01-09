package mama.pluto;

import android.os.AsyncTask;
import android.os.Bundle;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

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
            new AsyncTask<Void, Integer, Void>() {
                @Override
                protected Void doInBackground(Void[] objects) {
                    for (int i = 0; i < 10; i++) {
                        publishProgress(i * 10);
                        try {
                            Thread.sleep(400);
                        } catch (InterruptedException ignored) {
                        }
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void o) {
                    datiLoaded = true;
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
