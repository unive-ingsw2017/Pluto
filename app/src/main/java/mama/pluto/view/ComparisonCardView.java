package mama.pluto.view;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Ente;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.GeoItem;

import org.jetbrains.annotations.NotNull;

import java.util.Set;

import mama.pluto.R;
import mama.pluto.dataAbstraction.AnagraficheExtended;
import mama.pluto.dataAbstraction.ComuneStat;
import mama.pluto.dataAbstraction.DataUtils;
import mama.pluto.dataAbstraction.EntiComparator;
import mama.pluto.utils.EuroFormattingUtils;
import mama.pluto.utils.MetricsUtils;
import mama.pluto.utils.StringUtils;

public class ComparisonCardView extends CollapsableCardView {
    @NotNull
    private final AnagraficheExtended anagrafiche;
    private final TextView categoryNameView;
    private final TextView firstEnteTotal, secondEnteTotal;
    private EntiComparator.CategoryComparison categoryComparison;

    public ComparisonCardView(@NonNull Context context, @NonNull AnagraficheExtended anagrafiche) {
        super(context);
        this.anagrafiche = anagrafiche;
        header.setGravity(Gravity.CENTER);
        header.setOnClickListener(v -> toggle(true));
        header.setPadding(expandIcon.getLayoutParams().width, 0, 0, 0);


        categoryNameView = new TextView(context, null, android.R.attr.textAppearanceMedium);
        categoryNameView.setTextColor(Color.BLACK);
        categoryNameView.setGravity(Gravity.CENTER);
        header.addView(categoryNameView);

        LinearLayout totals = new LinearLayout(context);
        totals.setTranslationY(MetricsUtils.dpToPixel(getContext(), -8));
        totals.setOnClickListener(v -> toggle(true));
        content.addView(totals);

        firstEnteTotal = new TextView(context, null, android.R.attr.textAppearanceSmall);
        firstEnteTotal.setGravity(Gravity.END);
        totals.addView(firstEnteTotal, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        totals.addView(new Space(context), MetricsUtils.dpToPixel(context, 16), 0);

        secondEnteTotal = new TextView(context, null, android.R.attr.textAppearanceSmall);
        secondEnteTotal.setGravity(Gravity.START);
        totals.addView(secondEnteTotal, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

    }

    public void setCategory(EntiComparator.CategoryComparison categoryComparison) {
        if (!isCollapsed()) {
            collapse(false);
        }
        this.categoryComparison = categoryComparison;
        categoryNameView.setText(categoryComparison.getCategory().getName());
        EuroFormattingUtils.Base base = EuroFormattingUtils.Base.getBase(categoryComparison.getFirstBalance(), categoryComparison.getSecondBalance());

        firstEnteTotal.setText(base.format(categoryComparison.getFirstBalance(), true));
        if (categoryComparison.getFirstBalance() >= 0) {
            firstEnteTotal.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_trending_up_green_16dp, 0);
            firstEnteTotal.setTextColor(EuroFormattingUtils.POSITIVE_COLOR);
        } else {
            firstEnteTotal.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_trending_down_red_16dp, 0);
            firstEnteTotal.setTextColor(EuroFormattingUtils.NEGATIVE_COLOR);
        }

        secondEnteTotal.setText(base.format(categoryComparison.getSecondBalance(), true));
        if (categoryComparison.getSecondBalance() >= 0) {
            secondEnteTotal.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_trending_up_green_16dp, 0, 0, 0);
            secondEnteTotal.setTextColor(EuroFormattingUtils.POSITIVE_COLOR);
        } else {
            secondEnteTotal.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_trending_down_red_16dp, 0, 0, 0);
            secondEnteTotal.setTextColor(EuroFormattingUtils.NEGATIVE_COLOR);
        }
    }

    @Override
    protected View createExpandedView() {
        LinearLayout ll = new LinearLayout(getContext());
        ll.setOrientation(LinearLayout.VERTICAL);
        int dp8 = MetricsUtils.dpToPixel(getContext(), 8);
        ll.setPadding(dp8, dp8, dp8, 0);

        addRowSpesa(ll, getContext().getString(R.string.spese), categoryComparison.getFirstUscite(), categoryComparison.getSecondUscite());
        addRowSpesa(ll, getContext().getString(R.string.entrate), categoryComparison.getFirstEntrate(), categoryComparison.getSecondEntrate());
        addRowSpesa(ll, getContext().getString(R.string.bilancio), categoryComparison.getFirstBalance(), categoryComparison.getSecondBalance());

        GeoItem g1 = DataUtils.optGeoItemOfEnte(categoryComparison.getEntiComparator().getEnteSummary1().getEnte());
        GeoItem g2 = DataUtils.optGeoItemOfEnte(categoryComparison.getEntiComparator().getEnteSummary2().getEnte());
        if (g1 != null && g2 != null) {
            Set<ComuneStat> s1 = ComuneStat.getInstance(getContext(), anagrafiche, g1);
            Set<ComuneStat> s2 = ComuneStat.getInstance(getContext(), anagrafiche, g2);
            if (!s1.isEmpty() && !s2.isEmpty()) {
                addRowSpesa(ll, getContext().getString(R.string.bilancio_pro_capite),
                        Math.round(categoryComparison.getFirstBalance() / (float) ComuneStat.getAllPopulation(s1)),
                        Math.round(categoryComparison.getSecondBalance() / (float) ComuneStat.getAllPopulation(s1))
                );
                addRowSpesa(ll, getContext().getString(R.string.bilancio_per_superficie),
                        Math.round(categoryComparison.getFirstBalance() / ComuneStat.getAllSuperficie(s1)),
                        Math.round(categoryComparison.getSecondBalance() / ComuneStat.getAllSuperficie(s1)),
                        "/km²"
                );
            }
        }

        Button b1 = new Button(getContext(), null, android.R.attr.borderlessButtonStyle);
        b1.setText(R.string.view_all_movements_short);
        b1.setOnClickListener(v -> openMovementsDialog(categoryComparison.getEntiComparator().getEnteSummary1().getEnte()));

        Button b2 = new Button(getContext(), null, android.R.attr.borderlessButtonStyle);
        b2.setText(R.string.view_all_movements_short);
        b2.setOnClickListener(v -> openMovementsDialog(categoryComparison.getEntiComparator().getEnteSummary2().getEnte()));
        addRow(ll, null, b1, b2);
        return ll;
    }

    private void openMovementsDialog(Ente ente) {
        new AllMovementsDialog(getContext(), anagrafiche, ente, categoryComparison.getCategory())
                .setTitle(StringUtils.toNormalCase(ente.getNome()) + ": " + categoryComparison.getCategory().getName())
                .show();
    }

    private void addRowSpesa(LinearLayout ll, CharSequence text, long first, long second) {
        addRowSpesa(ll, text, first, second, null);
    }

    private void addRowSpesa(LinearLayout ll, CharSequence text, long first, long second, @Nullable String unitOfMeasure) {
        EuroFormattingUtils.Base base = EuroFormattingUtils.Base.getBase(first, second);
        String firstCS = base.format(first, true);
        String secondCS = base.format(second, true);
        if (unitOfMeasure != null) {
            firstCS += unitOfMeasure;
            secondCS += unitOfMeasure;
        }
        addRow(ll, text, firstCS, secondCS);
    }

    private void addRow(LinearLayout ll, CharSequence text, CharSequence first, CharSequence second) {
        TextView firstView = new TextView(getContext());
        firstView.setGravity(Gravity.END);

        TextView secondView = new TextView(getContext());
        secondView.setGravity(Gravity.START);

        firstView.setText(first);
        secondView.setText(second);
        addRow(ll, text, firstView, secondView);
    }

    private void addRow(LinearLayout ll, CharSequence text, View firstView, View secondView) {
        LinearLayout row = new LinearLayout(getContext());
        row.setGravity(Gravity.CENTER);
        int dp8 = MetricsUtils.dpToPixel(getContext(), 8);
        row.setPadding(dp8, dp8, dp8, dp8);

        row.addView(firstView, new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f));

        if (text != null) {
            TextView legend = new TextView(getContext());
            legend.setTypeface(ResourcesCompat.getFont(getContext(), R.font.roboto_medium));
            legend.setGravity(Gravity.CENTER);
            legend.setPadding(dp8, 0, dp8, 0);
            row.addView(legend);
            legend.setText(text);
        } else {
            row.addView(new Space(getContext()), MetricsUtils.dpToPixel(getContext(), 32), 0);
        }

        row.addView(secondView, new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f));


        ll.addView(row);
    }
}
