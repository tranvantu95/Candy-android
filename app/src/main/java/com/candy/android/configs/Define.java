package com.candy.android.configs;

import com.candy.android.BuildConfig;
import com.candy.android.R;
import com.candy.android.fragment.BaseFragment;

/**
 * Created by quannt on 10/12/2016.
 * <p>
 * Define constants and environment variables
 */

public class Define {
    public static final int SYSTEM_PERFORMER_CODE = 0;
    public static final String RAND_EMAIL_POSIX = "@candy-app.com";
    public static final String RAND_NAME_PREFIX = "未設定candy";
    public static final int RAND_ID_COUNT = 16;
    public static final int RAND_PASS_COUNT = 8;
    public static final int RAND_NAME_COUNT = 8;
    public static final int RAND_EMAIL_COUNT = 8;
    public static final int FIX_BIRTHDAY = 1;
    public static final int FIX_BIRTH_MONTH = 1;
    public static final int FIX_BIRTH_YEAR = 1996;
    public static final int REQUEST_OK = 1;
    public static final int REQUEST_FAILED = -1;
    public static final int DURATION_FAVORITE = 70;
    public static final int DURATION_DISCOUNT = 70;
    public static final int LIKED = 1;
    public static final int PERFORMER_NEW = 1;
    public static final int HAVE_BLOG_CAMPAIGN = 1;
    public static final int CAMPAIGN_IN_PROGRESS = 1;
    public static final int VIDEO_MODE_VALUE_DIRECT = 0;
    public static final int SUBMIT_OK = 1;
    public static final String ERROR_FORMAT_STRING = "&amp;";
    public static final String REPLACE_STRING = "&";
    public static final int TEL_AUTHEN_OK = 1;
    public static final String WEBVIEW_URL = "WEBVIEW_URL";
    public static final String PURCHASE_CURRENCY = "JPY";

    // Point Cost
    public static final int PEEP_VIDEO_POINT_PER_MINUTE = 150;

    public static final String CONST_UTF_8 = "utf-8";

    public static final String[] BLOOD_GROUP = {"ひみつ", "A型", "B型", "O型", "AB型"};
    public static final String[] MEMBER_AREA = {"ひみつ", "北海道", "東北地方", "関東地方",
            "甲信越地方", "東海地方", "北陸地方", "近畿地方", "中国地方",
            "四国地方", "九州地方", "沖縄", "海外"};

    public static final String[] PERFORMER_AREA = {"指定なし", "ひみつ", "北海道", "東北", "中部", "関東", "近畿",
            "中国", "四国", "九州", "沖縄", "海外", "甲信越", "北陸", "東海", "青森",
            "岩手", "宮城", "秋田", "山形", "福島", "茨城", "栃木", "群馬", "埼玉", "千葉",
            "東京", "神奈川", "新潟", "富山", "石川", "福井", "山梨", "長野", "岐阜", "静岡",
            "愛知", "三重", "滋賀", "京都", "大阪", "兵庫", "奈良", "和歌山", "鳥取", "島根",
            "岡山", "広島", "山口", "徳島", "香川", "愛媛", "高知", "福岡", "佐賀", "長崎", "熊本", "大分", "宮崎", "鹿児島"};

    public static final String[] NOTIFICATION_TIME = {"00:00", "01:00", "02:00", "03:00", "04:00",
            "05:00", "06:00", "07:00", "08:00", "09:00", "10:00", "11:00", "12:00", "13:00",
            "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00", "23:00"};

//    public static final String[] BLOG_CATEGORIES = {"New", "Well", "Face", "DS", "Follow", "Nice",
//            "Movie","Swimwear","Yukata","Halloween","Xmas","Valentine","Pet","Pajamas","FreeStyle",
//            "Academy", "Monomane"};
//    public static final String[] BLOG_CATEGORIES_TITLE = {"新着", "人気", "顔出し", "DSチャット", "お気に入り", "マイいいね",
//            "動画","水着","浴衣","ハロウィン","クリスマス","バレンタイン","ペット","パジャマ","フリースタイル",
//            "学園コスプレ", "ものまね"};

    // knv added
    public static final String[] PRODUCTS_FOR_MESSAGING = {
            "candy140jpy"
    };
    public static final String[] PRODUCTS_FOR_MESSAGING_IMAGE_ATTACH = {
            "candy540jpy"
    };

