package mama.pluto.view;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import mama.pluto.R;
import mama.pluto.dataAbstraction.AnagraficheExtended;
import mama.pluto.dataAbstraction.Category;
import mama.pluto.utils.MetricsUtils;

public class CategoriesMainView extends BaseLayoutView {
    @NotNull
    private final FrameLayout content;
    @NotNull
    private final AnagraficheExtended anagrafiche;
    @NotNull
    private final CategorySelector categorySelector;
    @NotNull
    private final CategoryDetailsView categoryDetailsView;
    @Nullable
    private Category selectedCategory;

    public CategoriesMainView(Context context, @NotNull AnagraficheExtended anagrafiche) {
        super(context);
        this.anagrafiche = anagrafiche;
        content = new FrameLayout(context);
        addView(content, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        categorySelector = new CategorySelector(context, anagrafiche);
        categorySelector.setOnCategorySelected(this::setSelectedCategory);
        content.addView(categorySelector, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        categoryDetailsView = new CategoryDetailsView(context, anagrafiche);
        content.addView(categoryDetailsView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        setSelectedCategory(null);
    }


    private void setContent(@NotNull View v) {
        for (int i = 0; i < content.getChildCount(); i++) {
            View view = content.getChildAt(i);
            view.setVisibility(view == v ? View.VISIBLE : View.GONE);
        }
    }

    public void setSelectedCategory(@Nullable Category selectedCategory) {
        this.selectedCategory = selectedCategory;
        setContent(selectedCategory == null ? categorySelector : categoryDetailsView);
        if (selectedCategory == null) {
            getToolbar().setTitle(R.string.categorie_di_bilancio);
        } else {
            categoryDetailsView.setCategory(selectedCategory);
            getToolbar().setTitle(selectedCategory.getName());
        }
    }

    public boolean onBackPressed() {
        if (selectedCategory != null) {
            setSelectedCategory(null);
            return true;
        }
        return false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int width = MeasureSpec.getSize(widthMeasureSpec);
        final int listPadding = Math.max(0, (width - MetricsUtils.dpToPixel(getContext(), 400)) / 2);
        MetricsUtils.applyLateralPadding(content, listPadding);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
