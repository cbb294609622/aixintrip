package net.aixin.app.bean;

/**
 * Created by BoBo on 2015/12/8.
 */
public class DetailBean {
    private String banner;
    private String type;
    private String inPrice;
    private String mKPrice;
    private String prdNo;
    private String rating;
    private String descPlace;
    private String fromPlace;


    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getInPrice() {
        return inPrice;
    }

    public void setInPrice(String inPrice) {
        this.inPrice = inPrice;
    }

    public String getmKPrice() {
        return mKPrice;
    }

    public void setmKPrice(String mKPrice) {
        this.mKPrice = mKPrice;
    }

    public String getPrdNo() {
        return prdNo;
    }

    public void setPrdNo(String prdNo) {
        this.prdNo = prdNo;
    }

    public String getDescPlace() {
        return descPlace;
    }

    public void setDescPlace(String descPlace) {
        this.descPlace = descPlace;
    }

    public String getFromPlace() {
        return fromPlace;
    }

    public void setFromPlace(String fromPlace) {
        this.fromPlace = fromPlace;
    }


    @Override
    public String toString() {
        return "DetailBean{" +
                ", banner='" + banner + '\'' +
                ", type='" + type + '\'' +
                ", inPrice='" + inPrice + '\'' +
                ", mKPrice='" + mKPrice + '\'' +
                ", prdNo='" + prdNo + '\'' +
                ", rating='" + rating + '\'' +
                ", descPlace='" + descPlace + '\'' +
                ", fromPlace='" + fromPlace + '\'' +
                '}';
    }


    public void setRating(String rating) {
        this.rating = rating;
    }
    public String getRating(){
        return rating;
    }
}