    public static final String[] PRODUCTS_FOR_VIDEO_CHAT = {
            "candy1080jpy", "candy3240jpy", "candy5400jpy"
    };

    public static final String[] BLOG_CATEGORIES = {"New", "Well", "Face", "Follow", "Nice", "Movie", "Swimwear", "Yukata", "Halloween", "Xmas", "Valentine", "Pet", "Pajamas", "Freestyle", "Academy", "Monomane"};
    public static final String[] BLOG_CATEGORIES_TITLE = {"新着", "人気", "顔出し", "お気に入り", "マイいいね", "動画", "水着", "浴衣", "ハロウィン", "クリスマス", "バレンタイン", "ペット", "パジャマ", "フリースタイル", "学園コスプレ", "ものまね"};
    public static final String TAG_EXCEPTION = "IDK-Exception";
    public static final String IMAGE_TEMP_PREFIX = "image_message";
    public static final java.lang.String IMAGE_JPG_POSTFIX = ".jpg";
    public static final String FULL_WIDTH_SPACE = "　";  // u+3000;
    public static final String CONST_NOT_POINT = "notpoint";
    public static final String CONST_OPEN = "open";
    public static final String CONST_SP = "SP";
    public static final String CONST_CANDY_ANDROID = "CandyAndroid";
    public static final int SMALL_AVATAR_SIZE = 200;

    public static String AVATARS_ASSETS_FOLDER = "avatars";

    public static final class ParamKeys {
        public static final String MEMBER_INFO_CHANGE = "member_info_change";
    }

    /**
     * API response code
     */
    public static class SubmitCode {
        public static final String OK = "OK";
        public static final String NG = "NG";
        public static final String INVALID = "INVALID";
    }


    /**
     * API path
     */
    public static class API {
        public static final String API_INC_CONFIG = "incConfigCandy.json";
        public static final String API_INC_PAYMENT_CONFIG = "incPaymentConfigCandy.json";
        public static final String API_MEMBER_REGISTER = "incMemberRegist.json";
        public static final String API_MEMBER_INFO_CONFIRM = "incMemberInfoConfirm.json";
        public static final String API_FAVORITE_DELETE = "incMemberFavoriteDel.json";
        public static final String API_UNFAVORITE_DELETE = "incMemberUnFavoriteDel.json";
        public static final String API_UNFAVORITE_ADD = "incMemberUnFavoriteAdd.json";
        public static final String API_REPORT_PERFORMER = "incMemberReportPerformer.json";
        public static final String API_MEMBER_SEND_PASSWORD = "incMemberSendPassword.json";

        public static final String API_MEMBER_MAIL_LIST = "incMemberMailList.json";
        public static final String API_MEMBER_MAIL_LINE = "incMemberMailLine.json";
        public static final String API_MEMBER_MAIL_DELETE = "incMemberMailDelete.json";
        public static final String API_MEMBER_MAIL_PAY_POST = "incMemberMailPostCandy.json";
        public static final String API_MEMBER_MAIL_PAY_POST_PICTURE = "incSendMailpictureSubPointCandy.json";

        //        API Change email
        public static final String API_CHANGE_MAIL_ONLY = "nativeAppUpdateMemberMailAddress.json";

        public static final String API_MEMBER_INFO = "incMemberInfo.json";
        public static final String API_PERFORMER_ALL_ACTIVE_LIST = "incPerformerAllActiveList.json";
        public static final String API_PERFORMER_PROFILES = "incPerformerProfiles.json";

        public static final String API_MEMBER_FOOTSTEPS = "incMemberGetFootstepsPerformers.json";

        //API RANKING
        public static final String API_RANKING = "incPerformerRanking.json";

        //        public static final String API_PERFORMER_ALL_ACTIVE_LIST = "incPerformerAllActiveList.161213.json";
//        public static final String API_PERFORMER_PROFILES = "incPerformerProfiles.161213.json";
        public static final String API_FAVORITE_ADD = "incMemberFavoriteAdd.json";
        public static final String API_MEMBER_CHANGE_INFO = "incMemberInfoChange.json";
        public static final String API_BLOG_LIST = "incPerformerBlogDataCandy.json";
        public static final String API_LIKE_BLOG = "incPerformerBlogNice.json";

