package mama.pluto.view.selectors;

import android.content.Context;

import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Anagrafiche;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Comune;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Ente;

import org.jetbrains.annotations.NotNull;

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
        return new AbstractEnteSelectorAdapter() {
            @Override
            protected Ente getItem(int position) {
                throw new UnsupportedOperationException();
            }

            @Override
            protected int getItemCount2() {
                return 0;
            }
        };
    }
}
