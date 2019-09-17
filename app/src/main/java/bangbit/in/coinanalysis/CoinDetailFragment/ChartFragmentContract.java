package bangbit.in.coinanalysis.CoinDetailFragment;

import java.util.ArrayList;

import bangbit.in.coinanalysis.pojo.Datum;

/**
 * Created by Nagarajan on 3/7/2018.
 */

public interface ChartFragmentContract {
    interface View {
        void displayChartData(ArrayList<Datum> data);
        void displayChartDataFor24Hrs(ArrayList<Datum> data);
        void set24HourData(ArrayList<Datum> data);
        void updatePercentage(String percentageString, float percentage);
        void showPercentage(boolean isDisplay);
        void showYearProgressBar(boolean isDisplay,boolean is24Hours);
        void showTryAgain(boolean isDisply);

    }

    interface UserActionsListener {
//        void loadHistoricalData(String symbol, String currency, int days);
        void loadChartData(ArrayList<Datum> data, int days);
        void load24HourData(String coin,String currency,int limit);
        void calculatePercentage(ArrayList<Datum> data);
    }
}
