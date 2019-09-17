package bangbit.in.coinanalysis.ChatModule;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import bangbit.in.coinanalysis.Chat.ChatData;
import bangbit.in.coinanalysis.R;
import bangbit.in.coinanalysis.Util;
import bangbit.in.coinanalysis.pojo.Exchange;

import static bangbit.in.coinanalysis.Constant.WHERE_TO_BUY;

public class ExchangeDataViewHolder extends RecyclerView.ViewHolder {

    private final Context context;
    private final LinearLayout linearLayout;
    private final HolderView chatView;
    private final HashMap<String, Integer> exchangeIconHashMap;
    boolean isBind = false;
    private ArrayList<Exchange> exchanges;
    private ChatData chatData;
    private final TextView exchangeDetailTextView;

    public ExchangeDataViewHolder(View itemView, HolderView chatView) {
        super(itemView);
        linearLayout = itemView.findViewById(R.id.exchangeLinearLayout);
        exchangeDetailTextView = itemView.findViewById(R.id.exchangeDetailTextView);
        context = itemView.getContext();
        this.chatView = chatView;
        exchangeIconHashMap = Util.getExchangeIconHashMap();
    }

    void bind(ChatData chatData) {
        linearLayout.removeAllViews();
        this.chatData = chatData;
        this.exchanges = chatData.getExchangeArrayList();
        addData();
    }

    void addData() {

        exchangeDetailTextView.setText("Below are the few of the exchanges to trade "+ chatData.getMessage().toUpperCase()+ " with " + chatData.getExchangeCurrency().toUpperCase());
        exchangeDetailTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatView.showDetail(chatData,WHERE_TO_BUY);
            }
        });
        String currency = chatData.getExchangeCurrency();
        String symbol=Util.isFiatCurrency(currency);
        Boolean isFiat = symbol.contains("1:");
        symbol = symbol.substring(2);

        if (exchanges.size() < 5) {
            for (Exchange exchange :
                    exchanges) {
                View itemView = LayoutInflater.from(context).inflate(R.layout.where_to_buy_row, null, false);
                TextView exchageNameTextView, priceTextView, volume24HourTextView;
                ImageView exchangeImageView;
                exchageNameTextView = itemView.findViewById(R.id.exchange_name);
                priceTextView = itemView.findViewById(R.id.price);
                volume24HourTextView = itemView.findViewById(R.id.volume_24_hour);
                exchangeImageView = itemView.findViewById(R.id.exchange_ImageView);
                exchageNameTextView.setText(exchange.getMARKET());

                if (isFiat) {
                    volume24HourTextView.setText(symbol + Util
                            .getMillionValue(exchange.getVOLUME24HOURTO()));
                    priceTextView.setText(symbol + Util.checkForNull(Util.getTwoDecimalPointValue(exchange.getPRICE())));
                } else {
                    volume24HourTextView.setText(Util.getMillionValue(exchange.getVOLUME24HOURTO()) + " " + symbol);
                    priceTextView.setText(Util.checkForNull(Util.getTwoDecimalPointValue(exchange.getPRICE())) + " " + symbol);
                }

                if (exchangeIconHashMap.containsKey(exchange.getMARKET().toLowerCase())) {
                    int drawable = exchangeIconHashMap.get(exchange.getMARKET().toLowerCase());
                    Picasso.with(context).load(drawable).into(exchangeImageView);
                } else {
                    Picasso.with(context).load(R.drawable.default_coin).into(exchangeImageView);
                }
                linearLayout.addView(itemView);
            }
        } else {
            for (int i = 0; i < 5; i++) {
                Exchange exchange = exchanges.get(i);
                View itemView = LayoutInflater.from(context).inflate(R.layout.where_to_buy_row, null, false);
                TextView exchageNameTextView, priceTextView, volume24HourTextView;
                ImageView exchangeImageView;
                exchageNameTextView = itemView.findViewById(R.id.exchange_name);
                priceTextView = itemView.findViewById(R.id.price);
                volume24HourTextView = itemView.findViewById(R.id.volume_24_hour);
                exchangeImageView = itemView.findViewById(R.id.exchange_ImageView);

                exchageNameTextView.setText(exchange.getMARKET());

                if (isFiat) {
                    volume24HourTextView.setText(symbol + Util
                            .getMillionValue(exchange.getVOLUME24HOURTO()));
                    priceTextView.setText(symbol + Util.checkForNull(Util.getTwoDecimalPointValue(exchange.getPRICE())));
                } else {
                    volume24HourTextView.setText(Util.getMillionValue(exchange.getVOLUME24HOURTO()) + " " + symbol);
                    priceTextView.setText(Util.checkForNull(Util.getTwoDecimalPointValue(exchange.getPRICE())) + " " + symbol);
                }

                if (exchangeIconHashMap.containsKey(exchange.getMARKET().toLowerCase())) {
                    int drawable = exchangeIconHashMap.get(exchange.getMARKET().toLowerCase());
                    Picasso.with(context).load(drawable).into(exchangeImageView);
                } else {
                    Picasso.with(context).load(R.drawable.default_coin).into(exchangeImageView);
                }
                linearLayout.addView(itemView);
            }
        }
        View moreDataView = LayoutInflater.from(context).inflate(R.layout.more_data_row, null, false);
        linearLayout.addView(moreDataView);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatView.showDetail(chatData,WHERE_TO_BUY);
            }
        });
    }
}
