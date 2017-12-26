package mama.pluto.view.selectors;

import android.content.Context;

import org.jetbrains.annotations.NotNull;

import mama.pluto.Ente;
import mama.pluto.utils.AbstractEnteSelectorAdapter;
import mama.pluto.utils.HierarchyLevel;

/**
 * Created by MMarco on 16/11/2017.
 */

public class ComuneSelectorView extends AbstractEnteSelectorView {

    public ComuneSelectorView(@NotNull Context context, @NotNull Ente provincia) {
        super(context, provincia);
    }

    @Override
    protected AbstractEnteSelectorAdapter createAdapter() {
        return new AbstractEnteSelectorAdapter() {
            @Override
            protected Ente getEnte(int position) {
                return new Ente(getMainEnte(), HierarchyLevel.COMUNE, "Comune " + position + " della provincia " + getMainEnte().getName());
            }

            @Override
            protected int getEntiCount() {
                return 20;
            }
        };
    }
}
