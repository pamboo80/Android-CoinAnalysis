package bangbit.in.coinanalysis.MyInvestmentFragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import bangbit.in.coinanalysis.R;
import bangbit.in.coinanalysis.Util;
import bangbit.in.coinanalysis.pojo.Investment;

import static bangbit.in.coinanalysis.MyApplication.currencyMultiplyingFactor;
import static bangbit.in.coinanalysis.MyApplication.currencySymbol;


/**
 * Created by Nagarajan on 3/28/2018.
 */

public class InvestmentAdapter extends RecyclerView.Adapter<InvestmentAdapter.ViewHolder> {

    private ArrayList<Investment> investmentList;
    private float currentPrice = 0;
    private Context context;
    ListItemListner listItemListner;

    public InvestmentAdapter(ArrayList<Investment> investmentList, ListItemListner listItemListner) {
        this.investmentList = investmentList;
        if (MyInvestmentFragment.coin != null && MyInvestmentFragment.coin.getPriceUsd() != null) {
            currentPrice = Float.parseFloat(MyInvestmentFragment.coin.getPriceUsd());
        }

        this.listItemListner = listItemListner;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.my_investment_row, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Investment investment = investmentList.get(position);
        float cost = investment.getQuantity() * investment.getBoughtPrice();
        cost *= currencyMultiplyingFactor;

        String boughtPrice = Util.getTwoDecimalPointValue(investment.getBoughtPrice() * currencyMultiplyingFactor);
        holder.quantityTextView.setText("" + Util.getTwoDecimalPointValue(investment.getQuantity()));
        holder.dateTextView.setText("" + Util.getDayMonthYearFromUnixTime(investment.getBoughtDate()));
        holder.boughtPriceTextView.setText(currencySymbol + Util.checkForNull(boughtPrice));
        holder.costTextView.setText(currencySymbol + Util.checkForNull(Util.getTwoDecimalPointValue(cost)));

        float profit = 0;
        if (investment.getBoughtPrice() == 0) {
            profit = currentPrice * 100;
        } else {
            profit = ((currentPrice - investment.getBoughtPrice()) / investment.getBoughtPrice()) * 100;
        }
        String profitString = Util.getOnlyTwoDecimalPointValue(profit) + "%";
        holder.profitTextView.setText(profitString);
        if (profit >= 0) {
            holder.profitTextView.setTextColor(context.getResources().getColor(R.color.green));
        } else {
            holder.profitTextView.setTextColor(context.getResources().getColor(R.color.red));
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listItemListner.onItemClickListner(investment);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (investmentList != null) {
            return investmentList.size();
        } else {
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView quantityTextView, dateTextView, boughtPriceTextView, costTextView, profitTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            quantityTextView = itemView.findViewById(R.id.quantity_textView);
            dateTextView = itemView.findViewById(R.id.date_textView);
            boughtPriceTextView = itemView.findViewById(R.id.bought_price_textView);
            costTextView = itemView.findViewById(R.id.cost_textView);
            profitTextView = itemView.findViewById(R.id.profit_textView);
        }
    }

    public ArrayList<Investment> getInvestmentList() {
        return investmentList;
    }

    public void setInvestmentList(ArrayList<Investment> investmentList) {
        this.investmentList = investmentList;
        notifyDataSetChanged();
    }
}
