package mama.pluto.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Provincia;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Regione;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.Map;

import mama.pluto.dataAbstraction.DataUtils;
import mama.pluto.dataAbstraction.JVectorRegioneCodes;

public class HeatMapView extends WebView {

    public enum MapProjection {
        MILLER("mill"),
        MERCATOR("merc");
        private final String codeName;

        MapProjection(String codeName) {
            this.codeName = codeName;
        }

        public String getCodeName() {
            return codeName;
        }
    }

    private String label;
    private Map<?, Float> data;
    private MapProjection mapProjection;
    private boolean isRegionLevel;

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    public HeatMapView(Context context) {
        super(context);
        getSettings().setJavaScriptEnabled(true);
        setWebChromeClient(new WebChromeClient());
        loadUrl("file:///android_asset/map.html");
    }

    public void setupForRegioneLevel(@NotNull String label, @NotNull MapProjection mapProjection, @NotNull Map<Regione, Float> data) {
        setData(label, mapProjection, DataUtils.mapConvertKeys(data, JVectorRegioneCodes::getJVectorCode), true);
    }

    public void setupForProvinciaLevel(@NotNull String label, @NotNull MapProjection mapProjection, @NotNull Map<Provincia, Float> data) {
        //setData(label, mapProjection, data, false);//TODO: make key a String
    }

    private void setData(String label, MapProjection mapProjection, Map<String, Float> data, boolean isRegionLevel) {
        this.label = label;
        this.mapProjection = mapProjection;
        this.data = data;
        this.isRegionLevel = isRegionLevel;
        loadUrl("javascript:refreshMap(" +
                new JSONObject(data) + "," +
                JSONObject.quote(label) + "," +
                JSONObject.quote(getMapCode()) +
                ")");
    }

    private String getMapCode() {
        String ret = "it_";
        if (isRegionLevel) {
            ret += "regions_";
        }
        return ret + mapProjection.getCodeName();
    }

}
