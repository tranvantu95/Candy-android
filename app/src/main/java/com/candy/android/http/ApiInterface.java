package com.candy.android.http;

import com.candy.android.configs.Define;
import com.candy.android.http.response.ApiCampaignResponse;
import com.candy.android.http.response.ApiCompletedMissionResponse;
import com.candy.android.http.response.ApiFavoriteResponse;
import com.candy.android.http.response.ApiFootstepsResponse;
import com.candy.android.http.response.ApiGeneralResponse;
import com.candy.android.http.response.ApiIncConfigResponse;
import com.candy.android.http.response.ApiIncPaymentConfigResponse;
import com.candy.android.http.response.ApiMailResponse;
import com.candy.android.http.response.ApiMemberFavoriteListResponse;
import com.candy.android.http.response.ApiMemberMissionInfoResponse;
import com.candy.android.http.response.ApiMemberResponse;
import com.candy.android.http.response.ApiMemberWithNoticeResponse;
import com.candy.android.http.response.ApiPointAdditionResponse;
import com.candy.android.http.response.ApiRankingResponse;
import com.candy.android.http.response.ApiRewardMissionResponse;
import com.candy.android.http.response.ApiSmsPostResponse;
import com.candy.android.http.response.ApiUncompletedMissionResponse;
import com.candy.android.http.response.ApiWebTokenResponse;
import com.candy.android.http.response.BlogCategoriesResponse;
import com.candy.android.http.response.BlogDetailResponse;
import com.candy.android.http.response.BlogsListResponse;
import com.candy.android.http.response.MemberCameraPointConsumptionResponse;
import com.candy.android.http.response.PerformerAllActiveResponse;
import com.candy.android.http.response.PerformerProfilesResponse;
import com.candy.android.model.member.MemberInfoChange;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by quannt on 10/19/2016.
 * Description: ApiInterface for Retrofit
 */

public interface ApiInterface {

    /**
     * Api for PerformerOnlineList
     */
    @FormUrlEncoded
    @POST(Define.API.API_PERFORMER_ALL_ACTIVE_LIST)
    Call<PerformerAllActiveResponse> getPerformerOnline(
            @Field(Define.Fields.FIELD_ID) String id,
            @Field(Define.Fields.FIELD_PASS) String pass,
            @Field(Define.Fields.FIELD_OS) String os,
            @Field(Define.Fields.FIELD_VERSION) int versionCode
    );

    /**
     * Search for Performer
     */
    @FormUrlEncoded
    @POST(Define.API.API_PERFORMER_ALL_ACTIVE_LIST)
    Call<PerformerAllActiveResponse> searchPerformerOnline(
            @Field(Define.Fields.FIELD_ID) String id,
            @Field(Define.Fields.FIELD_PASS) String pass,
            @Field(Define.Fields.FIELD_NEW) Integer isNew,
            @Field(Define.FIELD_NAME) String name,
            @Field(Define.Fields.FIELD_AREA) String area,
            @Field(Define.Fields.FIELD_AGE10) Integer isAge10,
            @Field(Define.Fields.FIELD_AGE20) Integer isAge20,
            @Field(Define.Fields.FIELD_AGE30) Integer isAge30,
            @Field(Define.Fields.FIELD_PICKUP) Integer pickup,
            @Field(Define.Fields.FIELD_OS) String os,
            @Field(Define.Fields.FIELD_VERSION) int versionCode
    );

    /**
     * Api for Ranking
     */
    @FormUrlEncoded
    @POST(Define.API.API_RANKING)
    Call<ApiRankingResponse> getPerformerRanking(@Field(Define.Fields.FIELD_ID) String id,
                                                 @Field(Define.Fields.FIELD_PASS) String pass,
                                                 @Field(Define.Fields.FIELD_TYPE) String type,
                                                 @Field(Define.Fields.FIELD_DATE) String date);

    @FormUrlEncoded
    @POST(Define.API.API_RANKING)
    Call<ApiRankingResponse> getPerformerRankingWithoutDate(@Field(Define.Fields.FIELD_ID) String id,
                                                 @Field(Define.Fields.FIELD_PASS) String pass,
                                                 @Field(Define.Fields.FIELD_TYPE) String type);

    /**
     * Api for PerformerProfiles
     */
    @FormUrlEncoded
    @POST(Define.API.API_PERFORMER_PROFILES)
    Call<PerformerProfilesResponse> getPerformerProfiles(@Field(Define.Fields.FIELD_ID) String id, @Field(Define.Fields.FIELD_PASS) String pass,
                                                         @Field(Define.Fields.FIELD_PERFORMER_CODE) int performerCode);


