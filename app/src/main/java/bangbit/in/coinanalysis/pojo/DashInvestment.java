package bangbit.in.coinanalysis.pojo;

/**
 * Created by Nagarajan on 4/10/2018.
 */

public class DashInvestment {
    public DashInvestment(String symbol, String imageUrl, float price, float currentInvestmentValue, float boughtInvestmentValue, float profit, float totalQuantity) {
        this.symbol = symbol;
        this.imageUrl = imageUrl;
        this.price = price;
        this.currentInvestmentValue = currentInvestmentValue;
        this.boughtInvestmentValue = boughtInvestmentValue;
        this.profit = profit;
        this.totalQuantity = totalQuantity;
    }

    private String symbol="";
    private String imageUrl="";
    private float price,currentInvestmentValue, boughtInvestmentValue, profit, totalQuantity;

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public float getCurrentInvestmentValue() {
        return currentInvestmentValue;
    }

    public void setCurrentInvestmentValue(float currentInvestmentValue) {
        this.currentInvestmentValue = currentInvestmentValue;
    }

    public float getBoughtInvestmentValue() {
        return boughtInvestmentValue;
    }

    public void setBoughtInvestmentValue(float boughtInvestmentValue) {
        this.boughtInvestmentValue = boughtInvestmentValue;
    }

    public float getProfit() {
        return profit;
    }

    public void setProfit(float profit) {
        this.profit = profit;
    }

    public float getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(float totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
