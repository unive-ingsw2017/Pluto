package mama.pluto.dataAbstraction;

import android.content.Context;

import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Ente;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Sottocomparto;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class EntiRankPerCategory {

   /* Category category;
    Sottocomparto sottoComparto;

    public EntiRankPerCategory(@NotNull Category category, @NotNull Sottocomparto sottoComparto) {
        this.category = category;
        this.sottoComparto = sottoComparto;
    }

    public List<Category> getRank(@NotNull AnagraficheExtended anagrafica,@NotNull Context context){

        List<EnteSummary> result = new ArrayList<>();

        for (Ente e: anagrafica.getEnti()) {
            if(e.getSottocomparto().equals(sottoComparto)){
                EnteSummary summary = EnteSummary.getInstance(context,anagrafica,e);
                result.add(summary);
            }
        }

        Collections.sort(result, new Comparator<EnteSummary>() {
            @Override
            public int compare(EnteSummary es1, EnteSummary es2) {


                return es1.getEntrateMap().get(category) - es2.getEntrateMap().get(category);
            }
        });*/

    }


