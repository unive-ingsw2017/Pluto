package mama.pluto.view;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Ente;

import mama.pluto.R;
import mama.pluto.utils.MetricsUtils;

public class EnteSummaryView extends LinearLayout {

    private final TextView nameView;
    private final TextView codiceView;
    private final TextView codiceFiscaleView;
    private final TextView comparto;
    private final TextView sottoComparto;
    private Ente ente;

    public EnteSummaryView(Context context) {
        super(context);
        setOrientation(VERTICAL);

        int dp16 = MetricsUtils.dpToPixel(context, 16);
        setPadding(dp16, dp16, dp16, dp16);

        nameView = new TextView(context, null, android.R.attr.textAppearanceLarge);
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

    public void setEnte(Ente ente) {
        this.ente = ente;
        nameView.setText(ente.getNome());
        codiceView.setText(getResources().getString(R.string.ente_codice_x, ente.getCodice()));
        codiceFiscaleView.setText(getResources().getString(R.string.ente_codice_fiscale_x, ente.getCodiceFiscale()));
        comparto.setText(getResources().getString(R.string.ente_comparto_x, ente.getSottocomparto().getComparto().getNome()));
        sottoComparto.setText(getResources().getString(R.string.ente_sottocomparto_x, ente.getSottocomparto().getNome()));
    }
}
