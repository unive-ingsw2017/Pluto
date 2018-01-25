package mama.pluto.dataAbstraction;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EntiComparator {
    private final EnteSummary ente1;
    private final EnteSummary ente2;

    public EntiComparator(EnteSummary ente1, EnteSummary ente2) {
        this.ente1 = ente1;
        this.ente2 = ente2;
    }

    private Set<Category> getCategories(EnteSummary enteSummary) {
        Set<Category> res = new HashSet<>(enteSummary.getEntrateMap().keySet());
        res.addAll(enteSummary.getUsciteMap().keySet());
        return res;
    }

    public List<ComparedItem> getSortedCategories() {
        Set<Category> categorySet = getCategories(this.ente1);
        Map<Category, Long> entrate1Map = ente1.getEntrateMap();
        Map<Category, Long> entrate2Map = ente2.getEntrateMap();
        Map<Category, Long> uscite1Map = ente1.getUsciteMap();
        Map<Category, Long> uscite2Map = ente2.getUsciteMap();
        categorySet.addAll(getCategories(this.ente2));
        List<ComparedItem> res = new ArrayList<>();
        for (Category category : categorySet) {
            long entrate2 = 0, entrate1 = 0, uscite1 = 0, uscite2 = 0;
            if(entrate1Map.containsKey(category)){
                entrate1 = entrate1Map.get(category);
            }
            if(uscite1Map.containsKey(category)){
                uscite1 = uscite1Map.get(category);
            }
            if(entrate2Map.containsKey(category)){
                entrate2 = entrate2Map.get(category);
            }
            if(uscite2Map.containsKey(category)){
                uscite2 = uscite2Map.get(category);
            }
            res.add(new ComparedItem(category, entrate1, uscite1, entrate2, uscite2));
        }
        Collections.sort(res);
        return res;
    }

    public class ComparedItem implements Comparable<ComparedItem> {
        private final Category category;
        private final long firstEntrate;
        private final long firstUscite;
        private final long secondEntrate;
        private final long secondUscite;


        private ComparedItem(Category category, long firstEntrate, long firstUscite, long secondEntrate, long secondUscite) {
            this.category = category;
            this.firstEntrate = firstEntrate;
            this.firstUscite = firstUscite;
            this.secondEntrate = secondEntrate;
            this.secondUscite = secondUscite;
        }

        public long getFirstBalance() {
            return firstEntrate - firstUscite;
        }

        public long getSecondBalance() {
            return secondEntrate - secondUscite;
        }

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
        public int compareTo(@NonNull ComparedItem comparedItem) {
           return Long.compare(Math.abs(comparedItem.getFirstBalance() - comparedItem.getSecondBalance()), Math.abs(this.getFirstBalance() - this.getSecondBalance()));
        }

        @Override
        public String toString() {
            return "ComparedItem{" +
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
