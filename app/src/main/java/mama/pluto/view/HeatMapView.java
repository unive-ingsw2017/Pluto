package mama.pluto.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Regione;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import mama.pluto.R;
import mama.pluto.dataAbstraction.AnagraficheExtended;
import mama.pluto.dataAbstraction.DataUtils;
import mama.pluto.dataAbstraction.JVectorProvinciaCodes;
import mama.pluto.dataAbstraction.JVectorRegioneCodes;
import mama.pluto.utils.BiFunction;
import mama.pluto.utils.DoubleMap;
import mama.pluto.utils.Function;
import mama.pluto.utils.MetricsUtils;

public class HeatMapView extends FrameLayout {

    public enum MapProjection {
        MILLER("mill"),
        MERCATOR("merc");

        @NotNull
        private final String codeName;

        MapProjection(@NonNull String codeName) {
            this.codeName = codeName;
        }

        @NonNull
        @Contract(pure = true)
        public String getCodeName() {
            return codeName;
        }
    }

    public static class Data<X, N extends Number> {
        @NotNull
        private final String meaning;
        @NotNull
        private final MapProjection mapProjection;
        private final boolean isRegionLevel;
        @NotNull
        private final DoubleMap<X, String> geoItemCodeMap;
        @NotNull
        private final Map<String, N> data;
        @NotNull
        private final Collection<String> missingCodes;
        @NotNull
        private final BiFunction<X, N, String> labelFunction;
        @NotNull
        private Number center = 1;

        private Data(@NonNull String meaning, @NotNull MapProjection mapProjection, boolean isRegionLevel, @NotNull DoubleMap<X, String> geoItemCodeMap, @NotNull Map<String, N> data, @NotNull Collection<String> missingCodes, @NotNull BiFunction<X, N, String> labelFunction) {
            this.meaning = meaning;
            this.mapProjection = mapProjection;
            this.isRegionLevel = isRegionLevel;
            this.geoItemCodeMap = geoItemCodeMap;
            this.data = data;
            this.missingCodes = missingCodes;
            this.labelFunction = labelFunction;
        }

        @NonNull
        public String getMeaning() {
            return meaning;
        }

        @NotNull
        public String getLabelForCode(@NotNull String code) {
            return labelFunction.apply(geoItemCodeMap.getK1(code), data.get(code));
        }

        public boolean has(@NotNull String code) {
            return data.containsKey(code);
        }

        @NonNull
        public static <X, N extends Number> Data<X, N> create(@NotNull String meaning, @NotNull MapProjection mapProjection, boolean isRegionLevel, @NotNull Map<X, N> data, @NotNull Function<X, String> toCodeFunction, @NotNull BiFunction<X, N, String> labelFunction, @NotNull Collection<X> allItems) {
            final DoubleMap<X, String> geoItemCodeMap = new DoubleMap<>(data.size());
            for (X x : data.keySet()) {
                final String code = toCodeFunction.apply(x);
                if (code != null) {
                    geoItemCodeMap.put(x, code);
                }
            }
            final Map<String, N> realData = DataUtils.mapConvertKeys(data, geoItemCodeMap::getK2);
            realData.remove(null);

            final HashSet<X> missingItems = new HashSet<>(allItems);
            missingItems.removeAll(data.keySet());
            final HashSet<String> missingCodes = new HashSet<>(missingItems.size());
            for (X x : missingItems) {
                missingCodes.add(toCodeFunction.apply(x));
            }
            return new Data<>(meaning, mapProjection, isRegionLevel, geoItemCodeMap, realData, missingCodes, labelFunction);
        }
    }

    @NotNull
    private final AnagraficheExtended anagrafiche;
    @NotNull
    private final WebView wv;
    @NotNull
    private final LinearLayout baslineLL;
    @NotNull
    private final TextView baselineTV;
    @NotNull
    private final TextView meaningView;

