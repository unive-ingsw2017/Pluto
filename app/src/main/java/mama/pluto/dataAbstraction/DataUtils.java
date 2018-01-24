package mama.pluto.dataAbstraction;

import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Anagrafiche;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Comune;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Ente;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DataUtils {

    public static List<Ente> getEntiFromComune(@NotNull Anagrafiche anagrafiche, @NotNull Comune comune) {
        List<Ente> ris = new ArrayList<>();
        for (Ente ente : anagrafiche.getEnti()) {
            if (ente.getComune().equals(comune)) {
                ris.add(ente);
            }
        }
        return ris;
    }

    public static List<Ente> searchEnte(@NotNull Anagrafiche anagrafiche, @NotNull String query) {
        query = query.toLowerCase();
        List<Ente> ris = new ArrayList<>();
        for (Ente ente : anagrafiche.getEnti()) {
            if (ente.getNome().toLowerCase().contains(query)) {
                ris.add(ente);
            }
        }
        Collections.sort(ris, (ente1, ente2) -> {
            String name1 = ente1.getNome();
            String name2 = ente2.getNome();
            int ret = Integer.compare(name1.length(), name2.length());
            if (ret != 0) {
                return ret;
            } else {
                return name1.compareToIgnoreCase(name2);
            }
        });
        return ris;
    }

    public static List<Ente> getRegioni(@NotNull Anagrafiche anagrafiche) {
        List<Ente> ris = new ArrayList<>();
        for (Ente ente : anagrafiche.getEnti()) {
            if (ente.getSottocomparto().getCodice().equals("REGIONE")) {
                ris.add(ente);
            }
        }
        return ris;
    }

    public static List<Ente> getProvince(@NotNull Anagrafiche anagrafiche) {
        List<Ente> ris = new ArrayList<>();
        for (Ente ente : anagrafiche.getEnti()) {
            if (ente.getSottocomparto().getCodice().equals("PROVINCIA")) {
                ris.add(ente);
            }
        }
        return ris;
    }

    public static List<Ente> getComuni(@NotNull Anagrafiche anagrafiche) {
        List<Ente> ris = new ArrayList<>();
        for (Ente ente : anagrafiche.getEnti()) {
            if (ente.getSottocomparto().getCodice().equals("COMUNE")) {
                ris.add(ente);
            }
        }
        return ris;
    }

}
