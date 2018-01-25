package mama.pluto.view.selectors;

import android.content.Context;

import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Anagrafiche;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Comune;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Ente;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

import mama.pluto.dataAbstraction.AnagraficheImproved;
import mama.pluto.dataAbstraction.DataUtils;
import mama.pluto.utils.AbstractEnteSelectorAdapter;

/**
 * Created by MMarco on 16/11/2017.
 */
public class EnteSelectorView extends AbstractEnteSelectorView<Comune> {

    public EnteSelectorView(@NotNull Context context, @NotNull AnagraficheImproved anagrafiche, @NotNull Comune comune) {
        super(context, anagrafiche, comune);
    }

    @Override
    protected AbstractEnteSelectorAdapter createAdapter() {
        assert getMainGeoItem() != null;
        final List<Ente> enti = DataUtils.getEntiFromComune(getAnagrafiche(), getMainGeoItem(), false);
        Collections.sort(enti, (e1, e2) -> e1.getNome().compareToIgnoreCase(e2.getNome()));
        //TODO: gestire se non ci sono enti
        return new AbstractEnteSelectorAdapter(anagrafiche) {
            @Override
            protected Ente getItem(int position) {
                return enti.get(position);
            }

            @Override
            protected int getItemCount2() {
                return enti.size();
            }
        };
    }
}
