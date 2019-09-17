
package bangbit.in.coinanalysis.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LambdaAnalysis implements Parcelable{

    @SerializedName("coin_symbol")
    @Expose
    private String coinSymbol;
    @SerializedName("coin_name")
    @Expose
    private String coinName;
    @SerializedName("price")
    @Expose
    private String price;
    @SerializedName("percentage_change")
    @Expose
    private String percentageChange;
    @SerializedName("platform_name")
    @Expose
    private String platformName;
    @SerializedName("rank")
    @Expose
    private String rank;

    public LambdaAnalysis(String coinSymbol, String coinName, String price, String percentageChange,String platformName, String rank) {
        this.coinSymbol = coinSymbol;
        this.coinName = coinName;
        this.price = price;
        this.percentageChange = percentageChange;
        this.platformName = platformName;
        this.rank = rank;
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPercentageChange() {
        return percentageChange;
    }

    public void setPercentageChange(String percentageChange) {
        this.percentageChange = percentageChange;
    }

    public String getPlatformName() {
        return platformName;
    }

    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.coinSymbol);
        dest.writeString(this.coinName);
        dest.writeString(this.price);
        dest.writeString(this.percentageChange);
        dest.writeString(this.platformName);
        dest.writeString(this.rank);
    }

    public LambdaAnalysis() {
    }

    protected LambdaAnalysis(Parcel in) {
        this.coinSymbol = in.readString();
        this.coinName = in.readString();
        this.price = in.readString();
        this.percentageChange = in.readString();
        this.platformName = in.readString();
        this.rank = in.readString();
    }

    public static final Parcelable.Creator<LambdaAnalysis> CREATOR = new Parcelable.Creator<LambdaAnalysis>() {
        @Override
        public LambdaAnalysis createFromParcel(Parcel source) {
            return new LambdaAnalysis(source);
        }

        @Override
        public LambdaAnalysis[] newArray(int size) {
            return new LambdaAnalysis[size];
        }
    };

}
