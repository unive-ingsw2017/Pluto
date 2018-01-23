package mama.pluto;

import android.icu.util.TimeUnit;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.CodiceGestionale;
import com.github.mmauro94.siopeDownloader.datastruct.operazioni.Operazione;
import com.github.mmauro94.siopeDownloader.utils.OnProgressListener;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

import mama.pluto.utils.CircularBuffer;

public class OperazioneIteratorBuffer<CG extends CodiceGestionale, T extends Operazione<CG>> implements Iterable<T> {

    @NotNull
    private final Downloader<CG, T> downloader;
    @NotNull
    private final OnProgressListener progressListener;
    private final LinkedBlockingQueue<T> buffer = new LinkedBlockingQueue<>(10000);

    private float lastDownloadProgress = 0;
    private int lastTotalDownloaded = 0;
    private int totalDownloaded = 0;
    private long lastPublishedProgress = 0;
    private boolean finished = false;

    public interface Downloader<CG extends CodiceGestionale, T extends Operazione<CG>> {
        Iterable<T> download(@NotNull OnProgressListener progressListener) throws IOException;
    }

    public OperazioneIteratorBuffer(@NotNull Downloader<CG, T> downloader, @NotNull OnProgressListener progressListener) {
        this.progressListener = progressListener;
        this.downloader = downloader;
    }

    private void publishProgress() {
        final long now = System.currentTimeMillis();
        if (now - lastPublishedProgress >= 16) {
            if (lastTotalDownloaded == 0) {
                progressListener.onProgress(0);
            } else {
                progressListener.onProgress((lastDownloadProgress / lastTotalDownloaded) * (lastTotalDownloaded - buffer.size()));
            }
            lastPublishedProgress = now;
        }
    }

    public OperazioneIteratorBuffer<CG, T> start() throws IOException {
        final Iterator<T> it = downloader.download(progress -> {
            lastDownloadProgress = progress;
            lastTotalDownloaded = totalDownloaded;
            publishProgress();
        }).iterator();

        new Thread() {
            @Override
            public void run() {
                while (it.hasNext()) {
                    try {
                        /*int suze = buffer.size();
                        if(suze >= 9500) {
                            System.out.println("Almost full: " + suze);
                        }*/
                        buffer.put(it.next());
                        totalDownloaded++;
                    } catch (InterruptedException e) {
                        throw new IllegalStateException(e);
                    }
                }
                finished = true;
            }
        }.start();
        return this;
    }

    @NonNull
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            @Override
            public boolean hasNext() {
                return !finished;
            }

            @Override
            public T next() {
                try {
                    T pop = buffer.take();
                    publishProgress();
                    return pop;
                } catch (InterruptedException e) {
                    throw new IllegalStateException(e);
                }
            }
        };
    }
}
