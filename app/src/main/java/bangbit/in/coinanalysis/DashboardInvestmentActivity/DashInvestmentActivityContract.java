package bangbit.in.coinanalysis.DashboardInvestmentActivity;

import java.util.ArrayList;
import java.util.HashMap;

import bangbit.in.coinanalysis.pojo.Coin;
import bangbit.in.coinanalysis.pojo.DashInvestment;
import bangbit.in.coinanalysis.pojo.Investment;

/**
 * Created by Nagarajan on 3/7/2018.
 */

public interface DashInvestmentActivityContract {
    interface View {
        void displayData(ArrayList<DashInvestment> dashInvestments);
        void totalCalculation(String profit, String totalCurrentvalue, String totalInvestmentString, boolean isColorGreen);
        void showNoInvestmentAvailable(boolean active);

    }

    interface UserActionsListener {
        void prepareDataForRecycleView(ArrayList<Coin> coinArrayList1,int sort,boolean isArrowUp);
        void sortData(int sort,ArrayList<DashInvestment> dashInvestmentArrayList);
        void reverse(ArrayList<DashInvestment> dashInvestmentArrayList);
        void  calculateTotalGain(ArrayList<Investment> allInvestmentList, HashMap<String, String> coinAndPriceHashMap);
    }
}
