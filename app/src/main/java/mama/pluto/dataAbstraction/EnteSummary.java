package mama.pluto.dataAbstraction;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Ente;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import mama.pluto.database.Database;

public final class EnteSummary {

    @NotNull
    private final Ente ente;
    @NotNull
    private final Map<Category, Long> entrateMap, usciteMap;

    private EnteSummary(@NotNull Ente ente, Map<Category, Long> entrateMap, Map<Category, Long> usciteMap) {
        this.ente = ente;
        this.entrateMap = entrateMap;
        this.usciteMap = usciteMap;
    }

    @NonNull
    public Ente getEnte() {
        return ente;
    }

    @NonNull
    public Map<Category, Long> getEntrateMap() {
        return Collections.unmodifiableMap(entrateMap);
    }

    @NonNull
    public Map<Category, Long> getUsciteMap() {
        return Collections.unmodifiableMap(usciteMap);
    }

    public long getTotalEntrateAmount() {
        long sum = 0;
        for (Long l : entrateMap.values()) {
            sum += l;
        }
        return sum;
    }

    public long getTotalUsciteAmount() {
        long sum = 0;
        for (Long l : usciteMap.values()) {
            sum += l;
        }
        return sum;
    }

    @NotNull
    public Map<Category, Long> getBalanceMap() {
        Map<Category, Long> result = new HashMap<>(entrateMap);
        for (Map.Entry<Category, Long> entry : usciteMap.entrySet()) {
            if (result.containsKey(entry.getKey())) {
                long newVal = result.get(entry.getKey()) - entry.getValue();
                result.put(entry.getKey(), newVal);
            } else {
                result.put(entry.getKey(), -entry.getValue());
            }
        }
        ArrayList<Map.Entry<Category, Long>> entriesList = new ArrayList<>(result.entrySet());
        Collections.sort(entriesList, (categoryLongEntry, t1) -> -Long.compare(categoryLongEntry.getValue(), t1.getValue()));
        LinkedHashMap<Category, Long> ret = new LinkedHashMap<>();
        for (Map.Entry<Category, Long> w : entriesList) {
            ret.put(w.getKey(), w.getValue());
        }
        return ret;

    }

    public long getBalance() {
        return getTotalEntrateAmount() - getTotalUsciteAmount();
    }

    @NotNull
    public static EnteSummary getInstance(@NotNull Context context, AnagraficheExtended anagraficheExtended, @NotNull Ente ente) {
        try (Cursor cursor = Database.getInstance(context).getReadableDatabase().rawQuery(
                "SELECT  o.tipo, cg.category, SUM(o.amount)" +
                        "FROM Operazione o " +
                        "INNER JOIN CodiceGestionale cg ON o.codiceGestionale = cg.id " +
                        "WHERE ente=? " +
                        "GROUP BY o.tipo, cg.category",
                new Long[]{anagraficheExtended.getIdEnte(ente)})) {

            final Map<Category, Long> entrateMap = new HashMap<>(cursor.getCount() / 2);
            final Map<Category, Long> usciteMap = new HashMap<>(cursor.getCount() / 2);
            while (cursor.moveToNext()) {
                final Map<Category, Long> map;
                switch (cursor.getInt(0)) {
                    case Database.TIPO_OPERAZIONE_ENTRATA:
                        map = entrateMap;
                        break;
                    case Database.TIPO_OPERAZIONE_USCITA:
                        map = usciteMap;
                        break;
                    default:
                        throw new IllegalStateException("Invalid operazione of type " + cursor.getString(0));
                }
                map.put(Category.getInstance(cursor.getInt(1)), cursor.getLong(2));
            }
            return new EnteSummary(ente, entrateMap, usciteMap);
        }
    }
}
