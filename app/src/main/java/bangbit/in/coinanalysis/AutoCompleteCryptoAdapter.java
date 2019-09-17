package bangbit.in.coinanalysis;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;

import bangbit.in.coinanalysis.pojo.Coin;

/**
 * Created by Nagarajan on 12/19/2017.
 */

public class AutoCompleteCryptoAdapter extends ArrayAdapter<Coin> {
    private ArrayList<Coin> coinArrayList;
    private ArrayList<Coin> coinAll;
    private final int rowLayout;

    public AutoCompleteCryptoAdapter(Context context, int resource, ArrayList<Coin> coinArrayList) {
        super(context, resource, coinArrayList);
        this.rowLayout = resource;
        this.coinArrayList = coinArrayList;
        this.coinAll = new ArrayList<>(coinArrayList);
    }

    public int getCount() {
        return coinArrayList.size();
    }

    public Coin getItem(int position) {
        return coinArrayList.get(position);
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
            Coin coin = getItem(position);
            TextView name = convertView.findViewById(R.id.autoCompleteTextViewName);
            name.setText(coin.getName() + " (" + coin.getSymbol() + ")");
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
                return ((Coin) resultValue).getSymbol();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults filterResults = new FilterResults();
                ArrayList<Coin> coinArrayListSuggestion = new ArrayList<>();
                if (constraint != null) {
                    for (Coin coin : coinAll) {
                        if (coin.getSymbol().toLowerCase().startsWith(constraint.toString().toLowerCase()) ||
                                coin.getName().toLowerCase().startsWith(constraint.toString().toLowerCase())) {
                            coinArrayListSuggestion.add(coin);
                        }
                    }
                    filterResults.values = coinArrayListSuggestion;
                    filterResults.count = coinArrayListSuggestion.size();
                } else {
                    coinArrayList.addAll(coinAll);
                    filterResults.values = coinArrayListSuggestion;
                    filterResults.count = coinArrayListSuggestion.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                coinArrayList.clear();
                if (results != null && results.count > 0) {
                    // avoids unchecked cast warning when using mDepartments.addAll((ArrayList<Department>) results.values);
                    coinArrayList = (ArrayList<Coin>) results.values;
                    notifyDataSetChanged();
                } else if (constraint == null) {
                    // no filter, add entire original ArrayList back in
                    coinArrayList.addAll(coinAll);
                    notifyDataSetChanged();

                }
            }
        };
    }

    public ArrayList<Coin> getCoinAll() {
        return coinAll;
    }

    public void setCoinArrayList(ArrayList<Coin> coinArrayList) {
        this.coinArrayList = coinArrayList;
        coinAll.clear();
        coinAll.addAll(coinArrayList);
    }
}