package mama.pluto.view.selectors;

import android.content.Context;

import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Anagrafiche;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Comune;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Ente;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import mama.pluto.dataAbstraction.DataUtils;
import mama.pluto.utils.AbstractEnteSelectorAdapter;

/**
 * Created by MMarco on 16/11/2017.
 */
public class EnteSelectorView extends AbstractEnteSelectorView<Comune> {

    public EnteSelectorView(@NotNull Context context, @NotNull Anagrafiche anagrafiche, @NotNull Comune comune) {
        super(context, anagrafiche, comune);
    }

    @Override
    protected AbstractEnteSelectorAdapter createAdapter() {
        assert getMainGeoItem() != null;
        final List<Ente> enti = DataUtils.getEntiFromComune(getAnagrafiche(), getMainGeoItem());
        return new AbstractEnteSelectorAdapter() {
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
