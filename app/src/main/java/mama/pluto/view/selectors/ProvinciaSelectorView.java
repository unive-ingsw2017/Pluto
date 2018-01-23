package mama.pluto.view.selectors;

import android.content.Context;

import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Anagrafiche;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Provincia;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Regione;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;

import mama.pluto.utils.AbstractSelectorAdapter;
import mama.pluto.utils.StringUtils;
import mama.pluto.view.SingleLineListItem;

/**
 * Created by MMarco on 16/11/2017.
 */
public class ProvinciaSelectorView extends AbstractGeoItemSelectorView<Regione, Provincia> {

    public ProvinciaSelectorView(@NotNull Context context, @NotNull Anagrafiche anagrafiche, @NotNull Regione regione) {
        super(context, anagrafiche, regione);
    }

    @Override
    protected AbstractSelectorAdapter<?, Provincia> createAdapter() {
        assert getMainGeoItem() != null;
        ArrayList<Provincia> province = new ArrayList<>(getMainGeoItem().getProvincie());
        Collections.sort(province, (p1, p2) -> p1.getNome().compareToIgnoreCase(p2.getNome()));

        return new AbstractSelectorAdapter<SingleLineListItem, Provincia>() {
            @Override
            protected Provincia getItem(int position) {
                return province.get(position);
            }

            @Override
            protected SingleLineListItem createView(@NotNull Context context) {
                return new SingleLineListItem(context);
            }

            @Override
            protected void bindView(SingleLineListItem view, Provincia provincia) {
                view.setText(StringUtils.toNormalCase(provincia.getNome()));
            }

            @Override
            public int getItemCount2() {
                return province.size();
            }
        };
    }
}
