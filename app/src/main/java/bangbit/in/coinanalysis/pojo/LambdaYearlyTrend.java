
package bangbit.in.coinanalysis.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LambdaYearlyTrend implements Parcelable{


    @SerializedName("coin_symbol")
    @Expose
    private String coinSymbol;
    @SerializedName("coin_name")
    @Expose
    private String coinName;
    @SerializedName("history_price")
    @Expose
    private String historyPrice;
    @SerializedName("history_price_date")
    @Expose
    private String historyPriceDate;

    public LambdaYearlyTrend(String coinSymbol, String coinName, String historyPrice, String historyPricedate) {
        this.coinSymbol = coinSymbol;
        this.coinName = coinName;
        this.historyPrice = historyPrice;
        this.historyPriceDate = historyPricedate;
    }


    public String getCoinSymbol() {
        return coinSymbol;
    }

    public void setCoinSymbol(String coinSymbol) {
        this.coinSymbol = coinSymbol;
    }

    public String getCoinName() {
        return coinName;
    }

    public void setCoinName(String coinName) {
        this.coinName = coinName;
    }

    public String getHistoryPrice() {
        return historyPrice;
    }

    public void setHistoryPrice(String historyPrice) {
        this.historyPrice = historyPrice;
    }

    public String getHistoryPriceDate() {
        return historyPriceDate;
    }

    public void setHistoryPriceDate(String historyPriceDate) {
        this.historyPriceDate = historyPriceDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.coinSymbol);
        dest.writeString(this.coinName);
        dest.writeString(this.historyPrice);
        dest.writeString(this.historyPriceDate);
    }

    public LambdaYearlyTrend() {
    }

    protected LambdaYearlyTrend(Parcel in) {
        this.coinSymbol = in.readString();
        this.coinName = in.readString();
        this.historyPrice = in.readString();
        this.historyPriceDate = in.readString();
    }

    public static final Parcelable.Creator<LambdaYearlyTrend> CREATOR = new Parcelable.Creator<LambdaYearlyTrend>() {
        @Override
        public LambdaYearlyTrend createFromParcel(Parcel source) {
            return new LambdaYearlyTrend(source);
        }

        @Override
        public LambdaYearlyTrend[] newArray(int size) {
            return new LambdaYearlyTrend[size];
        }
    };

}