        public static final String API_PUSH_NOTIFICATION_REGIST = "incMemberNoticeRegistCandy.json";
        public static final String API_PUSH_NOTIFICATION_GET = "incMemberNoticeCandy.json";
        public static final String API_PUSH_NOTIFICATION_CHANGE_FOR_PUSH = "incMemberNoticeChangeCandy.json";
        public static final String API_SMS_POST = "incMemberSmsPost.json";
        public static final String API_SMS_CHECK = "incMemberSmsCheck.json";
        public static final String API_MISSION_UNCOMPLETED_LIST = "incMemberMissionList.json";
        public static final String API_MISSION_COMPLETED_LIST = "incMemberMissionCompList.json";
        public static final String API_MISSION_ADD_POINT = "incMemberMissionAddPoint.json";
        public static final String API_MEMBER_MISSION_INFO = "incMemberMissionInfo.json";
        public static final String API_WEB_TOKEN = "getToken.php";
        public static final String API_ADD_POINT = "https://d3hx9ha4n2u8wv.cloudfront.net/_app/AndroidSettlement.do";
        public static final String API_PROBIHITED_WORDS = "http://ap.livede55.com/flax/fss/16605/prohibitedWord.json";
        public static final String API_POINT_ADDITION = "incMemberOnecLimitedPointAddition.json";
        public static final String API_GET_FAVORITE_PERFORMERS_LIST = "nativeAppGetMemberFavoritesList.json";
        public static final String API_MEMBER_CAMERA_POINT_CONSUMPTION = "incSendCameraSubPointCandy.json";
        public static final String API_BLOG_CATEGORIES = "incPerformerBlogCategoriesCandy.json";
    }

    /**
     * App web urls
     */
    public static class WebUrl {
        public static final String URL_TRAIL = "http://live-star.jp/webview/member/event/trial/";
        public static final String URL_PURCHASE = "member/point/purchase/";
        public static final String USAGE_GUIDE = "member/guide/beginner/";
        public static final String FEE = "member/guide/charge/";
        public static final String TERM = "member/guide/terms/";
        public static final String POLICY = "member/guide/privacy/";
        public static final String TRADE_LAW = "member/guide/law/";
        public static final String URL_INQUIRY_HISTORY = "member/support/thread/";
        public static final String URL_FAQ = "member/support/faq/";
        public static final String OFFLINE_TERM = "file:///android_asset/s_web_terms.html";
    }

    /**
     * All fields
     */
    public static class Fields {
        public static final String FIELD_VERSION = "version";
        public static final String FIELD_WEB_VIEW_BASE_URL = "webviewBaseUrl";
        public static final String FIELD_WEB_SOCKET_URL = "webSocketUrl";
        public static final String FIELD_OWNER_NAME = "ownerName";
        public static final String FIELD_IMAGE_URL_DOMAIN = "profileImageUrlDomain";
        public static final String FIELD_ID = "id";
        public static final String FIELD_PASS = "pass";
        public static final String FIELD_OS = "os";
        public static final String FIELD_DEVICE = "device";
        public static final String FIELD_DEVICE_VALUE = "Android";
        public static final String FIELD_CHANGE_PASS = "changePass";
        public static final String FIELD_EMAIL = "mail";
        public static final String FIELD_NAME = "name";
        public static final String FIELD_BIRTH_YEAR = "birthYear";
        public static final String FIELD_BIRTH_MONTH = "birthMonth";
        public static final String FIELD_BIRTH_DAY = "birthDay";
        public static final String FIELD_STATUS = "status";
        public static final String FIELD_RESPONSE = "response";
        public static final String FIELD_MEMBER = "member";
        public static final String FIELD_BANNER_CODE = "bannerCode";
        public static final String FIELD_OPTION15 = "option15";
        public static final String FIELD_OPTION28 = "option28";
        public static final String FIELD_CODE = "code";
        public static final String FIELD_POINT = "point";
        public static final String FIELD_NOT_OPEN_COUNT = "notOpenCount";
        public static final String FIELD_JOIN_DATE = "joinDate";
        public static final String FIELD_BIRTH_DATE = "birth";
        public static final String FIELD_FAVORITE = "favorite";
        public static final String FIELD_BUY_TIMES = "buyTimes";
        public static final String FIELD_IS_3DAY = "isRegistration3DaysWithin";
        public static final String FIELD_FREE_MAIL = "freeMail";
        public static final String FIELD_NOT_OPEN_SUPPORT_COUNT = "notOpenSupportCount";
        public static final String FIELD_BIRTH_AGE = "birthAge";
        public static final String FIELD_AGE = "age";
        public static final String FIELD_BLOOD = "blood";
        public static final String FIELD_TELEAUTH = "teleauth";
        public static final String FIELD_TELEAUTH_OK = "telauthOk";
        public static final String FIELD_UN_FAVORITE_CODES = "unFavoriteCodes";
        public static final String FIELD_UN_FAVORITE_LIST = "unFavorite";
        public static final String FIELD_SUBMIT = "submit";
        public static final String FIELD_ERROR_MESSAGE = "errorMessage";
        public static final String FIELD_ERROR_MESSEGE = "errorMessege";
        public static final String FIELD_NOTIFICATION = "notice";
        public static final String FIELD_NEW = "new";
        public static final String FIELD_ISSUCCES = "isSuccess";
        public static final String FIELD_IS_MEMBER_CONTENTS_FULL_MODE = "isMemberContentsFullMode";
        public static final String FIELD_PERFORMERS = "performers";
        public static final String FIELD_TYPE = "type";
        public static final String FIELD_DATE = "date";

