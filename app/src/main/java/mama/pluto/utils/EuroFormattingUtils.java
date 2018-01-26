package mama.pluto.utils;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class EuroFormattingUtils {

    private final static NumberFormat CURRENCY_FORMAT_NO_SYMBOL = new DecimalFormat("#,##0.00");

    public enum Base {
        MILIARDI_EURO("miliardi €", 1_000_000_000),
        MILIONI_EURO("milioni €", 1_000_000),
        MILA_EURO("mila €", 1_000),
        EURO("€", 1);
        private final String unitOfMeasure;
        private final long magnitude;

        Base(String unitOfMeasure, long magnitude) {
            this.unitOfMeasure = unitOfMeasure;
            this.magnitude = magnitude;
        }

        public long getMagnitude() {
            return magnitude;
        }

        public String getUnitOfMeasure() {
            return unitOfMeasure;
        }

        private boolean looksGreat(long euroCent) {
            long euro = Math.abs(euroCent / 100);
            return euro > magnitude && euro / magnitude < 1000;
        }

        private boolean looksOk(long euroCent) {
            long euro = Math.abs(euroCent / 100);
            return euro * 10 > magnitude && euro / magnitude < 1_000_000;
        }

        public static Base getBase(Long euroCent) {
            if (euroCent == null) {
                return EURO;
            }
            for (Base base : values()) {
                if (base.looksGreat(euroCent)) {
                    return base;
                }
            }
            return EURO;
        }

        public static Base getBase(Long... euroCent) {
            Map<Base, Integer> greatCount = new HashMap<>();
            for (Base base : values()) {
                greatCount.put(base, 0);
            }
            for (Long e : euroCent) {
                if (e != null) {
                    for (Iterator<Map.Entry<Base, Integer>> iterator = greatCount.entrySet().iterator(); iterator.hasNext(); ) {
                        Map.Entry<Base, Integer> entry = iterator.next();
                        if (!entry.getKey().looksOk(e)) {
                            iterator.remove();
                        } else if (entry.getKey().looksGreat(e)) {
                            entry.setValue(entry.getValue() + 1);
                        }
                    }
                }
            }
            Map.Entry<Base, Integer> maxEntry = null;
            for (Map.Entry<Base, Integer> entry : greatCount.entrySet()) {
                if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0) {
                    maxEntry = entry;
                }
            }
            return maxEntry == null ? EURO : maxEntry.getKey();
        }

        public String format(long euroCent, boolean showSymbol) {
            BigDecimal euroValue = new BigDecimal(euroCent).divide(BigDecimal.valueOf(100 * magnitude), 2, BigDecimal.ROUND_HALF_EVEN);
            String ret = CURRENCY_FORMAT_NO_SYMBOL.format(euroValue);
            if (showSymbol) {
                ret += " " + unitOfMeasure;
            }
            return ret;
        }
    }

    public static final int POSITIVE_COLOR = 0xff43A047;
    public static final int NEUTRAL_COLOR = 0xffdddddd;
    public static final int NEGATIVE_COLOR = 0xffBF360C;

    @NotNull
    public static String formatEuroCentString(long balance, boolean approximate, boolean showSymbol) {
        return (approximate ? Base.getBase(balance) : Base.EURO).format(balance, showSymbol);
    }
}
