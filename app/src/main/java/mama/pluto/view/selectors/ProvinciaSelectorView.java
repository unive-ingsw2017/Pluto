package mama.pluto.view.selectors;

import android.content.Context;

import org.jetbrains.annotations.NotNull;

import mama.pluto.Ente;
import mama.pluto.utils.AbstractEnteSelectorAdapter;
import mama.pluto.utils.HierarchyLevel;

/**
 * Created by MMarco on 16/11/2017.
 */
public class ProvinciaSelectorView extends AbstractEnteSelectorView {

    public ProvinciaSelectorView(@NotNull Context context, @NotNull Ente regione) {
        super(context, regione);
    }

    @Override
    protected AbstractEnteSelectorAdapter createAdapter() {
        return new AbstractEnteSelectorAdapter() {
            @Override
            protected Ente getEnte(int position) {
                return new Ente(getMainEnte(), HierarchyLevel.PROVINCIA, "Provincia " + position + " di " + getMainEnte().getName());
            }

            @Override
            public int getEntiCount() {
                return 10;
            }
        };
    }
}