    @FormUrlEncoded
    @POST(Define.API.API_MEMBER_INFO_CONFIRM)
    Call<ApiGeneralResponse> submit(@Field(Define.Fields.FIELD_ID) String id,
                                    @Field(Define.Fields.FIELD_PASS) String pass,
                                    @Field(Define.Fields.FIELD_EMAIL) String email,
                                    @Field(Define.Fields.FIELD_NAME) String name,
                                    @Field(Define.Fields.FIELD_BIRTH_YEAR) int birthYear,
                                    @Field(Define.Fields.FIELD_BIRTH_MONTH) int birthMonth,
                                    @Field(Define.Fields.FIELD_BIRTH_DAY) int birthDay);

    /**
     * api to get member's information
     */
    @FormUrlEncoded
    @POST(Define.API.API_MEMBER_INFO)
    Call<ApiMemberResponse> getMemberInfo(@Field(Define.Fields.FIELD_ID) String id,
                                          @Field(Define.Fields.FIELD_PASS) String pass);

    /**
     * api to get member's information
     */
    @FormUrlEncoded
    @POST(Define.API.API_MEMBER_FOOTSTEPS)
    Call<ApiFootstepsResponse> getMemberFootsteps(@Field(Define.Fields.FIELD_ID) String id,
                                                  @Field(Define.Fields.FIELD_PASS) String pass);

    @FormUrlEncoded
    @POST(Define.API.API_MEMBER_REGISTER)
    Call<ApiMemberResponse> register(@Field(Define.Fields.FIELD_ID) String id,
                                     @Field(Define.Fields.FIELD_PASS) String pass,
                                     @Field(Define.Fields.FIELD_EMAIL) String email,
                                     @Field(Define.Fields.FIELD_NAME) String name,
                                     @Field(Define.Fields.FIELD_BIRTH_YEAR) int birthYear,
                                     @Field(Define.Fields.FIELD_BIRTH_MONTH) int birthMonth,
                                     @Field(Define.Fields.FIELD_BIRTH_DAY) int birthDay,
                                     @Field(Define.Fields.FIELD_BANNER_CODE) String bannerCode,
                                     @Field(Define.Fields.FIELD_OPTION15) String option15,
                                     @Field(Define.Fields.FIELD_OPTION28) String option28);

    @FormUrlEncoded
    @POST(Define.API.API_MEMBER_SEND_PASSWORD)
    Call<ApiGeneralResponse> reIssuePassword(@Field(Define.Fields.FIELD_EMAIL) String email);

    @FormUrlEncoded
    @POST(Define.API.API_FAVORITE_ADD)
    Call<ApiFavoriteResponse> favoriteAdd(@Field(Define.Fields.FIELD_ID) String id,
                                          @Field(Define.Fields.FIELD_PASS) String pass,
                                          @Field(Define.Fields.FIELD_PERFORMER_CODE) int performerCode);

    @FormUrlEncoded
    @POST(Define.API.API_FAVORITE_DELETE)
    Call<ApiFavoriteResponse> favoriteDelete(@Field(Define.Fields.FIELD_ID) String id,
                                             @Field(Define.Fields.FIELD_PASS) String pass,
                                             @Field(Define.Fields.FIELD_PERFORMER_CODE) int performerCode);

    @FormUrlEncoded
    @POST(Define.API.API_UNFAVORITE_DELETE)
    Call<ApiFavoriteResponse> unfavoriteDelete(@Field(Define.Fields.FIELD_ID) String id,
                                               @Field(Define.Fields.FIELD_PASS) String pass,
                                               @Field(Define.Fields.FIELD_PERFORMER_CODE) int performerCode);

    @FormUrlEncoded
    @POST(Define.API.API_UNFAVORITE_ADD)
    Call<ApiFavoriteResponse> blockPerformer(@Field(Define.Fields.FIELD_ID) String id,
                                             @Field(Define.Fields.FIELD_PASS) String pass,
                                             @Field(Define.Fields.FIELD_PERFORMER_CODE) String performerCode);

    @FormUrlEncoded
    @POST(Define.API.API_GET_FAVORITE_PERFORMERS_LIST)
    Call<ApiMemberFavoriteListResponse> getFavoritePerformersList(@Field(Define.Fields.FIELD_ID) String id,
                                                                  @Field(Define.Fields.FIELD_PASS) String pass);

