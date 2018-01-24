package mama.pluto.database;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.util.SparseArray;

import com.github.mmauro94.siopeDownloader.datastruct.AutoMap;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Anagrafiche;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.CodiceGestionale;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.CodiceGestionaleEntrate;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.CodiceGestionaleUscite;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Comparto;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Comune;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Ente;
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
import java.util.Iterator;
import java.util.List;

import io.requery.android.database.sqlite.SQLiteDatabase;
import io.requery.android.database.sqlite.SQLiteOpenHelper;
import io.requery.android.database.sqlite.SQLiteStatement;
import mama.pluto.utils.Function;

public class Database extends SQLiteOpenHelper {

    public static final String NAME = "pluto.db";
    public static final int VERSION = 1;
    public static final String TIPO_CODICE_GESTIONALE_ENTRATA = "ENTRATA";
    public static final String TIPO_CODICE_GESTIONALE_USCITA = "USCITA";
    public static final String TIPO_ENTE_ENTRATA = "ENTRATA";
    public static final String TIPO_ENTE_USCITA = "USCITA";
    public static final int MAX_BINDINGS_PER_QUERY = 999;
    public static final int OPTIMAL_TRANSACTION_SIZE = 1000;

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
        db.execSQL("CREATE TABLE Provincia (" +
                "codice INTEGER NOT NULL PRIMARY KEY," +
                "nome TEXT NOT NULL," +
                "regione INTEGER NOT NULL REFERENCES Regione(codice)" +
                ")");
        db.execSQL("CREATE TABLE Comune (" +
                "codice INTEGER NOT NULL," +
                "provincia INTEGER NOT NULL REFERENCES Provincia(codice)," +
                "nome TEXT NOT NULL," +
                "PRIMARY KEY (codice,provincia)" +
                ")");
        db.execSQL("CREATE TABLE Comparto (" +
                "codice TEXT NOT NULL PRIMARY KEY," +
                "nome TEXT NOT NULL" +
                ")");
        db.execSQL("CREATE TABLE Sottocomparto (" +
                "codice TEXT NOT NULL PRIMARY KEY," +
                "nome TEXT NOT NULL," +
                "comparto TEXT NOT NULL REFERENCES Comparto(codice)" +
                ")");
        db.execSQL("CREATE TABLE Ente (" +
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
        db.execSQL("CREATE TABLE CodiceGestionale (" +
                "codice TEXT NOT NULL," +
                "tipo TEXT NOT NULL," +
                "nome TEXT NOT NULL," +
                "comparto TEXT NOT NULL REFERENCES Comparto(codice), " +
                "inizioValidita INTEGER NOT NULL," +
                "fineValidita INTEGER," +
                "PRIMARY KEY (codice, tipo)" +
                ")");

        //DATI
        db.execSQL("CREATE TABLE Operazione (" +
                "tipo TEXT NOT NULL," +
                "codiceGestionale_codice TEXT NOT NULL," +
                "codiceGestionale_tipo TEXT NOT NULL," +
                "ente TEXT NOT NULL REFERENCES Ente(codice)," +
                "year INTEGER NOT NULL," +
                "month INTEGER NOT NULL," +
                "amount INTEGER NOT NULL," +
                "PRIMARY KEY (tipo, codiceGestionale_codice, codiceGestionale_tipo, ente, year, month)," +
                "FOREIGN KEY (codiceGestionale_codice, codiceGestionale_tipo) REFERENCES CodiceGestionale(codice, tipo)" +
                ")");

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

    public void saveAnagrafiche(@NotNull Anagrafiche anagrafiche, @NotNull OnProgressListener onProgressListener) {
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

        saveEnti(anagrafiche.getEnti());
        onProgressListener.onProgress(.8f);

        saveCodiciGestionali(anagrafiche.getCodiciGestionaliEntrate());
        onProgressListener.onProgress(.9f);

        saveCodiciGestionali(anagrafiche.getCodiciGestionaliUscite());
        onProgressListener.onProgress(1f);
    }

    private interface Binder<T> {
        void bind(SQLiteStatement stmt, T obj, int startBind);
    }

    private <T> void save(@NotNull Iterable<T> collection, @NotNull Binder<T> bindValues, @NotNull String tableName, @NotNull String... columns) {
        int maxBatchSize = getMaxBatchSize(columns.length);

        SparseArray<SQLiteStatement> statements = new SparseArray<>(2);
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.beginTransaction();
            int transactionBatch = 0;
            List<T> batch = new ArrayList<>(maxBatchSize);
            Iterator<T> iterator = collection.iterator();

            while (iterator.hasNext()) {
                batch.clear();
                while (batch.size() < maxBatchSize && iterator.hasNext()) {
                    batch.add(iterator.next());
                }
                final int batchSize = batch.size();
                SQLiteStatement stmt = statements.get(batchSize);
                if (stmt == null) {
                    stmt = db.compileStatement(insertQuery(tableName, batchSize, columns));
                    statements.put(batchSize, stmt);
                }
                for (int i = 0; i < batchSize; i++) {
                    bindValues.bind(stmt, batch.get(i), i * columns.length);
                }
                transactionBatch++;
                stmt.executeInsert();
                if (transactionBatch % OPTIMAL_TRANSACTION_SIZE == 0) {
                    db.setTransactionSuccessful();
                    db.endTransaction();
                    db.beginTransaction();
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            for (int i = 0; i < statements.size(); i++) {
                statements.valueAt(i).close();
            }
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

    public void saveRipartizioneGeografiche(@NotNull Collection<RipartizioneGeografica> ripartizioniGeografiche) {
        save(ripartizioniGeografiche, (stmt, item, start) -> stmt.bindString(start + 1, item.getNome()), "RipartizioneGeografica", "nome");
    }

    public void saveRegioni(@NotNull Collection<Regione> regioni) {
        save(regioni, (stmt, item, start) -> {
            stmt.bindLong(start + 1, item.getCodice());
            stmt.bindString(start + 2, item.getNome());
            stmt.bindString(start + 3, item.getRipartizioneGeografica().getNome());
        }, "Regione", "codice", "nome", "ripartizioneGeografica");
    }

    public void saveProvincie(@NotNull Collection<Provincia> provincie) {
        save(provincie, (stmt, item, start) -> {
            stmt.bindLong(start + 1, item.getCodice());
            stmt.bindString(start + 2, item.getNome());
            stmt.bindLong(start + 3, item.getRegione().getCodice());
        }, "Provincia", "codice", "nome", "regione");
    }

    public void saveComuni(@NotNull Collection<Comune> comuni) {
        save(comuni, (stmt, item, start) -> {
            stmt.bindLong(start + 1, item.getComuneId().getCodice());
            stmt.bindLong(start + 2, item.getProvincia().getCodice());
            stmt.bindString(start + 3, item.getNome());
        }, "Comune", "codice", "provincia", "nome");
    }

    public void saveComparti(@NotNull Collection<Comparto> comparti) {
        save(comparti, (stmt, item, start) -> {
            stmt.bindString(start + 1, item.getCodice());
            stmt.bindString(start + 2, item.getNome());
        }, "Comparto", "codice", "nome");
    }

    public void saveSottocomparti(@NotNull Collection<Sottocomparto> sottocomparti) {
        save(sottocomparti, (stmt, item, start) -> {
            stmt.bindString(start + 1, item.getCodice());
            stmt.bindString(start + 2, item.getNome());
            stmt.bindString(start + 3, item.getComparto().getCodice());
        }, "Sottocomparto", "codice", "nome", "comparto");
    }

    public void saveEnti(@NotNull Collection<Ente> enti) {
        save(enti, (stmt, item, start) -> {
            stmt.bindString(start + 1, item.getCodice());
            stmt.bindLong(start + 2, item.getDataInclusione().getTime());
            if (item.hasDataEsclusione()) {
                stmt.bindLong(start + 3, item.getDataEsclusione().getTime());
            } else {
                stmt.bindNull(start + 3);
            }
            if (item.getCodiceFiscale() != null) {
                stmt.bindString(start + 4, item.getCodiceFiscale());
            } else {
                stmt.bindNull(start + 4);
            }
            stmt.bindString(start + 5, item.getNome());
            stmt.bindLong(start + 6, item.getComune().getComuneId().getCodice());
            stmt.bindLong(start + 7, item.getComune().getComuneId().getProvincia().getCodice());
            if (item.hasNumeroAbitanti()) {
                stmt.bindLong(start + 8, item.getNumeroAbitanti());
            } else {
                stmt.bindNull(start + 8);
            }
            stmt.bindString(start + 9, item.getSottocomparto().getCodice());
        }, "Ente", "codice", "dataInclusione", "dataEsclusione", "codiceFiscale", "nome", "comune_codice", "provincia_codice", "numeroAbitanti", "sottocomparto");
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

    public void saveCodiciGestionali(@NotNull Collection<? extends CodiceGestionale> codiciGestionali) {
        save(codiciGestionali, (stmt, item, start) -> {
            stmt.bindString(start + 1, item.getCodice());
            stmt.bindString(start + 2, getTipoCodiceGestionale(item));
            stmt.bindString(start + 3, item.getNome());
            stmt.bindString(start + 4, item.getComparto().getCodice());
            stmt.bindLong(start + 5, item.getInizioValidita().getTime());
            if (item.getFineValidita() != null) {
                stmt.bindLong(start + 6, item.getFineValidita().getTime());
            } else {
                stmt.bindNull(start + 6);
            }

        }, "CodiceGestionale", "codice", "tipo", "nome", "comparto", "inizioValidita", "fineValidita");
    }

    public static String getTipoOperazione(@NotNull Operazione<?> operazione) {
        if (operazione instanceof Entrata) {
            return TIPO_ENTE_ENTRATA;
        } else if (operazione instanceof Uscita) {
            return TIPO_ENTE_USCITA;
        } else {
            throw new IllegalStateException();
        }
    }

    public void saveOperazioni(@NotNull Iterable<? extends Operazione> operazioni) {
        save(operazioni, (stmt, item, start) -> {
            stmt.bindString(start + 1, getTipoOperazione(item));
            stmt.bindString(start + 2, item.getCodiceGestionale().getCodice());
            stmt.bindString(start + 3, getTipoCodiceGestionale(item.getCodiceGestionale()));
            stmt.bindString(start + 4, item.getEnte().getCodice());
            stmt.bindLong(start + 5, item.getYear());
            stmt.bindLong(start + 6, item.getMonth());
            stmt.bindLong(start + 7, item.getAmount());
        }, "Operazione", "tipo", "codiceGestionale_codice", "codiceGestionale_tipo", "ente", "year", "month", "amount");
    }

    private <K, V, R extends AutoMap<K, V>> R loadMap(@NotNull String query, @Nullable String[] selectionArgs, @NotNull R map, @NotNull Function<Cursor, V> f) {
        try (Cursor c = getReadableDatabase().rawQuery(query, selectionArgs)) {
            while (c.moveToNext()) {
                map.put(f.apply(c));
            }
        }
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

    @NotNull
    public Ente.Map loadEnti(@NotNull Comune.Map comuni, @NotNull Provincia.Map provincie, @NotNull Sottocomparto.Map sottocomparti) {
        return loadMap("SELECT codice, dataInclusione, dataEsclusione, codiceFiscale, nome, comune_codice, provincia_codice, numeroAbitanti, sottocomparto FROM Ente", null, new Ente.Map(), c ->
                new Ente(
                        c.getString(0),
                        new Date(c.getLong(1)),
                        c.isNull(2) ? null : new Date(c.getLong(2)),
                        c.isNull(3) ? null : c.getString(3),
                        c.getString(4),
                        comuni.get(new Comune.ComuneId(c.getInt(5), provincie.get(c.getInt(6)))),
                        c.isNull(7) ? null : c.getInt(7),
                        sottocomparti.get(c.getString(8))
                )
        );
    }

    public interface CodiceGestionaleConstructor<T extends CodiceGestionale> {
        T construct(@NotNull String codice, @NotNull String name, @NotNull Comparto comparto, @NotNull Date inizioValidita, @Nullable Date fineValidita);
    }

    @NotNull
    public <T extends CodiceGestionale, R extends CodiceGestionale.Map<T>> R loadCodiciGestionali(@NotNull Comparto.Map comparti, @NotNull String tipo, @NotNull R map, @NotNull CodiceGestionaleConstructor<T> codiceGestionaleConstructor) {
        return loadMap("SELECT codice, nome, comparto, inizioValidita, fineValidita FROM CodiceGestionale WHERE tipo=?", new String[]{tipo}, map, c ->
                codiceGestionaleConstructor.construct(
                        c.getString(0),
                        c.getString(1),
                        comparti.get(c.getString(2)),
                        new Date(c.getLong(3)),
                        c.isNull(4) ? null : new Date(c.getLong(4))
                )
        );
    }

    @NotNull
    public CodiceGestionaleEntrate.Map loadCodiciGestionaliEntrate(@NotNull Comparto.Map comparti) {
        return loadCodiciGestionali(comparti, TIPO_CODICE_GESTIONALE_ENTRATA, new CodiceGestionaleEntrate.Map(), CodiceGestionaleEntrate::new);
    }

    @NotNull
    public CodiceGestionaleUscite.Map loadCodiciGestionaliUscite(@NotNull Comparto.Map comparti) {
        return loadCodiciGestionali(comparti, TIPO_CODICE_GESTIONALE_USCITA, new CodiceGestionaleUscite.Map(), CodiceGestionaleUscite::new);
    }

    @NotNull
    public Anagrafiche loadAnagrafiche(@NotNull OnProgressListener progressListener) {
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

        final Ente.Map enti = loadEnti(comuni, provincie, sottocomparti);
        progressListener.onProgress(.8f);

        final CodiceGestionaleEntrate.Map codiciGestionaliEntrate = loadCodiciGestionaliEntrate(comparti);
        progressListener.onProgress(.9f);

        final CodiceGestionaleUscite.Map codiciGestionaliUscite = loadCodiciGestionaliUscite(comparti);
        progressListener.onProgress(1f);

        return new Anagrafiche(comparti, sottocomparti, ripartizioneGeografiche, regioni, provincie, comuni, enti, codiciGestionaliEntrate, codiciGestionaliUscite);
    }
}
