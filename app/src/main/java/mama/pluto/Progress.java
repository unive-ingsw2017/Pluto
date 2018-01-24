package mama.pluto;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.FloatRange;
import android.support.annotation.StringRes;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Progress {

    private final float progress;
    private final String message;

    private final Resources resources;
    @StringRes
    private final int resId;

    public Progress(@FloatRange(from = 0, to = 1) float progress, @Nullable String message) {
        this.progress = progress;
        this.message = message;
        this.resources = null;
        this.resId = 0;
    }

    public Progress(@FloatRange(from = 0, to = 1) float progress, @NotNull Context context, @StringRes int resId) {
        this.progress = progress;
        this.message = null;
        this.resources = context.getResources();
        this.resId = resId;
    }

    public float getProgress() {
        return progress;
    }

    @Nullable
    public String getMessage() {
        if (resources != null) {
            return resources.getString(resId);
        } else {
            return message;
        }
    }
}
