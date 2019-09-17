package bangbit.in.coinanalysis.repository;

import java.util.ArrayList;

import bangbit.in.coinanalysis.pojo.Coin;

public interface CoinListCallbackWithFirstCallComplete {
    void coinCallback(ArrayList<Coin> coinArrayList, boolean isSuccess);
    void firstCallCompleted();
}
