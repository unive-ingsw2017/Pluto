package mama.pluto.view.selectors;

import android.content.Context;

import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Anagrafiche;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.GeoItem;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Regione;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;

import mama.pluto.utils.AbstractEnteSelectorAdapter;

/**
 * Created by MMarco on 16/11/2017.
 */
public class RegioneSelectorView extends AbstractEnteSelectorView<GeoItem> {

    public RegioneSelectorView(@NotNull Context context, @NotNull Anagrafiche anagrafiche) {
        super(context, anagrafiche, null);
    }


    @Override
    protected AbstractEnteSelectorAdapter createAdapter() {
        ArrayList<Regione> regioni = new ArrayList<>(getAnagrafiche().getRegioni());
        Collections.sort(regioni, (r1, r2) -> r1.getNome().compareToIgnoreCase(r2.getNome()));
        return new AbstractEnteSelectorAdapter() {
            @Override
            protected Regione getGeoItem(int position) {
                return regioni.get(position);
            }

            @Override
            public int getEntiCount() {
                return regioni.size();
            }
        };
    }
}
