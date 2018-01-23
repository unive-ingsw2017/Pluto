package mama.pluto.view.selectors;

import android.content.Context;

import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Anagrafiche;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Provincia;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Regione;

import org.jetbrains.annotations.NotNull;

import mama.pluto.utils.AbstractEnteSelectorAdapter;

/**
 * Created by MMarco on 16/11/2017.
 */
public class ProvinciaSelectorView extends AbstractEnteSelectorView<Regione> {

    public ProvinciaSelectorView(@NotNull Context context, @NotNull Anagrafiche anagrafiche, @NotNull Regione regione) {
        super(context, anagrafiche, regione);
    }

    @Override
    protected AbstractEnteSelectorAdapter createAdapter() {
        return new AbstractEnteSelectorAdapter() {
            @Override
            protected Provincia getGeoItem(int position) {
                assert getMainGeoItem() != null;
                return getMainGeoItem().getProvincie().get(position);
            }

            @Override
            public int getEntiCount() {
                assert getMainGeoItem() != null;
                return getMainGeoItem().getProvincie().size();
            }
        };
    }
}
