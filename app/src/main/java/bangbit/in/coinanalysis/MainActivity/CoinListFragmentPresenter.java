package bangbit.in.coinanalysis.MainActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import bangbit.in.coinanalysis.ListOperations;
import bangbit.in.coinanalysis.pojo.Coin;
import bangbit.in.coinanalysis.repository.CoinImageUrlHashMapCallback;
import bangbit.in.coinanalysis.repository.CoinRepository;

import static bangbit.in.coinanalysis.Constant.SORT_BY_1_HOUR;
import static bangbit.in.coinanalysis.Constant.SORT_BY_24_HOURS;
import static bangbit.in.coinanalysis.Constant.SORT_BY_7_DAY;
import static bangbit.in.coinanalysis.Constant.SORT_BY_MARKET_CAP;
import static bangbit.in.coinanalysis.Constant.SORT_BY_NAME;
import static bangbit.in.coinanalysis.Constant.SORT_BY_PRICE;
import static bangbit.in.coinanalysis.Constant.SORT_BY_RANK;
import static bangbit.in.coinanalysis.Constant.SORT_BY_SUPPLY;
import static bangbit.in.coinanalysis.Constant.SORT_BY_VOLUME;

/**
 * Created by Nagarajan on 3/7/2018.
 */

public class CoinListFragmentPresenter implements CoinListFragmentContract.UserActionsListener {

    private final CoinListFragmentContract.View mCoinView;
    private final CoinRepository mCoinRepository;
    int coinNetworkCallcount = 0;

    public CoinListFragmentPresenter(CoinListFragmentContract.View mCoinView, CoinRepository mCoinRepository) {
        this.mCoinView = mCoinView;
        this.mCoinRepository = mCoinRepository;

    }


    @Override
    public void getFavoriteSet() {
        Set<String> favoriteCoinSet = mCoinRepository.getFavoriteSet();
        mCoinView.updateFavoriteSet(favoriteCoinSet);

    }

    @Override
    public void getImageHashMap() {

        mCoinRepository.getCoinUrl(new CoinImageUrlHashMapCallback() {
            @Override
            public void coinUrlCallback(HashMap<String, String> coinImageUrlHashmap, boolean isSuccessCall) {
                if (isSuccessCall) {
                    mCoinView.updateImageUrlHashMap(coinImageUrlHashmap);
                } else {
                    if (coinNetworkCallcount <= 3) {
                        getImageHashMap();
                        coinNetworkCallcount++;
                    }

                }
            }
        });
    }


    @Override
    public void sortList(int sortID, ArrayList<Coin> coinArrayList) {
        switch (sortID) {
            case SORT_BY_PRICE:
                coinArrayList = ListOperations.sortByPrice(coinArrayList);
                break;
            case SORT_BY_VOLUME:
                coinArrayList = ListOperations.sortByVolume(coinArrayList);
                break;
            case SORT_BY_MARKET_CAP:
                coinArrayList = ListOperations.sortByMarketcap(coinArrayList);
                break;
            case SORT_BY_RANK:
                coinArrayList = ListOperations.sortByRank(coinArrayList);
                break;
            case SORT_BY_SUPPLY:
                coinArrayList = ListOperations.sortBySupply(coinArrayList);
                break;
            case SORT_BY_NAME:
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

    @Override
    public void sortWithReverse(int sortID, ArrayList<Coin> coinArrayList) {
        switch (sortID) {
            case SORT_BY_PRICE:
                coinArrayList = ListOperations.sortByPrice(coinArrayList);
                break;
            case SORT_BY_VOLUME:
                coinArrayList = ListOperations.sortByVolume(coinArrayList);
                break;
            case SORT_BY_MARKET_CAP:
                coinArrayList = ListOperations.sortByMarketcap(coinArrayList);
                break;
            case SORT_BY_RANK:
                coinArrayList = ListOperations.sortByRank(coinArrayList);
                break;
            case SORT_BY_SUPPLY:
                coinArrayList = ListOperations.sortBySupply(coinArrayList);
                break;
            case SORT_BY_NAME:
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
        Collections.reverse(coinArrayList);
        mCoinView.updateAdapterList(coinArrayList);
    }

    @Override
    public void filterList(ArrayList<Coin> coinArrayList, String query) {
        coinArrayList = ListOperations.filterList(coinArrayList, query);
        mCoinView.updateAdapterList(coinArrayList);
    }

    @Override
    public void filterList(ArrayList<Coin> coinArrayList, String query, int day7, int hours24, int hour1) {
        coinArrayList = ListOperations.filterList(coinArrayList, query, day7, hours24, hour1);
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
        mCoinView.updateFavoriteSet(mCoinRepository.getFavoriteSet());

    }

    @Override
    public void udpateFavorite() {
        Set<String> favoriteCoinSet = mCoinRepository.getFavoriteSet();
        mCoinView.updateFavoriteSet(favoriteCoinSet);
    }

}
