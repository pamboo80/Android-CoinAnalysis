package bangbit.in.coinanalysis.pojo;

/**
 * Created by Nagarajan on 3/9/2018.
 */

public class CoinImageUrl {
    private String symbol, imageurl;

    public CoinImageUrl(String symbol,String imageurl){
        this.symbol=symbol;
        this.imageurl=imageurl;
    }
    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
}
