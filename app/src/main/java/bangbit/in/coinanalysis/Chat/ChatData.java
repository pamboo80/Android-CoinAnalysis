package bangbit.in.coinanalysis.Chat;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;

import bangbit.in.coinanalysis.pojo.Exchange;
import bangbit.in.coinanalysis.pojo.LambdaResponse;

public class ChatData implements Parcelable,Cloneable {
    private int viewType;
    private String message;
    private String dialogResponse;
    private LambdaResponse lambdaResponse;
    private ArrayList<String> sugessionArrayList;
    private ArrayList<Exchange> exchangeArrayList;
    private String time, date;
    private long id;

    private String currency;

    public ChatData() {
        Calendar calendar = Calendar.getInstance();
        calendar.getTimeInMillis();
        String date = "" + String.format("%02d", (calendar.get(Calendar.DAY_OF_MONTH))) + "/" + String.format("%02d", (calendar.get(Calendar.MONTH) + 1)) + "/" + String.format("%02d", (calendar.get(Calendar.YEAR)));
        String time = "" + String.format("%02d", (calendar.get(Calendar.HOUR))) + ":" + String.format("%02d", (calendar.get(Calendar.MINUTE)))  + ":" + String.format("%02d", (calendar.get(Calendar.SECOND)))  + " ";
        if (calendar.get(Calendar.AM_PM) == 0) {
            time += "AM";
        } else {
            time += "PM";
        }
        this.time = time;
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDialogResponse() {
        return dialogResponse;
    }

    public void setDialogResponse(String dialogResponse) {
        this.dialogResponse = dialogResponse;
    }

    public LambdaResponse getLambdaResponse() {
        return lambdaResponse;
    }

    public void setLambdaResponse(LambdaResponse lambdaResponse) {
        this.lambdaResponse = lambdaResponse;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public ArrayList<String> getSugessionArrayList() {
        return sugessionArrayList;
    }

    public void setSugessionArrayList(ArrayList<String> sugessionArrayList) {
        this.sugessionArrayList = sugessionArrayList;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setExchangeCurrency(String currency) {
        this.currency = currency;
    }

    public String getExchangeCurrency() {
        return this.currency;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ArrayList<Exchange> getExchangeArrayList() {
        return exchangeArrayList;
    }

    public void setExchangeArrayList(ArrayList<Exchange> exchangeArrayList) {
        this.exchangeArrayList = exchangeArrayList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.viewType);
        dest.writeString(this.message);
        dest.writeString(this.dialogResponse);
        dest.writeParcelable(this.lambdaResponse, flags);
        dest.writeStringList(this.sugessionArrayList);
        dest.writeTypedList(this.exchangeArrayList);
        dest.writeString(this.time);
        dest.writeString(this.date);
        dest.writeLong(this.id);
    }

    protected ChatData(Parcel in) {
        this.viewType = in.readInt();
        this.message = in.readString();
        this.dialogResponse = in.readString();
        this.lambdaResponse = in.readParcelable(LambdaResponse.class.getClassLoader());
        this.sugessionArrayList = in.createStringArrayList();
        this.exchangeArrayList = in.createTypedArrayList(Exchange.CREATOR);
        this.time = in.readString();
        this.date = in.readString();
        this.id = in.readLong();
    }

    public static final Parcelable.Creator<ChatData> CREATOR = new Parcelable.Creator<ChatData>() {
        @Override
        public ChatData createFromParcel(Parcel source) {
            return new ChatData(source);
        }

        @Override
        public ChatData[] newArray(int size) {
            return new ChatData[size];
        }
    };

    public Object clone()throws CloneNotSupportedException{
        return super.clone();
    }
}