    private Data<?, ?> data;
    private final AtomicBoolean pageLoaded = new AtomicBoolean(false);

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    public HeatMapView(Context context, @NotNull AnagraficheExtended anagrafiche) {
        super(context);
        this.anagrafiche = anagrafiche;
        final int dp2 = MetricsUtils.dpToPixel(context, 2);

        wv = new WebView(getContext());
        wv.getSettings().setJavaScriptEnabled(true);
        wv.addJavascriptInterface(new Object() {
            @JavascriptInterface
            public void onClick(@NotNull String code) {
                if (data.has(code)) {
                    data.center = data.data.get(code);
                    post(() -> {
                        baslineLL.setVisibility(View.VISIBLE);
                        baselineTV.setText(getContext().getString(R.string.baseline, data.getLabelForCode(code)));
                        apply();
                    });
                }
            }
        }, "regClick");
        wv.setWebChromeClient(new WebChromeClient());
        wv.loadUrl("file:///android_asset/map.html");
        wv.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                synchronized (pageLoaded) {
                    pageLoaded.set(true);
                    pageLoaded.notify();
                }
            }
        });
        addView(wv, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);

        baslineLL = new LinearLayout(getContext());
        baslineLL.setBackgroundColor(0xFF333333);
        baslineLL.setOrientation(LinearLayout.HORIZONTAL);
        baslineLL.setVisibility(View.GONE);
        addView(baslineLL, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, Gravity.BOTTOM));

        baselineTV = new TextView(getContext());
        final int dp16 = MetricsUtils.dpToPixel(getContext(), 16);
        baselineTV.setPadding(dp16, dp16, dp16, dp16);
        baselineTV.setTextSize(18);
        baselineTV.setTextColor(Color.WHITE);
        baslineLL.addView(baselineTV, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        final Button resetBaselineButton = new Button(getContext(), null, android.R.attr.buttonBarButtonStyle);
        resetBaselineButton.setPadding(dp16, dp16, dp16, dp16);
        resetBaselineButton.setTextSize(18);
        resetBaselineButton.setText(R.string.reset);
        resetBaselineButton.setOnClickListener(view -> {
            data.center = 1;
            post(() -> {
                baslineLL.setVisibility(View.GONE);
                apply();
            });
        });
        final LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER;
        baslineLL.addView(resetBaselineButton, lp);


        meaningView = new TextView(getContext(), null, android.R.attr.textAppearanceSmallInverse);
        meaningView.setTextColor(Color.WHITE);
        meaningView.setGravity(Gravity.CENTER);
        meaningView.setBackgroundColor(0x7f505050);
        meaningView.setPadding(dp2, dp2, dp2, dp2);
        addView(meaningView, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.TOP | Gravity.CENTER_HORIZONTAL));
    }

    public <N extends Number> Data<Regione, N> dataForRegioneLevel(@NotNull String meaning, @NotNull MapProjection mapProjection, @NotNull Map<Regione, N> data, @NotNull final BiFunction<Regione, N, String> labels) {
        return Data.create(meaning, mapProjection, true, data, JVectorRegioneCodes::getJVectorCode, labels, anagrafiche.getRegioni());
    }

    public <N extends Number> Data<String, N> dataForProvinciaLevel(@NotNull String meaning, @NotNull MapProjection mapProjection, @NotNull Map<String, N> data, @NotNull final BiFunction<String, N, String> labels) {
        return Data.create(meaning, mapProjection, false, data, x -> x, labels, JVectorProvinciaCodes.allCodes());
    }

    public void setData(@NotNull Data<?, ?> data) {
        this.data = data;
        apply();
        baslineLL.setVisibility(View.GONE);
    }

    private void apply() {
        final String centerStr;
        try {
            centerStr = JSONObject.numberToString(data.center);
        } catch (JSONException e) {
            throw new IllegalStateException(e);
        }
        if (!pageLoaded.get()) {
            throw new IllegalStateException("Page not loaded yet");
        }
        wv.loadUrl("javascript:refreshMap(" +
                new JSONObject(data.data) + "," +
                JSONObject.quote(getMapCode()) + "," +
                centerStr + "," +
                new JSONArray(data.missingCodes) +
                ")");
        meaningView.setText(data.meaning);
    }

    public void waitPageLoaded() throws InterruptedException {
        synchronized (pageLoaded) {
            while (!pageLoaded.get()) {
                pageLoaded.wait();
            }
        }
    }

    @NonNull
    @Contract(pure = true)
    private String getMapCode() {
        String ret = "it_";
        if (data.isRegionLevel) {
            ret += "regions_";
        }
        return ret + data.mapProjection.getCodeName();
    }

}
