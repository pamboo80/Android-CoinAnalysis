package bangbit.in.coinanalysis;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bangbit.in.coinanalysis.R;
import bangbit.in.coinanalysis.Util;
import bangbit.in.coinanalysis.WhereToBuy.ItemClickListner;
import bangbit.in.coinanalysis.database.DatabaseHelper;
import bangbit.in.coinanalysis.pojo.Exchange;
import bangbit.in.coinanalysis.pojo.LambdaAnalysis;

import static bangbit.in.coinanalysis.MyApplication.currencySymbol;
import static bangbit.in.coinanalysis.Util.getTwoDecimalPointValue;
import static bangbit.in.coinanalysis.Util.multiplyWithCurrency;
import static bangbit.in.coinanalysis.WhereToBuy.WhereToBuyFragment.isCurrencyFiat;
import static bangbit.in.coinanalysis.WhereToBuy.WhereToBuyFragment.symbol;

/**
 * Created by Nagarajan on 3/27/2018.
 */

public class AnalysisAdapter extends RecyclerView.Adapter<AnalysisAdapter.ViewHolder> {

    private List<LambdaAnalysis> analysisList;
    private HashMap<String, String> coinImageUrlHashmap;
    private Context context;
    ItemClickListner itemClickListner;
    private DatabaseHelper databaseHelper;

    public AnalysisAdapter(List<LambdaAnalysis> analysisList, ItemClickListner itemClickListner) {
        this.analysisList = analysisList;
        //this.itemClickListner = itemClickListner;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.analysis_row, parent, false);
        databaseHelper = new DatabaseHelper(context);
        coinImageUrlHashmap = databaseHelper.getImageUrlHashMap();
        return new AnalysisAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final LambdaAnalysis analysis = analysisList.get(position);

        final String imageUrl = coinImageUrlHashmap.get(analysis.getCoinSymbol());
        if (imageUrl != null) {
            Picasso.with(context).load(imageUrl).placeholder(R.drawable.default_coin).fit().into(holder.coinImageView);
        } else {
            Picasso.with(context).load(R.drawable.default_coin).fit().into(holder.coinImageView);
        }

        holder.coinSymbolTextView.setText(analysis.getCoinSymbol());
        String price = multiplyWithCurrency(analysis.getPrice());
        price = Util.checkForNull(price);
        price = currencySymbol + Util.getTwoDecimalPointValue(price);
        holder.priceTextView.setText(price);
        holder.percentageChangeTextView.setText(String.format("%.2f",Double.parseDouble(analysis.getPercentageChange()))+ "%");
        holder.rankTextView.setText(analysis.getRank());
    }

    @Override
    public int getItemCount() {
        if (analysisList != null) {
            return analysisList.size();
        } else {
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView coinSymbolTextView, priceTextView, percentageChangeTextView,rankTextView;
        ImageView coinImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            coinSymbolTextView = itemView.findViewById(R.id.coin_name);
            priceTextView = itemView.findViewById(R.id.price);
            percentageChangeTextView = itemView.findViewById(R.id.percentage_change);
            rankTextView = itemView.findViewById(R.id.rank);
            coinImageView = itemView.findViewById(R.id.coin_ImageView);

//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                }
//            });
        }
    }

    public void setExchanges(List<LambdaAnalysis> analysisList) {
        this.analysisList = analysisList;
        notifyDataSetChanged();
    }

    public List<LambdaAnalysis> getAnalysis() {
        return analysisList;
    }
}
