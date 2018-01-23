package mama.pluto.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.StringDef;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import org.json.JSONObject;

import java.lang.annotation.Retention;
import java.util.HashMap;

import static java.lang.annotation.RetentionPolicy.SOURCE;

public class HeatMapView extends WebView {

    @Retention(SOURCE)
    @StringDef({
            REGION_MERCATOR_PROJECTION,
            REGION_MILLER_PROJECTION,
            PROVINCE_MERCATOR_PROJECTION,
            PROVINCE_MILLER_PROJECTION
    })
    public @interface MapType {
    }

    public static final String REGION_MERCATOR_PROJECTION = "it_regions_merc";
    public static final String REGION_MILLER_PROJECTION = "it_regions_mill";
    public static final String PROVINCE_MERCATOR_PROJECTION = "it_merc";
    public static final String PROVINCE_MILLER_PROJECTION = "it_mill";


    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    public HeatMapView(Context context) {
        super(context);
        getSettings().setJavaScriptEnabled(true);
        setWebChromeClient(new WebChromeClient());
        addJavascriptInterface(new JavaScriptInterface(), "Android");

        loadUrl("file:///android_asset/map.html");
    }

    private class JavaScriptInterface {

        public JavaScriptInterface() {
        }

        @JavascriptInterface
        public String getData() {
            HashMap<String, Float> data = new HashMap<>();
            data.put("IT-VE", 12312312f);
            data.put("IT-MI", 123f);
            return new JSONObject(data).toString();
        }

        @JavascriptInterface
        public String getLabel() {
            return "Spesa";
        }

        /**
         * @return one of it_merc, it_mill, it_regions_merc, it_regions_mill
         */
        @MapType
        @JavascriptInterface
        public String getMap() {
            return PROVINCE_MERCATOR_PROJECTION;
        }

    }
}
