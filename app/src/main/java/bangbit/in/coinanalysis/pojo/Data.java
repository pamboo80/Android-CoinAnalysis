
package bangbit.in.coinanalysis.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Data implements Parcelable
{

    @SerializedName("CoinInfo")
    @Expose
    private CoinInfo coinInfo;
    @SerializedName("AggregatedData")
    @Expose
    private AggregatedData aggregatedData;
    @SerializedName("Exchanges")
    @Expose
    private ArrayList<Exchange> exchanges = null;
    public final static Creator<Data> CREATOR = new Creator<Data>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Data createFromParcel(Parcel in) {
            return new Data(in);
        }

        public Data[] newArray(int size) {
            return (new Data[size]);
        }
    }
    ;

    protected Data(Parcel in) {
        this.coinInfo = ((CoinInfo) in.readValue((CoinInfo.class.getClassLoader())));
        this.aggregatedData = ((AggregatedData) in.readValue((AggregatedData.class.getClassLoader())));
        in.readList(this.exchanges, (bangbit.in.coinanalysis.pojo.Exchange.class.getClassLoader()));
    }

    public Data() {
    }

    public CoinInfo getCoinInfo() {
        return coinInfo;
    }

    public void setCoinInfo(CoinInfo coinInfo) {
        this.coinInfo = coinInfo;
    }

    public AggregatedData getAggregatedData() {
        return aggregatedData;
    }

    public void setAggregatedData(AggregatedData aggregatedData) {
        this.aggregatedData = aggregatedData;
    }

    public ArrayList<Exchange> getExchanges() {
        return exchanges;
    }

    public void setExchanges(ArrayList<Exchange> exchanges) {
        this.exchanges = exchanges;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(coinInfo);
        dest.writeValue(aggregatedData);
        dest.writeList(exchanges);
    }

    public int describeContents() {
        return  0;
    }

}
