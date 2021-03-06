package mama.pluto.view.selectors;

import android.content.Context;

import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Comune;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Ente;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Provincia;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mama.pluto.dataAbstraction.AnagraficheExtended;
import mama.pluto.dataAbstraction.DataUtils;
import mama.pluto.utils.AbstractSelectorAdapter;
import mama.pluto.utils.StringUtils;
import mama.pluto.view.SingleLineListItem;

/**
 * Created by MMarco on 16/11/2017.
 */
public class ComuneSelectorView extends AbstractGeoItemSelectorView<Provincia, Comune> {

    public ComuneSelectorView(@NotNull Context context, @NotNull AnagraficheExtended anagrafiche, @NotNull Provincia provincia) {
        super(context, provincia, anagrafiche);
    }

    @Override
    protected AbstractSelectorAdapter<?, Comune> createAdapter() {
        assert getMainGeoItem() != null;
        ArrayList<Comune> comuni = new ArrayList<>(getMainGeoItem().getComuni());
        Collections.sort(comuni, (p1, p2) -> p1.getNome().compareToIgnoreCase(p2.getNome()));

        return new AbstractSelectorAdapter<SingleLineListItem, Comune>(anagrafiche) {
            @Override
            protected Comune getItem(int position) {
                return comuni.get(position);
            }

            @Override
            protected void bindView(SingleLineListItem view, Comune comune) {
                view.setText(StringUtils.toNormalCase(comune.getNome()));
            }

            @Override
            protected SingleLineListItem createView(@NotNull Context context) {
                return new SingleLineListItem(context);
            }

            @Override
            protected int getItemCount2() {
                return comuni.size();
            }

            @Override
            protected void onItemSelected(Comune comune) {
                final List<Ente> enti = DataUtils.getEntiFromComune(getAnagrafiche(), comune, false);
                if (enti.isEmpty()) {
                    super.onEnteSelected(DataUtils.getEnteOfComune(getAnagrafiche(), comune));
                } else {
                    super.onItemSelected(comune);
                }
            }
        };
    }
}
