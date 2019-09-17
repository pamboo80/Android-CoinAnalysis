package bangbit.in.coinanalysis.axisFormator;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import bangbit.in.coinanalysis.Util;

/**
 * Created by Nagarajan on 3/23/2018.
 */

public class DateMonFormatter implements IAxisValueFormatter {

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return Util.getDayMonthFromUnixTime(value);
    }
}