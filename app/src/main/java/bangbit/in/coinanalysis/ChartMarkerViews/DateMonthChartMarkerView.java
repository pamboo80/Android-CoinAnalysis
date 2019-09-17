package bangbit.in.coinanalysis.ChartMarkerViews;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import bangbit.in.coinanalysis.R;
import bangbit.in.coinanalysis.Util;

import static bangbit.in.coinanalysis.MyApplication.currencySymbol;


/**
 * Created by Nagarajan on 3/23/2018.
 */

public class DateMonthChartMarkerView extends MarkerView {

    private TextView priceTextView;
    private TextView timeTextView;

    public DateMonthChartMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);
        // this markerview only displays a textview
        priceTextView = findViewById(R.id.price);
        timeTextView = findViewById(R.id.time);
    }

    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {

        String price="Price : "+currencySymbol+e.getY();
        int val= (int) e.getX();
        String time="Time : "+ Util.getDayMonthFromUnixTime(val);
        priceTextView.setText(price);
        timeTextView.setText(time);
        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }


}
