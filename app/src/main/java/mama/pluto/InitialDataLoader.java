package mama.pluto;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.StringRes;

import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Anagrafiche;
import com.github.mmauro94.siopeDownloader.datastruct.operazioni.Entrata;
import com.github.mmauro94.siopeDownloader.datastruct.operazioni.Operazione;
import com.github.mmauro94.siopeDownloader.datastruct.operazioni.Uscita;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Arrays;

import mama.pluto.dataAbstraction.AnagraficheExtended;
import mama.pluto.dataAbstraction.ComuneStat;
import mama.pluto.database.Database;

public class InitialDataLoader extends AsyncTask<Void, Progress, Exception> {

    @NotNull
    private final Context context;
    private final boolean anagraficheToDownload, operazioniToDownload;
    private final int yearToDownload;

    @Nullable
    private AnagraficheExtended anagrafiche;
    private final float[] progresses = new float[5];

    private static final int[] progressesWeights = new int[5];

    public InitialDataLoader(@NotNull Context context, int yearToDownload) {
        this.context = context.getApplicationContext();
        this.yearToDownload = yearToDownload;
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        anagraficheToDownload = !preferences.getBoolean(SharedPreferencesConsts.ANAGRAFICHE_DOWNLOADED, false);
        final Integer downloadedYear = getDownloadedYear(context);
        operazioniToDownload = downloadedYear == null || downloadedYear != yearToDownload;

        Arrays.fill(progresses, 0);
        Arrays.fill(progressesWeights, 0);

        progressesWeights[0] = 1;
        if (anagraficheToDownload) {
            progressesWeights[1] = 1;
        }
        if (operazioniToDownload) {
            progressesWeights[2] = 100;
            progressesWeights[3] = 100;
        }
        progressesWeights[4] = 2;
    }

    @NotNull
    public AnagraficheExtended getAnagrafiche() {
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

            if (operazioniToDownload) {
                db.truncateOperazioni();
            }
            if (anagraficheToDownload) {
                Anagrafiche anagrafiche = new Anagrafiche.Downloader().setOnProgressListener(p -> progress(0, p, R.string.downloading_anagrafiche)).download();
                this.anagrafiche = db.saveAnagrafiche(anagrafiche, p -> progress(1, p, R.string.saving_anagrafiche));

                preferences.edit().putBoolean(SharedPreferencesConsts.ANAGRAFICHE_DOWNLOADED, true).apply();
            } else {
                anagrafiche = db.loadAnagrafiche(p -> progress(0, p, R.string.loading_anagrafiche));
            }
            if (operazioniToDownload) {
                final File tempFile = new File(context.getCacheDir(), "temp.zip");
                tempFile.deleteOnExit();
                assert anagrafiche != null;
                downloadOperazioni(db, new Entrata.Downloader(yearToDownload, anagrafiche.getAnagrafiche()), tempFile, 2, R.string.downloading_entrate);
                downloadOperazioni(db, new Uscita.Downloader(yearToDownload, anagrafiche.getAnagrafiche()), tempFile, 3, R.string.downloading_uscite);
                preferences.edit().putInt(SharedPreferencesConsts.OPERAZIONI_DOWNLOADED, yearToDownload).apply();
            }
            ComuneStat.ensureLoaded(context, anagrafiche, p -> progress(4, p, R.string.loading_geoitem_stats));
        } catch (Exception e) {
            e.printStackTrace();
            return e;
        }
        return null;
    }

    private void downloadOperazioni(Database db, Operazione.Downloader<?, ?, ?> downloader, File tempFile, int progressIndex, @StringRes int stringMessage) throws Exception {
        OperazioneIteratorBuffer<?, ?> iteratorBuffer = new OperazioneIteratorBuffer<>(downloader, p -> progress(progressIndex, p, stringMessage))
                .setTmpFile(tempFile)
                .start();
        db.saveOperazioni(iteratorBuffer, anagrafiche);
        iteratorBuffer.throwIfTerminatedUnsuccessfully();
    }

    private long lastPublished = 0;

    private void progress(int index, float progress, @StringRes int res) {
        if (lastPublished + 16 < System.currentTimeMillis()) {
            lastPublished = System.currentTimeMillis();
            progresses[index] = progress;
            float sum = 0;
            int weights = 0;
            for (int i = 0; i < progresses.length; i++) {
                sum += progresses[i] * progressesWeights[i];
                weights += progressesWeights[i];
            }
            publishProgress(new Progress(sum / weights, context, res));
        }
    }

    @Nullable
    public static Integer getDownloadedYear(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        int ret = preferences.getInt(SharedPreferencesConsts.OPERAZIONI_DOWNLOADED, -1);
        return ret > 0 ? ret : null;

    }
}
