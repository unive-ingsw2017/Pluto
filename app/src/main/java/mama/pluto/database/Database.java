package mama.pluto.database;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.SparseArray;

import com.github.mmauro94.siopeDownloader.datastruct.AutoMap;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Anagrafiche;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.CodiceGestionale;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.CodiceGestionaleEntrate;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.CodiceGestionaleUscite;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Comparto;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Comune;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Ente;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.GeoItem;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Provincia;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Regione;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.RipartizioneGeografica;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Sottocomparto;
import com.github.mmauro94.siopeDownloader.datastruct.operazioni.Entrata;
import com.github.mmauro94.siopeDownloader.datastruct.operazioni.Operazione;
import com.github.mmauro94.siopeDownloader.datastruct.operazioni.Uscita;
import com.github.mmauro94.siopeDownloader.utils.OnProgressListener;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.requery.android.database.sqlite.SQLiteBindableLong;
import io.requery.android.database.sqlite.SQLiteDatabase;
import io.requery.android.database.sqlite.SQLiteOpenHelper;
import io.requery.android.database.sqlite.SQLiteStatement;
import mama.pluto.dataAbstraction.AnagraficheExtended;
import mama.pluto.dataAbstraction.CategoryUtils;
import mama.pluto.dataAbstraction.DataUtils;
import mama.pluto.utils.BiConsumer;
import mama.pluto.utils.Function;
import mama.pluto.utils.Pair;

public class Database extends SQLiteOpenHelper {

    public static final String NAME = "pluto.db";
    public static final int VERSION = 1;
    public static final String TIPO_CODICE_GESTIONALE_ENTRATA = "ENTRATA";
    public static final String TIPO_CODICE_GESTIONALE_USCITA = "USCITA";
    public static final int TIPO_OPERAZIONE_ENTRATA = 0;
    public static final int TIPO_OPERAZIONE_USCITA = 1;
    public static final int MAX_BINDINGS_PER_QUERY = 999;
    public static final int OPTIMAL_TRANSACTION_SIZE = 20_000;

