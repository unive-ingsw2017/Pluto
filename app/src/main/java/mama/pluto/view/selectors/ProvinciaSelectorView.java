package mama.pluto.view.selectors;

import android.content.Context;
import android.support.annotation.NonNull;

import mama.pluto.utils.AbstractEnteSelectorAdapter;

/**
 * Created by MMarco on 16/11/2017.
 */

public class ProvinciaSelectorView extends AbstractEnteSelectorView {

    @NonNull
    private final String regione;

    public ProvinciaSelectorView(Context context, @NonNull String regione) {
        super(context);
        this.regione = regione;
    }

    @NonNull
    public String getRegione() {
        return regione;
    }

    @Override
    protected AbstractEnteSelectorAdapter createAdapter() {
        return new AbstractEnteSelectorAdapter() {
            @Override
            protected String getEnte(int position) {
                return "Provincia " + position + " di " + regione;
            }

            @Override
            public int getItemCount() {
                return 10;
            }
        };
    }
}
