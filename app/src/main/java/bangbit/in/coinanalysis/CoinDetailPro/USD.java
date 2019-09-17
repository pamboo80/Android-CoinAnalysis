
package bangbit.in.coinanalysis.CoinDetailPro;

import java.util.HashMap;
import java.util.Map;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class USD implements Parcelable
{

    private Double price;
    private Double volume24h;
    private Double percentChange1h;
    private Double percentChange24h;
    private Double percentChange7d;
    private Double marketCap;
    private String lastUpdated;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    public final static Parcelable.Creator<USD> CREATOR = new Creator<USD>() {


        @SuppressWarnings({
            "unchecked"
        })
        public USD createFromParcel(Parcel in) {
            return new USD(in);
        }

        public USD[] newArray(int size) {
            return (new USD[size]);
        }

    }
    ;

    protected USD(Parcel in) {
        this.price = ((Double) in.readValue((Double.class.getClassLoader())));
        this.volume24h = ((Double) in.readValue((Double.class.getClassLoader())));
        this.percentChange1h = ((Double) in.readValue((Double.class.getClassLoader())));
        this.percentChange24h = ((Double) in.readValue((Double.class.getClassLoader())));
        this.percentChange7d = ((Double) in.readValue((Double.class.getClassLoader())));
        this.marketCap = ((Double) in.readValue((Double.class.getClassLoader())));
        this.lastUpdated = ((String) in.readValue((String.class.getClassLoader())));
        this.additionalProperties = ((Map<String, Object> ) in.readValue((Map.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     * 
     */
    public USD() {
    }

    /**
     * 
     * @param marketCap
     * @param percentChange24h
     * @param volume24h
     * @param price
     * @param percentChange7d
     * @param lastUpdated
     * @param percentChange1h
     */
    public USD(Double price, Double volume24h, Double percentChange1h, Double percentChange24h, Double percentChange7d, Double marketCap, String lastUpdated) {
        super();
        this.price = price;
        this.volume24h = volume24h;
        this.percentChange1h = percentChange1h;
        this.percentChange24h = percentChange24h;
        this.percentChange7d = percentChange7d;
        this.marketCap = marketCap;
        this.lastUpdated = lastUpdated;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getVolume24h() {
        return volume24h;
    }

    public void setVolume24h(Double volume24h) {
        this.volume24h = volume24h;
    }

    public Double getPercentChange1h() {
        return percentChange1h;
    }

    public void setPercentChange1h(Double percentChange1h) {
        this.percentChange1h = percentChange1h;
    }

    public Double getPercentChange24h() {
        return percentChange24h;
    }

    public void setPercentChange24h(Double percentChange24h) {
        this.percentChange24h = percentChange24h;
    }

    public Double getPercentChange7d() {
        return percentChange7d;
    }

    public void setPercentChange7d(Double percentChange7d) {
        this.percentChange7d = percentChange7d;
    }

    public Double getMarketCap() {
        return marketCap;
    }

    public void setMarketCap(Double marketCap) {
        this.marketCap = marketCap;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(price);
        dest.writeValue(volume24h);
        dest.writeValue(percentChange1h);
        dest.writeValue(percentChange24h);
        dest.writeValue(percentChange7d);
        dest.writeValue(marketCap);
        dest.writeValue(lastUpdated);
        dest.writeValue(additionalProperties);
    }

    public int describeContents() {
        return  0;
    }

}
