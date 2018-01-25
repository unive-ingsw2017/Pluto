package mama.pluto.view;

import android.app.AlertDialog;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.CodiceGestionale;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Ente;
import com.github.mmauro94.siopeDownloader.datastruct.operazioni.Entrata;
import com.github.mmauro94.siopeDownloader.datastruct.operazioni.Operazione;

import org.jetbrains.annotations.NotNull;

import java.util.List;

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
        LinearLayout ll = new LinearLayout(context);
        ll.setOrientation(LinearLayout.VERTICAL);

        RecyclerView rr = new RecyclerView(context);
        rr.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        ll.addView(rr, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));

        List<Operazione<?>> allMovements = DataUtils.loadAllOperazioni(context, anagrafiche, ente, category, true);

        rr.setAdapter(new RecyclerView.Adapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return BaseViewHolder.getFullInstance(rr, new ThreeLineListItem(context));
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                Operazione<?> operazione = allMovements.get(position);
                CodiceGestionale codiceGestionale = operazione.getCodiceGestionale();
                ThreeLineListItem threeLineListItem = (ThreeLineListItem) holder.itemView;
                threeLineListItem.setText(
                        codiceGestionale.getNome() + " (" + operazione.getMonth() + "/" + operazione.getYear() + ")",
                        codiceGestionale.getComparto().getNome(),
                        EuroFormattingUtils.formatEuroCentString(operazione.getAmount(), false, true)
                );
                if (operazione instanceof Entrata) {
                    threeLineListItem.setThirdLineTextColor(EuroFormattingUtils.POSITIVE_COLOR);
                } else {
                    threeLineListItem.setThirdLineTextColor(EuroFormattingUtils.NEGATIVE_COLOR);
                }
            }

            @Override
            public int getItemCount() {
                return allMovements.size();
            }
        });


        TextView disclaimer = new TextView(context);
        final int dp4 = MetricsUtils.dpToPixel(context, 4);
        disclaimer.setPadding(dp4, dp4, dp4, dp4);
        disclaimer.setBackgroundColor(0x10000000);
        disclaimer.setText(R.string.disclaimer_zero_amount_hidden);
        ll.addView(disclaimer);

        setView(ll);
    }
}
