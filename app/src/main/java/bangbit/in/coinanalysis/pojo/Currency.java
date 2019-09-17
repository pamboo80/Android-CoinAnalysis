package bangbit.in.coinanalysis.pojo;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Nagarajan on 1/10/2018.
 */

public class Currency implements Parcelable {
    public Currency(String symbol, String name, String currencylogo, int logo){
        setImageLogo(logo);
        setName(name);
        setSymbol(symbol);
        setCurrencylogo(currencylogo);
    }

    private String name;
    private String symbol;
    private String currencylogo;
    private int imageLogo;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getCurrencylogo() {
        return currencylogo;
    }

    public void setCurrencylogo(String currencylogo) {
        this.currencylogo = currencylogo;
    }

    public int getImageLogo() {
        return imageLogo;
    }

    public void setImageLogo(int imageLogo) {
        this.imageLogo = imageLogo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.symbol);
        dest.writeString(this.currencylogo);
        dest.writeInt(this.imageLogo);
    }

    protected Currency(Parcel in) {
        this.name = in.readString();
        this.symbol = in.readString();
        this.currencylogo = in.readString();
        this.imageLogo = in.readInt();
    }

    public static final Parcelable.Creator<Currency> CREATOR = new Parcelable.Creator<Currency>() {
        @Override
        public Currency createFromParcel(Parcel source) {
            return new Currency(source);
        }

        @Override
        public Currency[] newArray(int size) {
            return new Currency[size];
        }
    };
}
