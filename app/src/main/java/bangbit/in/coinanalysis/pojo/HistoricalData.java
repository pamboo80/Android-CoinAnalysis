
package bangbit.in.coinanalysis.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class HistoricalData implements Parcelable
{

    @SerializedName("Response")
    @Expose
    private String response;
    @SerializedName("Type")
    @Expose
    private Integer type;
    @SerializedName("Aggregated")
    @Expose
    private Boolean aggregated;
    @SerializedName("Data")
    @Expose
    private ArrayList<Datum> data = null;
    @SerializedName("TimeTo")
    @Expose
    private Integer timeTo;
    @SerializedName("TimeFrom")
    @Expose
    private Integer timeFrom;
    @SerializedName("FirstValueInArray")
    @Expose
    private Boolean firstValueInArray;
    @SerializedName("ConversionType")
    @Expose
    private ConversionType conversionType;
    public final static Creator<HistoricalData> CREATOR = new Creator<HistoricalData>() {


        @SuppressWarnings({
            "unchecked"
        })
        public HistoricalData createFromParcel(Parcel in) {
            return new HistoricalData(in);
        }

        public HistoricalData[] newArray(int size) {
            return (new HistoricalData[size]);
        }

    }
    ;

    protected HistoricalData(Parcel in) {
        this.response = ((String) in.readValue((String.class.getClassLoader())));
        this.type = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.aggregated = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
        in.readList(this.data, (Datum.class.getClassLoader()));
        this.timeTo = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.timeFrom = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.firstValueInArray = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
        this.conversionType = ((ConversionType) in.readValue((ConversionType.class.getClassLoader())));
    }

    public HistoricalData() {
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Boolean getAggregated() {
        return aggregated;
    }

    public void setAggregated(Boolean aggregated) {
        this.aggregated = aggregated;
    }

    public ArrayList<Datum> getData() {
        return data;
    }

    public void setData(ArrayList<Datum> data) {
        this.data = data;
    }

    public Integer getTimeTo() {
        return timeTo;
    }

    public void setTimeTo(Integer timeTo) {
        this.timeTo = timeTo;
    }

    public Integer getTimeFrom() {
        return timeFrom;
    }

    public void setTimeFrom(Integer timeFrom) {
        this.timeFrom = timeFrom;
    }

    public Boolean getFirstValueInArray() {
        return firstValueInArray;
    }

    public void setFirstValueInArray(Boolean firstValueInArray) {
        this.firstValueInArray = firstValueInArray;
    }

    public ConversionType getConversionType() {
        return conversionType;
    }

    public void setConversionType(ConversionType conversionType) {
        this.conversionType = conversionType;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(response);
        dest.writeValue(type);
        dest.writeValue(aggregated);
        dest.writeList(data);
        dest.writeValue(timeTo);
        dest.writeValue(timeFrom);
        dest.writeValue(firstValueInArray);
        dest.writeValue(conversionType);
    }

    public int describeContents() {
        return  0;
    }

}
