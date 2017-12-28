package mama.pluto.utils;

import android.os.Bundle;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import mama.pluto.R;

/**
 * Call setupContentView() to add the navigation drawer and the app sections
 */
public abstract class BaseActivity extends AppCompatActivity {

    private final Map<Integer, AppSection> appSections = new HashMap<>();
    private AppSection selectedAppSection;
    private FrameLayout content;
    private SubMenu navigationMenu;

    protected void setupContentView() {
        content = new FrameLayout(this);
        DrawerLayout drawerLayout = new DrawerLayout(this);
        drawerLayout.addView(content, DrawerLayout.LayoutParams.MATCH_PARENT, DrawerLayout.LayoutParams.MATCH_PARENT);

        DrawerLayout.LayoutParams lp = new DrawerLayout.LayoutParams(DrawerLayout.LayoutParams.WRAP_CONTENT, DrawerLayout.LayoutParams.MATCH_PARENT, Gravity.START);
        drawerLayout.addView(createDrawer(drawerLayout), lp);
        setContentView(drawerLayout);
    }

    @NotNull
    protected abstract Set<AppSection> getSections();

    @NotNull
    protected abstract AppSection getDefaultAppSection();

    protected NavigationView createDrawer(DrawerLayout drawerLayout) {
        NavigationView navigationView = new NavigationView(this);
        navigationView.addHeaderView(createHeaderView());
        navigationView.inflateMenu(R.menu.navigation_menu);

        navigationMenu = navigationView.getMenu().findItem(R.id.section).getSubMenu();
        appSections.clear();
        AppSection defaultAppSection = getDefaultAppSection();
        Set<AppSection> sections = getSections();
        if (!sections.contains(defaultAppSection)) {
            throw new IllegalStateException();
        }
        for (AppSection appSection : sections) {
            int sectionId = ViewUtils.generateViewId();
            MenuItem menuItem = navigationMenu.add(Menu.NONE, sectionId, Menu.NONE, appSection.getTitle(this));
            appSection.setupItem(this, menuItem);
            appSections.put(sectionId, appSection);
        }
        selectAppSection(defaultAppSection);

        navigationView.setNavigationItemSelectedListener(menuItem -> {
            AppSection appSection = appSections.get(menuItem.getItemId());
            if (appSection != null) {
                selectAppSection(appSection);
            }
            drawerLayout.closeDrawers();
            return true;
        });
        return navigationView;
    }

    private void selectAppSection(AppSection appSection) {
        content.removeAllViews();
        final View view = appSection.getView(this);
        if (view.getParent() instanceof ViewGroup) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
        content.addView(view, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        for (Map.Entry<Integer, AppSection> entry : appSections.entrySet()) {
            navigationMenu.findItem(entry.getKey()).setChecked(entry.getValue() == appSection);
        }
        this.selectedAppSection = appSection;
    }


    protected View createHeaderView() {
        ImageView img = new FixedAspectRatioImageView(this, 16f / 9);
        img.setImageResource(R.drawable.wallpaper);
        img.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return img;
    }

    @Override
    public void onBackPressed() {
        if (selectedAppSection == null || !selectedAppSection.onBackPressed()) {
            super.onBackPressed();
        }
    }
}
