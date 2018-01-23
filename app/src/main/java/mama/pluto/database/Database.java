package mama.pluto.database;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;

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

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import io.requery.android.database.sqlite.SQLiteDatabase;
import io.requery.android.database.sqlite.SQLiteOpenHelper;
import io.requery.android.database.sqlite.SQLiteStatement;
import mama.pluto.utils.BiConsumer;
import mama.pluto.utils.Function;
import mama.pluto.utils.StringUtils;

public class Database extends SQLiteOpenHelper {

    public static final String NAME = "pluto.db";
    public static final int VERSION = 1;
    public static final String TIPO_CODICE_GESTIONALE_ENTRATA = "ENTRATA";
    public static final String TIPO_CODICE_GESTIONALE_USCITA = "USCITA";
    public static final String TIPO_ENTE_ENTRATA = "ENTRATA";
    public static final String TIPO_ENTE_USCITA = "USCITA";

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
                "amount TEXT NOT NULL," +
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

    private <T> void save(@NotNull Iterable<T> collection, @NotNull String query, @NotNull BiConsumer<SQLiteStatement, T> bindValues) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try (SQLiteStatement stmt = db.compileStatement(query)) {
            for (T t : collection) {
                bindValues.consume(stmt, t);
                stmt.execute();
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    @NonNull
    private static String insertQuery(@NotNull String tableName, @NotNull String... columns) {
        final String[] values = new String[columns.length];
        Arrays.fill(values, "?");

        return "REPLACE INTO " + tableName + " (" + StringUtils.join(", ", columns) + ") VALUES (" + StringUtils.join(", ", values) + ")";
    }

    public void saveRipartizioneGeografiche(@NotNull Collection<RipartizioneGeografica> ripartizioniGeografiche) {
        save(ripartizioniGeografiche, insertQuery("RipartizioneGeografica", "nome"), (stmt, item) -> stmt.bindString(1, item.getNome()));
    }

    public void saveRegioni(@NotNull Collection<Regione> regioni) {
        save(regioni, insertQuery("Regione", "codice", "nome", "ripartizioneGeografica"), (stmt, item) -> {
            stmt.bindLong(1, item.getCodice());
            stmt.bindString(2, item.getNome());
            stmt.bindString(3, item.getRipartizioneGeografica().getNome());
        });
    }

    public void saveProvincie(@NotNull Collection<Provincia> provincie) {
        save(provincie, insertQuery("Provincia", "codice", "nome", "regione"), (stmt, item) -> {
            stmt.bindLong(1, item.getCodice());
            stmt.bindString(2, item.getNome());
            stmt.bindLong(3, item.getRegione().getCodice());
        });
    }

    public void saveComuni(@NotNull Collection<Comune> comuni) {
        save(comuni, insertQuery("Comune", "codice", "provincia", "nome"), (stmt, item) -> {
            stmt.bindLong(1, item.getComuneId().getCodice());
            stmt.bindLong(2, item.getProvincia().getCodice());
            stmt.bindString(3, item.getNome());
        });
    }

    public void saveComparti(@NotNull Collection<Comparto> comparti) {
        save(comparti, insertQuery("Comparto", "codice", "nome"), (stmt, item) -> {
            stmt.bindString(1, item.getCodice());
            stmt.bindString(2, item.getNome());
        });
    }

    public void saveSottocomparti(@NotNull Collection<Sottocomparto> sottocomparti) {
        save(sottocomparti, insertQuery("Sottocomparto", "codice", "nome", "comparto"), (stmt, item) -> {
            stmt.bindString(1, item.getCodice());
            stmt.bindString(2, item.getNome());
            stmt.bindString(3, item.getComparto().getCodice());
        });
    }

    public void saveEnti(@NotNull Collection<Ente> enti) {
        save(enti, insertQuery("Ente", "codice", "dataInclusione", "dataEsclusione", "codiceFiscale", "nome", "comune_codice", "provincia_codice", "numeroAbitanti", "sottocomparto"), (stmt, item) -> {
            stmt.bindString(1, item.getCodice());
            stmt.bindLong(2, item.getDataInclusione().getTime());
            if (item.hasDataEsclusione()) {
                stmt.bindLong(3, item.getDataEsclusione().getTime());
            } else {
                stmt.bindNull(3);
            }
            if (item.getCodiceFiscale() != null) {
                stmt.bindString(4, item.getCodiceFiscale());
            } else {
                stmt.bindNull(4);
            }
            stmt.bindString(5, item.getNome());
            stmt.bindLong(6, item.getComune().getComuneId().getCodice());
            stmt.bindLong(7, item.getComune().getComuneId().getProvincia().getCodice());
            if (item.hasNumeroAbitanti()) {
                stmt.bindLong(8, item.getNumeroAbitanti());
            } else {
                stmt.bindNull(8);
            }
            stmt.bindString(9, item.getSottocomparto().getCodice());
        });
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
        save(codiciGestionali, insertQuery("CodiceGestionale", "codice", "tipo", "nome", "comparto", "inizioValidita", "fineValidita"), (stmt, item) -> {
            stmt.bindString(1, item.getCodice());
            stmt.bindString(2, getTipoCodiceGestionale(item));
            stmt.bindString(3, item.getNome());
            stmt.bindString(4, item.getComparto().getCodice());
            stmt.bindLong(5, item.getInizioValidita().getTime());
            if (item.getFineValidita() != null) {
                stmt.bindLong(6, item.getFineValidita().getTime());
            } else {
                stmt.bindNull(6);
            }

        });
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

    public void saveOperazioni(@NotNull Iterable<Operazione> operazioni) {
        save(operazioni, insertQuery("Operazione", "tipo", "codiceGestionale_codice", "codiceGestionale_tipo", "ente", "year", "month", "amount"), (stmt, item) -> {
            stmt.bindString(1, getTipoOperazione(item));
            stmt.bindString(2, item.getCodiceGestionale().getCodice());
            stmt.bindString(3, getTipoCodiceGestionale(item.getCodiceGestionale()));
            stmt.bindString(4, item.getEnte().getCodice());
            stmt.bindLong(5, item.getYear());
            stmt.bindLong(6, item.getMonth());
            stmt.bindString(7, item.getAmount().toPlainString());

        });
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
