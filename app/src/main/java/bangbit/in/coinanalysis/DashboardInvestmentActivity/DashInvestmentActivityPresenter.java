package bangbit.in.coinanalysis.DashboardInvestmentActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import bangbit.in.coinanalysis.ListOperations;
import bangbit.in.coinanalysis.Util;
import bangbit.in.coinanalysis.pojo.Coin;
import bangbit.in.coinanalysis.pojo.DashInvestment;
import bangbit.in.coinanalysis.pojo.Investment;
import bangbit.in.coinanalysis.repository.CoinImageUrlHashMapCallback;
import bangbit.in.coinanalysis.repository.CoinRepository;
import bangbit.in.coinanalysis.repository.InvestmentRepository;

import static bangbit.in.coinanalysis.Constant.SORT_BY_COIN;
import static bangbit.in.coinanalysis.Constant.SORT_BY_COST;
import static bangbit.in.coinanalysis.Constant.SORT_BY_INVESTMENT;
import static bangbit.in.coinanalysis.MyApplication.currencyMultiplyingFactor;

/**
 * Created by Nagarajan on 3/7/2018.
 */

public class DashInvestmentActivityPresenter implements DashInvestmentActivityContract.UserActionsListener {

    private final DashInvestmentActivityContract.View view;
    private final InvestmentRepository investmentRepository;
    private final CoinRepository coinRepository;
    private ArrayList<Coin> coinArrayList;
    HashMap<String, String> coinImageUrl;


    public DashInvestmentActivityPresenter(DashInvestmentActivityContract.View view,
                                           InvestmentRepository investmentRepository,
                                           CoinRepository coinRepository) {
        this.view = view;
        this.investmentRepository = investmentRepository;
        this.coinRepository = coinRepository;
    }

    @Override
    public void sortData(int sort, ArrayList<DashInvestment> dashInvestmentArrayList) {
        switch (sort) {
            case SORT_BY_COIN:
                ListOperations.sortByDashCoin(dashInvestmentArrayList);
                break;
            case SORT_BY_INVESTMENT:
                ListOperations.sortByDashInvestment(dashInvestmentArrayList);
                break;
            case SORT_BY_COST:
                ListOperations.sortByDashCost(dashInvestmentArrayList);
                break;
        }
        view.displayData(dashInvestmentArrayList);
    }

    @Override
    public void reverse(ArrayList<DashInvestment> dashInvestmentArrayList) {
        Collections.reverse(dashInvestmentArrayList);
        view.displayData(dashInvestmentArrayList);
    }

    @Override
    public void prepareDataForRecycleView(ArrayList<Coin> coinArrayList1, int sort, boolean isArrowUp) {

        if (coinArrayList1 == null) {
            if (coinArrayList == null) {
                return;
            }
        } else {
            coinArrayList = coinArrayList1;
        }
        HashMap<String, ArrayList<Investment>> individualCoin = new HashMap<>(0);

        ArrayList<Investment> allInvestmentList = investmentRepository.getAllCoinInvestmrnt();
        if (allInvestmentList.size() == 0) {
            view.showNoInvestmentAvailable(true);
        } else {
            view.showNoInvestmentAvailable(false);
        }

        HashMap<String, String> coinAndPriceHashMap = new HashMap<>();
        for (Coin coin : coinArrayList) {
            if (coin.getPriceUsd() == null) {
                coin.setPriceUsd("0");
            }
            coinAndPriceHashMap.put(coin.getSymbol(), coin.getPriceUsd());
        }

        for (Investment investment : allInvestmentList) {
            if (individualCoin.get(investment.getCoinSymbol()) != null) {
                individualCoin.get(investment.getCoinSymbol()).add(investment);
            } else {
                ArrayList<Investment> investmentList = new ArrayList<>(0);
                investmentList.add(investment);
                individualCoin.put(investment.getCoinSymbol(), investmentList);
            }
        }

        coinRepository.getCoinUrlFromDb(new CoinImageUrlHashMapCallback() {
            @Override
            public void coinUrlCallback(HashMap<String, String> coinImageUrlHashmap, boolean isSuccessCall) {
                coinImageUrl = coinImageUrlHashmap;
            }
        });

        ArrayList<DashInvestment> dashInvestmentArrayList = new ArrayList<>();

        for (String key :
                individualCoin.keySet()) {
            float totalQuantity = 0;
            float totalBoyghtInvestment = 0;
            float totalCurrentInvestment = 0;
            for (Investment investment : individualCoin.get(key)) {
                totalQuantity += investment.getQuantity();
                totalBoyghtInvestment += investment.getQuantity() * investment.getBoughtPrice();
            }
            float price = Float.parseFloat(coinAndPriceHashMap.get(key));
            totalCurrentInvestment = totalQuantity * price;
            float mainProfit = 0;
            if (totalBoyghtInvestment == 0) {
                mainProfit = totalCurrentInvestment * 100;
            } else {
                mainProfit = ((totalCurrentInvestment - totalBoyghtInvestment) / totalBoyghtInvestment) * 100;
            }
            DashInvestment dashInvestment = new DashInvestment(key, coinImageUrl.get(key), price, totalCurrentInvestment,
                    totalBoyghtInvestment, mainProfit, totalQuantity);
            dashInvestmentArrayList.add(dashInvestment);

        }


        sortData(sort, dashInvestmentArrayList);
        if (!isArrowUp) {
            reverse(dashInvestmentArrayList);
        }
//        calling this in sort
//        view.displayData(dashInvestmentArrayList);
        calculateTotalGain(allInvestmentList, coinAndPriceHashMap);

    }

    @Override
    public void calculateTotalGain(ArrayList<Investment> allInvestmentList, HashMap<String, String> coinAndPriceHashMap) {
        float totalBoughtValue = 0;
        float totalCurrentvalue = 0;
        if (allInvestmentList == null && coinAndPriceHashMap == null) {
            return;
        }
        for (Investment investment : allInvestmentList) {
            String symbol = investment.getCoinSymbol();
            String val = coinAndPriceHashMap.get(symbol);
            if (val != null) {
                float currentPrice = Float.parseFloat(val);
                float tempCurrentPrice = investment.getQuantity() * currentPrice;
                float tempInvestedPrice = investment.getBoughtPrice() * investment.getQuantity();

                totalBoughtValue += tempInvestedPrice;
                totalCurrentvalue += tempCurrentPrice;
            }
        }
        float profit = 0;
        if (totalBoughtValue != 0) {
            profit = ((totalCurrentvalue - totalBoughtValue) / totalBoughtValue) * 100;
        }

        totalCurrentvalue *= currencyMultiplyingFactor;
        totalBoughtValue *= currencyMultiplyingFactor;
        String profitString = Util.getOnlyTwoDecimalPointValue(profit);
        String totalCurrentvalueString = Util.getOnlyTwoDecimalPointValue(totalCurrentvalue);
        String totalBoughtValueString = Util.getOnlyTwoDecimalPointValue(totalBoughtValue);
        boolean isColorGreen = true;
        if (profit < 0) {
            isColorGreen = false;
        }
        view.totalCalculation(profitString, totalCurrentvalueString, totalBoughtValueString, isColorGreen);


    }

}
