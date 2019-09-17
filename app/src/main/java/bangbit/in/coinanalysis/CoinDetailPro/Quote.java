
package bangbit.in.coinanalysis.CoinDetailPro;

import java.util.HashMap;
import java.util.Map;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class Quote implements Parcelable
{

    private USD uSD;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    public final static Parcelable.Creator<Quote> CREATOR = new Creator<Quote>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Quote createFromParcel(Parcel in) {
            return new Quote(in);
        }

        public Quote[] newArray(int size) {
            return (new Quote[size]);
        }

    }
    ;

    protected Quote(Parcel in) {
        this.uSD = ((USD) in.readValue((USD.class.getClassLoader())));
        this.additionalProperties = ((Map<String, Object> ) in.readValue((Map.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     * 
     */
    public Quote() {
    }

    /**
     * 
     * @param uSD
     */
    public Quote(USD uSD) {
        super();
        this.uSD = uSD;
    }

    public USD getUSD() {
        return uSD;
    }

    public void setUSD(USD uSD) {
        this.uSD = uSD;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(uSD);
        dest.writeValue(additionalProperties);
    }

    public int describeContents() {
        return  0;
    }

}
