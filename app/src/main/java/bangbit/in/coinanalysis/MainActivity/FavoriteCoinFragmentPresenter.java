package bangbit.in.coinanalysis.MainActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import bangbit.in.coinanalysis.Constant;
import bangbit.in.coinanalysis.ListOperations;
import bangbit.in.coinanalysis.pojo.Coin;
import bangbit.in.coinanalysis.repository.CoinImageUrlHashMapCallback;
import bangbit.in.coinanalysis.repository.CoinRepository;

import static bangbit.in.coinanalysis.Constant.SORT_BY_1_HOUR;
import static bangbit.in.coinanalysis.Constant.SORT_BY_24_HOURS;
import static bangbit.in.coinanalysis.Constant.SORT_BY_7_DAY;

/**
 * Created by Nagarajan on 3/7/2018.
 */

public class FavoriteCoinFragmentPresenter implements FavoriteCoinFragmentContract.UserActionsListener {

    private final FavoriteCoinFragmentContract.View mCoinView;
    private final CoinRepository mCoinRepository;
    int coinNetworkCallcount = 0;
    boolean isLoadedCoins;
    private String TAG=FavoriteCoinFragmentPresenter.class.getSimpleName();

    public FavoriteCoinFragmentPresenter(FavoriteCoinFragmentContract.View mCoinView, CoinRepository mCoinRepository) {
        this.mCoinView = mCoinView;
        this.mCoinRepository = mCoinRepository;

    }

    @Override
    public void getImageHashMap() {

        mCoinRepository.getCoinUrl(new CoinImageUrlHashMapCallback() {
            @Override
            public void coinUrlCallback(HashMap<String, String> coinImageUrlHashmap, boolean isSuccessCall) {
                if (isSuccessCall) {
                    mCoinView.updateImageUrlHashMap(coinImageUrlHashmap);
                } else {
                    if (isLoadedCoins) {
                        coinNetworkCallcount++;
                    }
                    if (coinNetworkCallcount <= 3) {
                        getImageHashMap();
                    }

                }
            }
        });
    }



    @Override
    public void sortList(int sortID, ArrayList<Coin> coinArrayList) {
        switch (sortID) {
            case Constant.SORT_BY_PRICE:
                coinArrayList = ListOperations.sortByPrice(coinArrayList);
                break;
            case Constant.SORT_BY_VOLUME:
                coinArrayList = ListOperations.sortByVolume(coinArrayList);
                break;
            case Constant.SORT_BY_MARKET_CAP:
                coinArrayList = ListOperations.sortByMarketcap(coinArrayList);
                break;
            case Constant.SORT_BY_RANK:
                coinArrayList = ListOperations.sortByRank(coinArrayList);
                break;
            case Constant.SORT_BY_SUPPLY:
                coinArrayList = ListOperations.sortBySupply(coinArrayList);
                break;
            case Constant.SORT_BY_NAME:
                coinArrayList = ListOperations.sortByName(coinArrayList);
                break;
            case SORT_BY_7_DAY:
                coinArrayList = ListOperations.sortBy7Day(coinArrayList);
                break;
            case SORT_BY_24_HOURS:
                coinArrayList = ListOperations.sortBy24Hrs(coinArrayList);
                break;
            case SORT_BY_1_HOUR:
                coinArrayList = ListOperations.sortBy1Hr(coinArrayList);
                break;
        }
        mCoinView.updateAdapterList(coinArrayList);
    }

    public void filterList(ArrayList<Coin> coinArrayList, String query) {
        coinArrayList=ListOperations.filterList(coinArrayList,query);
        mCoinView.updateAdapterList(coinArrayList);
    }

    @Override
    public void filterList(ArrayList<Coin> coinArrayList, String query, int day7, int hours24, int hour1) {
        coinArrayList=ListOperations.filterList(coinArrayList,query,day7,hours24,hour1);
        mCoinView.updateAdapterList(coinArrayList);
    }


    @Override
    public void reverseList(ArrayList<Coin> coinArrayList) {
        coinArrayList = ListOperations.reverse(coinArrayList);
        mCoinView.updateAdapterList(coinArrayList);
    }


    @Override
    public void loadImageUrlFromDb() {
        mCoinRepository.getCoinUrlFromDb(new CoinImageUrlHashMapCallback() {
            @Override
            public void coinUrlCallback(HashMap<String, String> coinImageUrlHashmap, boolean isSuccessCall) {
                if (isSuccessCall) {
                    mCoinView.updateImageUrlHashMap(coinImageUrlHashmap);
                }
            }
        });
    }

    @Override
    public void insertFavorite(String symbol, boolean isInserting) {
        if (isInserting) {
            mCoinRepository.insertFavoriteCoin(symbol);
        } else {
            mCoinRepository.removeFavoriteCoin(symbol);
        }

    }

    @Override
    public void udpateFavorite() {
        Set<String> favoriteCoinSet =mCoinRepository.getFavoriteSet();
        mCoinView.updateFavoriteSet(favoriteCoinSet);
    }


}

//    public ArrayList<Coin> getFavoriteList(ArrayList<Coin> coinArrayList) {
//        Set<String> favoriteCoinSet = mCoinRepository.getFavoriteSet();
//        ArrayList<Coin> coinArrayListFavorite = new ArrayList<>();
//        for (Coin coin : coinArrayList) {
//            if (favoriteCoinSet.contains(coin.getSymbol())) {
//                coinArrayListFavorite.add(coin);
//            }
//        }
//        return coinArrayListFavorite;
//    }