        public static final String FIELD_PERFORMER = "performer";
        public static final String FIELD_PERFORMER_CODE = "performerCode";
        public static final String FIELD_MAIL_CODE = "mailCode";
        public static final String PERFORMER_NAME = "performerName";
        public static final String FIELD_PERFORMER_ORIGINAL_NAME = "performerOrignalName";
        public static final String FIELD_PERFORMER_IMAGE = "performerImage";
        public static final String FIELD_PERFORMER_STATUS = "performerStatus";
        public static final String FIELD_PERFORMER_STATUS_STRING = "performerStatusString";
        public static final String FIELD_PERFORMER_SMARTPHONE = "performerSmartPhone";
        public static final String FIELD_PERFORMER_POS = "performerPos";
        public static final String FIELD_PERFORMER_AGE = "performerAge";
        public static final String FIELD_MEMBER_CODE = "memberCode";
        public static final String FIELD_MEMBER_NAME = "memberName";
        public static final String FIELD_SUBJECT = "subject";
        public static final String FIELD_BODY = "body";
        public static final String FIELD_OPEN = "open";
        public static final String FIELD_RETURN = "return";
        public static final String FIELD_SEND_MAIL = "sendMail";
        public static final String FIELD_SEND_DATE = "sendDate";
        public static final String FIELD_PRESENT_POINT = "presentPoint";
        public static final String FIELD_LIST_EMAIL = "list";
        public static final String FIELD_ROW_COUNT = "rows";
        public static final String FIELD_MAIL = "mail";
        public static final String FIELD_LIMIT = "limit";
        public static final String FIELD_CATEGORY = "category";
        public static final String FIELD_MISSION_PAGE = "page";
        public static final String FIELD_MISSION_ID = "seq";
        public static final String FIELD_SORT = "sort";
        public static final String FIELD_ORDER = "order";
        public static final String FIELD_AREA = "area";
        public static final String FIELD_AGE10 = "age10";
        public static final String FIELD_AGE20 = "age20";
        public static final String FIELD_AGE30 = "age30";
        public static final String FIELD_PICKUP = "pickup";
        public static final String FIELD_PERFORMER_IMAGE_URL = "performerImageUrl";
        public static final String FIELD_PAGE = "pageNumber";
        public static final String FIELD_POST_ID = "postId";
        public static final String FIELD_MESSAGE = "message";
        public static final String FIELD_TERMINAL = "terminal";
        public static final String FIELD_TOKEN = "token";
        public static final String FIELD_SECRET = "secret";
        public static final String FIELD_PN_MAIL = "pushMail";
        public static final String FIELD_PN_LOGIN = "pushLogin";
        public static final String FIELD_PN_BLOG = "pushBlog";
        public static final String FIELD_PN_MAGA = "pushMaga";
        public static final String FIELD_PN_REJECT_FROM = "rejectFr";
        public static final String FIELD_PN_REJECT_TO = "rejectTo";
        public static final String FIELD_TELNO = "telno";
        public static final String FIELD_BIG = "big";
        public static final String FIELD_SMALL = "small";
        public static final String FIELD_ATTACH_IMAGE_URL = "attachImageUrl";
        public static final String FIELD_PAYMENT = "payment";
        public static final String FIELD_IMAGE = "image";
        public static final String FIELD_ITEM_ID = "itemId";
        public static final String FIELD_YEN = "yen";
        public static final String FIELD_MINUTE = "minute";
        public static final String FIELD_SIGNATURE = "signature";
        public static final String FIELD_PERFORMER_PUBLIC = "performerPublic";
        public static final String FIELD_IS_CONTENTS_FULL_MODE = "isContentsFullMode";
        public static final String FIELD_RANKING = "rankings";

