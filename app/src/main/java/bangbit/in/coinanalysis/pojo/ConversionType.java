
package bangbit.in.coinanalysis.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ConversionType implements Parcelable
{

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("conversionSymbol")
    @Expose
    private String conversionSymbol;
    public final static Creator<ConversionType> CREATOR = new Creator<ConversionType>() {


        @SuppressWarnings({
            "unchecked"
        })
        public ConversionType createFromParcel(Parcel in) {
            return new ConversionType(in);
        }

        public ConversionType[] newArray(int size) {
            return (new ConversionType[size]);
        }

    }
    ;

    protected ConversionType(Parcel in) {
        this.type = ((String) in.readValue((String.class.getClassLoader())));
        this.conversionSymbol = ((String) in.readValue((String.class.getClassLoader())));
    }

    public ConversionType() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getConversionSymbol() {
        return conversionSymbol;
    }

    public void setConversionSymbol(String conversionSymbol) {
        this.conversionSymbol = conversionSymbol;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(type);
        dest.writeValue(conversionSymbol);
    }

    public int describeContents() {
        return  0;
    }

}
