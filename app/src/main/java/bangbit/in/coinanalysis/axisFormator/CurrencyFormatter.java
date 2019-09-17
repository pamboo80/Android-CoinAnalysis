package bangbit.in.coinanalysis.axisFormator;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import bangbit.in.coinanalysis.Util;

/**
 * Created by Nagarajan on 3/23/2018.
 */

public class CurrencyFormatter implements IAxisValueFormatter {

    private final String currencySymbol;

    public CurrencyFormatter(String currencySymbol){
        this.currencySymbol=currencySymbol;
    }
    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        String val="";

        //Log.d("CurrencyFormatter", "getFormattedValue: "+value);
        if (value>1){
            val= Util.getTwoDecimalPointValue(value);
        }else {
            val+=value;
        }
        return currencySymbol+String.valueOf(val);
    }
}