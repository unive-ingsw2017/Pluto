package mama.pluto.dataAbstraction;

import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.CodiceGestionale;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Sottocomparto;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public final class CategoryUtils {

    private CategoryUtils() {
        throw new IllegalStateException();
    }


    private static final LinkedHashMap<Category, String[]> MAP = new LinkedHashMap<Category, String[]>() {
        {
            put(Category.getInstance(1), new String[]{"ELETTORALI", "REFERENDUM"});
            put(Category.getInstance(2), new String[]{"BENI"});
            put(Category.getInstance(3), new String[]{"FABBRICATI"});
            put(Category.getInstance(4), new String[]{"PENSIONI"});
            put(Category.getInstance(5), new String[]{"CONCESSIONE CREDITI"});
            put(Category.getInstance(6), new String[]{"RICERCA", "BREVETTI"});
            put(Category.getInstance(7), new String[]{"UNIVERSIT", "STUDIO", "SCUO", "DOTTORATO", "DOCENTI"});
            put(Category.getInstance(8), new String[]{"TERRENI"});
            put(Category.getInstance(9), new String[]{"MANUT"});
            put(Category.getInstance(10), new String[]{"INTERESSI"});
            put(Category.getInstance(11), new String[]{"SANIT"});
            put(Category.getInstance(12), new String[]{"VINCITE", "PREMI PER", "LOTTO"});
            put(Category.getInstance(13), new String[]{"STIPENDIALI", "STRAORDINARI", "TFR"});
            put(Category.getInstance(14), new String[]{"SERVIZI"});
            put(Category.getInstance(15), new String[]{"TITOLI"});
            put(Category.getInstance(16), new String[]{"ARMI"});
            put(Category.getInstance(17), new String[]{"INVESTIMENTI"});
            put(Category.getInstance(18), new String[]{"UTENZE"});
            put(Category.getInstance(19), new String[]{"RIMBORS"});
            put(Category.getInstance(20), new String[]{"INFRASTRUTTURE", "IMPIANTI", "CIMITERI"});
            put(Category.getInstance(21), new String[]{"SOFTWARE", "SERVER", "HARD", "INFORMATICO"});
            put(Category.getInstance(22), new String[]{"VIE", "STRADE"});
            put(Category.getInstance(23), new String[]{"IMPRESE"});
            put(Category.getInstance(24), new String[]{"FAUNA", "FLORA", "AMBIENTE", "PARCHI"});
            put(Category.getInstance(25), new String[]{"CULTURA", "TEATRI"});
            put(Category.getInstance(26), new String[]{"MEZZI"});
            put(Category.getInstance(27), new String[]{"AGRICOLTURA"});
            put(Category.getInstance(28), new String[]{"OSPEDA", "SANITA", "CLINIC", "MEDIC", "ISTITUTI", "FARMAC", "PSICHIATRIA"});
            put(Category.getInstance(29), new String[]{"ESTERO"});
            put(Category.getInstance(30), new String[]{"TRIBUT", "IMPOST"});
            put(Category.getInstance(31), new String[]{"NOLEGGI"});
            put(Category.getInstance(32), new String[]{"OPERE"});
            put(Category.getInstance(33), new String[]{"FORMAZIONE", "TIROCINI"});
            put(Category.getInstance(34), new String[]{"ELETTRICA", "GAS", "RISCALDAMENTO"});
            put(Category.getInstance(35), new String[]{"LAVORO", "STUDI", "CONSULENZ", "COLLABORA", "PERIZIE", "INCARICHI"});
            put(Category.getInstance(36), new String[]{"RITENUTE"});
            put(Category.getInstance(37), new String[]{"TASS"});
            put(Category.getInstance(38), new String[]{"MOBILI"});
            put(Category.getInstance(39), new String[]{"SMALTIMENTO"});
            put(Category.getInstance(40), new String[]{"MENS", "ACQUA", "ALIMENTARI"});
            put(Category.getInstance(41), new String[]{"PULIZIA"});
            put(Category.getInstance(42), new String[]{"PUBBLICIT"});
            put(Category.getInstance(43), new String[]{"CANCELLERIA", "RILEGATURA", "CARTA"});
            put(Category.getInstance(44), new String[]{"FAMIGLIE"});
            put(Category.getInstance(45), new String[]{"REGION"});
            put(Category.getInstance(46), new String[]{"UNIONE EUROPEA"});
            put(Category.getInstance(47), new String[]{"COMUNICAZION", "TELEFON", "POSTE", "TELEGRAFICI"});
            put(Category.getInstance(48), new String[]{"LEGAL", "CONTENZIOSO", "GIUDICI"});
            put(Category.getInstance(49), new String[]{"PROVIN"});
            put(Category.getInstance(50), new String[]{"COMUNI"});
            put(Category.getInstance(51), new String[]{"MACCHINARI"});
            put(Category.getInstance(52), new String[]{"LEASING"});
            put(Category.getInstance(53), new String[]{"PIGNORAMENTI"});
            put(Category.getInstance(54), new String[]{"PUBBLICAZIONI", "STAMPA"});
            put(Category.getInstance(55), new String[]{"PRODOTTI CHIMICI"});
            put(Category.getInstance(56), new String[]{"RISARCIMENT"});
            put(Category.getInstance(57), new String[]{"INPS"});
            put(Category.getInstance(58), new String[]{"MINISTERI"});
            put(Category.getInstance(59), new String[]{"CARBURANTI"});
            put(Category.getInstance(60), new String[]{"PORTUALI", "COMMERCIO", "ECONO"});
            put(Category.getInstance(61), new String[]{"PIANTE"});
            put(Category.getInstance(62), new String[]{"IRAP"});
            put(Category.getInstance(63), new String[]{"I.V.A."});
            put(Category.getInstance(64), new String[]{"GIORNALI E RIVISTE"});
            put(Category.getInstance(65), new String[]{"GIACIMENTI"});
            put(Category.getInstance(66), new String[]{"CAUZION"});
            put(Category.getInstance(67), new String[]{"EQUITALIA"});
            put(Category.getInstance(68), new String[]{"MATERIAL"});
            put(Category.getInstance(69), new String[]{"ACQUIST"});
            put(Category.getInstance(70), new String[]{"COMMISSIONI"});
            put(Category.getInstance(71), new String[]{"COMPETENZE"});
            put(Category.getInstance(72), new String[]{"FINANZIARIE"});
            put(Category.getInstance(73), new String[]{"SPESE"});
            put(Category.getInstance(74), new String[]{"VERSAMENTI", "PAGAMENTI"});
            put(Category.getInstance(75), new String[]{"TRASFERIMENTI"});
            put(Category.getInstance(76), new String[]{"PRESTITI"});
            put(Category.getInstance(77), new String[]{"ARRETRATI"});
            put(Category.getInstance(78), new String[]{"INDENNI"});
            put(Category.getInstance(79), new String[]{"ONERI"});
            put(Category.getInstance(81), new String[]{"PARTECIPAZION"});
        }
    };

    private static Map<CodiceGestionale, Category> cache = new HashMap<>();

    @NotNull
    public static Category getCategory(@NotNull CodiceGestionale sottocomparto) {
        if (cache.containsKey(sottocomparto)) {
            return cache.get(sottocomparto);
        } else {
            for (Map.Entry<Category, String[]> categoryEntry : MAP.entrySet()) {
                for (String s : categoryEntry.getValue()) {
                    if (sottocomparto.getNome().toUpperCase().contains(s.toUpperCase())) {
                        Category ret = categoryEntry.getKey();
                        cache.put(sottocomparto, ret);
                        return ret;
                    }
                }
            }
        }
        return Category.OTHER;
    }

    @NotNull
    public static Collection<Category> all() {
        return Collections.unmodifiableCollection(MAP.keySet());
    }
}
