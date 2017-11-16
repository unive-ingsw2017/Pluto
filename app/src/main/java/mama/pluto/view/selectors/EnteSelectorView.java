package mama.pluto.view.selectors;

import android.content.Context;
import android.support.annotation.NonNull;

import mama.pluto.utils.AbstractEnteSelectorAdapter;

/**
 * Created by MMarco on 16/11/2017.
 */

public class EnteSelectorView extends AbstractEnteSelectorView {

    @NonNull
    private final String comune;

    public EnteSelectorView(Context context, @NonNull String comune) {
        super(context);
        this.comune = comune;
    }

    @NonNull
    public String getComune() {
        return comune;
    }

    @Override
    protected AbstractEnteSelectorAdapter createAdapter() {
        return new AbstractEnteSelectorAdapter() {
            @Override
            protected String getEnte(int position) {
                return "Ente " + position + " del comune " + comune;
            }

            @Override
            public int getItemCount() {
                return 2000;
            }
        };
    }
}
