package mama.pluto.view.selectors;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import mama.pluto.utils.AbstractEnteSelectorAdapter;

/**
 * Created by MMarco on 16/11/2017.
 */

public class RegioneSelectorView extends AbstractEnteSelectorView {


    public RegioneSelectorView(Context context) {
        super(context);
    }

    public RegioneSelectorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RegioneSelectorView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected AbstractEnteSelectorAdapter createAdapter() {
        return new AbstractEnteSelectorAdapter() {
            @Override
            protected String getEnte(int position) {
                return "Regione " + position;
            }

            @Override
            public int getItemCount() {
                return 20;
            }
        };
    }
}
