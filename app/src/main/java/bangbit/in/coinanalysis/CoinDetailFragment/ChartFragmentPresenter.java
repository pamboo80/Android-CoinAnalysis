package bangbit.in.coinanalysis.CoinDetailFragment;

import java.util.ArrayList;
import java.util.List;

import bangbit.in.coinanalysis.MyApplication;
import bangbit.in.coinanalysis.Util;
import bangbit.in.coinanalysis.pojo.Coin;
import bangbit.in.coinanalysis.pojo.Datum;
import bangbit.in.coinanalysis.pojo.HistoricalData;
import bangbit.in.coinanalysis.repository.HistoryRepository;

/**
 * Created by Nagarajan on 3/7/2018.
 */

public class ChartFragmentPresenter implements ChartFragmentContract.UserActionsListener {
    private static final String TAG = ChartFragmentPresenter.class.getSimpleName();
    private final Coin coin;
    ChartFragmentContract.View view;
    HistoryRepository historyRepository;

    public ChartFragmentPresenter(ChartFragmentContract.View view, HistoryRepository historyRepository, Coin coin) {
        this.view = view;
        this.historyRepository = historyRepository;
        this.coin = coin;
    }

    @Override
    public void loadChartData(ArrayList<Datum> data, int days) {
        view.showYearProgressBar(true, false);

        if (data != null && data.size() > 0) {
            ArrayList<Datum> newData = new ArrayList<>();
            int start = data.size() - days;
            if (start > 0) {
                List<Datum> tempData = data.subList(start, data.size());
                newData.addAll(tempData);
            } else {
                newData.addAll(data);
            }

            Double firstPrice = newData.get(0).getOpen();
            Double lastPrice = newData.get(newData.size() - 1).getOpen(); //@@@ getClose()

            float percentage = 0;
            if (firstPrice != 0) {
                percentage = (float) (((lastPrice - firstPrice) / firstPrice) * 100);
            }
            view.updatePercentage("(" + Util.getOnlyTwoDecimalPointValue(percentage) + "%)", percentage);
            view.showPercentage(true);
            view.displayChartData(newData);
            view.showYearProgressBar(false, false);
            view.showTryAgain(false);

        } else {
            view.showYearProgressBar(false, false);
            view.showTryAgain(true);
        }

    }


    @Override
    public void load24HourData(String coin, String currency, int limit) {
        view.showYearProgressBar(true, true);
        historyRepository.getHistoricalDataByHour(coin, currency, limit, new HistoryRepository.HistoricalDataCallBack() {
            @Override
            public void onRetrieveHistoricalData(HistoricalData historicalData, boolean isSuccess) {
                if (isSuccess) {
                    ArrayList<Datum> data = historicalData.getData();
                    ArrayList<Datum> newData = new ArrayList();
                    for (Datum datum1 : data) {
                        float val = MyApplication.currencyMultiplyingFactor;
                        datum1.setClose(datum1.getClose() * val);
                        datum1.setOpen(datum1.getOpen() * val);
                        datum1.setHigh(datum1.getHigh() * val);
                        datum1.setLow(datum1.getLow() * val);
                        newData.add(datum1);
                    }
                    if (newData.size() > 0) {
                        Double firstPrice = newData.get(0).getOpen();
                        Double lastPrice = newData.get(newData.size() - 1).getOpen(); //@@@ getClose()

                        float percentage = 0;
                        if (firstPrice != 0) {
                            percentage = (float) (((lastPrice - firstPrice) / firstPrice) * 100);
                        }
                        view.updatePercentage("(" + Util.getOnlyTwoDecimalPointValue(percentage) + "%)", percentage);
                        view.showPercentage(true);

                    }
                    if (newData.size() > 0) {
                        view.displayChartDataFor24Hrs(newData);
                        view.showYearProgressBar(false, true);
                        view.set24HourData(newData);
                        view.showTryAgain(false);
                    } else {
                        view.showYearProgressBar(false, true);
                        view.set24HourData(null);
                    }
                } else {
                    view.showYearProgressBar(false, true);
                    view.set24HourData(null);
                }
            }
        });
    }

    @Override
    public void calculatePercentage(ArrayList<Datum> data) {
        if (data != null && data.size() > 0) {
            Double firstPrice = data.get(0).getOpen();
            Double lastPrice = data.get(data.size() - 1).getOpen(); //@@@ getClose()
            float percentage = 0;
            if (firstPrice != 0) {
                percentage = (float) (((lastPrice - firstPrice) / firstPrice) * 100);
            }
            view.updatePercentage("(" + Util.getOnlyTwoDecimalPointValue(percentage) + "%)", percentage);
            view.showPercentage(true);
        } else {
            view.showPercentage(false);
        }
    }
}