        // Add point fields
        public static final String FIELD_OWNER_CODE = "ownerCode";
        public static final String FIELD_OWNER_CODE_VALUE = "16605";
        public static final String FIELD_APP_CODE = "appCode";
        public static final String FIELD_APP_CODE_VALUE = "CandyAndroid";
        public static final String FIELD_RECEIPT = "receipt";

        // prohibited worlds
        public static final String FIELD_PROHIBITED = "prohibitedWord";

        //dayOfWeek

        public static final String FIELD_MONDAY = "月";
        public static final String FIELD_TUESDAY = "火";
        public static final String FIELD_WEDNESDAY = "水";
        public static final String FIELD_THURSDAY = "木";
        public static final String FIELD_FRIDAY = "金";
        public static final String FIELD_SATURDAY = "土";
        public static final String FIELD_SUNDAY = "日";
    }

    /**
     * Application custom actions
     */
    public static class IntentActions {
        public static final String ACTION_INAPP = BuildConfig.APPLICATION_ID + ".INAPP";
        public static final String ACTION_CHAT = BuildConfig.APPLICATION_ID + ".CHAT";
        public static final String ACTION_BLOCK = BuildConfig.APPLICATION_ID + ".BLOCK";
        public static final String ACTION_FAVORITE = BuildConfig.APPLICATION_ID + ".FAVORITE";
        public static final String ACTION_OPEN_HOME = BuildConfig.APPLICATION_ID + ".OPEN_HOME";
        public static final String ACTION_PERFORMER_DETAIL = BuildConfig.APPLICATION_ID + ".PERFORMER_DETAIL";
        public static final String ACTION_BLOG_DETAIL = BuildConfig.APPLICATION_ID + ".BLOG_DETAIL";
        public static final String ACTION_FREE_POINT = BuildConfig.APPLICATION_ID + ".FREE_POINT";
        public static final String ACTION_PURCHASE_POINT = BuildConfig.APPLICATION_ID + ".PURCHASE_POINT";
    }

    public static class UriId {
        public static final String FREE_POINT = "freepoint";
        public static final String PERFORMER_LIST = "performerlist";
        public static final String PERFORMER = "performer";
        public static final String BLOG_LIST = "bloglist";
        public static final String BLOG = "blog";
        public static final String MESSAGE_LIST = "messagelist";
        public static final String MESSAGE = "message";
        public static final String FAVOURITE = "favourite";
        public static final String MENU = "menu";
    }

    /**
     * KEY INTENT EXTRAS
     */
    public static class IntentExtras {
        public static final String POSITION = "position";
        public static final String PERFORMER_CODE = "performer_code";
        public static final String PERFORMER_NAME = "performer_name";
        public static final String PERFORMER_IMAGE = "performer_image";
        public static final String PERFORMER_MESSAGE = "performer_message";
        public static final String PERFORMER = "performer";
        public static final String POST_ID = "post_id";
        public static final String VIDEO_MODE = "video_mode";
        public static final String PERFORMER_AGE = "performer_age";
    }

    public static class PerformerStatus {
        public static final int CALL_WAITING = 1;
        public static final int WAITING = 4;
    }

