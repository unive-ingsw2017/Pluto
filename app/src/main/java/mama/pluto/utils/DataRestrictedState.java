package mama.pluto.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Build;
import android.support.annotation.StringRes;
import android.support.v4.net.ConnectivityManagerCompat;

import org.jetbrains.annotations.NotNull;

import mama.pluto.R;

public enum DataRestrictedState {
    DATA_UNRESTRICTED(0),
    DATA_RESTRICTED(R.string.data_restricted_warning),
    DATA_RESTRICTED_METERED(R.string.data_restricted_metered_warning),
    DATA_RESTRICTED_SAVING_ENABLED(R.string.data_restricted_saving_warning);
    @StringRes
    private final int stringMessageResId;

    DataRestrictedState(int stringMessageResId) {
        this.stringMessageResId = stringMessageResId;
    }

    public String getMessage(@NotNull Context context, int approximateMBDownload) {
        if (stringMessageResId == 0) {
            throw new IllegalStateException();
        } else {
            return context.getString(stringMessageResId, approximateMBDownload);
        }
    }

    public static DataRestrictedState getCurrentState(@NotNull Context context) {
        final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connMgr == null) {
            return DataRestrictedState.DATA_RESTRICTED;
        } else if (ConnectivityManagerCompat.isActiveNetworkMetered(connMgr)) {
            return DataRestrictedState.DATA_RESTRICTED_METERED;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && connMgr.getRestrictBackgroundStatus() != ConnectivityManager.RESTRICT_BACKGROUND_STATUS_DISABLED) {
            return DataRestrictedState.DATA_RESTRICTED_METERED;
        } else {
            return DataRestrictedState.DATA_UNRESTRICTED;
        }
    }
}
