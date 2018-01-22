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
public class EnteSelectorView extends AbstractEnteSelectorView {

    public EnteSelectorView(@NotNull Context context, @NotNull Anagrafiche anagrafiche, @NotNull Ente comune) {
        super(context, anagrafiche, comune);
    }


    @Override
    protected AbstractEnteSelectorAdapter createAdapter() {
        return new AbstractEnteSelectorAdapter() {
            @Override
            protected Ente getEnte(int position) {
                return new Ente(getMainEnte(), HierarchyLevel.ENTE, "Ente " + position + " del comune " + getMainEnte().getName());
            }

            @Override
            public int getEntiCount() {
                return 2000;
            }
        };
    }
}
