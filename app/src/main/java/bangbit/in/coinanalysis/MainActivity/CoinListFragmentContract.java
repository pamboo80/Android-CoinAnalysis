package bangbit.in.coinanalysis.MainActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import bangbit.in.coinanalysis.pojo.Coin;

/**
 * Created by Nagarajan on 3/7/2018.
 */

public interface CoinListFragmentContract {
    interface View {
        void setProgressIndicator(boolean active);
        void showCoins(ArrayList<Coin> coin);
        void updateAdapterList(ArrayList<Coin> coin);
        void updateImageUrlHashMap(HashMap<String, String> coinImageUrlHashMap);
        void updateCurrency(double multiplyValue);
        void updateFavoriteSet(Set<String> favoriteCoinSet);

    }

    interface UserActionsListener {
        void sortList(int sortID,ArrayList<Coin> coinArrayList);
        void sortWithReverse(int sortID,ArrayList<Coin> coinArrayList);
        void filterList(ArrayList<Coin> coinArrayList,String query);
        void filterList(ArrayList<Coin> coinArrayList,String query, int day7, int hours24, int hour1);
        void reverseList(ArrayList<Coin> coinArrayList);
        void loadImageUrlFromDb();
        void insertFavorite(String symbol,boolean isInserting);
        void udpateFavorite();
        void getImageHashMap();
        void getFavoriteSet();


    }
}
