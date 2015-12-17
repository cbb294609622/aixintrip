package net.aixin.app.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 *
 * 上传发帖内容的javaBean
 * Created by gzp on 2015/12/9.
 */
public class TechnicalBean extends Entity  implements Parcelable {
    public String title;
    public String connt;
    public String type;
    public String imgFile;

    public TechnicalBean(){}

    public TechnicalBean(Parcel in) {
        title = in.readString();
        connt = in.readString();
        type = in.readString();
        imgFile = in.readString();
    }

    public static final Creator<TechnicalBean> CREATOR = new Creator<TechnicalBean>() {
        @Override
        public TechnicalBean createFromParcel(Parcel in) {
            return new TechnicalBean(in);
        }

        @Override
        public TechnicalBean[] newArray(int size) {
            return new TechnicalBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(connt);
        dest.writeString(type);
        dest.writeString(imgFile);
    }
}