    public enum OnlineStatus {
        OFFLINE("メッセージ待ち"),
        CALL_WAITING("ビデオ通話待ち"),
        CALLING("ビデオ通話中"),
        TWO_SHOT("2SHOT中"),
        UNKNOW(""),
        MESSAGE_WAITING("メッセージ待ち"),
        WAITING("待ち合わせ中");
        private String name;

        OnlineStatus(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public static OnlineStatus getStatusFromCode(int code) {
            switch (code) {
                case 0:
                    return OFFLINE;
                case 1:
                    return CALL_WAITING;
                case 2:
                    return CALLING;
                case 3:
                    return TWO_SHOT;
                case 4:
                    return WAITING;
                case 5:
                    return MESSAGE_WAITING;
                default:
                    return UNKNOW;

            }
        }

        public static int getBackgroundDrawableFromCode(int code) {
            switch (code) {
                case 0:
                    return R.drawable.gradient_blue_background;
                case 1:
                    return R.drawable.gradient_red_background;
                case 2:
                    return R.drawable.gradient_orange_background;
                case 3:
                    return R.drawable.gradient_pink_background;
                case 4:
                    return R.drawable.gradient_green_background;
                case 5:
                    return R.drawable.gradient_blue_background;
                default:
                    return R.drawable.gradient_green_background;
            }
        }

        public static int getBackgroundDrawableCornerFromCode(int code) {
            switch (code) {
                case 0:
                    return R.drawable.gradient_blue_rounded_corner_bg;
                case 1:
                    return R.drawable.gradient_red_rounded_background;
                case 2:
                    return R.drawable.gradient_orange_rounded_corner_bg;
                case 3:
                    return R.drawable.gradient_pink_rounded_corner_bg;
                case 4:
                    return R.drawable.gradient_green_rounded_corner_bg;
                case 5:
                    return R.drawable.gradient_blue_rounded_corner_bg;
                default:
                    return R.drawable.gradient_green_rounded_corner_bg;
            }
        }

        public static int getBackgroundTransFromCode(int code) {
            switch (code) {
                case 0:
                    return R.drawable.gradient_blue_background_trans;
                case 1:
                    return R.drawable.gradient_red_background_trans;
                case 2:
                    return R.drawable.gradient_orange_background_trans;
                case 3:
                    return R.drawable.gradient_pink_background_trans;
                case 4:
                    return R.drawable.gradient_green_background_trans;
                case 5:
                    return R.drawable.gradient_blue_background_trans;
                default:
                    return R.drawable.gradient_green_background_trans;
            }
        }
    }

    public static String[] SMS_TRANSMISSION_ERROR = {"既にポイント追加済", "すでに登録されています",
            "既に他アカウントで電話番号利用済", "電話番号が正しくありません"};

    public static String[] SMS_TOKEN_ERROR = {"すでにポイントは追加されています", "認証コードが一致しません", "認証コードが正しくありません"};
    public static final int SMS_SUBMIT_SUCCESS = 0;

    public static class DateFormat {
        public static final String YYYYMMDD = "yyyy-mm-dd";
        public static final String MMddEHHmm = "mm-dd";
    }

    public static final String FIELD_ID = "id";
    public static final String FIELD_PASS = "pass";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_MEMBER = "member";
    /*
        add field for incMemberInfoChange.json
     */
    public static final String FIELD_CHANGE_INFO = "changeInfo";
    public static final String FIELD_CHANGE_PASS = "changePass";
    /*
        end
     */
    public static final String API_MEMBER_INFO_CHANGE = "incMemberInfoChange.json";

    public static final class SETTINGS {
        public static final int DEFAULT_LIMIT = 20;
        public static final int PN_SETTING_ON = 1;
        public static final String DEFAULT_SORT_FIELD = "send_date";
        public static final String DEFAULT_SORT_ORDER = "desc";
        public static final int MESSAGE_POINT_PAYMENT_COST = 100;
        public static final int MESSAGE_WITH_IMAGE_POINT_PAYMENT_COST = 200;
    }

    public static final class ADJUST {
        public static final String APP_TOKEN = "dfdavzaemdxc";
        public static final String EVENT_EMAIL_PASSWORD_TOKEN = "mdmk4z";
        public static final String EVENT_PURCHASE_SUCCESS_TOKEN = "2bw05v";
        public static final String EVENT_CURRENCY = "JPY";
        public static final String MEM_ID = "mem_id";
        public static final String MEM_PASS = "mem_pass";
    }

