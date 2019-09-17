
package bangbit.in.coinanalysis.v2Pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Quotes {

    @SerializedName("USD")
    @Expose
    private USD uSD;
    @SerializedName("BTC")
    @Expose
    private BTC bTC;

    public USD getUSD() {
        return uSD;
    }

    public void setUSD(USD uSD) {
        this.uSD = uSD;
    }

    public BTC getbTC() {
        return bTC;
    }

    public void setbTC(BTC bTC) {
        this.bTC = bTC;
    }
}
