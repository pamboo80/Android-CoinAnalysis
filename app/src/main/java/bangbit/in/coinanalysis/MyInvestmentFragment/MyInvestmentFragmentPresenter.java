package bangbit.in.coinanalysis.MyInvestmentFragment;

import java.util.ArrayList;
import java.util.Collections;

import bangbit.in.coinanalysis.ListOperations;
import bangbit.in.coinanalysis.Util;
import bangbit.in.coinanalysis.pojo.Investment;
import bangbit.in.coinanalysis.repository.InvestmentRepository;

import static bangbit.in.coinanalysis.Constant.SORT_BY_BOUGHT_PRICE;
import static bangbit.in.coinanalysis.Constant.SORT_BY_COST;
import static bangbit.in.coinanalysis.Constant.SORT_BY_QUANTITY;
import static bangbit.in.coinanalysis.MyApplication.currencyMultiplyingFactor;

/**
 * Created by Nagarajan on 3/7/2018.
 */

public class MyInvestmentFragmentPresenter implements MyInvestmentFragmentContract.UserActionsListener {
    private InvestmentRepository investmentRepository;
    MyInvestmentFragmentContract.View view;


    public MyInvestmentFragmentPresenter(MyInvestmentFragmentContract.View view,
                                         InvestmentRepository investmentRepository) {
        this.view = view;
        this.investmentRepository = investmentRepository;
    }

    @Override
    public void loadInvestmentData(String symbol) {
        ArrayList<Investment> investmentList = investmentRepository.getAllInvestmrnt(symbol);
        if (investmentList.size()==0){
            view.displayNoInvestmentAvailable(true);
            view.displayInvestmentData(investmentList);
        }else {
            view.displayInvestmentData(investmentList);
            view.displayNoInvestmentAvailable(false);
        }


    }

    @Override
    public void calculateTotalAndGain(ArrayList<Investment> investmentList, float usdPrice) {
        if (investmentList.size()==0){
            boolean isGreen=true;
            view.updateTotalAndGain("0", "0" ,"0", true);
        }
        float boughtPriceTotal = 0;
        float totalcoin = 0;
        boolean isGreen = true;
        for (Investment investment :
                investmentList) {
            totalcoin += investment.getQuantity();
            float valuecount = investment.getBoughtPrice() * investment.getQuantity();
            boughtPriceTotal += valuecount;
        }

        float currentTotalPrice = usdPrice * totalcoin;
        float mainProfit = 0;
        if (boughtPriceTotal > 0) {
            mainProfit = ((currentTotalPrice - boughtPriceTotal) / boughtPriceTotal) * 100;
        }
        String total_current = Util.getOnlyTwoDecimalPointValue(currentTotalPrice* currencyMultiplyingFactor);
        String total_bought = Util.getOnlyTwoDecimalPointValue(boughtPriceTotal*currencyMultiplyingFactor);
        String gain = Util.getOnlyTwoDecimalPointValue(mainProfit) + "%";
        if (mainProfit < 0) {
            isGreen = false;
        }

        view.updateTotalAndGain(total_current,total_bought,gain, isGreen);
    }

    @Override
    public void reverseData(ArrayList<Investment> investmentList) {
        Collections.reverse(investmentList);
        view.displayInvestmentData(investmentList);
    }

    @Override
    public void sortData(ArrayList<Investment> investmentList, int sort) {
        switch (sort){
            case SORT_BY_QUANTITY:
                ListOperations.sortByQuantity(investmentList);
                break;
            case SORT_BY_BOUGHT_PRICE:
                ListOperations.sortByBoughtPrice(investmentList);
                break;
            case SORT_BY_COST:
                ListOperations.sortByCost(investmentList);
                break;
        }
        view.displayInvestmentData(investmentList);

    }


}
