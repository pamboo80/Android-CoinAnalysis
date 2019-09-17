package bangbit.in.coinanalysis.SplashScreen;

import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import bangbit.in.coinanalysis.SplashScreenFragment.ChatBotSplashScreenFragment;
import bangbit.in.coinanalysis.SplashScreenFragment.CryptoFiatSplashScreenFragment;
import bangbit.in.coinanalysis.SplashScreenFragment.MyInvestmentSplashScreenFragment;
import bangbit.in.coinanalysis.SplashScreenFragment.PercentageFilterSplashScreenFragment;

/**
 * Created by Nagarajan on 3/7/2018.
 */
class ViewPagerAdapter extends FragmentStatePagerAdapter {
    private static int NUM_ITEMS = 4;

    public ViewPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
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
            case 0: // Fragment # 2 - This will show PercentageFilterSplashScreenFragment
                return PercentageFilterSplashScreenFragment.newInstance();
            case 1: // Fragment # 3 - This will show CryptoFiatSplashScreenFragment
                return CryptoFiatSplashScreenFragment.newInstance();
            case 2: // Fragment # 4 - This will show MyInvestmentSplashScreenFragment
                return MyInvestmentSplashScreenFragment.newInstance();
            case 3: // Fragment # 4 - This will show MyInvestmentSplashScreenFragment
                return ChatBotSplashScreenFragment.newInstance();
            default:
                return null;
        }
    }

    @Override
    public Parcelable saveState()
    {
        return null;
    }

}