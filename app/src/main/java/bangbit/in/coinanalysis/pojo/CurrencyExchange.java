package bangbit.in.coinanalysis.pojo;

/**
 * Created by Nagarajan on 4/19/2018.
 */

public class CurrencyExchange {
    private String name;
    private String country;

    public CurrencyExchange(String name, String country, String website) {
        this.name = name;
        this.country = country;
        this.website = website;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    private String website;
}
