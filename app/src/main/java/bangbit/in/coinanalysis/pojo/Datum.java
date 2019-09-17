
package bangbit.in.coinanalysis.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Datum implements Parcelable
{

    @SerializedName("time")
    @Expose
    private Integer time;
    @SerializedName("close")
    @Expose
    private Double close;
    @SerializedName("high")
    @Expose
    private Double high;
    @SerializedName("low")
    @Expose
    private Double low;
    @SerializedName("open")
    @Expose
    private Double open;
    @SerializedName("volumefrom")
    @Expose
    private Double volumefrom;
    @SerializedName("volumeto")
    @Expose
    private Double volumeto;
    public final static Creator<Datum> CREATOR = new Creator<Datum>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Datum createFromParcel(Parcel in) {
            return new Datum(in);
        }

        public Datum[] newArray(int size) {
            return (new Datum[size]);
        }

    }
    ;

    protected Datum(Parcel in) {
        this.time = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.close = ((Double) in.readValue((Double.class.getClassLoader())));
        this.high = ((Double) in.readValue((Integer.class.getClassLoader())));
        this.low = ((Double) in.readValue((Double.class.getClassLoader())));
        this.open = ((Double) in.readValue((Double.class.getClassLoader())));
        this.volumefrom = ((Double) in.readValue((Double.class.getClassLoader())));
        this.volumeto = ((Double) in.readValue((Double.class.getClassLoader())));
    }

    public Datum() {
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public Double getClose() {
        return close;
    }

    public void setClose(Double close) {
        this.close = close;
    }

    public Double getHigh() {
        return high;
    }

    public void setHigh(Double high) {
        this.high = high;
    }

    public Double getLow() {
        return low;
    }

    public void setLow(Double low) {
        this.low = low;
    }

    public Double getOpen() {
        return open;
    }

    public void setOpen(Double open) {
        this.open = open;
    }

    public Double getVolumefrom() {
        return volumefrom;
    }

    public void setVolumefrom(Double volumefrom) {
        this.volumefrom = volumefrom;
    }

    public Double getVolumeto() {
        return volumeto;
    }

    public void setVolumeto(Double volumeto) {
        this.volumeto = volumeto;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(time);
        dest.writeValue(close);
        dest.writeValue(high);
        dest.writeValue(low);
        dest.writeValue(open);
        dest.writeValue(volumefrom);
        dest.writeValue(volumeto);
    }

    public int describeContents() {
        return  0;
    }

}
