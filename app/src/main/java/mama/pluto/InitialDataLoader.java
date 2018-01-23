package mama.pluto;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.StringRes;

import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Anagrafiche;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.CodiceGestionale;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.CodiceGestionaleEntrate;
import com.github.mmauro94.siopeDownloader.datastruct.operazioni.Entrata;
import com.github.mmauro94.siopeDownloader.datastruct.operazioni.Operazione;
import com.github.mmauro94.siopeDownloader.datastruct.operazioni.Uscita;
import com.github.mmauro94.siopeDownloader.utils.OnProgressListener;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Arrays;

import mama.pluto.database.Database;

public class InitialDataLoader extends AsyncTask<Void, Progress, Exception> {

    @NotNull
    private final Context context;
    private final boolean anagraficheToDownload, operazioniToDownload;

    @Nullable
    private Anagrafiche anagrafiche;
    private final float[] progresses = new float[4];

    private static final int[] progressesWeights = new int[4];

    public InitialDataLoader(@NotNull Context context) {
        this.context = context.getApplicationContext();
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        anagraficheToDownload = !preferences.getBoolean(SharedPreferencesConsts.ANAGRAFICHE_DOWNLOADED, false);
        operazioniToDownload = !preferences.getBoolean(SharedPreferencesConsts.OPERAZIONI_DOWNLOADED, false);

        Arrays.fill(progresses, 0);
        Arrays.fill(progressesWeights, 0);

        progressesWeights[0] = 1;
        if (anagraficheToDownload) {
            progressesWeights[1] = 1;
        }
        if (operazioniToDownload) {
            progressesWeights[2] = 100;
            //progressesWeights[3] = 100;
        }
    }

    @NotNull
    public Anagrafiche getAnagrafiche() {
        if (anagrafiche == null) {
            throw new IllegalStateException("Anagrafiche not loaded yet");
        }
        return anagrafiche;
    }

    @Override
    protected Exception doInBackground(Void[] objects) {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        final Database db = Database.getInstance(context);
        try {

            if (anagraficheToDownload) {
                anagrafiche = Anagrafiche.downloadAnagrafiche(p -> progress(0, p, R.string.downloading_anagrafiche));
                db.saveAnagrafiche(anagrafiche, p -> progress(1, p, R.string.saving_anagrafiche));

                preferences.edit().putBoolean(SharedPreferencesConsts.ANAGRAFICHE_DOWNLOADED, true).apply();
            } else {
                anagrafiche = db.loadAnagrafiche(p -> progress(0, p, R.string.loading_anagrafiche));
            }
            if (operazioniToDownload) {
                db.saveOperazioni(
                        new OperazioneIteratorBuffer<>(
                                progressListener -> Entrata.downloadEntrate(2017, anagrafiche, progressListener),
                                p -> progress(2, p,R.string.downloading_entrate)
                        ).start()
                );
                //db.saveOperazioni(Entrata.downloadEntrate(2017, anagrafiche, p -> progress(2, p)));

                /*db.saveOperazioni(
                        Uscita.downloadUscite(2017, anagrafiche, p -> progress(3, p))
                );*/
            }
        } catch (Exception e) {
            return e;
        }

        return null;
    }

    private void progress(int index, float progress, @StringRes int res) {
        progresses[index] = progress;
        float sum = 0;
        int weights = 0;
        for (int i = 0; i < progresses.length; i++) {
            sum += progresses[i] * progressesWeights[i];
            weights += progressesWeights[i];
        }
        publishProgress(new Progress(sum / weights, context.getString(res)));
    }
}
