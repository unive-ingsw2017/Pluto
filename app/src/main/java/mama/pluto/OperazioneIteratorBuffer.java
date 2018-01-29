package mama.pluto;

import android.support.annotation.NonNull;
import android.util.Log;

import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.CodiceGestionale;
import com.github.mmauro94.siopeDownloader.datastruct.operazioni.Operazione;
import com.github.mmauro94.siopeDownloader.utils.ClosableIterator;
import com.github.mmauro94.siopeDownloader.utils.OnProgressListener;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class OperazioneIteratorBuffer<CG extends CodiceGestionale, T extends Operazione<CG>> implements Iterable<T> {

    public static final int BUFFER_CAPACITY = 5_000;
    private static final Object SENTINEL = new Object();
    @NotNull
    private final Operazione.Downloader<CG, T, ?> downloader;
    @NotNull
    private final OnProgressListener progressListener;
    private final BlockingQueue<Object> buffer = new ArrayBlockingQueue<>(BUFFER_CAPACITY);

    private File tmpFile;
    private float lastDownloadProgress = 0;
    private int lastTotalDownloaded = 0;
    private int totalDownloaded = 0;
    private Exception exception;


    public OperazioneIteratorBuffer(@NotNull Operazione.Downloader<CG, T, ?> downloader, @NotNull OnProgressListener progressListener) {
        this.progressListener = progressListener;
        this.downloader = downloader;
    }

    private void publishProgress() {
        if (lastTotalDownloaded == 0) {
            progressListener.onProgress(0);
        } else {
            progressListener.onProgress((lastDownloadProgress / lastTotalDownloaded) * (lastTotalDownloaded - buffer.size()));
        }
    }

    public OperazioneIteratorBuffer<CG, T> setTmpFile(File tmpFile) {
        this.tmpFile = tmpFile;
        return this;
    }

    public OperazioneIteratorBuffer<CG, T> start() throws IOException, InterruptedException {
        this.downloader.setOnProgressListener(progress -> {
            lastDownloadProgress = progress;
            lastTotalDownloaded = totalDownloaded;
            publishProgress();
        });
        ClosableIterator<T> it = this.downloader.download(tmpFile);
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    while (it.hasNext()) {
                        buffer.put(it.next());
                        totalDownloaded++;
                    }
                } catch (RuntimeException | InterruptedException e) {
                    exception = e;
                } finally {
                    try {
                        //noinspection unchecked
                        buffer.put(SENTINEL);
                    } catch (InterruptedException e) {
                        throw new IllegalStateException(e);
                    }
                    try {
                        it.close();
                    } catch (IOException ignored) {
                    }
                }
            }
        };
        t.setName("SIOPE Buffer filler");
        t.start();
        return this;
    }

    public boolean terminatedSuccessfully() {
        return exception == null;
    }

    public void throwIfTerminatedUnsuccessfully() throws Exception {
        if (exception != null) {
            throw exception;
        }
    }

    @NonNull
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Object pop = null;
            private boolean takeFirst = true;

            @Override
            public boolean hasNext() {
                if (takeFirst) {
                    try {
                        pop = buffer.take();
                        takeFirst = false;
                    } catch (InterruptedException e) {
                        throw new IllegalStateException(e);
                    }
                }
                return pop != SENTINEL;
            }

            private int cnt = 0;

            @Override
            public T next() {
                if (!(pop instanceof Operazione)) {
                    throw new IllegalStateException();
                }
                T ret = (T) pop;
                try {
                    pop = buffer.take();
                    publishProgress();
                    if (++cnt % 100_000 == 0) {
                        Log.d("SIOPE Downloader", "Inserted " + cnt + " rows");
                    }
                } catch (InterruptedException e) {
                    throw new IllegalStateException(e);
                }
                return ret;
            }
        };
    }
}