    @FormUrlEncoded
    @POST(Define.API.API_REPORT_PERFORMER)
    Call<ApiFavoriteResponse> reportPerformer(@Field(Define.Fields.FIELD_ID) String id,
                                              @Field(Define.Fields.FIELD_PASS) String pass,
                                              @Field(Define.Fields.FIELD_PERFORMER_CODE) String performerCode,
                                              @Field(Define.Fields.FIELD_SUBJECT) String subject, @Field(Define.Fields.FIELD_MESSAGE) String message);

    @FormUrlEncoded
    @POST(Define.API.API_MEMBER_CHANGE_INFO)
    Call<MemberInfoChange> changeMemberInfo(@Field(Define.Fields.FIELD_ID) String id,
                                            @Field(Define.Fields.FIELD_PASS) String pass,
                                            @Field(Define.Fields.FIELD_CHANGE_PASS) String newPass,
//                                             @Field(Define.Fields.FIELD_CODE) String code,
                                            @Field(Define.Fields.FIELD_NAME) String name,
                                            @Field(Define.Fields.FIELD_BLOOD) String blood,
                                            @Field(Define.Fields.FIELD_AREA) String area,
                                            @Field(Define.Fields.FIELD_EMAIL) String email,
                                            @Field(Define.Fields.FIELD_MESSAGE) String message,
//                                             @Field(Define.Fields.FIELD_LOGIN_MAIL_SUBMIT) String loginMailSubmit,
//                                             @Field(Define.Fields.FIELD_MAIL_MAGA_SUBMIT) String mailMagaSubmit,
                                            @Field(Define.Fields.FIELD_BIRTH_YEAR) int birthYear,
                                            @Field(Define.Fields.FIELD_BIRTH_MONTH) int birthMonth,
                                            @Field(Define.Fields.FIELD_BIRTH_DAY) int birthDay);

    @FormUrlEncoded
    @POST(Define.API.API_MEMBER_CHANGE_INFO)
    Call<MemberInfoChange> changeMemberInfo(@Field(Define.Fields.FIELD_ID) String id,
                                            @Field(Define.Fields.FIELD_PASS) String pass,
                                            @Field(Define.Fields.FIELD_CHANGE_PASS) String newPass,
//                                             @Field(Define.Fields.FIELD_CODE) String code,
                                            @Field(Define.Fields.FIELD_NAME) String name,
                                            @Field(Define.Fields.FIELD_BLOOD) String blood,
                                            @Field(Define.Fields.FIELD_AREA) String area,
                                            @Field(Define.Fields.FIELD_EMAIL) String email,
                                            @Field(Define.Fields.FIELD_MESSAGE) String message,
//                                             @Field(Define.Fields.FIELD_LOGIN_MAIL_SUBMIT) String loginMailSubmit,
//                                             @Field(Define.Fields.FIELD_MAIL_MAGA_SUBMIT) String mailMagaSubmit,
                                            @Field(Define.Fields.FIELD_BIRTH_YEAR) int birthYear,
                                            @Field(Define.Fields.FIELD_BIRTH_MONTH) int birthMonth,
                                            @Field(Define.Fields.FIELD_BIRTH_DAY) int birthDay,
                                            @Field("job") String job,
                                            @Field("typeWoman") String typeWoman,
                                            @Field("pet") String pet,
                                            @Field("geek") String geek,
                                            @Field("onlyOneWish") String onlyOneWish,
                                            @Field("engrossed") String engrossed);


    @FormUrlEncoded
    @POST(Define.API.API_MEMBER_CHANGE_INFO)
    Call<MemberInfoChange> changeMemberInfo(@Field(Define.Fields.FIELD_ID) String id,
                                            @Field(Define.Fields.FIELD_PASS) String pass,
                                            @Field(Define.Fields.FIELD_NAME) String name,
                                            @Field(Define.Fields.FIELD_AREA) int area,
                                            @Field(Define.Fields.FIELD_BIRTH_YEAR) int birthYear,
                                            @Field(Define.Fields.FIELD_BIRTH_MONTH) int birthMonth,
                                            @Field(Define.Fields.FIELD_BIRTH_DAY) int birthDay);

    /**
     * interface api to call to get member information change
     */
    @FormUrlEncoded
    @POST(Define.API_MEMBER_INFO_CHANGE)
    Call<MemberInfoChange> getMemberInfoChange(@Field(Define.FIELD_ID) String id,
                                               @Field(Define.FIELD_PASS) String pass);

