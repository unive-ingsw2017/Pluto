package mama.pluto.dataAbstraction;

import android.content.Context;
import android.support.annotation.NonNull;

import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Anagrafiche;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Ente;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Provincia;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Regione;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import mama.pluto.database.Database;
import mama.pluto.utils.DoubleMap;
import mama.pluto.utils.Pair;

public class JVectorProvinciaCodes {

    private JVectorProvinciaCodes() {
        throw new IllegalStateException();
    }

    private static final Map<String, String[]> MAP = new HashMap<String, String[]>() {
        {
            put("IT-TO", new String[]{"000401890", "800000722"}); //Torino
            put("IT-VC", new String[]{"000702944"}); //Vercelli
            put("IT-NO", new String[]{"000711680"}); //Novara
            put("IT-CN", new String[]{"000085938"}); //Cuneo
            put("IT-AT", new String[]{"000699840"}); //Asti
            put("IT-AL", new String[]{"000701750"}); //Alessandria
            put("IT-AO", new String[]{"000700347"}); //Valle d'Aosta
            put("IT-IM", new String[]{"000046633"}); //Imperia
            put("IT-SV", new String[]{"000061691"}); //Savona
            put("IT-GE", new String[]{"000704661", "800000719"}); //Genova
            put("IT-SP", new String[]{"000040065"}); //La Spezia
            put("IT-VA", new String[]{"000699193"}); //Varese
            put("IT-CO", new String[]{"000702462"}); //Como
            put("IT-SO", new String[]{"000700922"}); //Sondrio
            put("IT-MI", new String[]{"000439206", "029821248"}); //Milano
            put("IT-BG", new String[]{"000702662"}); //Bergamo
            put("IT-BS", new String[]{"000705692"}); //Brescia
            put("IT-PV", new String[]{"000698769"}); //Pavia
            put("IT-CR", new String[]{"000700222"}); //Cremona
            put("IT-MN", new String[]{"000699440"}); //Mantova
            put("IT-BZ", new String[]{"000075817"}); //Bolzano - Bozen
            put("IT-TN", new String[]{"000066532"}); //Trento
            put("IT-VR", new String[]{"000123395"}); //Verona
            put("IT-VI", new String[]{"000095150"}); //Vicenza
            put("IT-BL", new String[]{"000751189"}); //Belluno
            put("IT-TV", new String[]{"000705778"}); //Treviso
            put("IT-VE", new String[]{"000705761", "800000723"}); //Venezia
            put("IT-PD", new String[]{"000704024"}); //Padova
            put("IT-RO", new String[]{"000751382"}); //Rovigo
            put("IT-UD", new String[]{"000077523"}); //Udine
            put("IT-GO", new String[]{"000015899"}); //Gorizia
            put("IT-TS", new String[]{"000707312"}); //Trieste
            put("IT-PC", new String[]{"000043819"}); //Piacenza
            put("IT-PR", new String[]{"000708949"}); //Parma
            put("IT-RE", new String[]{"000037611"}); //Reggio nell'Emilia
            put("IT-MO", new String[]{"000285861"}); //Modena
            put("IT-BO", new String[]{"000710949", "029821247"}); //Bologna
            put("IT-FE", new String[]{"000066019"}); //Ferrara
            put("IT-RA", new String[]{"000069976"}); //Ravenna
            put("IT-FC", new String[]{"000699774"}); //Forli'-Cesena
            put("IT-PU", new String[]{"000038358"}); //Pesaro Urbino
            put("IT-AN", new String[]{"012537612"}); //Ancona
            put("IT-MC", new String[]{"000699570"}); //Macerata
            put("IT-AP", new String[]{"012537656"}); //Ascoli Piceno
            put("IT-MS", new String[]{"007503982"}); //Massa-Carrara
            put("IT-LU", new String[]{"010736263"}); //Lucca
            put("IT-PT", new String[]{"000044377"}); //Pistoia
            put("IT-FI", new String[]{"014555297", "800000718"}); //Firenze
            put("IT-LI", new String[]{"000707145"}); //Livorno
            put("IT-PI", new String[]{"000698996"}); //Pisa
            put("IT-AR", new String[]{"000699123"}); //Arezzo
            put("IT-SI", new String[]{"000699476"}); //Siena
            put("IT-GR", new String[]{"000698773"}); //Grosseto
            put("IT-PG", new String[]{"000085219"}); //Perugia
            put("IT-TR", new String[]{"000029856"}); //Terni
            put("IT-VT", new String[]{"000703283"}); //Viterbo
            put("IT-RI", new String[]{"000014003"}); //Rieti
            put("IT-RM", new String[]{"000712448", "800000721"}); //Roma
            put("IT-LT", new String[]{"000701442"}); //Latina
            put("IT-FR", new String[]{"000344764"}); //Frosinone
            put("IT-CE", new String[]{"000702574"}); //Caserta
            put("IT-BN", new String[]{"000743279"}); //Benevento
            put("IT-NA", new String[]{"000258636", "800000720"}); //Napoli
            put("IT-AV", new String[]{"018900411"}); //Avellino
            put("IT-SA", new String[]{"000698990"}); //Salerno
            put("IT-AQ", new String[]{"000700433"}); //L'Aquila
            put("IT-TE", new String[]{"000699443"}); //Teramo
            put("IT-PE", new String[]{"012540507"}); //Pescara
            put("IT-CH", new String[]{"000698841"}); //Chieti
            put("IT-CB", new String[]{"000019436"}); //Campobasso
            put("IT-FG", new String[]{"010780342"}); //Foggia
            put("IT-BA", new String[]{"000698826", "800000717"}); //Bari
            put("IT-TA", new String[]{"000702714"}); //Taranto
            put("IT-BR", new String[]{"000699677"}); //Brindisi
            put("IT-LE", new String[]{"000699280"}); //Lecce
            put("IT-PZ", new String[]{"000700728"}); //Potenza
            put("IT-MT", new String[]{"000699374"}); //Matera
            put("IT-CS", new String[]{"000701613"}); //Cosenza
            put("IT-CZ", new String[]{"000701045"}); //Catanzaro
            put("IT-RC", new String[]{"000698813"}); //Reggio di Calabria
            put("IT-TP", new String[]{"000751033"}); //Trapani
            put("IT-PA", new String[]{"000710820"}); //Palermo
            put("IT-ME", new String[]{"000700764"}); //Messina
            put("IT-AG", new String[]{"000700626"}); //Agrigento
            put("IT-CL", new String[]{"000014131"}); //Caltanissetta
            put("IT-EN", new String[]{"000699262"}); //Enna
            put("IT-CT", new String[]{"000077087"}); //Catania
            put("IT-RG", new String[]{"000698761"}); //Ragusa
            put("IT-SR", new String[]{"000699877"}); //Siracusa
            put("IT-SS", new String[]{"000043006"}); //Sassari
            put("IT-NU", new String[]{"000026630"}); //Nuoro
            put("IT-CA", new String[]{"000097836", "800000768"}); //Cagliari
            put("IT-PN", new String[]{"000018949"}); //Pordenone
            put("IT-IS", new String[]{"000713363"}); //Isernia
            put("IT-OR", new String[]{"000701896"}); //Oristano
            put("IT-BI", new String[]{"011146046"}); //Biella
            put("IT-LC", new String[]{"000746659"}); //Lecco
            put("IT-LO", new String[]{"011150775"}); //Lodi
            put("IT-RN", new String[]{"000741162"}); //Rimini
            put("IT-PO", new String[]{"011150578"}); //Prato
            put("IT-KR", new String[]{"000738809"}); //Crotone
            put("IT-VV", new String[]{"000762140"}); //Vibo Valentia
            put("IT-VB", new String[]{"011151637"}); //Verbano-Cusio-Ossola
            put("IT-OT", new String[]{"000000020928499", "000043006"}); //Olbia-Tempio
            put("IT-OG", new String[]{"000000020918910", "000026630"}); //Ogliastra
            put("IT-VS", new String[]{"025616978", "030861184"}); //Medio Campidano
            put("IT-CI", new String[]{"025501372", "030861184"}); //Carbonia-Iglesias
            put("IT-MB", new String[]{"025617022"}); //MONZA - BRIANZA
            put("IT-FM", new String[]{"000000020997795"}); //FERMO
            put("IT-BT", new String[]{"000000020997797"}); //Barletta Andria e Trani
        }
    };

