package bangbit.in.coinanalysis.WhereToBuy;

import java.util.ArrayList;

import bangbit.in.coinanalysis.pojo.Exchange;

/**
 * Created by Nagarajan on 3/7/2018.
 */

public interface WhereToBuyFragmentContract {
    interface View {
        void displayExchangeData(ArrayList<Exchange> exchanges);
        void displaySortedData(ArrayList<Exchange> exchanges);
        void displayMaxMin(String max, String min,String minMarket, String maxMarket);
        void showNoDataAvailable(boolean isActive);
        void setProgressIndicator(boolean active);
        void setProgressBarIndicator(boolean active);
        int getExchangeSize();
        void showTryAgain(boolean isDisplay);
        boolean isTryAgainVisible();
    }

    interface UserActionsListener {
//        void loadHistoricalData(String symbol, String currency, int days);
        void loadExchangeData(String symbol, String currency, boolean isInBackground, boolean isNewPair);
        void findMaxMin(ArrayList<Exchange> exchanges);
        void sortData(ArrayList<Exchange> exchanges, int sort);
        void reverseData(ArrayList<Exchange> exchanges);
        void sortAndReverseData(ArrayList<Exchange> exchanges, int sort);


    }
}
