package bangbit.in.coinanalysis.Dialogs.CryptoDialog;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import java.util.ArrayList;

import bangbit.in.coinanalysis.ListOperations;
import bangbit.in.coinanalysis.R;
import bangbit.in.coinanalysis.pojo.Coin;

public class CryptoCurrencyAdapter extends RecyclerView.Adapter<CryptoCurrencyAdapter.ViewHolder> {

    private Coin coin;
    private CryptoCurrencySelectorListner cryptoCurrencySelectorListner;
    private ArrayList<Coin> coinArrayList;
    private final ArrayList<Coin> originalCoinArrayList = new ArrayList<>();
    private int selectedPosition = 0;

    public CryptoCurrencyAdapter(ArrayList<Coin> coinArrayList,
                                 CryptoCurrencySelectorListner cryptoCurrencySelectorListner,
                                 Coin coin) {
        this.coin = coin;
        this.coinArrayList = coinArrayList;
        originalCoinArrayList.addAll(coinArrayList);
        this.cryptoCurrencySelectorListner = cryptoCurrencySelectorListner;
        selectedPosition = coinArrayList.indexOf(coin);
    }

    void filter(String query) {
        coinArrayList = ListOperations.filterList(originalCoinArrayList, query);
        selectedPosition = coinArrayList.indexOf(coin);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.select_crypto_currency_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Coin coin = coinArrayList.get(position);
        if (position == selectedPosition) {
            holder.radioButton.setChecked(true);
        } else {
            holder.radioButton.setChecked(false);
        }
        holder.radioButton.setText(coin.getName() + " (" + coin.getSymbol() + ")");
        holder.radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cryptoCurrencySelectorListner.onClickCurrency(coin);
            }
        });
    }

    @Override
    public int getItemCount() {
        return coinArrayList.size();
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final RadioButton radioButton;

        public ViewHolder(View itemView) {
            super(itemView);
            radioButton = itemView.findViewById(R.id.currency_radioButton);
        }
    }
}