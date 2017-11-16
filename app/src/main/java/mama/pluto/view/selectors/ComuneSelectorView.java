package mama.pluto.view.selectors;

import android.content.Context;
import android.support.annotation.NonNull;

import mama.pluto.utils.AbstractEnteSelectorAdapter;

/**
 * Created by MMarco on 16/11/2017.
 */

public class ComuneSelectorView extends AbstractEnteSelectorView {

    @NonNull
    private final String provincia;

    public ComuneSelectorView(Context context, @NonNull String provincia) {
        super(context);
        this.provincia = provincia;
    }

    @NonNull
    public String getProvincia() {
        return provincia;
    }

    @Override
    protected AbstractEnteSelectorAdapter createAdapter() {
        return new AbstractEnteSelectorAdapter() {
            @Override
            protected String getEnte(int position) {
                return "Comune " + position + " della provincia " + provincia;
            }

            @Override
            public int getItemCount() {
                return 20;
            }
        };
    }
}
