package mama.pluto.view.selectors;

import android.content.Context;

import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Anagrafiche;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Comune;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Provincia;

import org.jetbrains.annotations.NotNull;

import mama.pluto.utils.AbstractEnteSelectorAdapter;

/**
 * Created by MMarco on 16/11/2017.
 */
public class ComuneSelectorView extends AbstractEnteSelectorView<Provincia> {

    public ComuneSelectorView(@NotNull Context context, @NotNull Anagrafiche anagrafiche, @NotNull Provincia provincia) {
        super(context, anagrafiche, provincia);
    }

    @Override
    protected AbstractEnteSelectorAdapter createAdapter() {
        return new AbstractEnteSelectorAdapter() {
            @Override
            protected Comune getGeoItem(int position) {
                assert getMainGeoItem() != null;
                return getMainGeoItem().getComuni().get(position);
            }

            @Override
            protected int getEntiCount() {
                assert getMainGeoItem() != null;
                return getMainGeoItem().getComuni().size();
            }
        };
    }
}
