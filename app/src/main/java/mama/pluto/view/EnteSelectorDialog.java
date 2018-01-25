package mama.pluto.view;

import android.app.AlertDialog;
import android.content.Context;
import android.view.KeyEvent;

import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Ente;

import org.jetbrains.annotations.NotNull;

import mama.pluto.dataAbstraction.AnagraficheExtended;
import mama.pluto.utils.Consumer;
import mama.pluto.view.selectors.HierarchySelectorView;

public class EnteSelectorDialog extends AlertDialog.Builder {

    public EnteSelectorDialog(@NotNull Context context, @NotNull AnagraficheExtended anagrafiche, @NotNull Consumer<Ente> onEnteSelected) {
        super(context);
        HierarchySelectorView hierarchySelectorView = new HierarchySelectorView(context, anagrafiche);
        hierarchySelectorView.setOnEnteSelected(onEnteSelected);
        setView(hierarchySelectorView);
        setCancelable(true);
        setOnKeyListener((dialog, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                if (!hierarchySelectorView.onBackPressed()) {
                    dialog.dismiss(); // dismiss the dialog
                }
            }
            return true;
        });
    }
}
