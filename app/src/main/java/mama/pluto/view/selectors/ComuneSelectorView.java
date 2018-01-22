package mama.pluto.view.selectors;

import android.content.Context;

import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Anagrafiche;

import org.jetbrains.annotations.NotNull;

import mama.pluto.Ente;
import mama.pluto.utils.AbstractEnteSelectorAdapter;
import mama.pluto.utils.HierarchyLevel;

/**
 * Created by MMarco on 16/11/2017.
 */

public class ComuneSelectorView extends AbstractEnteSelectorView {

    public ComuneSelectorView(@NotNull Context context, @NotNull Anagrafiche anagrafiche, @NotNull Ente provincia) {
        super(context, anagrafiche, provincia);
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
