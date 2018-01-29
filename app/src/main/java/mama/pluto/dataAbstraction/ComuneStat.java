package mama.pluto.dataAbstraction;

import android.content.Context;

import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Comune;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.GeoItem;
import com.github.mmauro94.siopeDownloader.utils.ReaderUtils;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import mama.pluto.utils.Consumer;
import mama.pluto.utils.DoubleMap;

public final class ComuneStat {

    private static final CSVFormat CSV_FORMAT = CSVFormat.newFormat(';')
            .withIgnoreEmptyLines()
            .withFirstRecordAsHeader();

    private static final AtomicBoolean MAP_LOADED = new AtomicBoolean(false);
    private static final DoubleMap<Comune, ComuneStat> MAP = new DoubleMap<>();


    public enum ZonaAltimetrica {
        MONTAGNA_INTERNA("Montagna Interna"),
        MONTAGNA_LITORANEA("Montagna Litoranea"),
        PIANURA("Pianura"),
        COLLINA_INTERNA("Collina Interna"),
        COLLINA_LITORANEA("Collina Litoranea");

        @NotNull
        private final String name;

        ZonaAltimetrica(@NotNull String name) {
            this.name = name;
        }

        @NotNull
        @Contract(pure = true)
        public String getName() {
            return name;
        }

        @NotNull
        public static ZonaAltimetrica fromName(@NotNull String str) {
            for (ZonaAltimetrica za : values()) {
                if (str.equalsIgnoreCase(za.getName())) {
                    return za;
                }
            }
            throw new IllegalArgumentException();
        }
    }

    public enum TipoCapoluogo {
        COMUNE("Capoluogo di Provincia"), PROVINCIA("Capoluogo di Regione"), CAPITALE("Capitale della Repubblica Italiana");

        @NotNull
        private final String name;

        TipoCapoluogo(@NotNull String name) {
            this.name = name;
        }

        @NotNull
        @Contract(pure = true)
        public String getName() {
            return name;
        }

        @Nullable
        public static TipoCapoluogo fromName(@NotNull String str) {
            if (str.equalsIgnoreCase("No capoluogo")) {
                return null;
            }
            for (TipoCapoluogo tc : values()) {
                if (str.equalsIgnoreCase(tc.getName())) {
                    return tc;
                }
            }
            throw new IllegalArgumentException(str);
        }
    }

    public enum GradoUrbanizzazione {
        BASSO("Basso"),
        MEDIO("Medio"),
        ELEVATO("Elevato");

        @NotNull
        private final String name;

        GradoUrbanizzazione(@NotNull String name) {
            this.name = name;
        }

        @NotNull
        @Contract(pure = true)
        public String getName() {
            return name;
        }

        @NotNull
        public static GradoUrbanizzazione fromName(@NotNull String str) {
            for (GradoUrbanizzazione gu : values()) {
                if (str.equalsIgnoreCase(gu.getName())) {
                    return gu;
                }
            }
            throw new IllegalArgumentException();
        }
    }

    public enum IndiceMontanità {
        TOTALMENTE_MONTANO("Totalmente montano"),
        PARZIALMENTE_MONTANO("Parzialmente montano"),
        NON_MONTANO("Non montano");

        @NotNull
        private final String name;

        IndiceMontanità(@NotNull String name) {
            this.name = name;
        }

        @NotNull
        @Contract(pure = true)
        public String getName() {
            return name;
        }

        @NotNull
        public static IndiceMontanità fromName(@NotNull String str) {
            for (IndiceMontanità im : values()) {
                if (str.equalsIgnoreCase(im.getName())) {
                    return im;
                }
            }
            throw new IllegalArgumentException();
        }
    }

    public enum ZonaClimatica {
        A, B, C, D, E, F;

