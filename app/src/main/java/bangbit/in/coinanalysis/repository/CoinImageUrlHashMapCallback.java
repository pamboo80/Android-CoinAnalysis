package bangbit.in.coinanalysis.repository;

import java.util.HashMap;

/**
 * Created by Nagarajan on 3/13/2018.
 */

public interface CoinImageUrlHashMapCallback {
    void coinUrlCallback(HashMap<String, String> coinImageUrlHashmap, boolean isSuccessCall);
}
