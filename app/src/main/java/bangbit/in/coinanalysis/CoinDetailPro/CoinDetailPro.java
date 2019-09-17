
package bangbit.in.coinanalysis.CoinDetailPro;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class CoinDetailPro implements Parcelable
{

    private Long id;
    private String name;
    private String symbol;
    private String slug;
    private Long circulatingSupply;
    private Long totalSupply;
    private Long maxSupply;
    private String dateAdded;
    private Long numMarketPairs;
    private List<String> tags = new ArrayList<String>();
    private String platform;
    private Long cmcRank;
    private String lastUpdated;
    private Quote quote;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    public final static Parcelable.Creator<CoinDetailPro> CREATOR = new Creator<CoinDetailPro>() {


        @SuppressWarnings({
            "unchecked"
        })
        public CoinDetailPro createFromParcel(Parcel in) {
            return new CoinDetailPro(in);
        }

        public CoinDetailPro[] newArray(int size) {
            return (new CoinDetailPro[size]);
        }

    }
    ;

    protected CoinDetailPro(Parcel in) {
        this.id = ((Long) in.readValue((Long.class.getClassLoader())));
        this.name = ((String) in.readValue((String.class.getClassLoader())));
        this.symbol = ((String) in.readValue((String.class.getClassLoader())));
        this.slug = ((String) in.readValue((String.class.getClassLoader())));
        this.circulatingSupply = ((Long) in.readValue((Long.class.getClassLoader())));
        this.totalSupply = ((Long) in.readValue((Long.class.getClassLoader())));
        this.maxSupply = ((Long) in.readValue((Long.class.getClassLoader())));
        this.dateAdded = ((String) in.readValue((String.class.getClassLoader())));
        this.numMarketPairs = ((Long) in.readValue((Long.class.getClassLoader())));
        in.readList(this.tags, (java.lang.String.class.getClassLoader()));
        this.platform = ((String) in.readValue((String.class.getClassLoader())));
        this.cmcRank = ((Long) in.readValue((Long.class.getClassLoader())));
        this.lastUpdated = ((String) in.readValue((String.class.getClassLoader())));
        this.quote = ((Quote) in.readValue((Quote.class.getClassLoader())));
        this.additionalProperties = ((Map<String, Object> ) in.readValue((Map.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     * 
     */
    public CoinDetailPro() {
    }

    /**
     * 
     * @param tags
     * @param platform
     * @param symbol
     * @param lastUpdated
     * @param dateAdded
     * @param numMarketPairs
     * @param cmcRank
     * @param id
     * @param maxSupply
     * @param circulatingSupply
     * @param quote
     * @param name
     * @param slug
     * @param totalSupply
     */
    public CoinDetailPro(Long id, String name, String symbol, String slug, Long circulatingSupply, Long totalSupply, Long maxSupply, String dateAdded, Long numMarketPairs, List<String> tags, String platform, Long cmcRank, String lastUpdated, Quote quote) {
        super();
        this.id = id;
        this.name = name;
        this.symbol = symbol;
        this.slug = slug;
        this.circulatingSupply = circulatingSupply;
        this.totalSupply = totalSupply;
        this.maxSupply = maxSupply;
        this.dateAdded = dateAdded;
        this.numMarketPairs = numMarketPairs;
        this.tags = tags;
        this.platform = platform;
        this.cmcRank = cmcRank;
        this.lastUpdated = lastUpdated;
        this.quote = quote;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public Long getCirculatingSupply() {
        return circulatingSupply;
    }

    public void setCirculatingSupply(Long circulatingSupply) {
        this.circulatingSupply = circulatingSupply;
    }

    public Long getTotalSupply() {
        return totalSupply;
    }

    public void setTotalSupply(Long totalSupply) {
        this.totalSupply = totalSupply;
    }

    public Long getMaxSupply() {
        return maxSupply;
    }

    public void setMaxSupply(Long maxSupply) {
        this.maxSupply = maxSupply;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(String dateAdded) {
        this.dateAdded = dateAdded;
    }

    public Long getNumMarketPairs() {
        return numMarketPairs;
    }

    public void setNumMarketPairs(Long numMarketPairs) {
        this.numMarketPairs = numMarketPairs;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Object getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public Long getCmcRank() {
        return cmcRank;
    }

    public void setCmcRank(Long cmcRank) {
        this.cmcRank = cmcRank;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Quote getQuote() {
        return quote;
    }

    public void setQuote(Quote quote) {
        this.quote = quote;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(name);
        dest.writeValue(symbol);
        dest.writeValue(slug);
        dest.writeValue(circulatingSupply);
        dest.writeValue(totalSupply);
        dest.writeValue(maxSupply);
        dest.writeValue(dateAdded);
        dest.writeValue(numMarketPairs);
        dest.writeList(tags);
        dest.writeValue(platform);
        dest.writeValue(cmcRank);
        dest.writeValue(lastUpdated);
        dest.writeValue(quote);
        dest.writeValue(additionalProperties);
    }

    public int describeContents() {
        return  0;
    }

}
