package bangbit.in.coinanalysis.DashboardInvestmentActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import bangbit.in.coinanalysis.R;
import bangbit.in.coinanalysis.Util;
import bangbit.in.coinanalysis.pojo.DashInvestment;

import static bangbit.in.coinanalysis.MyApplication.currencyMultiplyingFactor;
import static bangbit.in.coinanalysis.MyApplication.currencySymbol;

/**
 * Created by Nagarajan on 4/2/2018.
 */

public class DashInvestmentRecyclerViewAdapter extends RecyclerView.Adapter<DashInvestmentRecyclerViewAdapter.ViewHolder> {
    private final RecyclerViewItemClick recyclerViewItemClick;
    private ArrayList<DashInvestment> dashInvestments;
    private Context context;

    public DashInvestmentRecyclerViewAdapter(ArrayList<DashInvestment> dashInvestments, RecyclerViewItemClick recyclerViewItemClick) {
        this.recyclerViewItemClick = recyclerViewItemClick;
        this.dashInvestments = dashInvestments;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.main_investment_row, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final DashInvestment dashInvestment = dashInvestments.get(position);
        Picasso.with(context).load(dashInvestment.getImageUrl()).placeholder(R.drawable.default_coin)
                .into(holder.coinLogoImageView);

        String totalCurrentprice = Util.getTwoDecimalPointValue(dashInvestment.getCurrentInvestmentValue() * currencyMultiplyingFactor);
        String boughtPrice = Util.getTwoDecimalPointValue(dashInvestment.getBoughtInvestmentValue() * currencyMultiplyingFactor);
        String currentPrice = Util.getTwoDecimalPointValue(dashInvestment.getPrice() * currencyMultiplyingFactor);

        holder.coinQuantityTextView.setText("" + Util.getTwoDecimalPointValue(dashInvestment.getTotalQuantity()));
        holder.coinSymbolTextView.setText(dashInvestment.getSymbol());
        holder.totalCurrentPriceTextView.setText(currencySymbol + Util.checkForNull(totalCurrentprice));
        holder.boughtPriceTextView.setText(currencySymbol + Util.checkForNull(boughtPrice));
        holder.currentPriceTextView.setText(currencySymbol + Util.checkForNull(currentPrice));
        String gain = Util.getOnlyTwoDecimalPointValue(dashInvestment.getProfit()) + "%";

        holder.profitTextView.setText(gain);
        if (dashInvestment.getProfit() < 0) {
            holder.profitTextView.setTextColor(context.getResources().getColor(R.color.red));
        } else {
            holder.profitTextView.setTextColor(context.getResources().getColor(R.color.green));
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerViewItemClick.itemClick(dashInvestment);
            }
        });


    }

    @Override
    public int getItemCount() {
        if (dashInvestments != null) {
            return dashInvestments.size();
        }
        return 0;

    }

    public ArrayList<DashInvestment> getDashInvestments() {
        return dashInvestments;
    }

    public void setDashInvestments(ArrayList<DashInvestment> dashInvestments) {
        this.dashInvestments = dashInvestments;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView coinLogoImageView;
        TextView coinSymbolTextView, coinQuantityTextView, boughtPriceTextView, profitTextView, totalCurrentPriceTextView,
                currentPriceTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            coinLogoImageView = itemView.findViewById(R.id.coin_ImageView);
            coinSymbolTextView = itemView.findViewById(R.id.coin_name_textView);
            coinQuantityTextView = itemView.findViewById(R.id.coin_count_textView);
            boughtPriceTextView = itemView.findViewById(R.id.bought_price_textView);
            profitTextView = itemView.findViewById(R.id.profit_textView);
            totalCurrentPriceTextView = itemView.findViewById(R.id.cost_textView);
            currentPriceTextView = itemView.findViewById(R.id.price_textView);


        }
    }
}
