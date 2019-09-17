
package bangbit.in.coinanalysis.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LambdaHistory implements Parcelable{


    @SerializedName("coin_symbol")
    @Expose
    private String coinSymbol;
    @SerializedName("open")
    @Expose
    private String open;
    @SerializedName("close")
    @Expose
    private String close;
    @SerializedName("low")
    @Expose
    private String low;
    @SerializedName("high")
    @Expose
    private String high;

    public LambdaHistory(String coinSymbol, String open, String close, String low, String high, String time) {
        this.coinSymbol = coinSymbol;
        this.open = open;
        this.close = close;
        this.low = low;
        this.high = high;
        this.time = time;
    }

    @SerializedName("time")
    @Expose
    private String time;


    public String getHigh() {
        return high;
    }

    public void setHigh(String high) {
        this.high = high;
    }

    public String getCoinSymbol() {
        return coinSymbol;
    }

    public void setCoinSymbol(String coinSymbol) {
        this.coinSymbol = coinSymbol;
    }

    public String getLow() {
        return low;
    }

    public void setLow(String low) {
        this.low = low;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getClose() {
        return close;
    }

    public void setClose(String close) {
        this.close = close;
    }

    public String getOpen() {
        return open;
    }

    public void setOpen(String open) {
        this.open = open;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.high);
        dest.writeString(this.coinSymbol);
        dest.writeString(this.low);
        dest.writeString(this.time);
        dest.writeString(this.close);
        dest.writeString(this.open);
    }

    public LambdaHistory() {
    }

    protected LambdaHistory(Parcel in) {
        this.high = in.readString();
        this.coinSymbol = in.readString();
        this.low = in.readString();
        this.time = in.readString();
        this.close = in.readString();
        this.open = in.readString();
    }

    public static final Parcelable.Creator<LambdaHistory> CREATOR = new Parcelable.Creator<LambdaHistory>() {
        @Override
        public LambdaHistory createFromParcel(Parcel source) {
            return new LambdaHistory(source);
        }

        @Override
        public LambdaHistory[] newArray(int size) {
            return new LambdaHistory[size];
        }
    };

}
