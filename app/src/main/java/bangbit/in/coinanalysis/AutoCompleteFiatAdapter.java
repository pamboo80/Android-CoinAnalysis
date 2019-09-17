package bangbit.in.coinanalysis;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;

import bangbit.in.coinanalysis.pojo.Currency;


/**
 * Created by Nagarajan on 12/19/2017.
 */

public class AutoCompleteFiatAdapter extends ArrayAdapter<Currency> {
    private ArrayList<Currency> currencyArrayList;
    private ArrayList<Currency> currencyAll = new ArrayList<>();
    private final int rowLayout;

    public AutoCompleteFiatAdapter(Context context, int resource, ArrayList<Currency> currencyArrayList) {
        super(context, resource, currencyArrayList);
        this.rowLayout = resource;
        this.currencyArrayList = currencyArrayList;
        this.currencyAll.addAll(currencyArrayList);
    }

    public int getCount() {
        return currencyArrayList.size();
    }

    public Currency getItem(int position) {
        return currencyArrayList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());

                convertView = inflater.inflate(rowLayout, parent, false);
            }
            Currency Currency = getItem(position);

            TextView name = convertView.findViewById(R.id.autoCompleteTextViewName);
            name.setText(Currency.getName() + " (" + Currency.getSymbol() + ")");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            public String convertResultToString(Object resultValue) {
                return ((Currency) resultValue).getSymbol();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults filterResults = new FilterResults();
                ArrayList<Currency> currencyArrayListSuggestion = new ArrayList<>();
                if (constraint != null) {

                    for (Currency currency : currencyAll) {
                        if (currency.getName().toLowerCase().contains(constraint.toString().toLowerCase()) ||
                                currency.getSymbol().toLowerCase().contains(constraint.toString().toLowerCase())) {
                            currencyArrayListSuggestion.add(currency);
                        }
                    }
                    filterResults.values = currencyArrayListSuggestion;
                    filterResults.count = currencyArrayListSuggestion.size();
                } else {
                    currencyArrayList.addAll(currencyAll);
                    filterResults.values = currencyArrayListSuggestion;
                    filterResults.count = currencyArrayListSuggestion.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                currencyArrayList.clear();
                if (results != null && results.count > 0) {
                    // avoids unchecked cast warning when using mDepartments.addAll((ArrayList<Department>) results.values);
                    currencyArrayList = (ArrayList<Currency>) results.values;
                    notifyDataSetChanged();
                } else if (constraint == null) {
                    // no filter, add entire original ArrayList back in
                    currencyArrayList.addAll(currencyAll);
                    notifyDataSetChanged();

                }
            }
        };
    }
}