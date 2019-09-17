package bangbit.in.coinanalysis.WhereToBuy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import bangbit.in.coinanalysis.ListOperations;
import bangbit.in.coinanalysis.pojo.Exchange;
import bangbit.in.coinanalysis.repository.HistoryRepository;

import static bangbit.in.coinanalysis.Constant.SORT_BY_EXCHANGE;
import static bangbit.in.coinanalysis.Constant.SORT_BY_PRICE;
import static bangbit.in.coinanalysis.Constant.SORT_BY_VOLUME;
import static bangbit.in.coinanalysis.Util.checkForNull;
import static bangbit.in.coinanalysis.Util.getTwoDecimalPointValue;

/**
 * Created by Nagarajan on 3/7/2018.
 */

public class WhereToBuyFragmentPresenter implements WhereToBuyFragmentContract.UserActionsListener {
    WhereToBuyFragmentContract.View view;
    HistoryRepository historyRepository;

    public WhereToBuyFragmentPresenter(WhereToBuyFragmentContract.View view, HistoryRepository historyRepository) {
        this.view = view;
        this.historyRepository = historyRepository;
    }


    @Override
    public void loadExchangeData(String symbol, String currency, boolean isInBackground, boolean isNewPair) {
        final boolean _isNewPair = isNewPair;
        if (isInBackground) {
            historyRepository.getExchangeData(symbol, currency, new HistoryRepository.ExchangeDataCallBack() {
                @Override
                public void onRetrieveExchangeData(ArrayList<Exchange> exchanges, boolean isSuccess, boolean isDataFound) {
                    if (isSuccess) {
                        if (exchanges != null && exchanges.size() > 0) {
                            view.displayExchangeData(exchanges);
                            findMaxMin(exchanges);
                            view.showNoDataAvailable(false);
                        }
                    }
                }
            });
        } else {

            if(view.isTryAgainVisible()==true)
            {
                view.setProgressBarIndicator(true);
            }
            else {
                view.setProgressIndicator(true);
            }

            historyRepository.getExchangeData(symbol, currency, new HistoryRepository.ExchangeDataCallBack() {
                @Override
                public void onRetrieveExchangeData(ArrayList<Exchange> exchanges, boolean isSuccess, boolean isDataFound) {
                    view.setProgressIndicator(false);
                    ArrayList<Exchange> emptyExchanges = new ArrayList<>();
                    isSuccess = isSuccess && (exchanges != null) && (exchanges.size() > 0);
                    if (isSuccess) {
                            view.displayExchangeData(exchanges);
                            findMaxMin(exchanges);
                            view.showNoDataAvailable(false);
                            view.showTryAgain(false);
                    } else {
                        if (_isNewPair || view.getExchangeSize()==0) {
                            view.displayExchangeData(emptyExchanges);
                            findMaxMin(emptyExchanges);
                            view.showNoDataAvailable(true);
                            if(exchanges == null && !isDataFound) {
                                view.showTryAgain(true);
                            }
                        }
                    }
                }
            });
        }
    }

    @Override
    public void findMaxMin(ArrayList<Exchange> exchanges) {
        String min = "NA";
        String max = "NA";
        String minMarket = "NA";
        String maxMarket = "NA";
        if (exchanges != null) {
            ArrayList<Exchange> exchangesVol24HRSGtZero = new ArrayList<>();
            for (Exchange exchange : exchanges) {
                if(exchange.getVOLUME24HOURTO()>0) {
                    exchangesVol24HRSGtZero.add(exchange);
                }
            }
            if (exchangesVol24HRSGtZero.size() == 1) {
                Exchange exchangeMin = exchangesVol24HRSGtZero.get(0);
                Exchange exchangeMax = exchangesVol24HRSGtZero.get(0);
                min = checkForNull(getTwoDecimalPointValue(exchangeMin.getPRICE()));
                max = checkForNull(getTwoDecimalPointValue(exchangeMax.getPRICE()));
                minMarket = "On " + exchangeMin.getMARKET();
                maxMarket = "On " + exchangeMax.getMARKET();
            } else if (exchangesVol24HRSGtZero.size() >= 2) {
                Exchange exchangeMin = Collections.max(exchangesVol24HRSGtZero, new Min());
                Exchange exchangeMax = Collections.max(exchangesVol24HRSGtZero, new Max());
                min = checkForNull(getTwoDecimalPointValue(exchangeMin.getPRICE()));
                max = checkForNull(getTwoDecimalPointValue(exchangeMax.getPRICE()));
                minMarket = "On " + exchangeMin.getMARKET();
                maxMarket = "On " + exchangeMax.getMARKET();
            }
        }
        view.displayMaxMin(max, min, minMarket, maxMarket);
    }


    @Override
    public void sortData(ArrayList<Exchange> exchanges, int sort) {
        switch (sort) {
            case SORT_BY_EXCHANGE:
                exchanges = ListOperations.sortByExchangeName(exchanges);
                break;
            case SORT_BY_PRICE:
                exchanges = ListOperations.sortByExchangePrice(exchanges);
                break;
            case SORT_BY_VOLUME:
                exchanges = ListOperations.sortByExchangeVolume(exchanges);
                break;
        }
        view.displaySortedData(exchanges);
    }

    @Override
    public void sortAndReverseData(ArrayList<Exchange> exchanges, int sort) {
        switch (sort) {
            case SORT_BY_EXCHANGE:
                exchanges = ListOperations.sortByExchangeName(exchanges);
                break;
            case SORT_BY_PRICE:
                exchanges = ListOperations.sortByExchangePrice(exchanges);
                break;
            case SORT_BY_VOLUME:
                exchanges = ListOperations.sortByExchangeVolume(exchanges);
                break;
        }
        view.displaySortedData(exchanges);
    }

    @Override
    public void reverseData(ArrayList<Exchange> exchanges) {
        Collections.reverse(exchanges);
        view.displaySortedData(exchanges);
    }

    public class Max implements Comparator<Exchange> {
        public int compare(Exchange a, Exchange b) {
            if (a.getPRICE() < b.getPRICE())
                return -1; // highest value first
            if (a.getPRICE() == b.getPRICE())
                return 0;
            return 1;
        }
    }

    public class Min implements Comparator<Exchange> {
        public int compare(Exchange a, Exchange b) {
            if (a.getPRICE() > b.getPRICE())
                return -1; // highest value first
            if (a.getPRICE() == b.getPRICE())
                return 0;
            return 1;
        }
    }
}
