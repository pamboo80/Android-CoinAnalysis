package bangbit.in.coinanalysis.repository;

import org.json.JSONArray;

import java.util.ArrayList;

import bangbit.in.coinanalysis.pojo.Coin;

/**
 * Created by Nagarajan on 3/13/2018.
 */

public interface CoinListCallback {
    void getCoinDetailPro(JSONArray jsonArray);
    void coinCallback(ArrayList<Coin> coinArrayList,boolean isSuccess);
    void coinCallbackFirst(JSONArray coinArrayList);
    void getDatabaseData(ArrayList<Coin> coinArrayList);
}
