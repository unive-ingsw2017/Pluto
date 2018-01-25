package mama.pluto.view.selectors;

import android.content.Context;
import android.view.View;

import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.GeoItem;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Regione;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.RipartizioneGeografica;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;

import mama.pluto.dataAbstraction.AnagraficheExtended;
import mama.pluto.utils.AbstractSelectorAdapter;
import mama.pluto.utils.StringUtils;
import mama.pluto.view.SingleLineListItem;
import mama.pluto.view.TutorialView;

/**
 * Created by MMarco on 16/11/2017.
 */
public class RegioneSelectorView extends AbstractGeoItemSelectorView<GeoItem, Regione> {

    public RegioneSelectorView(@NotNull Context context, @NotNull AnagraficheExtended anagrafiche) {
        super(context, anagrafiche, null);
    }

    public void setShowTutorial(boolean showTutorial) {
        adapter.setShowHeader(showTutorial);
    }

    @Override
    protected AbstractSelectorAdapter<?, Regione> createAdapter() {
        ArrayList<Regione> regioni = new ArrayList<>(getAnagrafiche().getRegioni());
        Collections.sort(
                regioni,
                (r1, r2) -> {
                    int ret = compare(r1.getParent(), r2.getParent());
                    if (ret == 0) {
                        ret = r1.getNome().compareToIgnoreCase(r2.getNome());
                    }
                    return ret;
                }
        );
        return new AbstractSelectorAdapter<SingleLineListItem, Regione>(anagrafiche) {

            @Override
            protected View createHeaderView(@NotNull Context context) {
                return new TutorialView(context);
            }

            @Override
            protected void bindHeaderView(@NotNull View mainEnteView, @Nullable GeoItem mainGeoItem) {
            }

            @Override
            protected Regione getItem(int position) {
                return regioni.get(position);
            }


            @Override
            protected SingleLineListItem createView(@NotNull Context context) {
                return new SingleLineListItem(context);
            }

            @Override
            protected void bindView(SingleLineListItem view, Regione regione) {
                view.setText(StringUtils.toNormalCase(regione.getNome()));
            }

            @Override
            public int getItemCount2() {
                return regioni.size();
            }

            @Override
            protected String getDivider(int position) {
                return regioni.get(position).getParent().getNome();
            }
        };
    }

    private int compare(@NotNull RipartizioneGeografica a, @NotNull RipartizioneGeografica b) {
        return -Integer.compare(getNordicità(a), getNordicità(b));
    }

    private int getNordicità(@NotNull RipartizioneGeografica a) {
        String nome = a.getNome().toLowerCase();
        if (nome.contains("insulare")) {
            return 0;
        } else if (nome.contains("meridionale")) {
            return 1;
        } else if (nome.contains("centrale")) {
            return 2;
        } else if (nome.contains("nord") && nome.contains("orientale")) {
            return 3;
        } else if (nome.contains("nord") && nome.contains("occidentale")) {
            return 4;
        } else {
            throw new IllegalStateException();
        }
    }
}
