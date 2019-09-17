
package bangbit.in.coinanalysis.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CoinInfo implements Parcelable
{

    @SerializedName("Id")
    @Expose
    private String id;
    @SerializedName("Name")
    @Expose
    private String name;
    @SerializedName("FullName")
    @Expose
    private String fullName;
    @SerializedName("Internal")
    @Expose
    private String internal;
    @SerializedName("ImageUrl")
    @Expose
    private String imageUrl;
    @SerializedName("Url")
    @Expose
    private String url;
    @SerializedName("Algorithm")
    @Expose
    private String algorithm;
    @SerializedName("ProofType")
    @Expose
    private String proofType;
    @SerializedName("TotalCoinsMined")
    @Expose
    private Double totalCoinsMined;
    @SerializedName("BlockNumber")
    @Expose
    private String blockNumber;
    @SerializedName("NetHashesPerSecond")
    @Expose
    private Double netHashesPerSecond;
    @SerializedName("BlockReward")
    @Expose
    private Double blockReward;
    @SerializedName("TotalVolume24H")
    @Expose
    private Double totalVolume24H;
    public final static Creator<CoinInfo> CREATOR = new Creator<CoinInfo>() {


        @SuppressWarnings({
            "unchecked"
        })
        public CoinInfo createFromParcel(Parcel in) {
            return new CoinInfo(in);
        }

        public CoinInfo[] newArray(int size) {
            return (new CoinInfo[size]);
        }

    }
    ;

    protected CoinInfo(Parcel in) {
        this.id = ((String) in.readValue((String.class.getClassLoader())));
        this.name = ((String) in.readValue((String.class.getClassLoader())));
        this.fullName = ((String) in.readValue((String.class.getClassLoader())));
        this.internal = ((String) in.readValue((String.class.getClassLoader())));
        this.imageUrl = ((String) in.readValue((String.class.getClassLoader())));
        this.url = ((String) in.readValue((String.class.getClassLoader())));
        this.algorithm = ((String) in.readValue((String.class.getClassLoader())));
        this.proofType = ((String) in.readValue((String.class.getClassLoader())));
        this.totalCoinsMined = ((Double) in.readValue((Double.class.getClassLoader())));
        this.blockNumber = ((String) in.readValue((String.class.getClassLoader())));
        this.netHashesPerSecond = ((Double) in.readValue((Double.class.getClassLoader())));
        this.blockReward = ((Double) in.readValue((Double.class.getClassLoader())));
        this.totalVolume24H = ((Double) in.readValue((Double.class.getClassLoader())));
    }

    public CoinInfo() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getInternal() {
        return internal;
    }

    public void setInternal(String internal) {
        this.internal = internal;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public String getProofType() {
        return proofType;
    }

    public void setProofType(String proofType) {
        this.proofType = proofType;
    }

    public Double getTotalCoinsMined() {
        return totalCoinsMined;
    }

    public void setTotalCoinsMined(Double totalCoinsMined) {
        this.totalCoinsMined = totalCoinsMined;
    }

    public String getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(String blockNumber) {
        this.blockNumber = blockNumber;
    }

    public Double getNetHashesPerSecond() {
        return netHashesPerSecond;
    }

    public void setNetHashesPerSecond(Double netHashesPerSecond) {
        this.netHashesPerSecond = netHashesPerSecond;
    }

    public Double getBlockReward() {
        return blockReward;
    }

    public void setBlockReward(Double blockReward) {
        this.blockReward = blockReward;
    }

    public Double getTotalVolume24H() {
        return totalVolume24H;
    }

    public void setTotalVolume24H(Double totalVolume24H) {
        this.totalVolume24H = totalVolume24H;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(name);
        dest.writeValue(fullName);
        dest.writeValue(internal);
        dest.writeValue(imageUrl);
        dest.writeValue(url);
        dest.writeValue(algorithm);
        dest.writeValue(proofType);
        dest.writeValue(totalCoinsMined);
        dest.writeValue(blockNumber);
        dest.writeValue(netHashesPerSecond);
        dest.writeValue(blockReward);
        dest.writeValue(totalVolume24H);
    }

    public int describeContents() {
        return  0;
    }

}
