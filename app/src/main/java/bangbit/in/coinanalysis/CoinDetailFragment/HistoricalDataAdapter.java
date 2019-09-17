package bangbit.in.coinanalysis.CoinDetailFragment;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import bangbit.in.coinanalysis.Dialogs.HistoricalDialog;
import bangbit.in.coinanalysis.R;
import bangbit.in.coinanalysis.Util;
import bangbit.in.coinanalysis.pojo.Datum;

import static bangbit.in.coinanalysis.MyApplication.currencySymbol;

/**
 * Created by Nagarajan on 3/26/2018.
 */

public class HistoricalDataAdapter extends RecyclerView.Adapter<HistoricalDataAdapter.ViewHolder> {
    private ArrayList<Datum> historicalDataList;
    private Context context;

    public HistoricalDataAdapter(ArrayList<Datum> historicalDataList) {
        this.historicalDataList = historicalDataList;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.historical_data_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Datum history = historicalDataList.get(position);

        String lowValue = Util.getMillionValue(history.getLow());
        final String highValue = Util.getMillionValue(history.getHigh());
        String openValue = Util.getMillionValue(history.getOpen());
        String closeValue = Util.getMillionValue(history.getClose());

        holder.dateTextView.setText(Util.getDayMonthYearFromUnixTime(history.getTime()-20*1000)); //@@@ -20*1000
        holder.lowTextView.setText(currencySymbol + lowValue);
        holder.highTextView.setText(currencySymbol + highValue);
        holder.openTextView.setText(currencySymbol + openValue);
        holder.closeTextView.setText(currencySymbol + closeValue);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HistoricalDialog historicalDialog=new HistoricalDialog();
                historicalDialog.showDialog(context,historicalDataList,position);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (historicalDataList!=null) {
            return historicalDataList.size();
        }else {
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

    public void setHistoricalDataList(ArrayList<Datum> historicalDataList) {
        this.historicalDataList = historicalDataList;
        notifyDataSetChanged();
    }

    public ArrayList<Datum> getHistoricalDataList() {
        return historicalDataList;
    }
}
