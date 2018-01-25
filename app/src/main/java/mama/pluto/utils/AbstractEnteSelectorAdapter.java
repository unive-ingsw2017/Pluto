package mama.pluto.utils;

import android.content.Context;

import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Ente;

import org.jetbrains.annotations.NotNull;

import mama.pluto.dataAbstraction.AnagraficheExtended;
import mama.pluto.view.DoubleLineListItem;

/**
 * Created by MMarco on 16/11/2017.
 */
public abstract class AbstractEnteSelectorAdapter extends AbstractSelectorAdapter<DoubleLineListItem, Ente> {

    public AbstractEnteSelectorAdapter(@NotNull AnagraficheExtended anagrafiche) {
        super(anagrafiche);
    }

    @Override
    protected DoubleLineListItem createView(@NotNull Context context) {
        return new DoubleLineListItem(context);
    }

    @Override
    protected void bindView(DoubleLineListItem view, Ente ente) {
        view.setText(
                StringUtils.toNormalCase(ente.getNome()),
                StringUtils.toNormalCase(ente.getSottocomparto().getNome()) + " (" + StringUtils.toNormalCase(ente.getSottocomparto().getComparto().getNome()) + ")"
        );
    }

}