    @NotNull
    public static Set<Ente> allEnti(@NotNull AnagraficheExtended anagrafiche) {
        final HashSet<Ente> ret = new HashSet<>(MAP.size());
        for (String[] enti : MAP.values()) {
            for (String ente : enti) {
                ret.add(anagrafiche.getEnti().get(ente));
            }
        }
        return ret;
    }

    @NotNull
    public static Set<String> allCodes() {
        return Collections.unmodifiableSet(MAP.keySet());
    }

    @NotNull
    public static Map<String, Pair<Long, Long>> getProvinciaBalances(@NotNull Context context, @NotNull AnagraficheExtended a) {
        final Map<Ente, Pair<Long, Long>> db = Database.getInstance(context).getProvinciaBalances(a);

        final Map<String, Pair<Long, Long>> ret = new HashMap<>(db.size());
        for (Map.Entry<String, String[]> entry : MAP.entrySet()) {
            final String jVectorId = entry.getKey();
            for (String enteCode : entry.getValue()) {
                final Pair<Long, Long> current = db.get(a.getEnti().get(enteCode));
                if (current != null) {
                    if (!ret.containsKey(jVectorId)) {
                        ret.put(jVectorId, new Pair<>(0L, 0L));
                    }
                    final Pair<Long, Long> old = ret.get(jVectorId);

                    ret.put(jVectorId, new Pair<>(old.getFirst() + current.getFirst(), old.getSecond() + current.getSecond()));
                }
            }
        }
        return ret;
    }
}
