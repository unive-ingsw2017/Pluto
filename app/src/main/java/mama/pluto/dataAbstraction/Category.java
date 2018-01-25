package mama.pluto.dataAbstraction;

import com.github.mmauro94.siopeDownloader.datastruct.AutoMap;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class Category {

    public static final Category OTHER = new Category(82, "ALTRI");

    private static final AutoMap<Integer, Category> CATEGORIES = new AutoMap<Integer, Category>() {
        @NotNull
        @Override
        protected Integer getKey(@NotNull Category value) {
            return value.getId();
        }

        {
            add(new Category(1, "SPESE ELETTORALI"));
            add(new Category(2, "BENI"));
            add(new Category(3, "FABBRICATI"));
            add(new Category(4, "PENSIONI"));
            add(new Category(5, "CONCESSIONE CREDITI"));
            add(new Category(6, "RICERCA"));
            add(new Category(7, "UNIVERSITÀ"));
            add(new Category(8, "TERRENI"));
            add(new Category(9, "MANUTENZIONE"));
            add(new Category(10, "INTERESSI"));
            add(new Category(11, "SANITÀ"));
            add(new Category(12, "LOTTERIE"));
            add(new Category(13, "STIPENDI"));
            add(new Category(14, "SERVIZI"));
            add(new Category(15, "TITOLI"));
            add(new Category(16, "ARMI"));
            add(new Category(17, "INVESTIMENTI"));
            add(new Category(18, "UTENZE"));
            add(new Category(19, "RIMBORSI"));
            add(new Category(20, "INFRASTRUTTURE"));
            add(new Category(21, "INFORMATICA"));
            add(new Category(22, "STRADE"));
            add(new Category(23, "IMPRESE"));
            add(new Category(24, "AMBIENTE"));
            add(new Category(25, "CULTURA"));
            add(new Category(26, "MEZZI"));
            add(new Category(27, "AGRICOLTURA"));
            add(new Category(28, "SANITÀ"));
            add(new Category(29, "ESTERO"));
            add(new Category(30, "TRIBUTI"));
            add(new Category(31, "NOLEGGI"));
            add(new Category(32, "OPERE"));
            add(new Category(33, "FORMAZIONE"));
            add(new Category(34, "LUCE & GAS"));
            add(new Category(35, "LAVORO"));
            add(new Category(36, "RITENUTE"));
            add(new Category(37, "TASSE"));
            add(new Category(38, "MOBILI"));
            add(new Category(39, "SMALTIMENTO"));
            add(new Category(40, "MENSA"));
            add(new Category(41, "PULIZIA"));
            add(new Category(42, "SERVIZI PUBBLICITARI"));
            add(new Category(43, "CANCELLERIA"));
            add(new Category(44, "FAMIGLIE"));
            add(new Category(45, "REGIONI"));
            add(new Category(46, "UNIONE EUROPEA"));
            add(new Category(47, "COMUNICAZIONI"));
            add(new Category(48, "SPESE LEGALI"));
            add(new Category(49, "PROVINCE"));
            add(new Category(50, "COMUNI"));
            add(new Category(51, "MACCHINARI"));
            add(new Category(52, "LEASING"));
            add(new Category(53, "PIGNORAMENTI"));
            add(new Category(54, "STAMPA"));
            add(new Category(55, "PRODOTTI CHIMICI"));
            add(new Category(56, "RISARCIMENTI"));
            add(new Category(57, "INPS"));
            add(new Category(58, "MINISTERI"));
            add(new Category(59, "CARBURANTI"));
            add(new Category(60, "PORTUALI"));
            add(new Category(61, "PIANTE"));
            add(new Category(62, "IRAP"));
            add(new Category(63, "I.V.A."));
            add(new Category(64, "GIORNALI E RIVISTE"));
            add(new Category(65, "GIACIMENTI"));
            add(new Category(66, "CAUZIONI"));
            add(new Category(67, "EQUITALIA"));
            add(new Category(68, "MATERIALI"));
            add(new Category(69, "ACQUISTI VARI"));
            add(new Category(70, "COMMISSIONI"));
            add(new Category(71, "COMPETENZE"));
            add(new Category(72, "FINANZIARIE"));
            add(new Category(73, "SPESE"));
            add(new Category(74, "PAGEMENTI"));
            add(new Category(75, "TRASFERIMENTI"));
            add(new Category(76, "PRESTITI"));
            add(new Category(77, "ARRETRATI"));
            add(new Category(78, "INDENNIZZI"));
            add(new Category(79, "ONERI"));
            add(new Category(81, "PARTECIPAZIONI"));
            add(OTHER);
        }
    };

    private final int id;
    @NotNull
    private final String name;

    private Category(int id, @NotNull String name) {
        this.id = id;
        this.name = name;
    }

    @Contract(pure = true)
    public int getId() {
        return id;
    }

    @Contract(pure = true)
    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public static Category getInstance(int id) {
        if (!CATEGORIES.hasKey(id)) {
            throw new IllegalArgumentException("Invalid category id " + id);
        }
        return CATEGORIES.get(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return id == category.id;
    }

    @Contract(pure = true)
    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return name + "(" + id + ")";
    }
}
