package com.candy.android.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.candy.android.configs.Define;
import com.google.gson.annotations.SerializedName;

/**
 * Purchase Point Option
 * Created by namhv on 03/12/2016.
 */
public class PurchasePointOption implements Parcelable {

    public enum Type {
        FREE(0),
        PURCHASE(1);

        Type(int i) {
        }
    }

    private Type mType = Type.PURCHASE;

    private String mName;
    private String mPosFix;

    @SerializedName(Define.Fields.FIELD_ITEM_ID)
    private String mProductId;

    @SerializedName(Define.Fields.FIELD_POINT)
    private int mPoint;

    @SerializedName(Define.Fields.FIELD_YEN)
    private int mPrice;

    @SerializedName(Define.Fields.FIELD_MINUTE)
    private int mMinute;

    public PurchasePointOption(Type type, String name, int point, int price) {
        mType = type;
        mName = name;
        mPoint = point;
        mPrice = price;
    }

    public Type getType() {
        return mType;
    }

    public String getName() {
        return mName;
    }

    public String getPosFix() {
        return mPosFix;
    }

    public int getPoint() {
        return mPoint;
    }

    public int getPrice() {
        return mPrice;
    }

    public int getMinute() {
        return mMinute;
    }

    public String getProductId() {
        return mProductId;
    }

    public void setName(String name) {
        mName = name;
    }

    public void setType(Type type) {
        mType = type;
    }

    public void setPosFix(String posFix) {
        mPosFix = posFix;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mType == null ? -1 : this.mType.ordinal());
        dest.writeString(this.mName);
        dest.writeInt(this.mPoint);
        dest.writeInt(this.mPrice);
    }

    protected PurchasePointOption(Parcel in) {
        int tmpMType = in.readInt();
        this.mType = tmpMType == -1 ? null : Type.values()[tmpMType];
        this.mName = in.readString();
        this.mPoint = in.readInt();
        this.mPrice = in.readInt();
    }

    public static final Parcelable.Creator<PurchasePointOption> CREATOR = new Parcelable.Creator<PurchasePointOption>() {
        @Override
        public PurchasePointOption createFromParcel(Parcel source) {
            return new PurchasePointOption(source);
        }

        @Override
        public PurchasePointOption[] newArray(int size) {
            return new PurchasePointOption[size];
        }
    };
}
