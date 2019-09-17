package bangbit.in.coinanalysis.MainActivity;

import bangbit.in.coinanalysis.pojo.Coin;

/**
 * Created by Nagarajan on 3/21/2018.
 */

public interface FragmentListner {
    void coinClick(Coin coin,String imageUrl,boolean isFavorite);
    void loadCoinOnSwipeRefreshLayout();
}
