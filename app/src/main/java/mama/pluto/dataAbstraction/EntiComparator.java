package mama.pluto.dataAbstraction;

import android.content.Context;

import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Ente;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EntiComparator {
    @NotNull
    private final EnteSummary enteSummary1;
    @NotNull
    private final EnteSummary enteSummary2;

    public EntiComparator(@NotNull EnteSummary enteSummary1, @NotNull EnteSummary enteSummary2) {
        this.enteSummary1 = enteSummary1;
        this.enteSummary2 = enteSummary2;
    }

    @NotNull
    public EnteSummary getEnteSummary1() {
        return enteSummary1;
    }

    @NotNull
    public EnteSummary getEnteSummary2() {
        return enteSummary2;
    }

    @NotNull
    public static EntiComparator getInstance(@NotNull Context context, @NotNull AnagraficheExtended anagrafiche, @NotNull Ente a, @NotNull Ente b) {
        return new EntiComparator(EnteSummary.getInstance(context, anagrafiche, a), EnteSummary.getInstance(context, anagrafiche, b));
    }

    private Set<Category> allCategories;

    @NotNull
    private Set<Category> getCategories(@NotNull EnteSummary enteSummary) {
        if (allCategories == null) {
            Set<Category> res = new HashSet<>(enteSummary.getEntrateMap().keySet());
            res.addAll(enteSummary.getUsciteMap().keySet());
            allCategories = res;
        }
        return allCategories;
    }

    private List<CategoryComparison> sortedCategories;

    @NotNull
    public List<CategoryComparison> getSortedCategories() {
        if (sortedCategories == null) {
            Set<Category> categorySet = getCategories(this.enteSummary1);
            Map<Category, Long> entrate1Map = enteSummary1.getEntrateMap();
            Map<Category, Long> entrate2Map = enteSummary2.getEntrateMap();
            Map<Category, Long> uscite1Map = enteSummary1.getUsciteMap();
            Map<Category, Long> uscite2Map = enteSummary2.getUsciteMap();
            categorySet.addAll(getCategories(this.enteSummary2));
            List<CategoryComparison> res = new ArrayList<>();
            for (Category category : categorySet) {
                long entrate2 = 0, entrate1 = 0, uscite1 = 0, uscite2 = 0;
                if (entrate1Map.containsKey(category)) {
                    entrate1 = entrate1Map.get(category);
                }
                if (uscite1Map.containsKey(category)) {
                    uscite1 = uscite1Map.get(category);
                }
                if (entrate2Map.containsKey(category)) {
                    entrate2 = entrate2Map.get(category);
                }
                if (uscite2Map.containsKey(category)) {
                    uscite2 = uscite2Map.get(category);
                }
                res.add(new CategoryComparison(this, category, entrate1, uscite1, entrate2, uscite2));
            }
            Collections.sort(res);
            sortedCategories = res;
        }
        return sortedCategories;
    }

    public static class CategoryComparison implements Comparable<CategoryComparison> {
        private final EntiComparator entiComparator;
        @NotNull
        private final Category category;
        private final long firstEntrate;
        private final long firstUscite;
        private final long secondEntrate;
        private final long secondUscite;


        private CategoryComparison(EntiComparator entiComparator, @NotNull Category category, long firstEntrate, long firstUscite, long secondEntrate, long secondUscite) {
            this.entiComparator = entiComparator;
            this.category = category;
            this.firstEntrate = firstEntrate;
            this.firstUscite = firstUscite;
            this.secondEntrate = secondEntrate;
            this.secondUscite = secondUscite;
        }

        public EntiComparator getEntiComparator() {
            return entiComparator;
        }

        public long getFirstBalance() {
            return firstEntrate - firstUscite;
        }

        public long getSecondBalance() {
            return secondEntrate - secondUscite;
        }

        @NotNull
        public Category getCategory() {
            return category;
        }

        public long getFirstEntrate() {
            return firstEntrate;
        }

        public long getFirstUscite() {
            return firstUscite;
        }

        public long getSecondEntrate() {
            return secondEntrate;
        }

        public long getSecondUscite() {
            return secondUscite;
        }

        @Override
        public int compareTo(@NotNull CategoryComparison categoryComparison) {
            return Long.compare(Math.abs(categoryComparison.getFirstBalance() - categoryComparison.getSecondBalance()), Math.abs(this.getFirstBalance() - this.getSecondBalance()));
        }

        @Override
        public String toString() {
            return "CategoryComparison{" +
                    "category=" + category +
                    ", firstEntrate=" + firstEntrate +
                    ", firstUscite=" + firstUscite +
                    ", secondEntrate=" + secondEntrate +
                    ", secondUscite=" + secondUscite +
                    ", total=" + Math.abs(getFirstBalance() - getSecondBalance()) +
                    '}';
        }
    }
}
