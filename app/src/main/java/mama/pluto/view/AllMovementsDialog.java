package mama.pluto.view;

import android.app.AlertDialog;
import android.content.Context;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.CodiceGestionale;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Ente;
import com.github.mmauro94.siopeDownloader.datastruct.operazioni.Operazione;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import mama.pluto.R;
import mama.pluto.dataAbstraction.AnagraficheExtended;
import mama.pluto.dataAbstraction.Category;
import mama.pluto.dataAbstraction.DataUtils;
import mama.pluto.utils.BaseViewHolder;
import mama.pluto.utils.EuroFormattingUtils;
import mama.pluto.utils.MetricsUtils;

public class AllMovementsDialog extends AlertDialog.Builder {

    public AllMovementsDialog(@NotNull Context context, AnagraficheExtended anagrafiche, @NotNull Ente ente, @NotNull Category category) {
        super(context);

        RecyclerView rr = new RecyclerView(context);
        rr.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        rr.setPadding(0, 0, 0, MetricsUtils.dpToPixel(getContext(), 32));
        rr.setClipToPadding(false);

        List<Operazione<?>> allMovements = DataUtils.loadAllOperazioni(context, anagrafiche, ente, category, true);
        List<CodiceGestionale> codiciGestionali = DataUtils.extractCodiciGestionaliByAmount(allMovements);

        rr.setAdapter(new RecyclerView.Adapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return BaseViewHolder.getFullInstance(rr, new CodiceGesionaleRecap(context));
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                CodiceGesionaleRecap codiceGesionaleRecap = (CodiceGesionaleRecap) holder.itemView;
                codiceGesionaleRecap.setData(codiciGestionali.get(position), allMovements);
            }

            @Override
            public int getItemCount() {
                return codiciGestionali.size();
            }
        });

        setView(rr);
    }

    private static class CodiceGesionaleRecap extends LinearLayout {
        private final DoubleLineListItem header;
        private final LabeledBalanceView[] monthViews = new LabeledBalanceView[12];
        private final LabeledBalanceView totalView;

        public CodiceGesionaleRecap(Context context) {
            super(context);
            setOrientation(VERTICAL);

            header = new DoubleLineListItem(context);
            addView(header);

            Calendar cal = Calendar.getInstance();
            for (short month = 0; month < 12; month++) {
                cal.set(1970, month, 1);
                String monthName = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
                monthViews[month] = new LabeledBalanceView(context);
                monthViews[month].setLabel(monthName);
                addView(monthViews[month]);
            }

            View divider = new View(context);
            divider.setBackgroundColor(0xff999999);
            LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, MetricsUtils.dpToPixel(context, 1));
            lp.rightMargin = lp.leftMargin = MetricsUtils.dpToPixel(getContext(), 16);
            addView(divider, lp);

            totalView = new LabeledBalanceView(context);
            totalView.boldLabel();
            totalView.setLabel(getResources().getString(R.string.total));
            addView(totalView);
        }

        public void setData(CodiceGestionale codiceGestionale, List<Operazione<?>> movements) {
            header.setText(codiceGestionale.getNome(), codiceGestionale.getComparto().getNome());
            Map<Short, Set<Operazione<?>>> movementsByMonth = DataUtils.groupMovementsByMonth(movements, codiceGestionale);

            long total = 0;

            for (short i = 1; i <= 12; i++) {
                long balance = DataUtils.getBalance(movementsByMonth.get(i));
                total += balance;
                monthViews[i - 1].setBalance(balance);
            }
            totalView.setBalance(total);
        }
    }

    private static class LabeledBalanceView extends LinearLayout {

        private final TextView labelView;
        private final TextView balance;


        public LabeledBalanceView(Context context) {
            super(context);
            labelView = new TextView(context);
            labelView.setGravity(Gravity.END);
            LayoutParams lp2 = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f);
            lp2.rightMargin = MetricsUtils.dpToPixel(getContext(), 32);
            addView(labelView, lp2);

            balance = new TextView(context);
            balance.setGravity(Gravity.START);
            LayoutParams lp = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f);
            addView(balance, lp);
        }

        public void boldLabel() {
            labelView.setTypeface(ResourcesCompat.getFont(getContext(), R.font.roboto_medium));
        }

        private void setLabel(String label) {
            this.labelView.setText(label);
        }

        public void setBalance(long balance) {
            this.balance.setText(EuroFormattingUtils.formatEuroCentString(balance, true, true));
            if (balance > 0) {
                this.balance.setTextColor(EuroFormattingUtils.POSITIVE_COLOR);
            } else if (balance == 0) {
                this.balance.setTextColor(EuroFormattingUtils.NEUTRAL_COLOR);
            } else {
                this.balance.setTextColor(EuroFormattingUtils.NEGATIVE_COLOR);
            }
        }
    }
}
