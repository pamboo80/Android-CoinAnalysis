package bangbit.in.coinanalysis.DetailActivity;

import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

import bangbit.in.coinanalysis.MyInvestmentFragment.MyInvestmentFragment;
import bangbit.in.coinanalysis.WhereToBuy.WhereToBuyFragment;
import bangbit.in.coinanalysis.pojo.Coin;
import bangbit.in.coinanalysis.pojo.Datum;

/**
 * Created by Nagarajan on 3/13/2018.
 */

public class CoinDetailPagerAdapter extends FragmentStatePagerAdapter {
    private static int NUM_ITEMS = 3;
    private final Coin coin;
    private final String currency;
    private final CoinDetailFragment coinDetailFragment;


    public CoinDetailPagerAdapter(FragmentManager fragmentManager, Coin coin, String currency) {
        super(fragmentManager);
        this.coin=coin;
        this.currency=currency;
        coinDetailFragment = CoinDetailFragment.newInstance(coin,currency);
    }

    void chartDatafetched(ArrayList<Datum> data){
        coinDetailFragment.chartDatafetched(data);
    }

    // Returns total number of pages
    @Override
    public int getCount() {
        return NUM_ITEMS;
    }



    // Returns the fragment to display for that page
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return coinDetailFragment;
            case 1:
                return WhereToBuyFragment.newInstance(coin,currency);
            case 2:
                return MyInvestmentFragment.newInstance(coin,currency,DetailActivity.imageUrl);
            default:
                return coinDetailFragment;
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0: return "Coin Details";
            case 1: return "Buy/Sell";
            case 2: return "My Investments";
            default:return "Coin Details";
        }
    }

    @Override
    public Parcelable saveState()
    {
        return null;
    }
}
