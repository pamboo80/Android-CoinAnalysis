package bangbit.in.coinanalysis.pojo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Nagarajan on 3/28/2018.
 */

public class Investment implements Parcelable {
    private String coinSymbol;
    private float quantity;
    private float boughtPrice;
    private String boughtDate;
    private String notes;
    private int id;
    @Override
    public String toString() {
        return "Investment{" +
                "coinSymbol='" + coinSymbol + '\'' +
                ", quantity=" + quantity +
                ", boughtPrice=" + boughtPrice +
                ", boughtDate='" + boughtDate + '\'' +
                ", notes='" + notes + '\'' +
                ", id=" + id +
                '}';
    }




    public String getCoinSymbol() {
        return coinSymbol;
    }

    public void setCoinSymbol(String coinSymbol) {
        this.coinSymbol = coinSymbol;
    }

    public float getQuantity() {
        return quantity;
    }

    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }

    public float getBoughtPrice() {
        return boughtPrice;
    }

    public void setBoughtPrice(float boughtPrice) {
        this.boughtPrice = boughtPrice;
    }

    public String getBoughtDate() {
        return boughtDate;
    }

    public void setBoughtDate(String boughtDate) {
        this.boughtDate = boughtDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.coinSymbol);
        dest.writeFloat(this.quantity);
        dest.writeFloat(this.boughtPrice);
        dest.writeString(this.boughtDate);
        dest.writeString(this.notes);
        dest.writeInt(this.id);
    }

    public Investment() {
    }

    protected Investment(Parcel in) {
        this.coinSymbol = in.readString();
        this.quantity = in.readFloat();
        this.boughtPrice = in.readFloat();
        this.boughtDate = in.readString();
        this.notes = in.readString();
        this.id = in.readInt();
    }

    public static final Parcelable.Creator<Investment> CREATOR = new Parcelable.Creator<Investment>() {
        @Override
        public Investment createFromParcel(Parcel source) {
            return new Investment(source);
        }

        @Override
        public Investment[] newArray(int size) {
            return new Investment[size];
        }
    };
}
