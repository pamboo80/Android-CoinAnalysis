
package bangbit.in.coinanalysis.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LambdaResponse implements Parcelable {

    @SerializedName("symbol")
    @Expose
    private String symbol;
    @SerializedName("circulating_supply")
    @Expose
    private String circulatingSupply;
    @SerializedName("data")
    @Expose
    private List<LambdaHistory> data = null;
    @SerializedName("total_supply")
    @Expose
    private String totalSupply;
    @SerializedName("percent_change_1h")
    @Expose
    private String percentChange1h;
    @SerializedName("coin_name")
    @Expose
    private String coinName;
    @SerializedName("market_cap")
    @Expose
    private String marketCap;
    @SerializedName("percent_change_24h")
    @Expose
    private String percentChange24h;
    @SerializedName("price")
    @Expose
    private String price;
    @SerializedName("max_supply")
    @Expose
    private String maxSupply;
    @SerializedName("volume_24h")
    @Expose
    private String volume24h;
    @SerializedName("rank")
    @Expose
    private String rank;
    @SerializedName("percent_change_7d")
    @Expose
    private String percentChange7d;
    @SerializedName("price_btc")
    @Expose
    private String price_btc;
    @SerializedName("is24Hour")
    @Expose
    private boolean is24Hour;

    @SerializedName("last_updated")
    @Expose
    private String last_updated;

    @SerializedName("success")
    @Expose
    private String success;

    @SerializedName("analysis")
    @Expose
    private List<LambdaAnalysis> analysis;
    @SerializedName("yearlytrend")
    @Expose
    private List<LambdaYearlyTrend> yearlytrend = null;

    @SerializedName("fromDate")
    @Expose
    private String fromDate;
    @SerializedName("toDate")
    @Expose
    private String toDate;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getCirculatingSupply() {
        return circulatingSupply;
    }

    public void setCirculatingSupply(String circulatingSupply) {
        this.circulatingSupply = circulatingSupply;
    }

    public List<LambdaHistory> getData() {
        return data;
    }

    public void setData(List<LambdaHistory> data) {
        this.data = data;
    }

    public List<LambdaYearlyTrend> getYearlyTrend() {
        return yearlytrend;
    }

    public void setYearlyTrendData(List<LambdaYearlyTrend> yearlytrend) {
        this.yearlytrend = yearlytrend;
    }

    public String getTotalSupply() {
        return totalSupply;
    }

    public void setTotalSupply(String totalSupply) {
        this.totalSupply = totalSupply;
    }

    public String getPercentChange1h() {
        return percentChange1h;
    }

    public void setPercentChange1h(String percentChange1h) {
        this.percentChange1h = percentChange1h;
    }

    public String getCoinName() {
        return coinName;
    }

    public void setCoinName(String coinName) {
        this.coinName = coinName;
    }

    public String getMarketCap() {
        return marketCap;
    }

    public void setMarketCap(String marketCap) {
        this.marketCap = marketCap;
    }

    public String getPercentChange24h() {
        return percentChange24h;
    }

    public void setPercentChange24h(String percentChange24h) {
        this.percentChange24h = percentChange24h;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String last_updated) {
        this.success = success;
    }

    public List<LambdaAnalysis>  getAnalysis() {
        return analysis;
    }

    public void setAnalysis(List<LambdaAnalysis>  analysis) {
        this.analysis = analysis;
    }

    public String getLastUpdated() {
        return last_updated;
    }

    public void setLastUpdated(String last_updated) {
        this.last_updated = last_updated;
    }

    public String getMaxSupply() {
        return maxSupply;
    }

    public void setMaxSupply(String maxSupply) {
        this.maxSupply = maxSupply;
    }

    public String getVolume24h() {
        return volume24h;
    }

    public void setVolume24h(String volume24h) {
        this.volume24h = volume24h;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String rank) {
        this.fromDate = fromDate;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getPercentChange7d() {
        return percentChange7d;
    }

    public void setPercentChange7d(String percentChange7d) {
        this.percentChange7d = percentChange7d;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.symbol);
        dest.writeString(this.circulatingSupply);
        dest.writeList(this.data);
        dest.writeString(this.totalSupply);
        dest.writeString(this.percentChange1h);
        dest.writeString(this.coinName);
        dest.writeString(this.marketCap);
        dest.writeString(this.percentChange24h);
        dest.writeString(this.price);
        dest.writeString(this.maxSupply);
        dest.writeString(this.volume24h);
        dest.writeString(this.rank);
        dest.writeString(this.percentChange7d);
        dest.writeString(this.price_btc);
        dest.writeByte((byte) (is24Hour ? 1 : 0));
        dest.writeString(this.last_updated);

        dest.writeString(this.success);
        dest.writeList(this.analysis);
        dest.writeList(this.yearlytrend);

        dest.writeString(this.fromDate);
        dest.writeString(this.toDate);
    }

    public LambdaResponse() {
    }

    protected LambdaResponse(Parcel in) {
        this.symbol = in.readString();
        this.circulatingSupply = in.readString();
        this.data = new ArrayList<LambdaHistory>();
        in.readList(this.data, LambdaHistory.class.getClassLoader());
        this.totalSupply = in.readString();
        this.percentChange1h = in.readString();
        this.coinName = in.readString();
        this.marketCap = in.readString();
        this.percentChange24h = in.readString();
        this.price = in.readString();
        this.maxSupply = in.readString();
        this.volume24h = in.readString();
        this.rank = in.readString();
        this.percentChange7d = in.readString();
        this.price_btc = in.readString();
        this.is24Hour=in.readByte() != 0;
        this.last_updated=in.readString();

        this.success=in.readString();

        this.analysis=new ArrayList<LambdaAnalysis>();
        in.readList(this.analysis, LambdaAnalysis.class.getClassLoader());

        this.yearlytrend = new ArrayList<LambdaYearlyTrend>();
        in.readList(this.yearlytrend, LambdaYearlyTrend.class.getClassLoader());

        this.fromDate=in.readString();
        this.toDate=in.readString();
    }

    public static final Parcelable.Creator<LambdaResponse> CREATOR = new Parcelable.Creator<LambdaResponse>() {
        @Override
        public LambdaResponse createFromParcel(Parcel source) {
            return new LambdaResponse(source);
        }

        @Override
        public LambdaResponse[] newArray(int size) {
            return new LambdaResponse[size];
        }
    };

    public boolean is24Hrs() {
        return is24Hour;
    }

    public void setIs24Hour(boolean is24Hour) {
        this.is24Hour = is24Hour;
    }

    public String getPrice_btc() {
        return price_btc;
    }

    public void setPrice_btc(String price_btc) {
        this.price_btc = price_btc;
    }

}
