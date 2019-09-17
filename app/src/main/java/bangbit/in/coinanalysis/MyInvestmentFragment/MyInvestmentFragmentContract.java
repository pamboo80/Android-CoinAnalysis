package bangbit.in.coinanalysis.MyInvestmentFragment;

import java.util.ArrayList;

import bangbit.in.coinanalysis.pojo.Investment;

/**
 * Created by Nagarajan on 3/7/2018.
 */

public interface MyInvestmentFragmentContract {
    interface View {
        void displayInvestmentData(ArrayList<Investment> investmentList);
        void displayNoInvestmentAvailable(boolean active);
        void updateTotalAndGain(String total_current, String total_bought, String gain, boolean isGreen);
    }

    interface UserActionsListener {
        void loadInvestmentData(String symbol);
        void calculateTotalAndGain(ArrayList<Investment> investmentList,float usdPrice);
        void sortData(ArrayList<Investment> investmentList, int sort);
        void reverseData(ArrayList<Investment> investmentList);
    }
}
