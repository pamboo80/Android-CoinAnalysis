package bangbit.in.coinanalysis.CoinDetailFragment;

import java.util.ArrayList;
import java.util.Collections;

import bangbit.in.coinanalysis.ListOperations;
import bangbit.in.coinanalysis.pojo.Datum;

import static bangbit.in.coinanalysis.Constant.SORT_BY_CLOSE;
import static bangbit.in.coinanalysis.Constant.SORT_BY_DATE;
import static bangbit.in.coinanalysis.Constant.SORT_BY_HIGH;
import static bangbit.in.coinanalysis.Constant.SORT_BY_LOW;
import static bangbit.in.coinanalysis.Constant.SORT_BY_OPEN;

/**
 * Created by Nagarajan on 3/7/2018.
 */

public class HistoricalFragmentPresenter implements HistoricalFragmentContract.UserActionsListener{
    HistoricalFragmentContract.View view;

    public HistoricalFragmentPresenter(HistoricalFragmentContract.View view){
        this.view=view;
    }
    @Override
    public void loadHistoricalData(ArrayList<Datum> data, int days) {
        if (data!=null && data.size()>=days) {
            ArrayList<Datum> newData = new ArrayList<>();
            if (data != null) {
                int start = data.size() - days;
                for (int i = 1; i < days; i++) {  //@@@
                    newData.add(data.get(start + i));
                }
                view.displayHistoricalData(newData);
                view.displayNoDataAvailable(false);
            }
        }else {
            view.displayNoDataAvailable(true);

        }
    }

    @Override
    public void sortData(int id,ArrayList<Datum> data) {

        switch (id){
            case SORT_BY_DATE:
                data= ListOperations.sortByDate(data);
                break;
            case SORT_BY_OPEN:
                data= ListOperations.sortByOpen(data);
                break;
            case SORT_BY_CLOSE:
                data= ListOperations.sortByClose(data);
                break;
            case SORT_BY_HIGH:
                data= ListOperations.sortByHigh(data);
                break;
            case SORT_BY_LOW:
                data= ListOperations.sortByLow(data);
                break;
        }
        view.displayHistoricalData(data);
    }

    @Override
    public void filterData(ArrayList<Datum> data, long startDate, long endDate, int sort, boolean isReverse) {
        if (data!=null && data.size()>0) {
            ArrayList<Datum> newData = new ArrayList<>();
            for (Datum datum : data) {
                if (datum.getTime() >= startDate && datum.getTime() <= endDate) {
                    newData.add(datum);
                }
            }
            if (newData.size()>0){
                sortData(sort,newData);
                if (isReverse){
                    reverse(newData);
                }
                view.displayHistoricalData(newData);
                view.displayNoDataAvailable(false);
            }else {
                view.displayHistoricalData(newData);
                view.displayNoDataAvailable(true);
            }
        }
    }

    @Override
    public void reverse( ArrayList<Datum> data) {
        Collections.reverse(data);
        view.displayHistoricalData(data);
    }

}
