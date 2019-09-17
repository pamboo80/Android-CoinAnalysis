package bangbit.in.coinanalysis.DetailActivity;

import java.util.ArrayList;

import bangbit.in.coinanalysis.MyApplication;
import bangbit.in.coinanalysis.pojo.Datum;
import bangbit.in.coinanalysis.pojo.HistoricalData;
import bangbit.in.coinanalysis.repository.CoinRepository;
import bangbit.in.coinanalysis.repository.HistoryRepository;

/**
 * Created by Nagarajan on 3/7/2018.
 */

public class DetailActivityPresenter implements DetailActivityContract.UserActionsListener {
    CoinRepository coinRepository;
    DetailActivityContract.View view;
    HistoryRepository historyRepository;

    public DetailActivityPresenter(DetailActivityContract.View view, HistoryRepository historyRepository, CoinRepository coinRepository) {
        this.view = view;
        this.historyRepository = historyRepository;
        this.coinRepository = coinRepository;
    }

    @Override
    public void loadChartData(String symbol, String currency, int days) {
        historyRepository.getHistoricalData(symbol, currency, days, new HistoryRepository.HistoricalDataCallBack() {
            @Override
            public void onRetrieveHistoricalData(HistoricalData historicalData, boolean isSuccess) {
                if (isSuccess) {

                    ArrayList<Datum> data = historicalData.getData();
                    ArrayList<Datum> newData = new ArrayList<>();
                    double old = 0;
                    for (Datum datum : data) {
                        if (old == 0) {
                            old = datum.getOpen();
                        }
                        if(old!=0) {
                            Datum datum1=datum;
                            float val=MyApplication.currencyMultiplyingFactor;
                            datum1.setClose(datum1.getClose()*val);
                            datum1.setOpen(datum1.getOpen()*val);
                            datum1.setHigh(datum1.getHigh()*val);
                            datum1.setLow(datum1.getLow()*val);
                            newData.add(datum);
                        }
                    }
                    view.loadChartData(newData);
                } else {
                    view.loadChartData(null);
                }
            }
        });
    }

    @Override
    public void favoriteIconClick(String symbol, boolean isInserting) {
        if (isInserting) {
            coinRepository.insertFavoriteCoin(symbol);
            view.setFavoriteIconImage(isInserting);
        } else {
            coinRepository.removeFavoriteCoin(symbol);
            view.setFavoriteIconImage(isInserting);

        }

    }
}
