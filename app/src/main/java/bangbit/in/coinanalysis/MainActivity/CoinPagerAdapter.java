package bangbit.in.coinanalysis.MainActivity;

import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import java.util.ArrayList;

import bangbit.in.coinanalysis.pojo.Coin;

import static bangbit.in.coinanalysis.MyApplication.currencyMultiplyingFactor;

/**
 * Created by Nagarajan on 3/13/2018.
 */

public class CoinPagerAdapter extends FragmentStatePagerAdapter {
    private static int NUM_ITEMS = 2;
    private CoinListFragment coinListFragment;
    private FavoriteCoinFragment favoriteCoinFragment;
    private String TAG=CoinPagerAdapter.class.getSimpleName();

    public CoinPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    // Returns total number of pages

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    public void filter(String query, int day7, int hours24, int hour1, boolean isFilterPErcentageApply) {
        if (coinListFragment!=null) {
            coinListFragment.filter(query, day7, hours24, hour1, isFilterPErcentageApply);
        }
        if (favoriteCoinFragment!=null) {
            favoriteCoinFragment.filter(query, day7, hours24, hour1, isFilterPErcentageApply);
        }
    }

    public void updateCurrency() {
        if (coinListFragment!=null) {
            coinListFragment.updateCurrency(currencyMultiplyingFactor);
        }
        if (favoriteCoinFragment!=null) {
            favoriteCoinFragment.updateCurrency(currencyMultiplyingFactor);
        }
    }

    public void setCoins(ArrayList<Coin> coinArrayList){
        if (favoriteCoinFragment!=null){
            favoriteCoinFragment.showCoins(coinArrayList);
        }
        if (coinListFragment!=null) {
            coinListFragment.showCoins(coinArrayList);
        }
    }

    public void refreshFavorite() {
        if (favoriteCoinFragment!=null){
            favoriteCoinFragment.refreshFavorite();
        }
        if (coinListFragment!=null) {
            coinListFragment.refreshFavorite();
        }
    }

    public void sort(int sort, boolean isReverse) {
        if (coinListFragment!=null) {
            coinListFragment.sort(sort, isReverse);
        }
        if (favoriteCoinFragment!=null) {
            favoriteCoinFragment.sort(sort, isReverse);
        }
    }

    public void moveToFirst() {
        if (coinListFragment!=null) {
            coinListFragment.moveToFirst();
        }
        if (favoriteCoinFragment!=null) {
            favoriteCoinFragment.moveToFirst();
        }
    }


    // Returns the fragment to display for that page
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:coinListFragment = CoinListFragment.newInstance();
                return coinListFragment;
            case 1:favoriteCoinFragment = FavoriteCoinFragment.newInstance();
                return favoriteCoinFragment;
            default:
                return coinListFragment;
        }
    }

    void selected(int selected){
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "All Coins";
            case 1:
                return "My Favorites";
            default:
                return "All Coins";
        }
    }

    @Override
    public Parcelable saveState()
    {
        return null;
    }

}
