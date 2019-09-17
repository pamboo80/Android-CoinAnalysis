package bangbit.in.coinanalysis.BottomSheetPair;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import bangbit.in.coinanalysis.R;
import bangbit.in.coinanalysis.pojo.Coin;

/**
 * Created by Nagarajan on 4/6/2018.
 */

public class PairAdapter extends RecyclerView.Adapter<PairAdapter.ViewHolder> {
    private final ArrayList<String> pairs;

    public int getSelectedPosition() {
        return selectedPosition;
    }

    private int selectedPosition = -1;
    private Context context;
    OnPairInteractionListener onPairInteractionListener;
    String currency;
    Coin coin;

    public PairAdapter(ArrayList<String> pairs, OnPairInteractionListener onPairInteractionListener, String currency, Coin coin) {
        this.pairs = pairs;
        this.onPairInteractionListener = onPairInteractionListener;
        this.currency = currency;
        this.coin = coin;
        for (int i = 0; i < pairs.size(); i++) {
            if (pairs.get(i).equals(currency)) {
                selectedPosition = i;
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.pair_row, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        holder.pairTextView.setText(coin.getSymbol() + "/" + pairs.get(position));
        if (selectedPosition == position) {
            holder.pairTextView.setTextColor(context.getResources().getColor(R.color.coin_header));
            holder.pairTextView.setTextAppearance(context, R.style.bold_16sp_pink);
        } else {
            holder.pairTextView.setTextColor(context.getResources().getColor(R.color.textColor));
            holder.pairTextView.setTextAppearance(context, R.style.regular_16sp_textColor);
        }
        holder.pairTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPosition = position;
                notifyDataSetChanged();
                onPairInteractionListener.onPairInteraction(pairs.get(position));

            }
        });
    }

    @Override
    public int getItemCount() {
        return pairs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView pairTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            pairTextView = itemView.findViewById(R.id.pair_TextView);
        }
    }
}
