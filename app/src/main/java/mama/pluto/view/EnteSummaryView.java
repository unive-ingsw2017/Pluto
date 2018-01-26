package mama.pluto.view;

import android.content.Context;
import android.support.annotation.StringRes;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
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
import mama.pluto.dataAbstraction.EnteSummary;
import mama.pluto.utils.Consumer;
import mama.pluto.utils.MetricsUtils;
import mama.pluto.utils.StringUtils;

public class EnteSummaryView extends LinearLayout {

    private final TextView nameView;
    private final TextView secondaryNameView;
    private final BalanceRowView balanceRowView;
    private final LinearLayout hierarcyView;
    private final TextView codiceView;
    private final TextView codiceFiscaleView;
    private final TextView sottoComparto;
    private final TextView comparto;
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
        addView(nameView);

        secondaryNameView = new TextView(context, null, android.R.attr.textAppearanceMedium);
        addView(secondaryNameView);

        LinearLayout ll = new LinearLayout(context);
        ll.setGravity(Gravity.CENTER_VERTICAL);
        addView(ll);

        TextView tv = new TextView(context);
        tv.setText(Html.fromHtml(getResources().getString(R.string.bilancio_)));
        tv.append(" ");
        ll.addView(tv);
        balanceRowView = new BalanceRowView(context);
        ll.addView(balanceRowView);

        codiceView = new TextView(context, null, android.R.attr.textAppearanceSmall);
        addView(codiceView);

        codiceFiscaleView = new TextView(context, null, android.R.attr.textAppearanceSmall);
        addView(codiceFiscaleView);

        sottoComparto = new TextView(context, null, android.R.attr.textAppearanceSmall);
        addView(sottoComparto);

        comparto = new TextView(context, null, android.R.attr.textAppearanceSmall);
        addView(comparto);

    }

    public void setEnte(@NotNull EnteSummary enteSummary, @NotNull Ente ente, @Nullable GeoItem geoItem) {
        this.ente = ente;
        this.geoItem = geoItem != null ? geoItem : DataUtils.optGeoItemOfEnte(ente);

        if (geoItem == null) {
            nameView.setText(StringUtils.toNormalCase(ente.getNome()));
            secondaryNameView.setVisibility(GONE);
        } else {
            nameView.setText(StringUtils.toNormalCase(geoItem.getNome()));
            secondaryNameView.setText(StringUtils.toNormalCase(ente.getNome()));
            secondaryNameView.setVisibility(VISIBLE);
        }
        computeHierarcy();

        setHtmlText(codiceView, R.string.ente_codice_x, ente.getCodice());
        setHtmlText(codiceFiscaleView, R.string.ente_codice_fiscale_x, ente.getCodiceFiscale());
        setHtmlText(comparto, R.string.ente_comparto_x, StringUtils.toNormalCase(ente.getSottocomparto().getComparto().getNome()));
        setHtmlText(sottoComparto, R.string.ente_sottocomparto_x, StringUtils.toNormalCase(ente.getSottocomparto().getNome()));

        balanceRowView.setBalance(enteSummary.getTotalEntrateAmount(), enteSummary.getTotalUsciteAmount());
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

    public void addExpandButton(String text, Consumer<Ente> onExpandPressed) {
        Button button = new Button(getContext(), null, android.R.attr.borderlessButtonStyle);
        button.setOnClickListener(view -> onExpandPressed.consume(ente));
        button.setText(text);
        addView(button);

        int dp8 = MetricsUtils.dpToPixel(getContext(), 8);
        int dp16 = MetricsUtils.dpToPixel(getContext(), 16);
        setPadding(dp16, dp8, dp16, 0);
    }
}
