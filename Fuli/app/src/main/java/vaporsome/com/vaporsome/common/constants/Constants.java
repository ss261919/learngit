package vaporsome.com.vaporsome.common.constants;


/**
 * Created by Administrator on 2016/3/21.
 */
public class Constants {
    //微信App_ID
    public static final String APP_ID = "wx2b47ad4b386f3e05";

    private static String baseUrl = "https://www.qckj1688.com/";
    public static String registerUri = baseUrl + "hongbao/register/register";
    public static String verificationNumberUri = baseUrl + "hongbao/register/verify-code";
    public static String forgetPsdVerificationNumberUri = baseUrl + "hongbao/register/forget-password-verify-code";
    public static String phoneLoginUri = baseUrl + "hongbao/register/mobile-login";
    public static String qiNiuTokenUri = baseUrl + "hongbao/user/image-token?token=";
    public static String shareImgQiNiuTokenUri = baseUrl + "hongbao/sun/image-token?token=";
    public static String shareImgUri = baseUrl + "hongbao/sun/upload?token=";
    public static String shareImgFragmentUri = baseUrl + "hongbao/sun/list?token=";

    public static String getProvinceUri = baseUrl + "hongbao/region/province";
    public static String getProvinceCityUri = baseUrl + "hongbao/region/city";
    public static String changeAreaUri = baseUrl + "hongbao/user/update-region?token=";
    public static String changePsdUri = baseUrl + "hongbao/user/update-pass-word?token=";
    public static String changeGenderUri = baseUrl + "hongbao/user/update-sex?token=";
    public static String changeAgeUri = baseUrl + "hongbao/user/update-age?token=";
    public static String changePickNameUri = baseUrl + "hongbao/user/update-nick-name?token=";
    public static String personCenterUri = baseUrl + "hongbao/user/info?token=";
    public static String splashActivityUri = baseUrl + "hongbao/splash";
    public static String forgetPsdUri = baseUrl + "hongbao/register/reset";
    public static String postLocation = baseUrl + "hongbao/user/location?token=";
    public static String moneyDetailInfo = baseUrl + "hongbao/user/money-detail?token=";
    public static String moneyDetail = baseUrl + "hongbao/user/money-list?token=";
    public static String getBankUrl = baseUrl + "hongbao/recharge/bank?token=";
    public static String outMoneyPostUrl = baseUrl + "hongbao/recharge/apply?token=";
    public static String userInfoChange = baseUrl + "hongbao/user/member-info?token=";
    public static String getAdsDetailInfo = baseUrl + "hongbao/shop/detail?id=2";
    public static String adsDetailInfoPost = "http://103.238.227.135/hongbao/order/apply";

    public static String redListUrl = baseUrl + "hongbao/bag/list?token=";
    public static String isRedOlded = baseUrl + "hongbao/bag/check?token=";
    public static String openRed = baseUrl + "hongbao/bag/take?token=";
    public static String redRankingUrl = baseUrl + "hongbao/bag/record-list?token=";
    public static String getGrabedRedMoney = baseUrl + "hongbao/bag/already?token=";

    public static String splashImgUrl = "splashImgUrl.png";
    //头像
    public static String userHeaderUrl = "userHeaderUrl.png";
    //personSenterAdsImg
    public static String personFragmentAdsImgUrl = "personFragmentAdsImgUrl.png";

    //微信登录
    public static String wxLoginUrl = baseUrl + "hongbao/wx/login";
    public static String wxShare = baseUrl + "hongbao/wx/share?";

    //获取当前版本号
    public static String getVersionUrl = baseUrl + "hongbao/register/android";
    //获取转账额度限制
    public static String getOutOfMoneyLimitUrl = baseUrl + "hongbao/recharge/quota?token=";
    //应用宝appId
    public static String yybAppID = "1105388728";
}
