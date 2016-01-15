package com.oic.vdd.common.views.listview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.oic.vdd.R;

/**
 * Created by khacpham on 1/7/16.
 */
public class FooterListView extends ListView {

    public FooterListView(Context context) {
        super(context);
        init();
    }

    public FooterListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FooterListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        LinearLayout footer = new LinearLayout(getContext());
        footer.setMinimumHeight((int) getContext().getResources().getDimension(R.dimen.listview_footer_height));
        addFooterView(footer);


    }
}
