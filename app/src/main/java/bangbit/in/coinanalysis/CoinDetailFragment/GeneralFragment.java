package bangbit.in.coinanalysis.CoinDetailFragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import bangbit.in.coinanalysis.DetailActivity.CoinUpdator;
import bangbit.in.coinanalysis.DetailActivity.DetailActivity;
import bangbit.in.coinanalysis.MyApplication;
import bangbit.in.coinanalysis.R;
import bangbit.in.coinanalysis.pojo.Coin;

import static bangbit.in.coinanalysis.Constant.COIN;
import static bangbit.in.coinanalysis.MyApplication.currencyMultiplyingFactor;
import static bangbit.in.coinanalysis.MyApplication.currencySymbol;
import static bangbit.in.coinanalysis.Util.checkForNull;
import static bangbit.in.coinanalysis.Util.getComaValue;
import static bangbit.in.coinanalysis.Util.getFloatValue;
import static bangbit.in.coinanalysis.Util.getFormattedPrice;

public class GeneralFragment extends Fragment implements CoinUpdator{


    private static String TAG = GeneralFragment.class.getSimpleName();
    Coin coin;
    private String volume;
    private String mcap;
    private String availableSupplayValue;
    private String totalsupplyValue;
    private String maxSupplyValue;
    private String pricebtc;
    private String priceUsd;
    private String currencyNameSymbol=(MyApplication.currency!=null)?MyApplication.currency.getSymbol():"";

    TextView priceBTCTextView, priceUSDTextView, hour24VolumeTextView, mCapTextView,
            availableSupplyTextView, totalSupplyTextView, maxSupplyTextView,
            change1hourTextView, change24hoursTextView, change7daysTextView,priceLableTextView,lastUpdatedTextView,rankTextView;
    private DetailActivity detailActivity;

    public static GeneralFragment newInstance(Coin coin) {
        GeneralFragment fragment = new GeneralFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        detailActivity = (DetailActivity) getActivity();
        coin = detailActivity.getCoin();
        detailActivity.addCoinUpdator(this);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_coin_detail_general, container, false);

        priceLableTextView=view.findViewById(R.id.price_lable_textView);
        priceLableTextView.setText(getResources().getString(R.string.price)+" ["+currencyNameSymbol+"] :");
        priceBTCTextView = view.findViewById(R.id.price_btc);
        priceUSDTextView = view.findViewById(R.id.price_usd);
        hour24VolumeTextView = view.findViewById(R.id.hour_24_volume);
        mCapTextView = view.findViewById(R.id.mcap);
        availableSupplyTextView = view.findViewById(R.id.available_supply);
        totalSupplyTextView = view.findViewById(R.id.total_supply);
        maxSupplyTextView = view.findViewById(R.id.max_supply);
        change1hourTextView = view.findViewById(R.id.change_1_hour);
        change24hoursTextView = view.findViewById(R.id.change_24_hour);
        change7daysTextView = view.findViewById(R.id.change_7_days);
        lastUpdatedTextView = view.findViewById(R.id.last_updated);
        rankTextView =  view.findViewById(R.id.rank);

        if (savedInstanceState != null) {
            coin = savedInstanceState.getParcelable(COIN);
        }
        setData();
        return view;
    }

    private void setData() { //@@@ Crash reported here

        pricebtc = checkForNull(coin.getPriceBtc()) + " BTC";
        priceUsd = getFormattedPrice(coin.getPriceUsd());

        volume = String.valueOf(getFloatValue(coin.get24hVolumeUsd()) * currencyMultiplyingFactor);
        mcap = String.valueOf(getFloatValue(coin.getMarketCapUsd()) * currencyMultiplyingFactor);

        volume = currencySymbol + checkForNull(volume);
        mcap = currencySymbol + checkForNull(mcap) ;
        availableSupplayValue = checkForNullWithoutDecimal(coin.getAvailableSupply());
        totalsupplyValue = checkForNullWithoutDecimal(coin.getTotalSupply());
        maxSupplyValue = checkForNullWithoutDecimal(coin.getMaxSupply());

        priceUsd = containNA(priceUsd);
        volume = containNA(volume);
        mcap = containNA(mcap);
        availableSupplayValue = ifValueZero(availableSupplayValue);
        totalsupplyValue = ifValueZero(totalsupplyValue);
        maxSupplyValue = ifValueZero(maxSupplyValue);

        priceBTCTextView.setText(pricebtc);
        priceUSDTextView.setText(priceUsd);
        hour24VolumeTextView.setText(volume);
        mCapTextView.setText(mcap);
        availableSupplyTextView.setText(availableSupplayValue);
        totalSupplyTextView.setText(totalsupplyValue);
        maxSupplyTextView.setText(maxSupplyValue);

        try{
            float hour1, hour24, day7;
            if (coin.getPercentChange1h() != null) {
                hour1 = Float.parseFloat(coin.getPercentChange1h());
                if (hour1 < 0) {
                    change1hourTextView.setTextColor(getResources().getColor(R.color.red));
                } else {
                    change1hourTextView.setTextColor(getResources().getColor(R.color.green));
                }
                change1hourTextView.setText(coin.getPercentChange1h() + " %");
            }
            if (coin.getPercentChange24h() != null) {
                hour24 = Float.parseFloat(coin.getPercentChange24h());
                if (hour24 < 0) {
                    change24hoursTextView.setTextColor(getResources().getColor(R.color.red));
                } else {
                    change24hoursTextView.setTextColor(getResources().getColor(R.color.green));
                }
                change24hoursTextView.setText(coin.getPercentChange24h() + " %");
            }
            if (coin.getPercentChange7d() != null) {
                day7 = Float.parseFloat(coin.getPercentChange7d());
                if (day7 < 0) {
                    change7daysTextView.setTextColor(getResources().getColor(R.color.red));
                } else {
                    change7daysTextView.setTextColor(getResources().getColor(R.color.green));
                }
                change7daysTextView.setText(coin.getPercentChange7d() + " %");
            }
        }
        catch(NumberFormatException ex) {
        }

        rankTextView.setText(ifValueZero(coin.getRank()));
        lastUpdatedTextView.setText( MyApplication.lastUpdatedString.replace("Data last updated on ",""));

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(COIN, coin);
    }

    String checkForNullWithoutDecimal(String value) {
        if (value == null || value.compareToIgnoreCase("")==0) {
            return "NA";
        } else {
            try
            {
                long val = (long) Double.parseDouble(value);
                value = String.valueOf(val);
                return getComaValue(value) + " " + coin.getSymbol();
            }
            catch (NumberFormatException e) {
                return "NA";
            }
        }
    }

    String ifValueZero(String value)
    {
        value= value.trim();
        if(value.compareTo("")==0 || value.startsWith("0 ")
                || value.startsWith("0.0 ") || value.startsWith("0.00 ")
                || value.startsWith("00 ") || value.contains("NA "))
        {
            return "NA";
        }
        return value;
    }

    String containNA(String value) {
        if (value.contains("NA")) {
            return "NA";
        } else {
            return ifValueZero(value);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        detailActivity.removeCoinUpdator(this);
        super.onDetach();

    }

    @Override
    public void updateCoin(Coin coin) {
        this.coin=coin;
        setData();
    }
}
