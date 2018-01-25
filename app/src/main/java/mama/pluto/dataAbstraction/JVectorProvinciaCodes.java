package mama.pluto.dataAbstraction;

import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Anagrafiche;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Provincia;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Regione;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

import mama.pluto.utils.DoubleMap;

public class JVectorProvinciaCodes {

    private JVectorProvinciaCodes() {
        throw new IllegalStateException();
    }

    public static final DoubleMap<Integer, String> MAP = new DoubleMap<Integer, String>() {
        {
            put(1, "IT-TO"); //Torino
            put(2, "IT-VC"); //Vercelli
            put(3, "IT-NO"); //Novara
            put(4, "IT-CN"); //Cuneo
            put(5, "IT-AT"); //Asti
            put(6, "IT-AL"); //Alessandria
            put(7, "IT-AO"); //Valle d'Aosta
            put(8, "IT-IM"); //Imperia
            put(9, "IT-SV"); //Savona
            put(10, "IT-GE"); //Genova
            put(11, "IT-SP"); //La Spezia
            put(12, "IT-VA"); //Varese
            put(13, "IT-CO"); //Como
            put(14, "IT-SO"); //Sondrio
            put(15, "IT-MI"); //Milano
            put(16, "IT-BG"); //Bergamo
            put(17, "IT-BS"); //Brescia
            put(18, "IT-PV"); //Pavia
            put(19, "IT-CR"); //Cremona
            put(20, "IT-MN"); //Mantova
            put(21, "IT-BZ"); //Bolzano - Bozen
            put(22, "IT-TN"); //Trento
            put(23, "IT-VR"); //Verona
            put(24, "IT-VI"); //Vicenza
            put(25, "IT-BL"); //Belluno
            put(26, "IT-TV"); //Treviso
            put(27, "IT-VE"); //Venezia
            put(28, "IT-PD"); //Padova
            put(29, "IT-RO"); //Rovigo
            put(30, "IT-UD"); //Udine
            put(31, "IT-GO"); //Gorizia
            put(32, "IT-TS"); //Trieste
            put(33, "IT-PC"); //Piacenza
            put(34, "IT-PR"); //Parma
            put(35, "IT-RE"); //Reggio nell'Emilia
            put(36, "IT-MO"); //Modena
            put(37, "IT-BO"); //Bologna
            put(38, "IT-FE"); //Ferrara
            put(39, "IT-RA"); //Ravenna
            put(40, "IT-FC"); //Forli'-Cesena
            put(41, "IT-PU"); //Pesaro Urbino
            put(42, "IT-AN"); //Ancona
            put(43, "IT-MC"); //Macerata
            put(44, "IT-AP"); //Ascoli Piceno
            put(45, "IT-MS"); //Massa-Carrara
            put(46, "IT-LU"); //Lucca
            put(47, "IT-PT"); //Pistoia
            put(48, "IT-FI"); //Firenze
            put(49, "IT-LI"); //Livorno
            put(50, "IT-PI"); //Pisa
            put(51, "IT-AR"); //Arezzo
            put(52, "IT-SI"); //Siena
            put(53, "IT-GR"); //Grosseto
            put(54, "IT-PG"); //Perugia
            put(55, "IT-TR"); //Terni
            put(56, "IT-VT"); //Viterbo
            put(57, "IT-RI"); //Rieti
            put(58, "IT-RM"); //Roma
            put(59, "IT-LT"); //Latina
            put(60, "IT-FR"); //Frosinone
            put(61, "IT-CE"); //Caserta
            put(62, "IT-BN"); //Benevento
            put(63, "IT-NA"); //Napoli
            put(64, "IT-AV"); //Avellino
            put(65, "IT-SA"); //Salerno
            put(66, "IT-AQ"); //L'Aquila
            put(67, "IT-TE"); //Teramo
            put(68, "IT-PE"); //Pescara
            put(69, "IT-CH"); //Chieti
            put(70, "IT-CB"); //Campobasso
            put(71, "IT-FG"); //Foggia
            put(72, "IT-BA"); //Bari
            put(73, "IT-TA"); //Taranto
            put(74, "IT-BR"); //Brindisi
            put(75, "IT-LE"); //Lecce
            put(76, "IT-PZ"); //Potenza
            put(77, "IT-MT"); //Matera
            put(78, "IT-CS"); //Cosenza
            put(79, "IT-CZ"); //Catanzaro
            put(80, "IT-RC"); //Reggio di Calabria
            put(81, "IT-TP"); //Trapani
            put(82, "IT-PA"); //Palermo
            put(83, "IT-ME"); //Messina
            put(84, "IT-AG"); //Agrigento
            put(85, "IT-CL"); //Caltanissetta
            put(86, "IT-EN"); //Enna
            put(87, "IT-CT"); //Catania
            put(88, "IT-RG"); //Ragusa
            put(89, "IT-SR"); //Siracusa
            put(90, "IT-SS"); //Sassari
            put(91, "IT-NU"); //Nuoro
            put(92, "IT-CA"); //Cagliari
            put(93, "IT-PN"); //Pordenone
            put(94, "IT-IS"); //Isernia
            put(95, "IT-OR"); //Oristano
            put(96, "IT-BI"); //Biella
            put(97, "IT-LC"); //Lecco
            put(98, "IT-LO"); //Lodi
            put(99, "IT-RN"); //Rimini
            put(100, "IT-PO"); //Prato
            put(101, "IT-KR"); //Crotone
            put(102, "IT-VV"); //Vibo Valentia
            put(103, "IT-VB"); //Verbano-Cusio-Ossola
            put(104, "IT-OT"); //Olbia-Tempio
            put(105, "IT-OG"); //Ogliastra
            put(106, "IT-VS"); //Medio Campidano
            put(107, "IT-CI"); //Carbonia-Iglesias
            put(108, "IT-MB"); //MONZA - BRIANZA
            put(109, "IT-FM"); //FERMO
            put(110, "IT-BT"); //Barletta Andria e Trani
            //111 -> Sud sardegna... Siope: ti pare una fucking procincia?
        }
    };

    @Nullable
    public static String optJVectorCode(@NotNull Provincia provincia) {
        String ret = MAP.getK2(provincia.getCodice());
        if (ret != null) {
            return ret;
        } else {
            return null;
        }
    }

    @Nullable
    public static Provincia optProvincia(@NotNull Anagrafiche anagrafiche, @NotNull String jVectorCode) {
        Integer ret = MAP.getK1(jVectorCode);
        if (ret != null) {
            return anagrafiche.getProvincie().get(ret);
        } else {
            return null;
        }
    }
}
