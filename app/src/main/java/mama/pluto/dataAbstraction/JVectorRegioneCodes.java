package mama.pluto.dataAbstraction;

import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Regione;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class JVectorRegioneCodes {

    private JVectorRegioneCodes() {
        throw new IllegalStateException();
    }

    public static final Map<Integer, String> MAP = new HashMap<Integer, String>() {
        {
            put(1, "IT-21"); //Piemonte
            put(2, "IT-23"); //Valle d'Aosta
            put(3, "IT-25"); //Lombardia
            put(4, "IT-32"); //Trentino-Alto Adige
            put(5, "IT-34"); //Veneto
            put(6, "IT-36"); //Friuli-Venezia Giulia
            put(7, "IT-42"); //Liguria
            put(8, "IT-45"); //Emilia-Romagna
            put(9, "IT-52"); //Toscana
            put(10, "IT-55"); //Umbria
            put(11, "IT-57"); //Marche
            put(12, "IT-62"); //Lazio
            put(13, "IT-65"); //Abruzzo
            put(14, "IT-67"); //Molise
            put(15, "IT-72"); //Campania
            put(16, "IT-75"); //Puglia
            put(17, "IT-77"); //Basilicata
            put(18, "IT-78"); //Calabria
            put(19, "IT-82"); //Sicilia
            put(20, "IT-88"); //Sardegna

        }
    };

    @NotNull
    public static String getJVectorCode(@NotNull Regione regione) {
        String ret = MAP.get(regione.getCodice());
        if (ret != null) {
            return ret;
        } else {
            throw new IllegalStateException("Regione " + regione.getNome() + " with code " + regione.getCodice() + " not in map");
        }
    }
}
