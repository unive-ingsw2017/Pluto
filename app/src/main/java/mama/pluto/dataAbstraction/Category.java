package mama.pluto.dataAbstraction;

import com.github.mmauro94.siopeDownloader.datastruct.AutoMap;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class Category {

    public static final Category OTHER = new Category(82, "Altro");

    private static final AutoMap<Integer, Category> CATEGORIES = new AutoMap<Integer, Category>() {
        @NotNull
        @Override
        protected Integer getKey(@NotNull Category value) {
            return value.getId();
        }

        {
            add(new Category(1, "Spese elettorali"));
            add(new Category(2, "Beni"));
            add(new Category(3, "Fabbricati"));
            add(new Category(4, "Pensioni"));
            add(new Category(6, "Ricerca"));
            add(new Category(7, "Università"));
            add(new Category(8, "Terreni"));
            add(new Category(9, "Manutenzione"));
            add(new Category(11, "Sanità"));//Merged with 28
            add(new Category(12, "Lotterie"));
            add(new Category(13, "Stipendi"));
            add(new Category(14, "Servizi"));
            add(new Category(15, "Titoli"));
            add(new Category(16, "Armi"));
            add(new Category(17, "Investimenti"));
            add(new Category(18, "Utenze"));
            add(new Category(19, "Rimborsi"));
            add(new Category(20, "Infrastrutture"));
            add(new Category(21, "Informatica"));
            add(new Category(22, "Strade"));
            add(new Category(23, "Imprese"));
            add(new Category(24, "Ambiente"));
            add(new Category(25, "Cultura"));
            add(new Category(26, "Mezzi"));
            add(new Category(27, "Agricoltura"));
            add(new Category(29, "Estero", true));
            add(new Category(30, "Tributi"));
            add(new Category(31, "Noleggi", true));
            add(new Category(32, "Opere"));
            add(new Category(33, "Formazione"));
            add(new Category(34, "Luce e gas"));
            add(new Category(36, "Ritenute"));
            add(new Category(37, "Tasse"));
            add(new Category(38, "Mobili"));
            add(new Category(39, "Smaltimento"));
            add(new Category(40, "Mensa"));
            add(new Category(41, "Pulizia", true));
            add(new Category(42, "Servizi pubblicitari"));
            add(new Category(43, "Cancelleria"));
            add(new Category(44, "Famiglie"));
            add(new Category(45, "Regioni"));
            add(new Category(46, "Unione Europea"));
            add(new Category(47, "Comunicazioni"));
            add(new Category(48, "Spese legali"));
            add(new Category(49, "Province"));
            add(new Category(50, "Comuni"));
            add(new Category(51, "Macchinari"));
            add(new Category(53, "Interessi e Pignoramenti"));
            add(new Category(54, "Stampa"));
            add(new Category(55, "Prodotti chimici"));
            add(new Category(58, "Ministeri"));
            add(new Category(59, "Carburanti"));
            add(new Category(60, "Portuali"));
            add(new Category(61, "Piante", true));
            add(new Category(62, "IRAP", true));
            add(new Category(63, "I.V.A.", true));
            add(new Category(64, "Giornali e riviste"));
            add(new Category(65, "Giacimenti"));
            add(new Category(66, "Cauzioni"));
            add(new Category(67, "Equitalia"));
            add(new Category(68, "Materiali"));
            add(new Category(70, "Commissioni"));
            add(new Category(71, "Competenze", true));
            add(new Category(73, "Spese"));
            add(new Category(74, "Pagamenti"));
            add(new Category(76, "Prestiti"));
            add(new Category(77, "Arretrati"));
            add(new Category(78, "Indennizzi"));
            add(new Category(79, "Oneri"));
            add(new Category(81, "Partecipazioni"));
            add(OTHER);
        }
    };

    private final int id;
    @NotNull
    private final String name;
    private final boolean entiOnly;

    private Category(int id, @NotNull String name) {
        this(id, name, false);
    }

    private Category(int id, @NotNull String name, boolean entiOnly) {
        this.id = id;
        this.name = name;
        this.entiOnly = entiOnly;
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

    /**
     * If this category is NEVER applied to regioni/province/comuni
     */
    public boolean isEntiOnly() {
        return entiOnly;
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
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
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
