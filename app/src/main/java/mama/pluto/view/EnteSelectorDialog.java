package mama.pluto.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Ente;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.GeoItem;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import mama.pluto.R;
import mama.pluto.dataAbstraction.AnagraficheExtended;
import mama.pluto.dataAbstraction.DataUtils;
import mama.pluto.utils.AbstractSelectorAdapter;
import mama.pluto.utils.Consumer;
import mama.pluto.utils.MetricsUtils;
import mama.pluto.utils.StringUtils;
import mama.pluto.view.selectors.HierarchySelectorView;

public class EnteSelectorDialog extends AlertDialog.Builder {
    private AlertDialog lastCreated;

    public EnteSelectorDialog(@NotNull Context context, @NotNull AnagraficheExtended anagrafiche, @NotNull Consumer<Ente> onEnteSelected) {
        super(context);
        HierarchySelectorView hierarchySelectorView = new HierarchySelectorView(context, anagrafiche);
        hierarchySelectorView.setOnEnteSelected(ente -> {
            lastCreated.dismiss();
            onEnteSelected.consume(ente);
        });
        setView(hierarchySelectorView);
        setCancelable(true);
        setPositiveButton("", null);
        hierarchySelectorView.setOnSelectedGeoItemChanges(geoItem -> {
            if (lastCreated != null) {
                Button positive = lastCreated.getButton(DialogInterface.BUTTON_POSITIVE);
                Button neutral = lastCreated.getButton(DialogInterface.BUTTON_NEUTRAL);
                if (geoItem == null) {
                    positive.setVisibility(View.GONE);
                    neutral.setVisibility(View.VISIBLE);
                } else {
                    positive.setVisibility(View.VISIBLE);
                    neutral.setVisibility(View.GONE);
                    positive.setText(context.getString(R.string.compare_with_x, StringUtils.toNormalCase(geoItem.getNome())));
                    positive.setOnClickListener(view1 -> {
                        onEnteSelected.consume(DataUtils.getEnteOfGeoItem(anagrafiche, geoItem));
                        lastCreated.dismiss();
                    });
                }
            }
        });
        hierarchySelectorView.setHeaderCreator(new AbstractSelectorAdapter.HeaderCreator<TextView>() {
            @Override
            public TextView createView(@NotNull Context context, @NotNull Consumer onEnteSelected) {
                TextView ret = new TextView(context, null, android.R.attr.textAppearanceLarge);
                ret.setGravity(Gravity.CENTER);
                int dp16 = MetricsUtils.dpToPixel(context, 16);
                ret.setPadding(dp16, dp16, dp16, dp16);
                ret.setBackgroundColor(0x10ffffff);
                return ret;
            }

            @Override
            public void bind(@NotNull TextView view, @NotNull AnagraficheExtended anagrafiche, @Nullable Ente ente, @Nullable GeoItem geoItem) {
                if (ente == null) {
                    throw new IllegalStateException();
                }
                if (geoItem != null) {
                    view.setText(StringUtils.toNormalCase(geoItem.getNome()));
                } else {
                    view.setText(StringUtils.toNormalCase(ente.getNome()));
                }
            }
        });
        setNeutralButton(android.R.string.cancel, null);
        setOnKeyListener((dialog, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                if (!hierarchySelectorView.onBackPressed()) {
                    dialog.dismiss(); // dismiss the dialog
                }
            }
            return true;
        });
    }

    @Override
    public AlertDialog create() {
        if (lastCreated != null) {
            throw new IllegalStateException();
        }
        lastCreated = super.create();
        return lastCreated;
    }

    @Override
    public AlertDialog show() {
        AlertDialog show = super.show();
        lastCreated.getButton(DialogInterface.BUTTON_POSITIVE).setVisibility(View.GONE);
        return show;

    }
}