        @Nullable
        public static ZonaClimatica optValueOf(@NotNull String s) {
            try {
                return valueOf(s);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    }

    public enum Classe {
        POLO_DI_ATTRAZIONE_INTERCOMUNALE("Polo di attrazione intercomunale"),
        POLO_DI_ATTRAZIONE_URBANA("Polo di attrazione urbana"),
        AREA_DI_CINTURA("Area di cintura"),
        AREA_PERIFERICA("Area periferica"),
        AREA_INTERMEDIA("Area intermedia"),
        AREA_ULTRA_PERIFERICA("Area ultra-periferica");

        @NotNull
        private final String name;

        Classe(@NotNull String name) {
            this.name = name;
        }

        @NotNull
        @Contract(pure = true)
        public String getName() {
            return name;
        }

        @Nullable
        public static Classe fromName(@NotNull String str) {
            for (Classe im : values()) {
                if (str.equalsIgnoreCase(im.getName())) {
                    return im;
                }
            }
            return null;
        }

    }

    private final int istatCode;
    private final int popolazioneResidente, popolazioneStraniera;
    private final float densitàDemografica;
    private final float superficie;
    private final int altezzaCentro, altezzaMinima, altezzaMassima;
    @NotNull
    private final ZonaAltimetrica zonaAltimetrica;
    @Nullable
    private final TipoCapoluogo tipoCapoluogo;
    @NotNull
    private final GradoUrbanizzazione gradoUrbanizzazione;
    @NotNull
    private final IndiceMontanità indiceMontanità;
    @Nullable
    private final ZonaClimatica zonaClimatica;
    @Nullable
    private final Classe classe;
    private final double latitude, longitude;

    private ComuneStat(int istatCode, int popolazioneResidente, int popolazioneStraniera, float densitàDemografica, float superficie, int altezzaCentro, int altezzaMinima, int altezzaMassima, @NotNull ZonaAltimetrica zonaAltimetrica, @Nullable TipoCapoluogo tipoCapoluogo, @NotNull GradoUrbanizzazione gradoUrbanizzazione, @NotNull IndiceMontanità indiceMontanità, @Nullable ZonaClimatica zonaClimatica, @Nullable Classe classe, double latitude, double longitude) {
        this.istatCode = istatCode;
        this.popolazioneResidente = popolazioneResidente;
        this.popolazioneStraniera = popolazioneStraniera;
        this.densitàDemografica = densitàDemografica;
        this.superficie = superficie;
        this.altezzaCentro = altezzaCentro;
        this.altezzaMinima = altezzaMinima;
        this.altezzaMassima = altezzaMassima;
        this.zonaAltimetrica = zonaAltimetrica;
        this.tipoCapoluogo = tipoCapoluogo;
        this.gradoUrbanizzazione = gradoUrbanizzazione;
        this.indiceMontanità = indiceMontanità;
        this.zonaClimatica = zonaClimatica;
        this.classe = classe;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @NotNull
    public Comune getComune() {
        return MAP.getK1(this);
    }

    @Contract(pure = true)
    public int getIstatCode() {
        return istatCode;
    }

    @Contract(pure = true)
    public int getPopolazioneResidente() {
        return popolazioneResidente;
    }

    @Contract(pure = true)
    public int getPopolazioneStraniera() {
        return popolazioneStraniera;
    }

    @Contract(pure = true)
    public int getPopolazioneTotale() {
        return popolazioneStraniera + popolazioneResidente;
    }

    @Contract(pure = true)
    public float getDensitàDemografica() {
        return densitàDemografica;
    }

    @Contract(pure = true)
    public float getSuperficie() {
        return superficie;
    }

    @Contract(pure = true)
    public int getAltezzaCentro() {
        return altezzaCentro;
    }

    @Contract(pure = true)
    public int getAltezzaMinima() {
        return altezzaMinima;
    }

    @Contract(pure = true)
    public int getAltezzaMassima() {
        return altezzaMassima;
    }

    @Contract(pure = true)
    @NotNull
    public ZonaAltimetrica getZonaAltimetrica() {
        return zonaAltimetrica;
    }

    @Contract(pure = true)
    @Nullable
    public TipoCapoluogo optTipoCapoluogo() {
        return tipoCapoluogo;
    }

    @NotNull
    public TipoCapoluogo getTipoCapoluogo() {
        if (tipoCapoluogo == null) {
            throw new IllegalStateException("Not a capoluogo");
        }
        return tipoCapoluogo;
    }

    @Contract(pure = true)
    public boolean isCapoluogo() {
        return tipoCapoluogo != null;
    }

    @Contract(pure = true)
    @NotNull
    public GradoUrbanizzazione getGradoUrbanizzazione() {
        return gradoUrbanizzazione;
    }

    @Contract(pure = true)
    @NotNull
    public IndiceMontanità getIndiceMontanità() {
        return indiceMontanità;
    }

    @Contract(pure = true)
    @Nullable
    public ZonaClimatica getZonaClimatica() {
        return zonaClimatica;
    }

    @Contract(pure = true)
    @Nullable
    public Classe getClasse() {
        return classe;
    }

    @Contract(pure = true)
    public double getLatitude() {
        return latitude;
    }

    @Contract(pure = true)
    public double getLongitude() {
        return longitude;
    }

    @NotNull
    private static ComuneStat parse(@NotNull CSVRecord record) {
        return new ComuneStat(
                Integer.parseInt(record.get(1)),
                Integer.parseInt(record.get(6)),
                Integer.parseInt(record.get(7)),
                Float.parseFloat(record.get(8).replace(',', '.')),
                Float.parseFloat(record.get(9).replace(',', '.')),
                Integer.parseInt(record.get(10)),
                Integer.parseInt(record.get(11)),
                Integer.parseInt(record.get(12)),
                ZonaAltimetrica.fromName(record.get(13)),
                TipoCapoluogo.fromName(record.get(14)),
                GradoUrbanizzazione.fromName(record.get(15)),
                IndiceMontanità.fromName(record.get(16)),
                ZonaClimatica.optValueOf(record.get(17)),
                Classe.fromName(record.get(19)),
                Float.parseFloat(record.get(20).replace(',', '.')),
                Float.parseFloat(record.get(21).replace(',', '.'))
        );
    }

    public static void ensureLoaded(@NotNull Context context, @NotNull AnagraficheExtended anagrafiche) {
        ensureLoaded(context, anagrafiche, null);
    }

    public static void ensureLoaded(@NotNull Context context, @NotNull AnagraficheExtended anagrafiche, @Nullable Consumer<Float> approximateProgressListener) {
        synchronized (MAP_LOADED) {
            if (!MAP_LOADED.get()) {
                if (approximateProgressListener != null) {
                    approximateProgressListener.consume(0f);
                }
                final Iterator<CSVRecord> records;
                try {
                    records = CSVParser.parse(ReaderUtils.readAll(new InputStreamReader(context.getAssets().open("ancitel_comuni.csv"))), CSV_FORMAT).iterator();
                } catch (IOException e) {
                    throw new IllegalStateException("Error reading asset", e);
                }
                int parsed = 0;
                while (records.hasNext()) {
                    if (approximateProgressListener != null) {
                        approximateProgressListener.consume(Math.min(1, parsed / (float) anagrafiche.getComuni().size()));
                    }
                    CSVRecord record = records.next();
                    final ComuneStat stat = ComuneStat.parse(record);
                    final int provCode = stat.getIstatCode() / 1000;
                    final int comuneCode = stat.getIstatCode() % 1000;

                    final Comune comune = anagrafiche.getComuni().get(new Comune.ComuneId(comuneCode, anagrafiche.getProvincie().get(provCode)));
                    if (MAP.containsK1(comune)) {
                        throw new IllegalArgumentException("comune doppio " + comune.getNome());
                    }
                    MAP.put(comune, stat);
                    parsed++;
                }

                MAP_LOADED.set(true);
            }
        }
    }

    public static long getAllPopulation(@NotNull Set<ComuneStat> stats) {
        long sum = 0;
        for (ComuneStat stat : stats) {
            sum += stat.getPopolazioneTotale();
        }
        return sum;
    }

    public static float getAllSuperficie(@NotNull Set<ComuneStat> stats) {
        float sum = 0;
        for (ComuneStat stat : stats) {
            sum += stat.getSuperficie();
        }
        return sum;
    }

    @Nullable
    public static ComuneStat getInstance(@NotNull Context context, @NotNull AnagraficheExtended anagrafiche, @NotNull Comune comune) {
        ensureLoaded(context, anagrafiche);
        return MAP.getK2(comune);
    }

    @NotNull
    public static Set<ComuneStat> getInstance(@NotNull Context context, @NotNull AnagraficheExtended anagrafiche, @NotNull GeoItem geoItem) {
        return getInstance(context, anagrafiche, DataUtils.getAllComuni(geoItem));
    }

    @NotNull
    public static Set<ComuneStat> getInstance(@NotNull Context context, @NotNull AnagraficheExtended anagrafiche, @NotNull Set<Comune> comuni) {
        ensureLoaded(context, anagrafiche);
        Set<ComuneStat> ret = new HashSet<>();
        for (Comune comune : comuni) {
            ComuneStat s = MAP.getK2(comune);
            if (s != null) {
                ret.add(s);
            }
        }
        return ret;
    }
}
