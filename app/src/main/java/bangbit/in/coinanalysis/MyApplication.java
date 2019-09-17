package bangbit.in.coinanalysis;

import android.app.Application;
import android.util.Log;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import bangbit.in.coinanalysis.NetworkCall.GetValueFromUsd;
import bangbit.in.coinanalysis.database.DatabaseHelper;
import bangbit.in.coinanalysis.pojo.Coin;
import bangbit.in.coinanalysis.pojo.Currency;

/**
 * Created by Nagarajan on 4/3/2018.
 */

public class MyApplication extends Application {
    public ArrayList<Coin> coinArrayList = null;
    public static String lastUpdatedString="";
    public static float currencyMultiplyingFactor = 1;
    public static String currencySymbol = "$";
    private ArrayList<ListUpdatorListner> listUpdatorListners = new ArrayList<>();
    public static Currency currency;
    public static HashMap<String, Float> currencyMultiplyingFactorMap;
    public static long openTimeStamp= 0;
    private DatabaseHelper databaseHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        //databaseHelper = new DatabaseHelper(this);
        //databaseHelper.init();
    }

    public void setCurrencyToDatabase() {
        databaseHelper = new DatabaseHelper(this);
        currencyMultiplyingFactorMap = databaseHelper.getCurrencyFactor();

        final Currency defaultCurrency = Util.getCurrency(this);
        setCurrency(defaultCurrency);

        GetValueFromUsd.getValueFromUsd(new CurrencyUpdator() {
            @Override
            public void updateCurrency(JSONObject rates) {
                if(rates!=null)
                {
                    for (final Currency currencyFor : Util.getCurrencyList()) {

                        Log.d("My Application", "updateCurrency: ");
                        float factor = 0;
                        try {
                            factor = Float.parseFloat(rates.getString(currencyFor.getSymbol()));
                            currencyMultiplyingFactorMap.put(currencyFor.getSymbol(), factor);
                            databaseHelper.updateCurrencyFactor(currencyFor.getSymbol(), factor);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if(defaultCurrency.getSymbol()==currencyFor.getSymbol())
                        {
                            currencyMultiplyingFactor = currencyMultiplyingFactorMap.get(currency.getSymbol());
                            //currencyMultiplyingFactor = factor;
                        }
                    }
                }
            }

        });
    }

    public void setCoinArrayList(ArrayList<Coin> coinArrayList) {
        this.coinArrayList = coinArrayList;
        for (ListUpdatorListner listUpdatorListner : listUpdatorListners) {
            listUpdatorListner.updateList(coinArrayList);
        }
    }

    public void addListUpdatorListners(ListUpdatorListner listUpdatorListner) {
        listUpdatorListners.add(listUpdatorListner);
    }

    public void removeListUpdatorListners(ListUpdatorListner listUpdatorListner) {
        listUpdatorListners.remove(listUpdatorListner);
    }

    public void setListUpdatorListners(ArrayList<ListUpdatorListner> listUpdatorListners) {
        this.listUpdatorListners = listUpdatorListners;
    }

    public float getCurrencyMultiplyingFactor() {
        return currencyMultiplyingFactor;
    }

    public void setCurrencyMultiplyingFactor(float currencyMultiplyingFactor) {
        MyApplication.currencyMultiplyingFactor = currencyMultiplyingFactor;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(final Currency currency) {
        currencySymbol = currency.getCurrencylogo();
        MyApplication.currency = currency;
        Util.setCurrency(currency, this);

        if(currencyMultiplyingFactorMap !=null && currencyMultiplyingFactorMap.get(currency.getSymbol())!=null)
        {
            currencyMultiplyingFactor = currencyMultiplyingFactorMap.get(currency.getSymbol());
        }
        else
        {

//            GetValueFromUsd.getValueFromUsd(new CurrencyUpdator() {
//                @Override
//                public void updateCurrency(JSONObject rates) {
//                }
//                @Override
//                public void updateCurrencyUSD(String currencyUSDValue) {
//                }
//
//                @Override
//                public void updateBTCUSD(String btcUsdValue) {
                    GetValueFromUsd.getValueFromUsd(new CurrencyUpdator() {
                        @Override
                        public void updateCurrency(JSONObject rates) {
                            if(rates!=null)
                            {
                                try {
                                    currencyMultiplyingFactor = Float.parseFloat(rates.getString(currency.getSymbol()));
                                    databaseHelper.updateCurrencyFactor(currency.getSymbol(), currencyMultiplyingFactor);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                    });
//                }
//            });

        }

    }
}