    @FormUrlEncoded
    @POST(Define.API.API_MEMBER_MAIL_LIST)
    Call<ApiMailResponse> getMemberMailList(@Field(Define.FIELD_ID) String id,
                                            @Field(Define.FIELD_PASS) String pass,
                                            @Field(Define.Fields.FIELD_LIMIT) Integer limit,
                                            @Field(Define.Fields.FIELD_PAGE) Integer pageNumber,
                                            @Field(Define.Fields.FIELD_SORT) String sort,
                                            @Field(Define.Fields.FIELD_ORDER) String order);

    @FormUrlEncoded
    @POST(Define.API.API_MEMBER_MAIL_LINE)
    Call<ApiMailResponse> getMemberMailLine(@Field(Define.FIELD_ID) String id,
                                            @Field(Define.FIELD_PASS) String pass,
                                            @Field(Define.Fields.FIELD_LIMIT) int limit,
                                            @Field(Define.Fields.FIELD_PERFORMER_CODE) int performerCode,
                                            @Field(Define.Fields.FIELD_ORDER) String order);

    @FormUrlEncoded
    @POST(Define.API.API_MEMBER_MAIL_DELETE)
    Call<ApiMailResponse> incMemberMailDelete(@Field(Define.Fields.FIELD_ID) String id,
                                              @Field(Define.Fields.FIELD_PASS) String pass,
                                              @Field(Define.Fields.FIELD_MAIL_CODE) int mailCode);

    @FormUrlEncoded
    @POST(Define.API.API_MEMBER_INFO_CONFIRM)
    Call<ApiGeneralResponse> confirmCanChangeEmailPassword(@Field(Define.Fields.FIELD_EMAIL) String id,
                                                           @Field(Define.Fields.FIELD_PASS) String pass);

    //Api blog list
    @FormUrlEncoded
    @POST(Define.API.API_BLOG_LIST)
    Call<BlogsListResponse> getBlogList(@Field(Define.Fields.FIELD_ID) String id, @Field(Define.FIELD_PASS) String pass,
                                        @Field(Define.Fields.FIELD_PAGE) int page, @Field(Define.Fields.FIELD_LIMIT) int limit,
                                        @Field(Define.Fields.FIELD_CATEGORY) String category, @Field(Define.Fields.FIELD_OS) String os,
                                        @Field(Define.Fields.FIELD_VERSION) String version);

    //Api blog list
    @FormUrlEncoded
    @POST(Define.API.API_BLOG_LIST)
    Call<BlogsListResponse> getBlogListOfAPerformer(@Field(Define.Fields.FIELD_ID) String id, @Field(Define.FIELD_PASS) String pass,
                                                    @Field(Define.Fields.FIELD_PAGE) int page, @Field(Define.Fields.FIELD_LIMIT) int limit,
                                                    @Field(Define.Fields.FIELD_PERFORMER_CODE) int performerCode);

    //Api blog detail
    @FormUrlEncoded
    @POST(Define.API.API_BLOG_LIST)
    Call<BlogDetailResponse> getBlogDetail(@Field(Define.Fields.FIELD_ID) String id, @Field(Define.FIELD_PASS) String pass,
                                           @Field(Define.Fields.FIELD_POST_ID) int postId);

    //Api like a blog
    @FormUrlEncoded
    @POST(Define.API.API_LIKE_BLOG)
    Call<ApiFavoriteResponse> likeBlog(@Field(Define.Fields.FIELD_ID) String id, @Field(Define.FIELD_PASS) String pass,
                                       @Field(Define.Fields.FIELD_POST_ID) int postId);

    /**
     * register PN setting
     *
     * @param token    fire-base token
     * @param terminal 0 = android, 1 - ios
     */
    @FormUrlEncoded
    @POST(Define.API.API_PUSH_NOTIFICATION_REGIST)
    Call<ApiMemberWithNoticeResponse> registerPNSetting(@Field(Define.Fields.FIELD_ID) String id,
                                                        @Field(Define.FIELD_PASS) String pass,
                                                        @Field(Define.Fields.FIELD_TOKEN) String token,
                                                        @Field(Define.Fields.FIELD_TERMINAL) int terminal);

    /**
     * retrieve PN setting
     */
    @FormUrlEncoded
    @POST(Define.API.API_PUSH_NOTIFICATION_GET)
    Call<ApiMemberResponse> getPNSetting(@Field(Define.Fields.FIELD_ID) String id,
                                         @Field(Define.FIELD_PASS) String pass);

