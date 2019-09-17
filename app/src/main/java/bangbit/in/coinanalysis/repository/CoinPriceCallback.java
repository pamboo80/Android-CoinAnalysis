package bangbit.in.coinanalysis.repository;

import java.util.ArrayList;
import java.util.HashMap;

import bangbit.in.coinanalysis.pojo.Coin;

/**
 * Created by Nagarajan on 4/2/2018.
 */

public interface CoinPriceCallback {
    void coinCallback(HashMap<String, String> coinHashMap, ArrayList<Coin> coinArrayList, boolean isSuccess);
}
