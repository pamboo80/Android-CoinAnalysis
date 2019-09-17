package bangbit.in.coinanalysis;

import org.json.JSONObject;

/**
 * Created by Nagarajan on 2/27/2018.
 */

public interface CurrencyUpdator {
    void updateCurrency(JSONObject rates);
}
