package mama.pluto.view;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import mama.pluto.R;
import mama.pluto.dataAbstraction.AnagraficheExtended;
import mama.pluto.dataAbstraction.Category;
import mama.pluto.dataAbstraction.EnteSummary;
import mama.pluto.utils.EuroFormattingUtils;
import mama.pluto.utils.MetricsUtils;

public class CategoryBalanceView extends CollapsableCardView {
    @NotNull
    private final AnagraficheExtended anagrafiche;
    private final TextView categoryNameView;
    private final BalanceRowView balanceRowView;
    @Nullable
    private EnteSummary enteSummary;
    @Nullable
    private Category category;

    public CategoryBalanceView(@NotNull Context context, @NonNull AnagraficheExtended anagrafiche) {
        super(context);
        this.anagrafiche = anagrafiche;
        int dp2 = MetricsUtils.dpToPixel(context, 2);
        int dp8 = MetricsUtils.dpToPixel(context, 8);
        int dp16 = MetricsUtils.dpToPixel(context, 16);
        header.setOnClickListener(view -> toggle(true));
        header.setGravity(Gravity.CENTER_VERTICAL);


        categoryNameView = new TextView(context);
        categoryNameView.setTextColor(Color.BLACK);
        categoryNameView.setPadding(0, 0, dp8, 0);
        header.addView(categoryNameView);

        header.addView(new Space(getContext()), new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f));


        balanceRowView = new BalanceRowView(getContext());
        balanceRowView.setTranslationY(-MetricsUtils.dpToPixel(getContext(), 8));
        balanceRowView.setOnClickListener(view -> toggle(true));
        balanceRowView.setPadding(dp16, 0, dp16, 0);
        content.addView(balanceRowView);

    }

    public void setCategory(@NotNull EnteSummary enteSummary, @NotNull Category category) {
        if (!isCollapsed()) {
            collapse(false);
        }
        this.enteSummary = enteSummary;
        this.category = category;
        categoryNameView.setText(category.getName());
        Long in = enteSummary.getEntrateMap().get(category);
        Long out = enteSummary.getUsciteMap().get(category);
        balanceRowView.setBalance(in, out);
    }


    @Override
    protected View createExpandedView() {
        if (category == null || enteSummary == null) {
            throw new IllegalStateException();
        }
        final LinearLayout ll = new LinearLayout(getContext());
        ll.setOrientation(LinearLayout.VERTICAL);
        final int dp16 = MetricsUtils.dpToPixel(getContext(), 16);
        ll.setPadding(dp16, 0, dp16, 0);

        Long in = enteSummary.getEntrateMap().get(category);
        Long out = enteSummary.getUsciteMap().get(category);

        addItem(ll, R.string.spese_x, out == null ? "-" : EuroFormattingUtils.formatEuroCentString(out, false, true));
        addItem(ll, R.string.entrate_x, in == null ? "-" : EuroFormattingUtils.formatEuroCentString(in, false, true));
        long total = (in == null ? 0 : in) - (out == null ? 0 : out);
        addItem(ll, R.string.bilancio_x, EuroFormattingUtils.formatEuroCentString(total, false, true));

        Button button = new Button(getContext(), null, android.R.attr.borderlessButtonStyle);
        button.setText(R.string.view_all_movements);
        button.setOnClickListener(view ->
                new AllMovementsDialog(getContext(), anagrafiche, enteSummary.getEnte(), category)
                        .setTitle(category.getName())
                        .show());
        ll.addView(button);
        return ll;
    }

    private void addItem(ViewGroup vg, @StringRes int stringRes, Object... bindings) {
        TextView tv = new TextView(getContext());
        tv.setText(Html.fromHtml(getResources().getString(stringRes, bindings)));

        vg.addView(tv);
    }
}