    //Campaign status
    public static final String CAMPAIGN_STATUS_NOW = "now";
    public static final String CAMPAIGN_STATUS_SOON = "soon";

    public static final String STATUS_STRING_OFFLINE = "オフライン";
    public static final int SMARTPHONE_ON = 1;

    public static class MissionType {
        public static final String TIME_24H = "時間(24)";
        public static final String TIME_12H = "時間";
    }

    public static class MissionTextFormat {
        public static final String TIME_FORMAT = "あと%sで再取得";
        public static final String NUMBER_FORMAT = "達成状況：%s";
        public static final String MISSION_REWARD_FORMAT = "<small>獲得報酬 </small><big>%,d</big> <small>pts</small>";
        public static final String MISSION_START_NOTE = "<font color=\"#ff0ec6c3\">★</font><font>ヒント</font><font color=\"#ff0ec6c3\">★</font>";
        public static final String BLOCKED_MISSION_NAME = "新しい女の子を見つけよう";

    }

    public static final int MAIN_MENU=1;
    public static final int LAST_BACK=0;
    public static final int CAN_GO_BACK=2;

    public static BaseFragment webFragment = null;

    public static final String STATUS_0="メッセージ待ち";

    public enum ReproEventType {
        startScreen("【画面】利用開始"),
        tapTermsOfUseButton("【タップ】利用規約に同意してはじめる"),
        userRegisterSuccessfully("ユーザ登録"),
        loginScreen("【画面】ログイン"),
        loginSuccessfully("【完了】ログイン"),

        // オンライン一覧
        femaleListScreen("【画面】オンライン一覧"),
        tapFemaleAtFemaleListScreen("【タップ】お相手詳細（【画面】オンライン一覧）"),

        // ランキング
        rankingPreviousDayScreen("【画面】ランキング_前日"),
        rankingWeekScreen("【画面】ランキング_週間"),
        rankingMonthScreen("【画面】ランキング_月間"),
        tapFemaleAtRankingScreen("【タップ】お相手詳細（【画面】ランキング）"),

        // お気に入り
        favoriteScreen("【画面】お気に入り"),
        tapFemaleAtFavoriteScreen("【タップ】お相手詳細（【画面】お気に入り）"),
        tapMessageAtFavoriteScreen("【タップ】メッセージ（【画面】お気に入り）"),

        // メッセージ

        // お相手詳細
        femaleDetailScreen("閲覧"),
        tapFavoriteButton("【タップ】お気に入り追加"),
        chatDetailScreen("【画面】メッセージ詳細"),

        // メッセージ詳細
        tapSendMessageButtonWithText("【タップ】メッセージ送信"),
        tapSenMessageButtonWithImage("【タップ】メッセージ送信_画像付き"),

        // ビデオ通話
        videoCallScreen("【画面】ビデオ通話"),
        videoCallSendComment("【タップ】コメント送信"),
        peepScreen("【画面】のぞき"),
        peepScreenSendComment("【タップ】ナイショ話する"),
        twoShotScreen("【画面】2SHOT通話"),
        tapSwitchToVideoCallButton("【タップ】ビデオ通話に切り替え"),
        tap2ShotButton("【タップ】2SHOTに誘う"),
        tapMicroButton("【タップ】マイクを使用"),
        tapCameraButton("【タップ】カメラを使用"),
        tapLogoutYestButton("【完了】ビデオ通話"),
        tapLogoutYestButtonFreePerformer("【完了】ビデオ通話無料体験"),

        // 購入画面
        pointPurchaseScreen("【画面】ポイント購入"),
        getFreePoint("【完了】取得する_無料ポイント"),
        verifySMSSuccessfully("【完了】SMS連携"),
        buyPoint("購入"),
        buyPointWithPrice("【完了】%d購入"),

        // ミッション
        missionScreen("【画面】ミッション"),
        tapReceiveAwardButton("【タップ】報酬を受け取る"),
        tapMissionDetailButton("【タップ】ミッション詳細確認"),
        tapBannerWebView("【画面】ビデオ通話無料体験");

        public String text;

        ReproEventType(String s) {
            this.text = s;
        }
    }
}
