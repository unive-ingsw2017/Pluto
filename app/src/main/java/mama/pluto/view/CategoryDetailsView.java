package mama.pluto.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mmauro94.siopeDownloader.datastruct.anagrafiche.Ente;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import mama.pluto.R;
import mama.pluto.dataAbstraction.AnagraficheExtended;
import mama.pluto.dataAbstraction.Category;
import mama.pluto.dataAbstraction.DataUtils;
import mama.pluto.utils.BaseViewHolder;
import mama.pluto.utils.MetricsUtils;
import mama.pluto.utils.StringUtils;
import mama.pluto.utils.Triple;

public class CategoryDetailsView extends LinearLayout {

    @NotNull
    private final AnagraficheExtended anagrafiche;
    private final Spinner hierarchySelector;
    private final Spinner orderBySelector;
    private final TextView orderedByTextView;
    private final RecyclerView recyclerView;
    private Category category;
    private String codiceSottocomparto = DataUtils.SOTTOCOMPARTO_REGIONE;
    private boolean orderByHighBalance = true;

    //Loaded data
    private List<Triple<Ente, Long, Long>> enteList;
    private long iteration = 0;

    public CategoryDetailsView(Context context, @NotNull AnagraficheExtended anagrafiche) {
        super(context);
        this.anagrafiche = anagrafiche;
        setOrientation(VERTICAL);
        int dp16 = MetricsUtils.dpToPixel(context, 16);

        LinearLayout topBar = new LinearLayout(getContext());
        addView(topBar);
        topBar.setBackgroundColor(0x10000000);
        topBar.setPadding(dp16, dp16, dp16, dp16);

        hierarchySelector = new Spinner(context);
        ArrayAdapter<CharSequence> hierarcyAdapter = ArrayAdapter.createFromResource(context, R.array._hierarcy_evels, android.R.layout.simple_spinner_item);
        hierarcyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hierarchySelector.setAdapter(hierarcyAdapter);
        hierarchySelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        codiceSottocomparto = DataUtils.SOTTOCOMPARTO_REGIONE;
                        orderedByTextView.setText(R.string._ordered_female_by);
                        break;
                    case 1:
                        codiceSottocomparto = DataUtils.SOTTOCOMPARTO_PROVINCIA;
                        orderedByTextView.setText(R.string._ordered_female_by);
                        break;
                    case 2:
                        codiceSottocomparto = DataUtils.SOTTOCOMPARTO_COMUNE;
                        orderedByTextView.setText(R.string._ordered_by);
                        break;
                    default:
                        throw new IllegalStateException();
                }
                reloadData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                throw new IllegalStateException();
            }
        });
        topBar.addView(hierarchySelector);

        orderedByTextView = new TextView(context);
        orderedByTextView.setText(R.string._ordered_female_by);
        topBar.addView(orderedByTextView);

        orderBySelector = new Spinner(context);
        ArrayAdapter<CharSequence> orderByAdapter = ArrayAdapter.createFromResource(context, R.array._balance, android.R.layout.simple_spinner_item);
        orderByAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        orderBySelector.setAdapter(orderByAdapter);
        topBar.addView(orderBySelector);
        orderBySelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        orderByHighBalance = true;
                        break;
                    case 1:
                        orderByHighBalance = false;
                        break;
                    default:
                        throw new IllegalStateException();
                }
                reloadData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                throw new IllegalStateException();
            }
        });

        recyclerView = new RecyclerView(context);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(new MyAdapter());
        addView(recyclerView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @SuppressLint("StaticFieldLeak")
    private void reloadData() {
        enteList = null;
        if (category != null) {
            long myIteration = ++iteration;
            new AsyncTask<Void, Void, List<Triple<Ente, Long, Long>>>() {
                @Override
                protected List<Triple<Ente, Long, Long>> doInBackground(Void[] objects) {
                    long now = System.currentTimeMillis();
                    List<Triple<Ente, Long, Long>> enteList = DataUtils.getEntiRankPerCategory(category, anagrafiche.getSottocomparti().get(codiceSottocomparto), getContext(), anagrafiche, orderByHighBalance);
                    System.out.println("Loading list took " + (System.currentTimeMillis() - now) / 1000f + " seconds");
                    return enteList;
                }

                @Override
                protected void onPostExecute(List<Triple<Ente, Long, Long>> enti) {
                    if (myIteration == iteration) {
                        enteList = enti;
                        recyclerView.getAdapter().notifyDataSetChanged();
                    }
                }
            }.execute();
        }
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    public void setCategory(@NotNull Category category) {
        this.category = category;
        recyclerView.getAdapter().notifyDataSetChanged();
        reloadData();
    }

    private class MyAdapter extends RecyclerView.Adapter {
        private final int VIEW_TYPE_LOADING = 0;
        private final int VIEW_TYPE_ENTE = 1;
        private final int VIEW_TYPE_EMPTY = 2;

        @Override
        public int getItemViewType(int position) {
            return enteList == null ? VIEW_TYPE_LOADING : enteList.isEmpty() ? VIEW_TYPE_EMPTY : VIEW_TYPE_ENTE;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
                case VIEW_TYPE_LOADING:
                    FrameLayout f = new FrameLayout(getContext());
                    int dp64 = MetricsUtils.dpToPixel(getContext(), 64);
                    int dp96 = MetricsUtils.dpToPixel(getContext(), 96);
                    f.setPadding(dp64, dp64, dp64, dp64);
                    ProgressBar pb = new ProgressBar(getContext());
                    f.addView(pb, new FrameLayout.LayoutParams(dp96, dp96, Gravity.CENTER));
                    return BaseViewHolder.getFullInstance(recyclerView, f);
                case VIEW_TYPE_ENTE:
                    return BaseViewHolder.getFullInstance(recyclerView, new EnteRow(getContext()));
                case VIEW_TYPE_EMPTY:
                    int dp16 = MetricsUtils.dpToPixel(getContext(), 16);
                    TextView emptyView = new TextView(getContext(), null, android.R.attr.textAppearanceMedium);
                    emptyView.setPadding(dp16, dp16, dp16, dp16);
                    emptyView.setGravity(Gravity.CENTER);
                    emptyView.setCompoundDrawablePadding(dp16);
                    emptyView.setTextColor(0xff666666);
                    emptyView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_sentiment_dissatisfied_grey_240dp, 0, 0);
                    emptyView.setText(R.string.no_details_for_category);
                    return BaseViewHolder.getFullInstance(recyclerView, emptyView);
                default:
                    throw new IllegalStateException();
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (enteList != null) {
                if (!enteList.isEmpty()) {
                    ((EnteRow) holder.itemView).setEnte(enteList.get(position));
                }
            }
        }

        @Override
        public int getItemCount() {
            return enteList == null ? 1 : Math.max(1, enteList.size());
        }
    }

    private class EnteRow extends DoubleLineListItem {

        private BalanceRowView balanceRowView;

        public EnteRow(Context context) {
            super(context);
            balanceRowView = new BalanceRowView(context);
            addView(balanceRowView);
        }

        public void setEnte(Triple<Ente, Long, Long> data) {
            Ente ente = data.getFirst();
            Long in = data.getSecond();
            Long out = data.getThird();
            balanceRowView.setBalance(in, out);
            setText(StringUtils.toNormalCase(ente.getNome()), null);
        }
    }
}
