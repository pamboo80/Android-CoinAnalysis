package bangbit.in.coinanalysis.ChatModule;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bangbit.in.coinanalysis.Chat.ChatData;
import bangbit.in.coinanalysis.R;
import bangbit.in.coinanalysis.Util;
import bangbit.in.coinanalysis.database.DatabaseHelper;
import bangbit.in.coinanalysis.pojo.Exchange;
import bangbit.in.coinanalysis.pojo.LambdaAnalysis;

import static android.content.ContentValues.TAG;
import static bangbit.in.coinanalysis.Constant.ANALYSIS;
import static bangbit.in.coinanalysis.Constant.WHERE_TO_BUY;
import static bangbit.in.coinanalysis.MyApplication.currencySymbol;
import static bangbit.in.coinanalysis.Util.multiplyWithCurrency;

public class AnalysisDataViewHolder extends RecyclerView.ViewHolder {

    private final Context context;
    private final LinearLayout linearLayout;
    private final HolderView chatView;
    private HashMap<String, String> coinImageUrlHashmap ;
    boolean isBind = false;
    private List<LambdaAnalysis> analysisData;
    private ChatData chatData;
    private final TextView analysisDetailTextView;
    private DatabaseHelper databaseHelper;

    public AnalysisDataViewHolder(View itemView, HolderView chatView) {
        super(itemView);
        linearLayout = itemView.findViewById(R.id.analysisLinearLayout);
        analysisDetailTextView = itemView.findViewById(R.id.analyisDetailTextView);
        context = itemView.getContext();
        this.chatView = chatView;
        databaseHelper = new DatabaseHelper(context);
        coinImageUrlHashmap = databaseHelper.getImageUrlHashMap();
    }

    void bind(ChatData chatData) {
        linearLayout.removeAllViews();
        this.chatData = chatData;
        this.analysisData = chatData.getLambdaResponse().getAnalysis();
        addData();
    }

    void addData() {

        analysisDetailTextView.setText("Below are the cryptocurrencies listed based on the percentage changes of their price values from "
                + chatData.getLambdaResponse().getFromDate()+ " to " + chatData.getLambdaResponse().getToDate());
        analysisDetailTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatView.showDetail(chatData,ANALYSIS);
            }
        });

        int size = analysisData.size();
        size = (size>5)? size=5:size;
        for (int i = 0; i < size; i++) {
            LambdaAnalysis analysis = analysisData.get(i);
            View itemView = LayoutInflater.from(context).inflate(R.layout.analysis_row, null, false);
            TextView coinNameTextView, priceTextView, percentageChangeTextView, rankTextView;
            ImageView coinImageView;
            coinNameTextView = itemView.findViewById(R.id.coin_name);
            priceTextView = itemView.findViewById(R.id.price);
            percentageChangeTextView = itemView.findViewById(R.id.percentage_change);
            rankTextView = itemView.findViewById(R.id.rank);
            coinImageView = itemView.findViewById(R.id.coin_ImageView);
            coinNameTextView.setText(analysis.getCoinSymbol());

            String price = multiplyWithCurrency(analysis.getPrice());
            price = Util.checkForNull(price);
            price = currencySymbol + Util.getTwoDecimalPointValue(price);
            priceTextView.setText(price);

            percentageChangeTextView.setText(String.format("%.2f",Double.parseDouble(analysis.getPercentageChange())) + "%");
            rankTextView.setText(analysis.getRank());

            final String imageUrl = coinImageUrlHashmap.get(analysis.getCoinSymbol());
            if (imageUrl != null) {
                Picasso.with(context).load(imageUrl).placeholder(R.drawable.default_coin).fit().into(coinImageView);
            } else {
                Picasso.with(context).load(R.drawable.default_coin).fit().into(coinImageView);
            }

            linearLayout.addView(itemView);
        }

        View moreDataView = LayoutInflater.from(context).inflate(R.layout.more_data_row, null, false);
        linearLayout.addView(moreDataView);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatView.showDetail(chatData,ANALYSIS);
            }
        });
    }
}
