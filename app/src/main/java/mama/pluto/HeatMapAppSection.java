package mama.pluto;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.HashMap;

import mama.pluto.utils.AppSection;

public class HeatMapAppSection extends AppSection {

    @Override
    public String getTitle(@NotNull Context context) {
        return context.getString(R.string.heat_map);
    }

    @Override
    public Drawable getIcon(@NotNull Context context) {
        return context.getResources().getDrawable(R.drawable.ic_heatmap_black_24dp);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected View createView(@NotNull Context context) {
        WebView ret = new WebView(context);
        ret.getSettings().setJavaScriptEnabled(true);
        ret.setWebChromeClient(new WebChromeClient());
        ret.addJavascriptInterface(new JavaScriptInterface(), "Android");

        ret.loadUrl("file:///android_asset/map.html");
        return ret;
    }

    private class JavaScriptInterface {
        public JavaScriptInterface() {
        }

        @JavascriptInterface
        public String getData() {
            HashMap data = new HashMap();
            data.put("IT-VE", 12312312);
            data.put("IT-MI", 123);
            return new JSONObject(data).toString();
        }

        @JavascriptInterface
        public String getLabel() {
            return "Spesa";
        }

        /**
         * @return one of it_merc, it_mill, it_regions_merc, it_regions_mill
         */
        @JavascriptInterface
        public String getMap() {
            return "it_merc";
        }

    }
}
