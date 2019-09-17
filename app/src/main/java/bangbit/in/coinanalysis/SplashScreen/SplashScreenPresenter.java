package bangbit.in.coinanalysis.SplashScreen;

import android.support.v4.app.Fragment;

import bangbit.in.coinanalysis.SplashScreenFragment.SelectCurrencySplashScreenFragment;

/**
 * Created by Nagarajan on 3/7/2018.
 */

public class SplashScreenPresenter implements SplashScreenContract.UserActionsListener {


    private final SplashScreenContract.View mSplashScreenView;

    public SplashScreenPresenter(SplashScreenContract.View mCoinView) {
        this.mSplashScreenView = mCoinView;
    }

    @Override
    public void nextButtonClick(int pos) {
        if (pos==4){
            mSplashScreenView.loadFragment(SelectCurrencySplashScreenFragment.newInstance());
            mSplashScreenView.showViewPager(false);
            mSplashScreenView.showPrevButton(false);
//            mSplashScreenView.loadMainActivity();
        }else {
            mSplashScreenView.loadNextFragment(pos);
        }

    }

    @Override
    public void setViewPager() {
        mSplashScreenView.setViewPagerAdapter();
    }

    @Override
    public void loadMainFragment(Fragment fragment) {
        mSplashScreenView.loadFragment(fragment);
    }

    @Override
    public void onPageChange(int position) {
        if (position==3){
            mSplashScreenView.changeNextButton("Got It");
        }else {
            mSplashScreenView.changeNextButton("Next");
        }
        if (position>0){
            mSplashScreenView.showPrevButton(true);
        }else {
            mSplashScreenView.showPrevButton(false);
        }
    }
}
