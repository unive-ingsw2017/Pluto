package mama.pluto.utils;

import android.content.Context;
import android.icu.util.Measure;
import android.widget.ImageView;

/**
 * Created by MMarco on 05/12/2017.
 */

public class FixedAspectRatioImageView extends ImageView {
    private final float ratio;

    public FixedAspectRatioImageView(Context context, float ratio) {
        super(context);
        this.ratio = ratio;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = Math.round(width / ratio);
        setMeasuredDimension(width, height);
    }
}
