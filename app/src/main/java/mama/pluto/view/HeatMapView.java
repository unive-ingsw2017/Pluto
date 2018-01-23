package mama.pluto.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Provincia;
import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Regione;

import org.json.JSONObject;

import java.util.Map;

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
        addJavascriptInterface(new JavaScriptInterface(), "Android");

        loadUrl("file:///android_asset/map.html");
    }

    public void setupForRegioneLevel(String label, MapProjection mapProjection, Map<Regione, Float> data) {
        setData(label, mapProjection, data, true);//TODO: make key a String
    }

    public void setupForProvinciaLevel(String label, MapProjection mapProjection, Map<Provincia, Float> data) {
        setData(label, mapProjection, data, false);//TODO: make key a String
    }

    private void setData(String label, MapProjection mapProjection, Map<?, Float> data, boolean isRegionLevel) {
        this.label = label;
        this.mapProjection = mapProjection;
        this.data = data;
        this.isRegionLevel = isRegionLevel;
        loadUrl("javascript:refreshMap()");
    }

    private class JavaScriptInterface {

        private JavaScriptInterface() {
        }

        @JavascriptInterface
        public String getData() {
            return new JSONObject(data).toString();
        }

        @JavascriptInterface
        public String getLabel() {
            return label;
        }

        /**
         * @return one of it_merc, it_mill, it_regions_merc, it_regions_mill
         */
        @JavascriptInterface
        public String getMap() {
            String ret = "it_";
            if (isRegionLevel) {
                ret += "regions_";
            }
            return ret + mapProjection.getCodeName();
        }

    }
}
