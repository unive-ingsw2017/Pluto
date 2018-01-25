package mama.pluto.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Ente;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.GeoItem;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import mama.pluto.dataAbstraction.AnagraficheImproved;
import mama.pluto.dataAbstraction.Category;
import mama.pluto.dataAbstraction.EnteSummary;


public class EnteView extends ScrollView {
    @NotNull
    private final AnagraficheImproved anagrafiche;
    private final LinearLayout container;
    private final EnteSummaryView enteSummaryView;
    private Ente ente;
    private EnteSummary enteSummary;

    public EnteView(@NotNull Context context, @NonNull AnagraficheImproved anagrafiche) {
        super(context);
        container = new LinearLayout(context);
        this.anagrafiche = anagrafiche;
        container.setOrientation(LinearLayout.VERTICAL);
        addView(container);

        enteSummaryView = new EnteSummaryView(context);
        container.addView(enteSummaryView);
    }

    public void setEnte(@NotNull Ente ente, @Nullable GeoItem geoItem) {
        this.ente = ente;
        this.enteSummary = EnteSummary.getInstance(getContext(), anagrafiche, ente);
        enteSummaryView.setEnte(enteSummary, ente, geoItem);

        while (container.getChildCount() > 1) {
            container.removeViewAt(1);
        }

        Set<Category> allCategories = new HashSet<>();
        allCategories.addAll(enteSummary.getEntrateMap().keySet());
        allCategories.addAll(enteSummary.getUsciteMap().keySet());
        ArrayList<Category> categoriesList = new ArrayList<>(allCategories);
        Collections.sort(categoriesList, (c1, c2) -> c1.getName().compareToIgnoreCase(c2.getName()));

        for (Category category : categoriesList) {
            CategoryBalanceView categoryBalanceView = new CategoryBalanceView(getContext());
            categoryBalanceView.setCategory(category, enteSummary.getEntrateMap().get(category), enteSummary.getUsciteMap().get(category));
            container.addView(categoryBalanceView);
        }
    }
}
