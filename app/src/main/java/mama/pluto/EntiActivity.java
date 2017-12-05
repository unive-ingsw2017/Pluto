package mama.pluto;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.view.SubMenu;

import java.util.HashSet;
import java.util.Set;

import mama.pluto.utils.AppSection;
import mama.pluto.utils.BaseActivity;

public class EntiActivity extends BaseActivity {
    public final AppSection entiAppSection = new EntiAppSection();
    public final AppSection categorieDiBilancioAppSection = new CategorieDiBilancioAppSection();
    protected NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    protected Set<AppSection> getSections() {
        Set<AppSection> ret = new HashSet<>();
        ret.add(entiAppSection);
        ret.add(categorieDiBilancioAppSection);
        return ret;
    }

    @NonNull
    @Override
    protected AppSection getDefaultAppSection() {
        return entiAppSection;
    }
}
