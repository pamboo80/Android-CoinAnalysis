package bangbit.in.coinanalysis.DetailActivity;

import java.util.ArrayList;

import bangbit.in.coinanalysis.pojo.Datum;

/**
 * Created by Nagarajan on 3/7/2018.
 */

public interface DetailActivityContract {
    interface View {
        void loadChartData(ArrayList<Datum> data);
        void setFavoriteIconImage(boolean isFavorited);
        void showSnackbar(boolean isFavorited);
        void displayTabandToolbar(boolean isDisplay);
    }

    interface UserActionsListener {
        void loadChartData(String symbol, String currency, int days);
        void favoriteIconClick(String symbol, boolean isInserting);

    }
}
