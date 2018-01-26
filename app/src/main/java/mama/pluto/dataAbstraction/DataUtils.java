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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.requery.android.database.sqlite.SQLiteDatabase;
import mama.pluto.database.Database;
import mama.pluto.utils.Function;
import mama.pluto.utils.Pair;
import mama.pluto.utils.Triple;

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

    public static Map<Ente, Pair<Long, Long>> loadBalances(@NotNull Context context, @NotNull AnagraficheExtended anagrafiche, @NotNull Sottocomparto sottoComparto, @NotNull Category category) {
        final Map<Ente, Pair<Long, Long>> ret = new HashMap<>();

        SQLiteDatabase db = Database.getInstance(context).getReadableDatabase();
        try (Cursor c = db.rawQuery("SELECT ente, SUM(CASE o.tipo WHEN ? THEN o.amount ELSE 0 END) as entrate, SUM(CASE o.tipo WHEN ? THEN o.amount ELSE 0 END) as uscite " +
                "FROM Operazione o " +
                "INNER JOIN CodiceGestionale cg ON o.codiceGestionale = cg.id " +
                "INNER JOIN ente e ON o.ente = e.id " +
                "WHERE cg.category=? and e.sottocomparto=? " +
                "GROUP BY ente", new Object[]{Database.TIPO_OPERAZIONE_ENTRATA, Database.TIPO_OPERAZIONE_USCITA, category.getId(), sottoComparto.getCodice()})) {
            while (c.moveToNext()) {
                long idEnte = c.getLong(0);
                long entrate = c.getLong(1);
                long uscite = c.getLong(2);
                ret.put(anagrafiche.getEnteById(idEnte), new Pair<>(entrate, uscite));
            }
        }
        return ret;
    }

    public static List<CodiceGestionale> extractCodiciGestionaliByAmount(List<Operazione<?>> operazioni) {
        Map<CodiceGestionale, Long> totalAmount = new HashMap<>();
        for (Operazione<?> operazione : operazioni) {
            if (!totalAmount.containsKey(operazione.getCodiceGestionale())) {
                totalAmount.put(operazione.getCodiceGestionale(), 0L);
            }
            totalAmount.put(operazione.getCodiceGestionale(), totalAmount.get(operazione.getCodiceGestionale()) + operazione.getAmount());
        }
        List<CodiceGestionale> ret = new ArrayList<>(totalAmount.keySet());
        Collections.sort(ret, (o1, o2) -> -totalAmount.get(o1).compareTo(totalAmount.get(o2)));
        return ret;
    }

    public static Map<Short, Set<Operazione<?>>> groupMovementsByMonth(List<Operazione<?>> operazioni, CodiceGestionale codiceGestionale) {
        Map<Short, Set<Operazione<?>>> ret = new HashMap<>(12);
        for (short i = 1; i <= 12; i++) {
            ret.put(i, new HashSet<>());
        }
        for (Operazione<?> operazione : operazioni) {
            if (operazione.getCodiceGestionale().equals(codiceGestionale)) {
                ret.get(((short) operazione.getMonth())).add(operazione);
            }
        }
        return ret;
    }

    public static long getBalance(Iterable<Operazione<?>> operazioni) {
        long ret = 0;
        for (Operazione<?> operazione : operazioni) {
            if (operazione instanceof Entrata) {
                ret += operazione.getAmount();
            } else {
                ret -= operazione.getAmount();
            }
        }
        return ret;
    }


    public static List<Triple<Ente, Long, Long>> getEntiRankPerCategory(@NotNull Category category, @NotNull Sottocomparto sottoComparto, @NotNull Context context, @NotNull AnagraficheExtended anagrafiche, boolean desc) {
        Map<Ente, Pair<Long, Long>> balances = loadBalances(context, anagrafiche, sottoComparto, category);
        List<Triple<Ente, Long, Long>> ret = new ArrayList<>();
        for (Map.Entry<Ente, Pair<Long, Long>> entry : balances.entrySet()) {
            ret.add(new Triple<>(entry.getKey(), entry.getValue().getFirst(), entry.getValue().getSecond()));
        }

        Collections.sort(ret, (o1, o2) -> (desc ? -1 : 1) * Long.compare(o1.getSecond() - o1.getThird(), o2.getSecond() - o2.getThird()));

        return ret;
    }
}
