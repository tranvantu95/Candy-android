package com.candy.android.model.member;

import android.os.Parcel;
import android.os.Parcelable;

import com.candy.android.configs.Define;
import com.candy.android.http.response.ApiGeneralResponse;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Quynv
 * Created on 18/10/2016.
 */

public class MemberInfoChange extends ApiGeneralResponse implements Parcelable {
    @SerializedName(Define.FIELD_CHANGE_INFO)
    private ApiGeneralResponse mChangeInfo;
    @SerializedName(Define.FIELD_CHANGE_PASS)
    private ApiGeneralResponse mChangePass;

    @SerializedName(Define.FIELD_MEMBER)
    private Member mMember;

    protected MemberInfoChange(Parcel in) {
        mMember = in.readParcelable(Member.class.getClassLoader());
        mStatus = in.readInt();
        mResponse = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(mMember, flags);
        dest.writeInt(mStatus);
        dest.writeString(mResponse);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MemberInfoChange> CREATOR = new Creator<MemberInfoChange>() {
        @Override
        public MemberInfoChange createFromParcel(Parcel in) {
            return new MemberInfoChange(in);
        }

        @Override
        public MemberInfoChange[] newArray(int size) {
            return new MemberInfoChange[size];
        }
    };

    public ApiGeneralResponse getChangeInfo() {
        return mChangeInfo;
    }

    public ApiGeneralResponse getChangePass() {
        return mChangePass;
    }

    public int getStatus() {
        return mStatus;
    }

    public String getResponse() {
        return mResponse;
    }

    public Member getMember() {
        return mMember;
    }

    public void setmMember(Member member) {
        mMember = member;
    }

    public static class Member implements Parcelable {

        @SerializedName("code")
        @Expose
        private String code;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("mail")
        @Expose
        private String mail;
        @SerializedName("point")
        @Expose
        private String point;
        @SerializedName("freeMail")
        @Expose
        private String freeMail;
        @SerializedName("notOpenCount")
        @Expose
        private String notOpenCount;
        @SerializedName("joinDate")
        @Expose
        private String joinDate;
        @SerializedName("option5")
        @Expose
        private String option5;
        @SerializedName("birthAge")
        @Expose
        private String birthAge;
        @SerializedName("age")
        @Expose
        private String age;
        @SerializedName("blood")
        @Expose
        private String blood;
        @SerializedName("area")
        @Expose
        private String area;
        @SerializedName("birth")
        @Expose
        private String birth;
        @SerializedName("message")
        @Expose
        private String message;
        @SerializedName("favorite")
        @Expose
        private List<Object> favorite = new ArrayList<>();

        @SerializedName("job")
        @Expose
        private String job;
        @SerializedName("typeWoman")
        @Expose
        private String typeWoman;
        @SerializedName("pet")
        @Expose
        private String pet;
        @SerializedName("geek")
        @Expose
        private String geek;
        @SerializedName("onlyOneWish")
        @Expose
        private String onlyOneWish;
        @SerializedName("engrossed")
        @Expose
        private String engrossed;
        @SerializedName("isRegistration3DaysWithin")
        private boolean mIs3Day;

        protected Member(Parcel in) {
            code = in.readString();
            name = in.readString();
            mail = in.readString();
            point = in.readString();
            freeMail = in.readString();
            notOpenCount = in.readString();
            joinDate = in.readString();
            option5 = in.readString();
            birthAge = in.readString();
            age = in.readString();
            blood = in.readString();
            area = in.readString();
            birth = in.readString();
            message = in.readString();

            job = in.readString();
            typeWoman = in.readString();
            pet = in.readString();
            geek = in.readString();
            onlyOneWish = in.readString();
            engrossed = in.readString();
            engrossed = in.readString();
            mIs3Day = Boolean.parseBoolean(in.readString());
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(code);
            dest.writeString(name);
            dest.writeString(mail);
            dest.writeString(point);
            dest.writeString(freeMail);
            dest.writeString(notOpenCount);
            dest.writeString(joinDate);
            dest.writeString(option5);
            dest.writeString(birthAge);
            dest.writeString(age);
            dest.writeString(blood);
            dest.writeString(area);
            dest.writeString(birth);
            dest.writeString(message);

            dest.writeString(job);
            dest.writeString(typeWoman);
            dest.writeString(pet);
            dest.writeString(geek);
            dest.writeString(onlyOneWish);
            dest.writeString(engrossed);
            dest.writeString(String.valueOf(mIs3Day));

        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<Member> CREATOR = new Creator<Member>() {
            @Override
            public Member createFromParcel(Parcel in) {
                return new Member(in);
            }

            @Override
            public Member[] newArray(int size) {
                return new Member[size];
            }
        };

        /**
         * @return The code
         */
        public String getCode() {
            return code;
        }

        /**
         * @param code The code
         */
        public void setCode(String code) {
            this.code = code;
        }

        /**
         * @return The name
         */
        public String getName() {
            return name;
        }

        /**
         * @param name The name
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * @return The mail
         */
        public String getMail() {
            return mail;
        }

        /**
         * @param mail The mail
         */
        public void setMail(String mail) {
            this.mail = mail;
        }

        /**
         * @return The point
         */
        public String getPoint() {
            return point;
        }

        /**
         * @param point The point
         */
        public void setPoint(String point) {
            this.point = point;
        }

        /**
         * @return The freeMail
         */
        public String getFreeMail() {
            return freeMail;
        }

        /**
         * @param freeMail The freeMail
         */
        public void setFreeMail(String freeMail) {
            this.freeMail = freeMail;
        }

        /**
         * @return The notOpenCount
         */
        public String getNotOpenCount() {
            return notOpenCount;
        }

        /**
         * @param notOpenCount The notOpenCount
         */
        public void setNotOpenCount(String notOpenCount) {
            this.notOpenCount = notOpenCount;
        }

        /**
         * @return The joinDate
         */
        public String getJoinDate() {
            return joinDate;
        }

        /**
         * @param joinDate The joinDate
         */
        public void setJoinDate(String joinDate) {
            this.joinDate = joinDate;
        }

        /**
         * @return The option5
         */
        public String getOption5() {
            return option5;
        }

        /**
         * @param option5 The option5
         */
        public void setOption5(String option5) {
            this.option5 = option5;
        }

        /**
         * @return The birthAge
         */
        public String getBirthAge() {
            return birthAge;
        }

        /**
         * @param birthAge The birthAge
         */
        public void setBirthAge(String birthAge) {
            this.birthAge = birthAge;
        }

        /**
         * @return The age
         */
        public String getAge() {
            return age;
        }

        /**
         * @param age The age
         */
        public void setAge(String age) {
            this.age = age;
        }

        /**
         * @return The blood
         */
        public String getBlood() {
            return blood;
        }

        /**
         * @param blood The blood
         */
        public void setBlood(String blood) {
            this.blood = blood;
        }

        /**
         * @return The area
         */
        public String getArea() {
            return area;
        }

        /**
         * @param area The area
         */
        public void setArea(String area) {
            this.area = area;
        }

        /**
         * @return The birth
         */
        public String getBirth() {
            return birth;
        }

        /**
         * @param birth The birth
         */
        public void setBirth(String birth) {
            this.birth = birth;
        }

        /**
         * @return The message
         */
        public String getMessage() {
            try {
                return URLDecoder.decode(message, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return message;
        }

        /**
         * @param message The message
         */
        public void setMessage(String message) {
            this.message = message;
        }

        public String getJob() {
            try {
                return URLDecoder.decode(job, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return job;
        }

        public void setJob(String job) {
            this.job = job;
        }

        public String getTypeWoman() {

            try {
                return URLDecoder.decode(typeWoman, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return typeWoman;
        }

        public void setTypeWoman(String typeWoman) {
            this.typeWoman = typeWoman;
        }

        public String getPet() {
            try {
                return URLDecoder.decode(pet, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return pet;
        }

        public void setPet(String pet) {
            this.pet = pet;
        }

        public String getGeek() {
            try {
                return URLDecoder.decode(geek, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return geek;
        }

        public void setGeek(String geek) {
            this.geek = geek;
        }

        public String getOnlyOneWish() {
            try {
                return URLDecoder.decode(onlyOneWish, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return onlyOneWish;
        }

        public void setOnlyOneWish(String onlyOneWish) {
            this.onlyOneWish = onlyOneWish;
        }

        public String getEngrossed() {
            try {
                return URLDecoder.decode(engrossed, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return engrossed;
        }

        public void setEngrossed(String engrossed) {
            this.engrossed = engrossed;
        }

        public boolean ismIs3Day() {
            return mIs3Day;
        }

        public void setmIs3Day(boolean mIs3Day) {
            this.mIs3Day = mIs3Day;
        }

        /**
         * @return The favorite
         */
        public List<Object> getFavorite() {
            return favorite;
        }

        /**
         * @param favorite The favorite
         */
        public void setFavorite(List<Object> favorite) {
            this.favorite = favorite;
        }

        @Override
        public String toString() {
            return "Member{" +
                    "message='" + message + '\'' +
                    ", code='" + code + '\'' +
                    ", name='" + name + '\'' +
                    ", mail='" + mail + '\'' +
                    ", point='" + point + '\'' +
                    ", freeMail='" + freeMail + '\'' +
                    ", notOpenCount='" + notOpenCount + '\'' +
                    ", joinDate='" + joinDate + '\'' +
                    ", option5='" + option5 + '\'' +
                    ", birthAge='" + birthAge + '\'' +
                    ", age='" + age + '\'' +
                    ", blood='" + blood + '\'' +
                    ", area='" + area + '\'' +
                    ", birth='" + birth + '\'' +
                    '}';
        }
    }
}
