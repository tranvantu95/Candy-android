package com.candy.android.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.candy.android.configs.Define;
import com.google.gson.annotations.SerializedName;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Created by quannt on 10/14/2016.
 * Description: Base property of performer
 */

public class BasePerformer implements Parcelable {

    public static final String NO_PUBLIC_STRING = "0";
    public static final int NO_PUBLIC_INT = 0;
    @SerializedName("name")
    protected String name;
    @SerializedName("orignalName")
    private String orignalName;
    @SerializedName("character")
    private String character;
    @SerializedName("image")
    protected String image;
    @SerializedName("profileImageUrl")
    private String profileImageUrl;
    @SerializedName("pointPerMinute")
    private String pointPerMinute;
    @SerializedName("public")
    protected String isPublic;
    @SerializedName("status")
    protected String status;
    @SerializedName("statusString")
    private String statusString;
    @SerializedName("count")
    private String count;
    @SerializedName("new")
    private String isNew;
    @SerializedName("age")
    protected String age;
    @SerializedName("birthMonth")
    private String birthMonth;
    @SerializedName("ranking")
    private String ranking;
    @SerializedName("pos")
    private String pos;
    @SerializedName("myType")
    private String myType;
    @SerializedName("area")
    protected String area;
    @SerializedName("smartPhone")
    private String smartPhone;
    @SerializedName("isPublicAge")
    private boolean isPublicAge;
    @SerializedName("chatStatusString")
    private String chatStatusString;
    @SerializedName("profileImage")
    private String profileImage;

    public BasePerformer() {
        name = "";
        orignalName = "";
        character = "";
        image = "";
        profileImageUrl = "";
        pointPerMinute = "";
        isPublic = "1";
        status = "";
        statusString = "";
        count = "";
        isNew = "";
        age = "";
        birthMonth = "";
        ranking = "";
        pos = "";
        myType = "";
        area = "";
        smartPhone = "";
    }

    protected BasePerformer(Parcel in) {
        name = in.readString();
        orignalName = in.readString();
        character = in.readString();
        image = in.readString();
        profileImageUrl = in.readString();
        pointPerMinute = in.readString();
        isPublic = in.readString();
        status = in.readString();
        statusString = in.readString();
        count = in.readString();
        isNew = in.readString();
        age = in.readString();
        birthMonth = in.readString();
        ranking = in.readString();
        pos = in.readString();
        myType = in.readString();
        area = in.readString();
        smartPhone = in.readString();
    }

    public static final Creator<BasePerformer> CREATOR = new Creator<BasePerformer>() {
        @Override
        public BasePerformer createFromParcel(Parcel in) {
            return new BasePerformer(in);
        }

        @Override
        public BasePerformer[] newArray(int size) {
            return new BasePerformer[size];
        }
    };

    public String getName() {
        try {
            String str = URLDecoder.decode(name, "UTF-8");
            if (str != null) {
                return str.replace(Define.ERROR_FORMAT_STRING, Define.REPLACE_STRING);
            } else {
                return "";
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return name;
    }

    public String getOrignalName() {
        try {
            return URLDecoder.decode(orignalName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return orignalName;
    }

    public String getCharacter() {
        try {
            return URLDecoder.decode(character, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return character;
    }

    public void setCharacter(String character) {
        this.character = character;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public int getPointPerMinute() {
        try {
            return Integer.valueOf(pointPerMinute);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public int getIsPublic() {
        try {
            return Integer.valueOf(isPublic);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public void setIsPublic(String isPublic) {
        this.isPublic = isPublic;
    }

    public int getStatus() {
        try {
            if (getSmartPhone() == Define.SMARTPHONE_ON) {
                return 5;
            } else {
                return Integer.valueOf(status);
            }
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusString() {
        if (getSmartPhone() == Define.SMARTPHONE_ON) {
            return Define.STATUS_STRING_OFFLINE;
        }
        return statusString;
    }

    public int getCount() {
        try {
            return Integer.valueOf(count);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public int getIsNew() {
        try {
            return Integer.valueOf(isNew);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public int getAge() {
        try {
            return Integer.valueOf(age);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public int getBirthMonth() {
        try {
            return Integer.valueOf(birthMonth);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public String getRanking() {
        return ranking;
    }

    public int getPos() {
        try {
            return Integer.valueOf(pos);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public String getMyType() {
        try {
            return URLDecoder.decode(myType, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return myType;
    }

    public String getArea() {
        return area;
    }

    public int getSmartPhone() {
        try {
            return Integer.valueOf(smartPhone);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public boolean isPublicAge() {
        return isPublicAge;
    }

    public void setPublicAge(boolean publicAge) {
        isPublicAge = publicAge;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(orignalName);
        parcel.writeString(character);
        parcel.writeString(image);
        parcel.writeString(profileImageUrl);
        parcel.writeString(pointPerMinute);
        parcel.writeString(isPublic);
        parcel.writeString(status);
        parcel.writeString(statusString);
        parcel.writeString(count);
        parcel.writeString(isNew);
        parcel.writeString(age);
        parcel.writeString(birthMonth);
        parcel.writeString(ranking);
        parcel.writeString(pos);
        parcel.writeString(myType);
        parcel.writeString(area);
        parcel.writeString(smartPhone);
    }

    public void setChatStatusString(String chatStatusString) {
        this.chatStatusString = chatStatusString;
    }

    public void setPrimaryName(String name) {
        this.name = name;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    //
    public boolean isNoPublic() {
        return getIsPublic() == BasePerformer.NO_PUBLIC_INT;
    }

    public boolean isLive() {
        if(status == null || status.isEmpty()){
            return false;
        }
        Define.OnlineStatus onlineStatus = Define.OnlineStatus.getStatusFromCode(getStatus());
        return onlineStatus == Define.OnlineStatus.CALL_WAITING || onlineStatus == Define.OnlineStatus.CALLING;
    }

    public boolean canPeep() {
        if(status == null || status.isEmpty()){
            return false;
        }
        Define.OnlineStatus onlineStatus = Define.OnlineStatus.getStatusFromCode(getStatus());
        return onlineStatus == Define.OnlineStatus.CALLING;
    }

    public boolean canShowSlideImage() {
        if(status == null || status.isEmpty()){
            return false;
        }
        Define.OnlineStatus onlineStatus = Define.OnlineStatus.getStatusFromCode(getStatus());
        return onlineStatus == Define.OnlineStatus.CALL_WAITING;
    }

}
