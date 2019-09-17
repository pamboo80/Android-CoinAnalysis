package bangbit.in.coinanalysis.Dialogs.FiatDialog;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import bangbit.in.coinanalysis.ListOperations;
import bangbit.in.coinanalysis.R;
import bangbit.in.coinanalysis.Util;
import bangbit.in.coinanalysis.pojo.Currency;

/**
 * Created by Nagarajan on 4/20/2018.
 */

public class CurrencyAdapter extends RecyclerView.Adapter<CurrencyAdapter.ViewHolder> {

    ArrayList<Currency> currencyArrayList;
    final ArrayList<Currency> originalCurrencyArrayList = new ArrayList<>();
    private Currency currency;
    private final CurrencySelector currencySelector;

    public int getSelectedPosition() {
        return selectedPosition;
    }

    private int selectedPosition;

    public CurrencyAdapter(Context context, CurrencySelector currencySelector, Currency currency) {
        this.currencySelector = currencySelector;
        currencyArrayList = Util.getCurrencyList();

        Collections.sort(currencyArrayList, new Comparator<Currency>() {
            @Override
            public int compare(Currency o1, Currency o2) {
                if (o1.getName() == null) {
                    return (o2.getName() == null) ? 0 : -1;
                }
                if (o2.getName() == null) {
                    return 1;
                }
                return o1.getName().compareTo(o2.getName());
            }
        });
        originalCurrencyArrayList.addAll(currencyArrayList);
        this.currency = currency;
        for (int i = 0; i < currencyArrayList.size(); i++) {
            if (currencyArrayList.get(i).getSymbol().equals(currency.getSymbol())) {
                selectedPosition = i;
                this.currency = currencyArrayList.get(i);
                break;
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.currency_row, parent, false);
        return new ViewHolder(view);
    }

    void filter(String query) {
        currencyArrayList = ListOperations.filterCurrencyList(originalCurrencyArrayList, query);
        selectedPosition = currencyArrayList.indexOf(currency);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Currency currencyAtPosition = currencyArrayList.get(position);
        if (selectedPosition == position) {
            holder.radioButton.setChecked(true);
        } else {
            holder.radioButton.setChecked(false);
        }
        holder.radioButton.setText(currencyAtPosition.getName() + " (" + currencyAtPosition.getSymbol() + ")");
        holder.radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPosition = position;
                currencySelector.selectcurrency(currencyAtPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return currencyArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RadioButton radioButton;

        public ViewHolder(View itemView) {
            super(itemView);
            radioButton = itemView.findViewById(R.id.currency_radioButton);
        }
    }
}
