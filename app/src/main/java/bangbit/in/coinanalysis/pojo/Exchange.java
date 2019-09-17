
package bangbit.in.coinanalysis.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Exchange implements Parcelable
{

    @SerializedName("TYPE")
    @Expose
    private String tYPE;
    @SerializedName("MARKET")
    @Expose
    private String mARKET;
    @SerializedName("FROMSYMBOL")
    @Expose
    private String fROMSYMBOL;
    @SerializedName("TOSYMBOL")
    @Expose
    private String tOSYMBOL;
    @SerializedName("FLAGS")
    @Expose
    private String fLAGS;
    @SerializedName("PRICE")
    @Expose
    private Double pRICE;
    @SerializedName("LASTUPDATE")
    @Expose
    private Double lASTUPDATE;
    @SerializedName("LASTVOLUME")
    @Expose
    private Double lASTVOLUME;
    @SerializedName("LASTVOLUMETO")
    @Expose
    private Double lASTVOLUMETO;
    @SerializedName("LASTTRADEID")
    @Expose
    private String lASTTRADEID;
    @SerializedName("VOLUME24HOUR")
    @Expose
    private Double vOLUME24HOUR;
    @SerializedName("VOLUME24HOURTO")
    @Expose
    private Double vOLUME24HOURTO;
    @SerializedName("OPEN24HOUR")
    @Expose
    private Double oPEN24HOUR;
    @SerializedName("HIGH24HOUR")
    @Expose
    private Double hIGH24HOUR;
    @SerializedName("LOW24HOUR")
    @Expose
    private Double lOW24HOUR;
    @SerializedName("CHANGE24HOUR")
    @Expose
    private Double cHANGE24HOUR;
    @SerializedName("CHANGEPCT24HOUR")
    @Expose
    private Double cHANGEPCT24HOUR;
    @SerializedName("CHANGEDAY")
    @Expose
    private Double cHANGEDAY;
    @SerializedName("CHANGEPCTDAY")
    @Expose
    private Double cHANGEPCTDAY;
    @SerializedName("SUPPLY")
    @Expose
    private Double sUPPLY;
    @SerializedName("MKTCAP")
    @Expose
    private Double mKTCAP;
    @SerializedName("TOTALVOLUME24H")
    @Expose
    private Double tOTALVOLUME24H;
    @SerializedName("TOTALVOLUME24HTO")
    @Expose
    private Double tOTALVOLUME24HTO;
    public final static Creator<Exchange> CREATOR = new Creator<Exchange>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Exchange createFromParcel(Parcel in) {
            return new Exchange(in);
        }

        public Exchange[] newArray(int size) {
            return (new Exchange[size]);
        }

    }
    ;

    protected Exchange(Parcel in) {
        this.tYPE = ((String) in.readValue((String.class.getClassLoader())));
        this.mARKET = ((String) in.readValue((String.class.getClassLoader())));
        this.fROMSYMBOL = ((String) in.readValue((String.class.getClassLoader())));
        this.tOSYMBOL = ((String) in.readValue((String.class.getClassLoader())));
        this.fLAGS = ((String) in.readValue((String.class.getClassLoader())));
        this.pRICE = ((Double) in.readValue((Double.class.getClassLoader())));
        this.lASTUPDATE = ((Double) in.readValue((Double.class.getClassLoader())));
        this.lASTVOLUME = ((Double) in.readValue((Double.class.getClassLoader())));
        this.lASTVOLUMETO = ((Double) in.readValue((Double.class.getClassLoader())));
        this.lASTTRADEID = ((String) in.readValue((String.class.getClassLoader())));
        this.vOLUME24HOUR = ((Double) in.readValue((Double.class.getClassLoader())));
        this.vOLUME24HOURTO = ((Double) in.readValue((Double.class.getClassLoader())));
        this.oPEN24HOUR = ((Double) in.readValue((Double.class.getClassLoader())));
        this.hIGH24HOUR = ((Double) in.readValue((Double.class.getClassLoader())));
        this.lOW24HOUR = ((Double) in.readValue((Double.class.getClassLoader())));
        this.cHANGE24HOUR = ((Double) in.readValue((Double.class.getClassLoader())));
        this.cHANGEPCT24HOUR = ((Double) in.readValue((Double.class.getClassLoader())));
        this.cHANGEDAY = ((Double) in.readValue((Double.class.getClassLoader())));
        this.cHANGEPCTDAY = ((Double) in.readValue((Double.class.getClassLoader())));
        this.sUPPLY = ((Double) in.readValue((Double.class.getClassLoader())));
        this.mKTCAP = ((Double) in.readValue((Double.class.getClassLoader())));
        this.tOTALVOLUME24H = ((Double) in.readValue((Double.class.getClassLoader())));
        this.tOTALVOLUME24HTO = ((Double) in.readValue((Double.class.getClassLoader())));
    }

    public Exchange() {
    }

    public String getTYPE() {
        return tYPE;
    }

    public void setTYPE(String tYPE) {
        this.tYPE = tYPE;
    }

    public String getMARKET() {
        return mARKET;
    }

    public void setMARKET(String mARKET) {
        this.mARKET = mARKET;
    }

    public String getFROMSYMBOL() {
        return fROMSYMBOL;
    }

    public void setFROMSYMBOL(String fROMSYMBOL) {
        this.fROMSYMBOL = fROMSYMBOL;
    }

    public String getTOSYMBOL() {
        return tOSYMBOL;
    }

    public void setTOSYMBOL(String tOSYMBOL) {
        this.tOSYMBOL = tOSYMBOL;
    }

    public String getFLAGS() {
        return fLAGS;
    }

    public void setFLAGS(String fLAGS) {
        this.fLAGS = fLAGS;
    }

    public Double getPRICE() {
        return pRICE;
    }

    public void setPRICE(Double pRICE) {
        this.pRICE = pRICE;
    }

    public Double getLASTUPDATE() {
        return lASTUPDATE;
    }

    public void setLASTUPDATE(Double lASTUPDATE) {
        this.lASTUPDATE = lASTUPDATE;
    }

    public Double getLASTVOLUME() {
        return lASTVOLUME;
    }

    public void setLASTVOLUME(Double lASTVOLUME) {
        this.lASTVOLUME = lASTVOLUME;
    }

    public Double getLASTVOLUMETO() {
        return lASTVOLUMETO;
    }

    public void setLASTVOLUMETO(Double lASTVOLUMETO) {
        this.lASTVOLUMETO = lASTVOLUMETO;
    }

    public String getLASTTRADEID() {
        return lASTTRADEID;
    }

    public void setLASTTRADEID(String lASTTRADEID) {
        this.lASTTRADEID = lASTTRADEID;
    }

    public Double getVOLUME24HOUR() {
        return vOLUME24HOUR;
    }

    public void setVOLUME24HOUR(Double vOLUME24HOUR) {
        this.vOLUME24HOUR = vOLUME24HOUR;
    }

    public Double getVOLUME24HOURTO() {
        return vOLUME24HOURTO;
    }

    public void setVOLUME24HOURTO(Double vOLUME24HOURTO) {
        this.vOLUME24HOURTO = vOLUME24HOURTO;
    }

    public Double getOPEN24HOUR() {
        return oPEN24HOUR;
    }

    public void setOPEN24HOUR(Double oPEN24HOUR) {
        this.oPEN24HOUR = oPEN24HOUR;
    }

    public Double getHIGH24HOUR() {
        return hIGH24HOUR;
    }

    public void setHIGH24HOUR(Double hIGH24HOUR) {
        this.hIGH24HOUR = hIGH24HOUR;
    }

    public Double getLOW24HOUR() {
        return lOW24HOUR;
    }

    public void setLOW24HOUR(Double lOW24HOUR) {
        this.lOW24HOUR = lOW24HOUR;
    }

    public Double getCHANGE24HOUR() {
        return cHANGE24HOUR;
    }

    public void setCHANGE24HOUR(Double cHANGE24HOUR) {
        this.cHANGE24HOUR = cHANGE24HOUR;
    }

    public Double getCHANGEPCT24HOUR() {
        return cHANGEPCT24HOUR;
    }

    public void setCHANGEPCT24HOUR(Double cHANGEPCT24HOUR) {
        this.cHANGEPCT24HOUR = cHANGEPCT24HOUR;
    }

    public Double getCHANGEDAY() {
        return cHANGEDAY;
    }

    public void setCHANGEDAY(Double cHANGEDAY) {
        this.cHANGEDAY = cHANGEDAY;
    }

    public Double getCHANGEPCTDAY() {
        return cHANGEPCTDAY;
    }

    public void setCHANGEPCTDAY(Double cHANGEPCTDAY) {
        this.cHANGEPCTDAY = cHANGEPCTDAY;
    }

    public Double getSUPPLY() {
        return sUPPLY;
    }

    public void setSUPPLY(Double sUPPLY) {
        this.sUPPLY = sUPPLY;
    }

    public Double getMKTCAP() {
        return mKTCAP;
    }

    public void setMKTCAP(Double mKTCAP) {
        this.mKTCAP = mKTCAP;
    }

    public Double getTOTALVOLUME24H() {
        return tOTALVOLUME24H;
    }

    public void setTOTALVOLUME24H(Double tOTALVOLUME24H) {
        this.tOTALVOLUME24H = tOTALVOLUME24H;
    }

    public Double getTOTALVOLUME24HTO() {
        return tOTALVOLUME24HTO;
    }

    public void setTOTALVOLUME24HTO(Double tOTALVOLUME24HTO) {
        this.tOTALVOLUME24HTO = tOTALVOLUME24HTO;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(tYPE);
        dest.writeValue(mARKET);
        dest.writeValue(fROMSYMBOL);
        dest.writeValue(tOSYMBOL);
        dest.writeValue(fLAGS);
        dest.writeValue(pRICE);
        dest.writeValue(lASTUPDATE);
        dest.writeValue(lASTVOLUME);
        dest.writeValue(lASTVOLUMETO);
        dest.writeValue(lASTTRADEID);
        dest.writeValue(vOLUME24HOUR);
        dest.writeValue(vOLUME24HOURTO);
        dest.writeValue(oPEN24HOUR);
        dest.writeValue(hIGH24HOUR);
        dest.writeValue(lOW24HOUR);
        dest.writeValue(cHANGE24HOUR);
        dest.writeValue(cHANGEPCT24HOUR);
        dest.writeValue(cHANGEDAY);
        dest.writeValue(cHANGEPCTDAY);
        dest.writeValue(sUPPLY);
        dest.writeValue(mKTCAP);
        dest.writeValue(tOTALVOLUME24H);
        dest.writeValue(tOTALVOLUME24HTO);
    }

    public int describeContents() {
        return  0;
    }

}
