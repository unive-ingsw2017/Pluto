package mama.pluto.dataAbstraction;

import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Anagrafiche;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Comune;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Ente;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.GeoItem;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Provincia;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Regione;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mama.pluto.utils.Function;
import mama.pluto.utils.Producer;

public class DataUtils {

    public static final String SOTTOCOMPARTO_COMUNE = "COMUNE";
    public static final String SOTTOCOMPARTO_PROVINCIA = "PROVINCIA";
    public static final String SOTTOCOMPARTO_REGIONE = "REGIONE";

    @NotNull
    public static List<Ente> getEntiFromComune(@NotNull Anagrafiche anagrafiche, @NotNull Comune comune) {
        List<Ente> ris = new ArrayList<>();
        for (Ente ente : anagrafiche.getEnti()) {
            if (ente.getComune().equals(comune)) {
                ris.add(ente);
            }
        }
        return ris;
    }

    @NotNull
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

    @NotNull
    public static List<Ente> getRegioni(@NotNull Anagrafiche anagrafiche) {
        List<Ente> ris = new ArrayList<>();
        for (Ente ente : anagrafiche.getEnti()) {
            if (ente.getSottocomparto().getCodice().equals(SOTTOCOMPARTO_REGIONE)) {
                ris.add(ente);
            }
        }
        return ris;
    }

    @NotNull
    public static List<Ente> getProvince(@NotNull Anagrafiche anagrafiche) {
        List<Ente> ris = new ArrayList<>();
        for (Ente ente : anagrafiche.getEnti()) {
            if (ente.getSottocomparto().getCodice().equals(SOTTOCOMPARTO_PROVINCIA)) {
                ris.add(ente);
            }
        }
        return ris;
    }

    @NotNull
    public static List<Ente> getComuni(@NotNull Anagrafiche anagrafiche) {
        List<Ente> ris = new ArrayList<>();
        for (Ente ente : anagrafiche.getEnti()) {
            if (ente.getSottocomparto().getCodice().equals(SOTTOCOMPARTO_COMUNE)) {
                ris.add(ente);
            }
        }
        return ris;
    }


    @NotNull
    public static Ente getEnteOfGeoItem(@NotNull Anagrafiche anagrafiche, @NotNull GeoItem geoItem) {
        if (geoItem instanceof Comune) {
            return getEnteOfComune(anagrafiche, ((Comune) geoItem));
        } else if (geoItem instanceof Provincia) {
            return getEnteOfProvincia(anagrafiche, ((Provincia) geoItem));
        } else if (geoItem instanceof Regione) {
            return getEnteOfRegione(anagrafiche, ((Regione) geoItem));
        } else {
            throw new IllegalArgumentException();
        }
    }

    @NotNull
    public static Ente getEnteOfRegione(@NotNull Anagrafiche anagrafiche, @NotNull Regione regione) {
        return getEnteOf(anagrafiche, regione, SOTTOCOMPARTO_REGIONE, e -> e.getComune().getProvincia().getRegione());
    }

    @NotNull
    public static Ente getEnteOfProvincia(@NotNull Anagrafiche anagrafiche, @NotNull Provincia provincia) {
        return getEnteOf(anagrafiche, provincia, SOTTOCOMPARTO_PROVINCIA, e -> e.getComune().getProvincia());
    }

    @NotNull
    public static Ente getEnteOfComune(@NotNull Anagrafiche anagrafiche, @NotNull Comune comune) {
        return getEnteOf(anagrafiche, comune, SOTTOCOMPARTO_COMUNE, Ente::getComune);
    }

    @NotNull
    private static <T extends GeoItem> Ente getEnteOf(@NotNull Anagrafiche anagrafiche, @NotNull T geoItem, @NotNull String sottocompartoCode, @NotNull Function<Ente, T> producer) {
        for (Ente ente : anagrafiche.getEnti()) {
            if (ente.getSottocomparto().getCodice().equals(sottocompartoCode) && producer.apply(ente).equals(geoItem)) {
                return ente;
            }
        }
        throw new IllegalStateException("Ente for " + sottocompartoCode + " " + geoItem.getNome() + " not found");
    }

    @NotNull
    public static GeoItem getGeoItemOfEnte(@NotNull Ente ente) {
        switch (ente.getSottocomparto().getCodice()) {
            case SOTTOCOMPARTO_COMUNE:
                return ente.getComune();
            case SOTTOCOMPARTO_PROVINCIA:
                return ente.getComune().getProvincia();
            case SOTTOCOMPARTO_REGIONE:
                return ente.getComune().getProvincia().getRegione();
            default:
                throw new IllegalArgumentException("Invalid ente");
        }
    }

    @NotNull
    public static <K1, K2, V> Map<K2, V> mapConvertKeys(@NotNull Map<K1, V> map, @NotNull Function<K1, K2> transformer) {
        final Map<K2, V> ret = new HashMap<>(map.size());
        for (Map.Entry<K1, V> entry : map.entrySet()) {
            ret.put(transformer.apply(entry.getKey()), entry.getValue());
        }
        return ret;
    }
}
