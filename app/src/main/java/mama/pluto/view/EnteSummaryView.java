package mama.pluto.view;

import android.content.Context;
import android.support.annotation.StringRes;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Ente;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.GeoItem;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.RipartizioneGeografica;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import mama.pluto.R;
import mama.pluto.dataAbstraction.DataUtils;
import mama.pluto.utils.Consumer;
import mama.pluto.utils.Function;
import mama.pluto.utils.MetricsUtils;
import mama.pluto.utils.StringUtils;

public class EnteSummaryView extends LinearLayout {

    private final TextView nameView;
    private final LinearLayout hierarcyView;
    private final TextView codiceView;
    private final TextView codiceFiscaleView;
    private final TextView comparto;
    private final TextView sottoComparto;
    private Ente ente;
    private GeoItem geoItem;

    public EnteSummaryView(Context context) {
        super(context);
        setOrientation(VERTICAL);

        int dp8 = MetricsUtils.dpToPixel(context, 8);
        int dp16 = MetricsUtils.dpToPixel(context, 16);
        setPadding(dp16, dp8, dp16, dp16);

        hierarcyView = new LinearLayout(context);
        addView(hierarcyView);

        nameView = new TextView(context, null, android.R.attr.textAppearanceLarge);
        //nameView.setPadding(0, dp8, 0, 0);
        addView(nameView);

        codiceView = new TextView(context, null, android.R.attr.textAppearanceSmall);
        addView(codiceView);

        codiceFiscaleView = new TextView(context, null, android.R.attr.textAppearanceSmall);
        addView(codiceFiscaleView);

        comparto = new TextView(context, null, android.R.attr.textAppearanceSmall);
        addView(comparto);

        sottoComparto = new TextView(context, null, android.R.attr.textAppearanceSmall);
        addView(sottoComparto);
    }

    public void setEnte(@NotNull Ente ente, @Nullable GeoItem geoItem) {
        this.ente = ente;
        this.geoItem = geoItem != null ? geoItem : DataUtils.optGeoItemOfEnte(ente);

        nameView.setText(StringUtils.toNormalCase(get(GeoItem::getNome, Ente::getNome)));
        computeHierarcy();

        setHtmlText(codiceView, R.string.ente_codice_x, ente.getCodice());
        setHtmlText(codiceFiscaleView, R.string.ente_codice_fiscale_x, ente.getCodiceFiscale());
        setHtmlText(comparto, R.string.ente_comparto_x, StringUtils.toNormalCase(ente.getSottocomparto().getComparto().getNome()));
        setHtmlText(sottoComparto, R.string.ente_sottocomparto_x, StringUtils.toNormalCase(ente.getSottocomparto().getNome()));
    }

    private void setHtmlText(TextView tv, @StringRes int stringRes, Object... bindings) {
        tv.setText(Html.fromHtml(getResources().getString(stringRes, bindings)));
    }

    private void computeHierarcy() {
        hierarcyView.removeAllViews();
        if (geoItem == null) {
            hierarcyView.addView(createHierarcyView("/" + ente.getNome()), 0);
            geoItem = ente.getComune();
        }
        GeoItem g = geoItem;
        while (g != null && !(g instanceof RipartizioneGeografica)) {
            hierarcyView.addView(createHierarcyView("/" + g.getNome()), 0);
            g = g.getParent();
        }
        hierarcyView.addView(createHierarcyView(getResources().getString(R.string.italia)), 0);
    }

    private View createHierarcyView(String name) {
        TextView ret = new TextView(getContext());
        ret.setSingleLine(true);
        ret.setEllipsize(TextUtils.TruncateAt.END);
        ret.setText(StringUtils.toNormalCase(name));
        return ret;
    }

    private <T> T get(Function<GeoItem, T> a, Function<Ente, T> b) {
        if (geoItem != null) {
            return a.apply(geoItem);
        } else {
            return b.apply(ente);
        }
    }

    public void addExpandButton(Consumer<Ente> onExpandPressed) {
        Button button = new Button(getContext(), null, android.R.attr.borderlessButtonStyle);
        button.setText(R.string.view_all_ente_details);
        button.setOnClickListener(view -> onExpandPressed.consume(ente));
        addView(button);

        int dp8 = MetricsUtils.dpToPixel(getContext(), 8);
        int dp16 = MetricsUtils.dpToPixel(getContext(), 16);
        setPadding(dp16, dp8, dp16, 0);
    }
}