    private Database(@NotNull Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //ANAGRAFICHE
        db.execSQL("CREATE TABLE RipartizioneGeografica (nome TEXT NOT NULL PRIMARY KEY)");
        db.execSQL("CREATE TABLE Regione (" +
                "codice INTEGER NOT NULL PRIMARY KEY," +
                "nome TEXT NOT NULL," +
                "ripartizioneGeografica TEXT NOT NULL REFERENCES RipartizioneGeografica(nome)" +
                ")");
        db.execSQL("CREATE INDEX regione_ripartizioneGeografica ON Regione(ripartizioneGeografica)");
        db.execSQL("CREATE TABLE Provincia (" +
                "codice INTEGER NOT NULL PRIMARY KEY," +
                "nome TEXT NOT NULL," +
                "regione INTEGER NOT NULL REFERENCES Regione(codice)" +
                ")");
        db.execSQL("CREATE INDEX provincia_regione ON Provincia(regione)");
        db.execSQL("CREATE TABLE Comune (" +
                "codice INTEGER NOT NULL," +
                "provincia INTEGER NOT NULL REFERENCES Provincia(codice)," +
                "nome TEXT NOT NULL," +
                "PRIMARY KEY (codice,provincia)" +
                ")");
        db.execSQL("CREATE INDEX comune_provincia ON Comune(provincia)");
        db.execSQL("CREATE TABLE Comparto (" +
                "codice TEXT NOT NULL PRIMARY KEY," +
                "nome TEXT NOT NULL" +
                ")");
        db.execSQL("CREATE TABLE Sottocomparto (" +
                "codice TEXT NOT NULL PRIMARY KEY," +
                "nome TEXT NOT NULL," +
                "comparto TEXT NOT NULL REFERENCES Comparto(codice)" +
                ")");
        db.execSQL("CREATE INDEX sottocomparto_comparto ON Sottocomparto(comparto)");
        db.execSQL("CREATE TABLE Ente (" +
                "id INTEGER PRIMARY KEY," +
                "codice TEXT NOT NULL," +
                "dataInclusione INTEGER NOT NULL," +
                "dataEsclusione INTEGER," +
                "codiceFiscale TEXT," +
                "nome TEXT NOT NULL," +
                "comune_codice INTEGER NOT NULL," +
                "provincia_codice INTEGER NOT NULL," +
                "numeroAbitanti INTEGER," +
                "sottocomparto TEXT NOT NULL REFERENCES Sottocomparto(codice)," +
                "FOREIGN KEY (comune_codice, provincia_codice) REFERENCES Comune(codice, provincia)" +
                ")");
        db.execSQL("CREATE INDEX ente_codice ON Ente(codice)");
        db.execSQL("CREATE INDEX ente_sottocomparto ON Ente(sottocomparto)");
        db.execSQL("CREATE INDEX ente_codici ON Ente(comune_codice, provincia_codice)");
        db.execSQL("CREATE TABLE CodiceGestionale (" +
                "id INTEGER PRIMARY KEY," +
                "codice TEXT NOT NULL," +
                "tipo TEXT NOT NULL," +
                "nome TEXT NOT NULL," +
                "comparto TEXT NOT NULL REFERENCES Comparto(codice), " +
                "inizioValidita INTEGER NOT NULL," +
                "fineValidita INTEGER," +
                "category INTEGER NOT NULL," +
                "CONSTRAINT codice_tipo UNIQUE(codice,tipo)" +
                ")");
        db.execSQL("CREATE INDEX codiceGestionale_comparto ON CodiceGestionale(comparto)");

        //DATI
        db.execSQL("CREATE TABLE Operazione (" +
                "tipo INTEGER NOT NULL," +
                "codiceGestionale INTEGER NOT NULL REFERENCES CodiceGestionale(id)," +
                "ente INTEGER NOT NULL REFERENCES Ente(id)," +
                "year INTEGER NOT NULL," +
                "month INTEGER NOT NULL," +
                "amount INTEGER NOT NULL" +
                ")");
        db.execSQL("CREATE INDEX operazione_ente ON Operazione(ente)");
        db.execSQL("CREATE INDEX operazione_codice_gestionale ON Operazione(codiceGestionale)");
    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    @Nullable
    private static Database database;

    public static Database getInstance(@NotNull Context context) {
        if (database == null) {
            database = new Database(context.getApplicationContext());
        }
        return database;
    }

    /**
     * @return the Mpa of generated Ids for CodiceGestionale
     */
    public AnagraficheExtended saveAnagrafiche(@NotNull Anagrafiche anagrafiche, @NotNull OnProgressListener onProgressListener) {
        final AnagraficheExtended.Builder builder = new AnagraficheExtended.Builder();

        onProgressListener.onProgress(0f);

        saveRipartizioneGeografiche(anagrafiche.getRipartizioniGeografiche());
        onProgressListener.onProgress(.1f);

        saveRegioni(anagrafiche.getRegioni());
        onProgressListener.onProgress(.2f);

        saveProvincie(anagrafiche.getProvincie());
        onProgressListener.onProgress(.3f);

        saveComuni(anagrafiche.getComuni());
        onProgressListener.onProgress(.4f);

        saveComparti(anagrafiche.getComparti());
        onProgressListener.onProgress(.5f);

        saveSottocomparti(anagrafiche.getSottocomparti());
        onProgressListener.onProgress(.6f);

        saveEnti(anagrafiche.getEnti(), builder);
        onProgressListener.onProgress(.8f);

        saveCodiciGestionali(anagrafiche.getCodiciGestionaliEntrate(), builder);
        onProgressListener.onProgress(.9f);

        saveCodiciGestionali(anagrafiche.getCodiciGestionaliUscite(), builder);
        onProgressListener.onProgress(1f);
        return builder.build(anagrafiche);
    }

    private interface Binder<T> {
        void bind(T obj, Object[] toFill);
    }

    private <T> void save(@NotNull Iterable<T> collection, @NotNull Binder<T> bindValues, @NotNull String tableName, @Nullable BiConsumer<Long, T> onItemSaved, @NotNull String... columns) {
        save(getWritableDatabase(), collection, bindValues, tableName, onItemSaved, columns);
    }

    private <T> void save(@NotNull Iterable<T> collection, @NotNull Binder<T> bindValues, @NotNull String tableName, @NotNull String... columns) {
        save(getWritableDatabase(), collection, bindValues, tableName, columns);
    }

    private <T> void save(@NotNull SQLiteDatabase writableDb, @NotNull Iterable<T> collection, @NotNull Binder<T> bindValues, @NotNull String tableName, @NotNull String... columns) {
        save(writableDb, collection, bindValues, tableName, null, columns);
    }

    private <T> void save(@NotNull SQLiteDatabase writableDb, @NotNull Iterable<T> collection, @NotNull Binder<T> bindValues, @NotNull String tableName, @Nullable BiConsumer<Long, T> onItemSaved, @NotNull String... columns) {
        long now = System.currentTimeMillis();
        final int maxBatchSize = onItemSaved != null ? 1 : getMaxBatchSize(columns.length);//TO obtain the id, the query must insert only one row at a time

        final SparseArray<SQLiteStatement> statements = new SparseArray<>(2);
        try {
            writableDb.beginTransaction();
            int transactionBatch = 0;
            List<T> batch = new ArrayList<>(maxBatchSize);
            Iterator<T> iterator = collection.iterator();
            Object[][] bindings = new Object[maxBatchSize][columns.length];

            while (iterator.hasNext()) {
                batch.clear();
                while (batch.size() < maxBatchSize && iterator.hasNext()) {
                    batch.add(iterator.next());
                }
                final int batchSize = batch.size();
                SQLiteStatement stmt = statements.get(batchSize);
                if (stmt == null) {
                    stmt = writableDb.compileStatement(insertQuery(tableName, batchSize, columns));
                    statements.put(batchSize, stmt);
                }
                for (int i = 0; i < batchSize; i++) {
                    bindValues.bind(batch.get(i), bindings[i]);
                    for (int j = 0; j < bindings[i].length; j++) {
                        stmt.bindObject(1 + i * columns.length + j, bindings[i][j]);
                    }
                }
                transactionBatch++;
                long id = stmt.executeInsert();
                if (onItemSaved != null) {
                    onItemSaved.consume(id, batch.get(0));
                }
                if (transactionBatch % OPTIMAL_TRANSACTION_SIZE == 0) {
                    writableDb.setTransactionSuccessful();
                    writableDb.endTransaction();
                    writableDb.beginTransaction();
                }
            }
            writableDb.setTransactionSuccessful();
        } finally {
            writableDb.endTransaction();
            for (int i = 0; i < statements.size(); i++) {
                statements.valueAt(i).close();
            }
            System.out.println("Saving on table " + tableName + " took " + (System.currentTimeMillis() - now) / 1000f + "s");
        }
    }

    private static int getMaxBatchSize(int bindingsPerRow) {
        return MAX_BINDINGS_PER_QUERY / bindingsPerRow;
    }

    @NonNull
    private static String insertQuery(@NotNull String tableName, @NotNull String... columns) {
        return insertQuery(tableName, 1, columns);
    }

    @NonNull
    private static String insertQuery(@NotNull String tableName, int batchSize, @NotNull String... columns) {
        if (batchSize * columns.length > MAX_BINDINGS_PER_QUERY) {
            throw new IllegalArgumentException("The number of bindings cannot be bigger than " + MAX_BINDINGS_PER_QUERY);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("REPLACE INTO ").append(tableName).append(" (");
        for (int i = 0; i < columns.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(columns[i]);
        }
        sb.append(") VALUES ");
        for (int i = 0; i < batchSize; i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append("(");
            for (int j = 0; j < columns.length; j++) {
                if (j > 0) {
                    sb.append(",");
                }
                sb.append("?");
            }
            sb.append(")");
        }
        return sb.toString();
    }

    private static final class LongWrapper implements SQLiteBindableLong {
        private long l;

        public LongWrapper(long l) {
            this.l = l;
        }

        public void setLong(long l) {
            this.l = l;
        }

        @Override
        public long getLong() {
            return l;
        }
    }

    private static void bindNumber(Object[] toFill, int index, long toBind) {
        if (toFill[index] instanceof LongWrapper) {
            ((LongWrapper) toFill[index]).setLong(toBind);
        } else {
            toFill[index] = new LongWrapper(toBind);
        }
    }

    public void saveRipartizioneGeografiche(@NotNull Collection<RipartizioneGeografica> ripartizioniGeografiche) {
        save(ripartizioniGeografiche, (item, toFill) -> toFill[0] = item.getNome(), "RipartizioneGeografica", "nome");
    }

    public void saveRegioni(@NotNull Collection<Regione> regioni) {
        save(regioni, (item, toFill) -> {
            toFill[0] = item.getCodice();
            toFill[1] = item.getNome();
            toFill[2] = item.getRipartizioneGeografica().getNome();
        }, "Regione", "codice", "nome", "ripartizioneGeografica");
    }

    public void saveProvincie(@NotNull Collection<Provincia> provincie) {
        save(provincie, (item, toFill) -> {
            toFill[0] = item.getCodice();
            toFill[1] = item.getNome();
            toFill[2] = item.getRegione().getCodice();
        }, "Provincia", "codice", "nome", "regione");
    }

    public void saveComuni(@NotNull Collection<Comune> comuni) {
        save(comuni, (item, toFill) -> {
            toFill[0] = item.getComuneId().getCodice();
            toFill[1] = item.getProvincia().getCodice();
            toFill[2] = item.getNome();
        }, "Comune", "codice", "provincia", "nome");
    }

    public void saveComparti(@NotNull Collection<Comparto> comparti) {
        save(comparti, (item, toFill) -> {
            toFill[0] = item.getCodice();
            toFill[1] = item.getNome();
        }, "Comparto", "codice", "nome");
    }

    public void saveSottocomparti(@NotNull Collection<Sottocomparto> sottocomparti) {
        save(sottocomparti, (item, toFill) -> {
            toFill[0] = item.getCodice();
            toFill[1] = item.getNome();
            toFill[2] = item.getComparto().getCodice();
        }, "Sottocomparto", "codice", "nome", "comparto");
    }

    public void saveEnti(@NotNull Collection<Ente> enti, AnagraficheExtended.Builder toFillWithIds) {
        save(enti, (item, toFill) -> {
            toFill[0] = item.getCodice();
            toFill[1] = item.getDataInclusione().getTime();
            toFill[2] = item.hasDataEsclusione() ? item.getDataEsclusione().getTime() : null;
            toFill[3] = item.getCodiceFiscale();
            toFill[4] = item.getNome();
            toFill[5] = item.getComune().getComuneId().getCodice();
            toFill[6] = item.getComune().getComuneId().getProvincia().getCodice();
            toFill[7] = item.hasNumeroAbitanti() ? item.getNumeroAbitanti() : null;
            toFill[8] = item.getSottocomparto().getCodice();
        }, "Ente", toFillWithIds::put, "codice", "dataInclusione", "dataEsclusione", "codiceFiscale", "nome", "comune_codice", "provincia_codice", "numeroAbitanti", "sottocomparto");
    }

    public static String getTipoCodiceGestionale(@NotNull CodiceGestionale codiceGestionale) {
        if (codiceGestionale instanceof CodiceGestionaleEntrate) {
            return TIPO_CODICE_GESTIONALE_ENTRATA;
        } else if (codiceGestionale instanceof CodiceGestionaleUscite) {
            return TIPO_CODICE_GESTIONALE_USCITA;
        } else {
            throw new IllegalStateException();
        }
    }

    public void saveCodiciGestionali(@NotNull Collection<? extends CodiceGestionale> codiciGestionali, AnagraficheExtended.Builder toFillWithIds) {
        save(codiciGestionali, (item, toFill) -> {
            toFill[0] = item.getCodice();
            toFill[1] = getTipoCodiceGestionale(item);
            toFill[2] = item.getNome();
            toFill[3] = item.getComparto().getCodice();
            toFill[4] = item.getInizioValidita().getTime();
            toFill[5] = item.getFineValidita() != null ? item.getFineValidita().getTime() : null;
            toFill[6] = CategoryUtils.getCategory(item).getId();
        }, "CodiceGestionale", toFillWithIds::put, "codice", "tipo", "nome", "comparto", "inizioValidita", "fineValidita", "category");
    }

    public static int getTipoOperazione(@NotNull Operazione<?> operazione) {
        if (operazione instanceof Entrata) {
            return TIPO_OPERAZIONE_ENTRATA;
        } else if (operazione instanceof Uscita) {
            return TIPO_OPERAZIONE_USCITA;
        } else {
            throw new IllegalStateException();
        }
    }

    public void truncateOperazioni() {
        getWritableDatabase().execSQL("DELETE FROM Operazione");
    }

    public void saveOperazioni(@NotNull Iterable<? extends Operazione> operazioni, AnagraficheExtended anagraficheExtended) {
        save(operazioni, (item, toFill) -> {
            bindNumber(toFill, 0, getTipoOperazione(item));
            bindNumber(toFill, 1, anagraficheExtended.getIdCodiceGestionale(item.getCodiceGestionale()));
            bindNumber(toFill, 2, anagraficheExtended.getIdEnte(item.getEnte()));
            bindNumber(toFill, 3, item.getYear());
            bindNumber(toFill, 4, item.getMonth());
            bindNumber(toFill, 5, item.getAmount());
        }, "Operazione", "tipo", "codiceGestionale", "ente", "year", "month", "amount");
    }

    private <K, V, R extends AutoMap<K, V>> R loadMap(@NotNull String query, @Nullable String[] selectionArgs, @NotNull R map, @NotNull Function<Cursor, V> f) {
        long now = System.currentTimeMillis();
        try (Cursor c = getReadableDatabase().rawQuery(query, selectionArgs)) {
            while (c.moveToNext()) {
                map.put(f.apply(c));
            }
        }
        Log.d("SIOPE loading", "Loading " + map.getClass().getName() + " took " + (System.currentTimeMillis() - now) / 1000f + " seconds");
        return map;
    }

    @NotNull
    public RipartizioneGeografica.Map loadRipartizioniGeografiche() {
        return loadMap("SELECT nome FROM RipartizioneGeografica", null, new RipartizioneGeografica.Map(), c -> new RipartizioneGeografica(c.getString(0)));
    }

    @NotNull
    public Regione.Map loadRegioni(@NotNull RipartizioneGeografica.Map ripartizioniGeografiche) {
        return loadMap("SELECT codice, nome, ripartizioneGeografica FROM Regione", null, new Regione.Map(), c ->
                new Regione(
                        c.getInt(0),
                        c.getString(1),
                        ripartizioniGeografiche.get(c.getString(2))
                )
        );
    }

    @NotNull
    public Provincia.Map loadProvincie(@NotNull Regione.Map regioni) {
        return loadMap("SELECT codice, nome, regione FROM Provincia", null, new Provincia.Map(), c ->
                new Provincia(
                        c.getInt(0),
                        c.getString(1),
                        regioni.get(c.getInt(2))
                )
        );
    }

    @NotNull
    public Comune.Map loadComuni(@NotNull Provincia.Map provincie) {
        return loadMap("SELECT codice, provincia, nome FROM Comune", null, new Comune.Map(), c ->
                new Comune(
                        new Comune.ComuneId(c.getInt(0), provincie.get(c.getInt(1))),
                        c.getString(2)
                )
        );
    }

    @NotNull
    public Comparto.Map loadComparti() {
        return loadMap("SELECT codice, nome FROM Comparto", null, new Comparto.Map(), c ->
                new Comparto(
                        c.getString(0),
                        c.getString(1)
                )
        );
    }

    @NotNull
    public Sottocomparto.Map loadSottocomparti(@NotNull Comparto.Map comparti) {
        return loadMap("SELECT codice, nome, comparto FROM Sottocomparto", null, new Sottocomparto.Map(), c ->
                new Sottocomparto(
                        c.getString(0),
                        c.getString(1),
                        comparti.get(c.getString(2))
                )
        );
    }

    public Ente.@NotNull Map loadEnti(@NotNull Comune.Map comuni, @NotNull Provincia.Map provincie, @NotNull Sottocomparto.Map sottocomparti, @NotNull AnagraficheExtended.Builder builder) {
        return loadMap("SELECT id, codice, dataInclusione, dataEsclusione, codiceFiscale, nome, comune_codice, provincia_codice, numeroAbitanti, sottocomparto FROM Ente", null, new Ente.Map(), c -> {
                    Ente ret = new Ente(
                            c.getString(1),
                            new Date(c.getLong(2)),
                            c.isNull(3) ? null : new Date(c.getLong(3)),
                            c.isNull(4) ? null : c.getString(4),
                            c.getString(5),
                            comuni.get(new Comune.ComuneId(c.getInt(6), provincie.get(c.getInt(7)))),
                            c.isNull(8) ? null : c.getInt(8),
                            sottocomparti.get(c.getString(9))
                    );
                    builder.put(c.getLong(0), ret);
                    return ret;
                }
        );
    }

    public interface CodiceGestionaleConstructor<T extends CodiceGestionale> {
        T construct(@NotNull String codice, @NotNull String name, @NotNull Comparto comparto, @NotNull Date inizioValidita, @Nullable Date fineValidita);
    }

    @NotNull
    public <T extends CodiceGestionale, R extends CodiceGestionale.Map<T>> R loadCodiciGestionali(@NotNull Comparto.Map comparti, @NotNull AnagraficheExtended.Builder toFill, @NotNull String tipo, @NotNull R map, @NotNull CodiceGestionaleConstructor<T> codiceGestionaleConstructor) {
        return loadMap("SELECT id, codice, nome, comparto, inizioValidita, fineValidita FROM CodiceGestionale WHERE tipo=?", new String[]{tipo}, map, c -> {
                    T ret = codiceGestionaleConstructor.construct(
                            c.getString(1),
                            c.getString(2),
                            comparti.get(c.getString(3)),
                            new Date(c.getLong(4)),
                            c.isNull(5) ? null : new Date(c.getLong(5))
                    );
                    toFill.put(c.getLong(0), ret);
                    return ret;
                }
        );
    }

    public CodiceGestionaleEntrate.@NotNull Map loadCodiciGestionaliEntrate(@NotNull Comparto.Map comparti, @NotNull AnagraficheExtended.Builder toFill) {
        return loadCodiciGestionali(comparti, toFill, TIPO_CODICE_GESTIONALE_ENTRATA, new CodiceGestionaleEntrate.Map(), CodiceGestionaleEntrate::new);
    }

    public CodiceGestionaleUscite.@NotNull Map loadCodiciGestionaliUscite(@NotNull Comparto.Map comparti, @NotNull AnagraficheExtended.Builder toFill) {
        return loadCodiciGestionali(comparti, toFill, TIPO_CODICE_GESTIONALE_USCITA, new CodiceGestionaleUscite.Map(), CodiceGestionaleUscite::new);
    }

    @NotNull
    public AnagraficheExtended loadAnagrafiche(@NotNull OnProgressListener progressListener) {
        final AnagraficheExtended.Builder builder = new AnagraficheExtended.Builder();
        progressListener.onProgress(0f);

        final Comparto.Map comparti = loadComparti();
        progressListener.onProgress(.1f);

        final Sottocomparto.Map sottocomparti = loadSottocomparti(comparti);
        progressListener.onProgress(.2f);

        final RipartizioneGeografica.Map ripartizioneGeografiche = loadRipartizioniGeografiche();
        progressListener.onProgress(.3f);

        final Regione.Map regioni = loadRegioni(ripartizioneGeografiche);
        progressListener.onProgress(.4f);

        final Provincia.Map provincie = loadProvincie(regioni);
        progressListener.onProgress(.5f);

        final Comune.Map comuni = loadComuni(provincie);
        progressListener.onProgress(.6f);

        final Ente.Map enti = loadEnti(comuni, provincie, sottocomparti, builder);
        progressListener.onProgress(.8f);

        final CodiceGestionaleEntrate.Map codiciGestionaliEntrate = loadCodiciGestionaliEntrate(comparti, builder);
        progressListener.onProgress(.9f);

        final CodiceGestionaleUscite.Map codiciGestionaliUscite = loadCodiciGestionaliUscite(comparti, builder);
        progressListener.onProgress(1f);

        return builder.build(new Anagrafiche(comparti, sottocomparti, ripartizioneGeografiche, regioni, provincie, comuni, enti, codiciGestionaliEntrate, codiciGestionaliUscite));
    }

    @NotNull
    public Map<Regione, Pair<Long, Long>> getRegioneBalances(@NotNull AnagraficheExtended a) {
        return getBalances(a, DataUtils.SOTTOCOMPARTO_REGIONE, gi -> (Regione) gi);
    }

    @NotNull
    public Map<Provincia, Pair<Long, Long>> getProvinciaBalances(@NotNull AnagraficheExtended a) {
        return getBalances(a, DataUtils.SOTTOCOMPARTO_PROVINCIA, gi -> (Provincia) gi);
    }

    @NotNull
    private <T extends GeoItem> Map<T, Pair<Long, Long>> getBalances(@NotNull AnagraficheExtended a, @NotNull String tipoSottocomparto, @NotNull Function<GeoItem, T> caster) {
        try (Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT e.codice, SUM(CASE o.tipo WHEN ? THEN o.amount ELSE 0 END) AS entrate, SUM(CASE o.tipo WHEN ? THEN o.amount ELSE 0 END) AS uscite " +
                        "FROM Ente e " +
                        "LEFT OUTER JOIN Operazione o ON e.id = o.ente " +
                        "WHERE e.sottocomparto=? " +
                        "GROUP BY e.id " +
                        "HAVING entrate <> 0 OR uscite <> 0", new Object[]{TIPO_OPERAZIONE_ENTRATA, TIPO_OPERAZIONE_USCITA, tipoSottocomparto})) {
            final Map<T, Pair<Long, Long>> map = new HashMap<>();
            while (cursor.moveToNext()) {
                GeoItem gi = DataUtils.getGeoItemOfEnte(a.getEnti().get(cursor.getString(0)));
                map.put(caster.apply(gi), new Pair<>(cursor.getLong(1), cursor.getLong(2)));
            }
            return map;
        }
    }
}