    @FormUrlEncoded
    @POST(Define.API.API_PUSH_NOTIFICATION_CHANGE_FOR_PUSH)
    Call<ApiMemberResponse> changePNSettingForPush(@Field(Define.Fields.FIELD_ID) String id,
                                                   @Field(Define.Fields.FIELD_PASS) String pass,
                                                   @Field(Define.Fields.FIELD_PN_MAIL) int pushMail,
                                                   @Field(Define.Fields.FIELD_PN_LOGIN) int pushLogin,
                                                   @Field(Define.Fields.FIELD_PN_BLOG) int pushBlog,
                                                   @Field(Define.Fields.FIELD_PN_MAGA) int pushMaga,
                                                   @Field(Define.Fields.FIELD_PN_REJECT_FROM) int reject_fr,
                                                   @Field(Define.Fields.FIELD_PN_REJECT_TO) int reject_to);

    //Api sms post
    @FormUrlEncoded
    @POST(Define.API.API_SMS_POST)
    Call<ApiSmsPostResponse> postTelnoSms(@Field(Define.Fields.FIELD_ID) String id, @Field(Define.FIELD_PASS) String pass,
                                          @Field(Define.Fields.FIELD_TELNO) String telno);

    //Api sms check
    @FormUrlEncoded
    @POST(Define.API.API_SMS_CHECK)
    Call<ApiSmsPostResponse> postTelnoSms(@Field(Define.Fields.FIELD_ID) String id, @Field(Define.Fields.FIELD_PASS) String pass,
                                          @Field(Define.Fields.FIELD_TELNO) String telno, @Field(Define.Fields.FIELD_TOKEN) int token);

    @GET(Define.API.API_INC_CONFIG)
    Call<ApiIncConfigResponse> getIncConfig();

    /**  */
    @GET(Define.API.API_INC_PAYMENT_CONFIG)
    Call<ApiIncPaymentConfigResponse> getIncPaymentConfig();

    //Api get web token
    @FormUrlEncoded
    @POST(Define.API.API_WEB_TOKEN)
    Call<ApiWebTokenResponse> getWebToken(@Field(Define.Fields.FIELD_ID) String id, @Field(Define.Fields.FIELD_PASS) String pass);

    @FormUrlEncoded
    @POST(Define.API.API_MISSION_UNCOMPLETED_LIST)
    Call<ApiUncompletedMissionResponse> getListUncompletedMission(@Field(Define.Fields.FIELD_MISSION_PAGE) int page, @Field(Define.Fields.FIELD_ID) String id, @Field(Define.Fields.FIELD_PASS) String pass);

    @FormUrlEncoded
    @POST(Define.API.API_MISSION_COMPLETED_LIST)
    Call<ApiCompletedMissionResponse> getListCompletedMission(@Field(Define.Fields.FIELD_MISSION_PAGE) int page, @Field(Define.Fields.FIELD_ID) String id, @Field(Define.Fields.FIELD_PASS) String pass);

    @FormUrlEncoded
    @POST(Define.API.API_MISSION_ADD_POINT)
    Call<ApiRewardMissionResponse> getRewardPointMission(@Field(Define.Fields.FIELD_MISSION_ID) int missionID, @Field(Define.Fields.FIELD_ID) String id, @Field(Define.Fields.FIELD_PASS) String pass);

    @FormUrlEncoded
    @POST(Define.API.API_MEMBER_MISSION_INFO)
    Call<ApiMemberMissionInfoResponse> getMemberMissionInfo(@Field(Define.Fields.FIELD_ID) String id, @Field(Define.Fields.FIELD_PASS) String pass);

    @FormUrlEncoded
    @POST(Define.API.API_POINT_ADDITION)
    Call<ApiPointAdditionResponse> getPointAddition(@Field(Define.Fields.FIELD_ID) String id, @Field(Define.Fields.FIELD_PASS) String pass, @Field(Define.Fields.FIELD_DEVICE) String device);

    @FormUrlEncoded
    @POST(Define.API.API_MEMBER_CAMERA_POINT_CONSUMPTION)
    Call<MemberCameraPointConsumptionResponse> sendMemberCameraPointConsumption(
            @Field(Define.Fields.FIELD_ID) String id,
            @Field(Define.Fields.FIELD_PASS) String pass,
            @Field(Define.Fields.FIELD_PERFORMER_CODE) String performerCode
    );

    @GET(Define.API.API_BLOG_CATEGORIES)
    Call<BlogCategoriesResponse> getBlogCategories();

}
