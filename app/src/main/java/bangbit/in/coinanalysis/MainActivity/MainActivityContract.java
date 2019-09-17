package bangbit.in.coinanalysis.MainActivity;

import java.util.ArrayList;

import bangbit.in.coinanalysis.pojo.Coin;

/**
 * Created by Nagarajan on 3/14/2018.
 */

public interface MainActivityContract {
    interface View {
        void displayFilter(boolean active);
        void enableFilterInteraction(boolean active);
        void setSevenDay(int day7Value);
        void set24Hours(int hour24Value);
        void set1Hours(int hour1Value);
        void showCoins(ArrayList<Coin> coinArrayLis);
        void setProgressIndicator(boolean active);
        void showTryAgainMessage(boolean isDisplay);
        void setLastUpdatedString(String lastUpdatedString);

    }

    interface UserActionsListener {

        void filterButtonClick(boolean showFilter);
        void switchFilterCilck(boolean active);
        void loadCoinsV2(boolean inBackground);

    }
}
