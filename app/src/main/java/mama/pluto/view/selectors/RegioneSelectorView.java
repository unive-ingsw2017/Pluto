package mama.pluto.view.selectors;

import android.content.Context;

import org.jetbrains.annotations.NotNull;

import mama.pluto.Ente;
import mama.pluto.utils.AbstractEnteSelectorAdapter;
import mama.pluto.utils.HierarchyLevel;

/**
 * Created by MMarco on 16/11/2017.
 */
public class RegioneSelectorView extends AbstractEnteSelectorView {

    public RegioneSelectorView(@NotNull Context context) {
        super(context, null);
    }


    @Override
    protected AbstractEnteSelectorAdapter createAdapter() {
        return new AbstractEnteSelectorAdapter() {
            @Override
            protected Ente getEnte(int position) {
                return new Ente(getMainEnte(), HierarchyLevel.REGIONE, "Regione " + position);
            }

            @Override
            public int getEntiCount() {
                return 20;
            }
        };
    }
}
