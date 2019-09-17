package bangbit.in.coinanalysis.CoinDetailFragment;

import java.util.ArrayList;

import bangbit.in.coinanalysis.pojo.Datum;

/**
 * Created by Nagarajan on 3/7/2018.
 */

public interface HistoricalFragmentContract {
    interface View {
        void displayHistoricalData(ArrayList<Datum> data);
        void displayNoDataAvailable(boolean isDisplay);
        void displayTryAgain(boolean isDisplay);
    }

    interface UserActionsListener {
//        void loadHistoricalData(String symbol, String currency, int days);
        void loadHistoricalData(ArrayList<Datum> data, int days);
        void sortData(int id,ArrayList<Datum> data);
        void reverse(ArrayList<Datum> data);
        void filterData(ArrayList<Datum> data,long startDate,long endDate,int sort,boolean isReverse);
    }
}
