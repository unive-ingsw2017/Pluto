package mama.pluto.dataAbstraction;

import android.content.Context;
import android.database.Cursor;

import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.CodiceGestionale;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.CodiceGestionaleEntrate;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.CodiceGestionaleUscite;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Comune;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Ente;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.GeoItem;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Provincia;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Regione;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Sottocomparto;
import com.github.mmauro94.siopeDownloader.datastruct.operazioni.Entrata;
import com.github.mmauro94.siopeDownloader.datastruct.operazioni.Operazione;
import com.github.mmauro94.siopeDownloader.datastruct.operazioni.Uscita;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.requery.android.database.sqlite.SQLiteDatabase;
import mama.pluto.database.Database;
import mama.pluto.utils.Function;
import mama.pluto.utils.Pair;

public final class DataUtils {
    private DataUtils() {
        throw new IllegalStateException();
    }

    public static final String SOTTOCOMPARTO_COMUNE = "COMUNE";
    public static final String SOTTOCOMPARTO_PROVINCIA = "PROVINCIA";
    public static final String SOTTOCOMPARTO_REGIONE = "REGIONE";

    @NotNull
    public static List<Ente> getEntiFromComune(@NotNull AnagraficheExtended anagrafiche, @NotNull Comune comune, boolean includeEnteOfComune) {
        List<Ente> ris = new ArrayList<>();
        for (Ente ente : anagrafiche.getEnti()) {
            if (ente.getComune().equals(comune) && (includeEnteOfComune || !ente.getSottocomparto().getCodice().equals(SOTTOCOMPARTO_COMUNE))) {
                ris.add(ente);
            }
        }
        return ris;
    }

