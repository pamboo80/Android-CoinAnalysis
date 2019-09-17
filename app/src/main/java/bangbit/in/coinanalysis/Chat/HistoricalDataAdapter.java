package bangbit.in.coinanalysis.Chat;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import bangbit.in.coinanalysis.LambdaHistoryComparator;
import bangbit.in.coinanalysis.ListOperations;
import bangbit.in.coinanalysis.R;
import bangbit.in.coinanalysis.Util;
import bangbit.in.coinanalysis.pojo.LambdaHistory;

import static bangbit.in.coinanalysis.MyApplication.currencySymbol;
import static bangbit.in.coinanalysis.Util.getMillionValue;
import static bangbit.in.coinanalysis.Util.multiplyWithCurrency;

/**
 * Created by Nagarajan on 3/26/2018.
 */

public class HistoricalDataAdapter extends RecyclerView.Adapter<HistoricalDataAdapter.ViewHolder> {
    private final boolean is24Hour;
    List<LambdaHistory> data;
    private Context context;

    public HistoricalDataAdapter(List<LambdaHistory> data, boolean is24Hour) {
        this.is24Hour=is24Hour;
        List<LambdaHistory> historyData = new ArrayList<LambdaHistory>(data);
        Collections.reverse(historyData);
        this.data  = historyData;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.data_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        LambdaHistory history = data.get(position);
        if (is24Hour){
            holder.dateTextView.setText(Util.getTimeFromUnixTime(Float.parseFloat(history.getTime())));
        }else {
            holder.dateTextView.setText(Util.getDayMonthYearFromUnixTime(Float.parseFloat(history.getTime())-20*1000)); //@@@ //-20*1000
        }
        String low = getMillionValue(Double.parseDouble(multiplyWithCurrency(history.getLow())));
        holder.lowTextView.setText(currencySymbol +low);
        String high = getMillionValue(Double.parseDouble(multiplyWithCurrency(history.getHigh())));
        holder.highTextView.setText(currencySymbol +high);
        String open = getMillionValue(Double.parseDouble(multiplyWithCurrency(history.getOpen())));
        holder.openTextView.setText(currencySymbol +open);
        String close = getMillionValue(Double.parseDouble(multiplyWithCurrency(history.getClose())));
        holder.closeTextView.setText(currencySymbol +close);
    }

    @Override
    public int getItemCount() {
        if (data != null) {
            return data.size();
        } else {
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView, openTextView, closeTextView, highTextView, lowTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            dateTextView = itemView.findViewById(R.id.date_textView);
            openTextView = itemView.findViewById(R.id.open_textView);
            closeTextView = itemView.findViewById(R.id.close_textView);
            highTextView = itemView.findViewById(R.id.high_textView);
            lowTextView = itemView.findViewById(R.id.low_textView);

        }
    }

}
