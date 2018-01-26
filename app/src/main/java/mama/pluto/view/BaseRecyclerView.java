package mama.pluto.view;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import mama.pluto.R;
import mama.pluto.utils.MetricsUtils;

public class BaseRecyclerView extends RecyclerView {

    public BaseRecyclerView(Context context) {
        super(context);
        final int dp32 = MetricsUtils.dpToPixel(getContext(), 32);
        setPadding(0, 0, 0, dp32);
        setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.divider));
        addItemDecoration(dividerItemDecoration);
        setClipToPadding(false);
        setBackgroundColor(Color.WHITE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setElevation(MetricsUtils.dpToPixel(getContext(), 4f));//TODO: spostare
        }
    }
}