    @NotNull
    public static List<Ente> searchEnte(@NotNull AnagraficheExtended anagrafiche, @NotNull String query) {
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
    public static List<Ente> getRegioni(@NotNull AnagraficheExtended anagrafiche) {
        List<Ente> ris = new ArrayList<>();
        for (Ente ente : anagrafiche.getEnti()) {
            if (ente.getSottocomparto().getCodice().equals(SOTTOCOMPARTO_REGIONE)) {
                ris.add(ente);
            }
        }
        return ris;
    }

    @NotNull
    public static List<Ente> getProvince(@NotNull AnagraficheExtended anagrafiche) {
        List<Ente> ris = new ArrayList<>();
        for (Ente ente : anagrafiche.getEnti()) {
            if (ente.getSottocomparto().getCodice().equals(SOTTOCOMPARTO_PROVINCIA)) {
                ris.add(ente);
            }
        }
        return ris;
    }

    @NotNull
    public static List<Ente> getComuni(@NotNull AnagraficheExtended anagrafiche) {
        List<Ente> ris = new ArrayList<>();
        for (Ente ente : anagrafiche.getEnti()) {
            if (ente.getSottocomparto().getCodice().equals(SOTTOCOMPARTO_COMUNE)) {
                ris.add(ente);
            }
        }
        return ris;
    }


    @NotNull
    public static Ente getEnteOfGeoItem(@NotNull AnagraficheExtended anagrafiche, @NotNull GeoItem geoItem) {
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
    public static Ente getEnteOfRegione(@NotNull AnagraficheExtended anagrafiche, @NotNull Regione regione) {
        return getEnteOf(anagrafiche, regione, SOTTOCOMPARTO_REGIONE, e -> e.getComune().getProvincia().getRegione());
    }

    @NotNull
    public static Ente getEnteOfProvincia(@NotNull AnagraficheExtended anagrafiche, @NotNull Provincia provincia) {
        Ente ret = optEnteOf(anagrafiche, provincia, SOTTOCOMPARTO_PROVINCIA, e -> e.getComune().getProvincia());
        if (ret == null) {
            ret = getEnteOf(anagrafiche, provincia, SOTTOCOMPARTO_REGIONE, e -> e.getComune().getProvincia());
        }
        return ret;
    }

    @NotNull
    public static Ente getEnteOfComune(@NotNull AnagraficheExtended anagrafiche, @NotNull Comune comune) {
        return getEnteOf(anagrafiche, comune, SOTTOCOMPARTO_COMUNE, Ente::getComune);
    }

    @NotNull
    private static <T extends GeoItem> Ente getEnteOf(@NotNull AnagraficheExtended anagrafiche, @NotNull T geoItem, @NotNull String sottocompartoCode, @NotNull Function<Ente, T> producer) {
        Ente ente = optEnteOf(anagrafiche, geoItem, sottocompartoCode, producer);
        if (ente == null) {
            throw new IllegalStateException("Ente for " + sottocompartoCode + " " + geoItem.getNome() + " not found");
        }
        return ente;
    }

    @Nullable
    private static <T extends GeoItem> Ente optEnteOf(@NotNull AnagraficheExtended anagrafiche, @NotNull T geoItem, @NotNull String sottocompartoCode, @NotNull Function<Ente, T> producer) {
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

    public static int getPopulationOfGeoItem(@NotNull AnagraficheExtended a, @NotNull GeoItem g) {
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

    public static List<Operazione<?>> loadAllOperazioni(@NotNull Context context, @NotNull AnagraficheExtended anagrafiche, @NotNull Ente ente, @NotNull Category category, boolean hideZeros) {
        SQLiteDatabase db = Database.getInstance(context).getReadableDatabase();
        List<Operazione<?>> ret = new ArrayList<>();
        try (Cursor c = db.rawQuery("SELECT o.tipo, o.codiceGestionale, o.year, o.month, o.amount " +
                "FROM Operazione o " +
                "INNER JOIN CodiceGestionale cg ON o.codiceGestionale = cg.id " +
                "WHERE ente = ? AND cg.category=? " + (hideZeros ? "AND amount != 0 " : "") +
                "ORDER BY amount DESC, cg.nome ASC", new Number[]{anagrafiche.getIdEnte(ente), category.getId()})) {
            while (c.moveToNext()) {
                final Operazione operazione;
                CodiceGestionale codiceGestionale = anagrafiche.getCodiceGestionaleById(c.getLong(1));
                if (c.getLong(0) == Database.TIPO_OPERAZIONE_ENTRATA) {
                    operazione = new Entrata(ente, c.getInt(2), c.getInt(3), (CodiceGestionaleEntrate) codiceGestionale, c.getLong(4));
                } else if (c.getLong(0) == Database.TIPO_OPERAZIONE_USCITA) {
                    operazione = new Uscita(ente, c.getInt(2), c.getInt(3), (CodiceGestionaleUscite) codiceGestionale, c.getLong(4));
                } else {
                    throw new IllegalStateException();
                }
                ret.add(operazione);
            }
        }
        return ret;
    }

    public static List<Ente> getEntiRankPerCategory(@NotNull Category category,
                                                    @NotNull Sottocomparto sottoComparto,
                                                    @NotNull Context context,
                                                    @NotNull AnagraficheExtended anagrafiche,
                                                    boolean desc) {
        List<Pair<Ente, Long>> entiAmounts = new ArrayList<>();
        for (Ente e : anagrafiche.getEnti()) {
            if (e.getSottocomparto().equals(sottoComparto)) {
                List<Operazione<?>> operazioni = loadAllOperazioni(context, anagrafiche, e, category, false);
                long amount = 0;
                for (Operazione<?> operazione : operazioni) {
                    if (operazione instanceof Entrata) {
                        amount += operazione.getAmount();
                    } else {
                        amount -= operazione.getAmount();
                    }
                }
                entiAmounts.add(new Pair(e, amount));
            }
        }
        if(desc){
            Collections.sort(entiAmounts, (p1, p2) -> Long.compare(p2.getSecond(), p1.getSecond()));
        }
        else{
            Collections.sort(entiAmounts, (p1, p2) -> Long.compare(p1.getSecond(), p2.getSecond()));
        }
        List<Ente> result = new ArrayList<>();
        for (Pair<Ente, Long> entiAmount : entiAmounts) {
            result.add(entiAmount.getFirst());
        }
        return result;
    }
}
