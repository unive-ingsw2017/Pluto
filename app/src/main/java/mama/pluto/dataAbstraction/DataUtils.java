package mama.pluto.dataAbstraction;

import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Anagrafiche;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Comune;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Ente;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.GeoItem;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Provincia;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Regione;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mama.pluto.utils.Function;

public final class DataUtils {
    private DataUtils() {
        throw new IllegalStateException();
    }

    public static final String SOTTOCOMPARTO_COMUNE = "COMUNE";
    public static final String SOTTOCOMPARTO_PROVINCIA = "PROVINCIA";
    public static final String SOTTOCOMPARTO_REGIONE = "REGIONE";

    @NotNull
    public static List<Ente> getEntiFromComune(@NotNull Anagrafiche anagrafiche, @NotNull Comune comune, boolean includeEnteOfComune) {
        List<Ente> ris = new ArrayList<>();
        for (Ente ente : anagrafiche.getEnti()) {
            if (ente.getComune().equals(comune) && (includeEnteOfComune || !ente.getSottocomparto().getCodice().equals(SOTTOCOMPARTO_COMUNE))) {
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
        Ente ret = optEnteOf(anagrafiche, provincia, SOTTOCOMPARTO_PROVINCIA, e -> e.getComune().getProvincia());
        if (ret == null) {
            ret = getEnteOf(anagrafiche, provincia, SOTTOCOMPARTO_REGIONE, e -> e.getComune().getProvincia());
        }
        return ret;
    }

    @NotNull
    public static Ente getEnteOfComune(@NotNull Anagrafiche anagrafiche, @NotNull Comune comune) {
        return getEnteOf(anagrafiche, comune, SOTTOCOMPARTO_COMUNE, Ente::getComune);
    }

    @NotNull
    private static <T extends GeoItem> Ente getEnteOf(@NotNull Anagrafiche anagrafiche, @NotNull T geoItem, @NotNull String sottocompartoCode, @NotNull Function<Ente, T> producer) {
        Ente ente = optEnteOf(anagrafiche, geoItem, sottocompartoCode, producer);
        if (ente == null) {
            throw new IllegalStateException("Ente for " + sottocompartoCode + " " + geoItem.getNome() + " not found");
        }
        return ente;
    }

    @Nullable
    private static <T extends GeoItem> Ente optEnteOf(@NotNull Anagrafiche anagrafiche, @NotNull T geoItem, @NotNull String sottocompartoCode, @NotNull Function<Ente, T> producer) {
        for (Ente ente : anagrafiche.getEnti()) {
            if (ente.getSottocomparto().getCodice().equals(sottocompartoCode) && producer.apply(ente).equals(geoItem)) {
                return ente;
            }
        }
        return null;
    }

    @NotNull
    public static GeoItem getGeoItemOfEnte(@NotNull Ente ente) {
        GeoItem geoItem = optGeoItemOfEnte(ente);
        if (geoItem == null) {
            throw new IllegalArgumentException("Invalid ente");
        }
        return geoItem;
    }

    @Nullable
    public static GeoItem optGeoItemOfEnte(@NotNull Ente ente) {
        switch (ente.getSottocomparto().getCodice()) {
            case SOTTOCOMPARTO_COMUNE:
                return ente.getComune();
            case SOTTOCOMPARTO_PROVINCIA:
                return ente.getComune().getProvincia();
            case SOTTOCOMPARTO_REGIONE:
                return ente.getComune().getProvincia().getRegione();
            default:
                return null;
        }
    }

    public static int getPopulationOfGeoItem(@NotNull Anagrafiche a, @NotNull GeoItem g) {
        if (g instanceof Comune) {
            return getEnteOfComune(a, (Comune) g).getNumeroAbitanti();
        } else {
            final Collection<? extends GeoItem> children = g.getChildren();
            assert children != null;
            int ret = 0;
            for (GeoItem c : children) {
                ret += getPopulationOfGeoItem(a, c);
            }
            return ret;
        }
    }

    @NotNull
    public static <K1, K2, V> Map<K2, V> mapConvertKeys(@NotNull Map<K1, ? extends V> map, @NotNull Function<? super K1, ? extends K2> transformer) {
        final Map<K2, V> ret = new HashMap<>(map.size());
        for (Map.Entry<K1, ? extends V> entry : map.entrySet()) {
            ret.put(transformer.apply(entry.getKey()), entry.getValue());
        }
        return ret;
    }

    @NotNull
    public static <K, V1, V2> Map<K, V2> mapConvertValues(@NotNull Map<? extends K, V1> map, @NotNull Function<? super V1, ? extends V2> transformer) {
        final Map<K, V2> ret = new HashMap<>(map.size());
        for (Map.Entry<? extends K, V1> entry : map.entrySet()) {
            ret.put(entry.getKey(), transformer.apply(entry.getValue()));
        }
        return ret;
    }
}
