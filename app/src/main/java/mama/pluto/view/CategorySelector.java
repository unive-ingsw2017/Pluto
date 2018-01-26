package mama.pluto.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.view.ViewGroup;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;

import mama.pluto.dataAbstraction.AnagraficheExtended;
import mama.pluto.dataAbstraction.Category;
import mama.pluto.dataAbstraction.CategoryUtils;
import mama.pluto.utils.BaseViewHolder;
import mama.pluto.utils.Consumer;

public class CategorySelector extends BaseRecyclerView {
    @NotNull
    private final AnagraficheExtended anagrafiche;
    private final ArrayList<Category> categories;
    private Consumer<Category> onCategorySelected;

    public CategorySelector(Context context, @NotNull AnagraficheExtended anagrafiche) {
        super(context);
        this.anagrafiche = anagrafiche;
        setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));

        categories = new ArrayList<>(CategoryUtils.all());
        Collections.sort(categories, (o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName()));

        setAdapter(new MyAdapter());
    }

    public void setOnCategorySelected(Consumer<Category> onCategorySelected) {
        this.onCategorySelected = onCategorySelected;
    }

    private class MyAdapter extends Adapter {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return BaseViewHolder.getFullInstance(CategorySelector.this, new SingleLineListItem(getContext()));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Category category = categories.get(position);
            ((SingleLineListItem) holder.itemView).setText(category.getName());
            holder.itemView.setOnClickListener(v -> {
                if (onCategorySelected != null) {
                    onCategorySelected.consume(category);
                }
            });
        }

        @Override
        public int getItemCount() {
            return categories.size();
        }
    }
}
