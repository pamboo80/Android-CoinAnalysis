package bangbit.in.coinanalysis.WhereToBuy;

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

import bangbit.in.coinanalysis.R;
import bangbit.in.coinanalysis.Util;
import bangbit.in.coinanalysis.pojo.Exchange;

import static bangbit.in.coinanalysis.Util.getTwoDecimalPointValue;
import static bangbit.in.coinanalysis.WhereToBuy.WhereToBuyFragment.isCurrencyFiat;
import static bangbit.in.coinanalysis.WhereToBuy.WhereToBuyFragment.symbol;

/**
 * Created by Nagarajan on 3/27/2018.
 */

public class WhereToBuyAdapter extends RecyclerView.Adapter<WhereToBuyAdapter.ViewHolder> {

    private ArrayList<Exchange> exchanges;
    private HashMap<String, Integer> exchangeIconHashMap;
    private Context context;
    ItemClickListner itemClickListner;

    public WhereToBuyAdapter(ArrayList<Exchange> exchanges, ItemClickListner itemClickListner) {
        this.exchanges = exchanges;
        this.itemClickListner = itemClickListner;
        exchangeIconHashMap = Util.getExchangeIconHashMap();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.where_to_buy_row, parent, false);
        return new WhereToBuyAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Exchange exchange = exchanges.get(position);
        if (exchangeIconHashMap.containsKey(exchange.getMARKET().toLowerCase())) {
            int drawable = exchangeIconHashMap.get(exchange.getMARKET().toLowerCase());
            Picasso.with(context).load(drawable).into(holder.exchangeImageView);
        } else {
            Picasso.with(context).load(R.drawable.default_coin).into(holder.exchangeImageView);
        }
        holder.exchageNameTextView.setText(exchange.getMARKET());
        if (isCurrencyFiat) {
            holder.volume24HourTextView.setText(symbol + Util
                    .getMillionValue(exchange.getVOLUME24HOURTO()));
            holder.priceTextView.setText(symbol + Util.checkForNull(getTwoDecimalPointValue(exchange.getPRICE())));
        } else {
            holder.volume24HourTextView.setText(Util.getMillionValue(exchange.getVOLUME24HOURTO()) + " " + symbol);
            holder.priceTextView.setText(Util.checkForNull(getTwoDecimalPointValue(exchange.getPRICE())) + " " + symbol);
        }
//        holder.pairTextView.setText(exchange.getFROMSYMBOL()+"/"+exchange.getTOSYMBOL());
    }

    @Override
    public int getItemCount() {
        if (exchanges != null) {
            return exchanges.size();
        } else {
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView exchageNameTextView, priceTextView, volume24HourTextView;
        ImageView exchangeImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            exchageNameTextView = itemView.findViewById(R.id.exchange_name);
//            pairTextView = itemView.findViewById(R.id.exchange_pair);
            priceTextView = itemView.findViewById(R.id.price);
            volume24HourTextView = itemView.findViewById(R.id.volume_24_hour);
            exchangeImageView = itemView.findViewById(R.id.exchange_ImageView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Exchange exchange = exchanges.get(getAdapterPosition());
                    itemClickListner.itemClick(exchange.getMARKET());
                }
            });
        }
    }

    public void setExchanges(ArrayList<Exchange> exchanges) {
        this.exchanges = exchanges;
        notifyDataSetChanged();
    }

    public ArrayList<Exchange> getExchanges() {
        return exchanges;
    }
}
