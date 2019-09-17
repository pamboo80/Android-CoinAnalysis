package bangbit.in.coinanalysis.SplashScreen;

import android.support.v4.app.Fragment;

/**
 * Created by Nagarajan on 3/7/2018.
 */

public interface SplashScreenContract {
    interface View {
        void loadFragment(Fragment fragment);
        void setViewPagerAdapter();
        void showNextButton(boolean setVisible);
        void showPrevButton(boolean setVisible);
        void changeNextButton(String updatedText);
        void loadNextFragment(int position);
        void loadMainActivity();
        void showViewPager(boolean isDisplay);
    }

    interface UserActionsListener {

        void nextButtonClick(int pos);
        void setViewPager();
        void loadMainFragment(Fragment fragment);
        void onPageChange(int position);


    }
}
