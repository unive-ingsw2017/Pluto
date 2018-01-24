package mama.pluto;

import android.support.annotation.FloatRange;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Progress {


    private final float progress;
    private final String message;

    public Progress(@FloatRange(from=0, to=1) float progress, @Nullable String message) {
        this.progress = progress;
        this.message = message;
    }

    public float getProgress() {
        return progress;
    }

    @Nullable
    public String getMessage() {
        return message;
    }
}
