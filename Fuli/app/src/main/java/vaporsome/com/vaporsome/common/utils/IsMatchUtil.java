package vaporsome.com.vaporsome.common.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ${Bash} on 2016/4/1.
 */
public class IsMatchUtil {
    public static boolean isPhoneNumberValid(String phoneNumber) {
        boolean isValid = false;
        String expression = "^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$";
        CharSequence inputStr = phoneNumber;
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    public static boolean isConfirmNumberValid(String confirmNumber) {
        boolean isValid = false;
        String expression = "^\\d{6}$";
        CharSequence inputStr = confirmNumber;
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    public static boolean isPsdrValid(String strPsd) {
        boolean isValid = false;
        String expression = "^(\\w*(?=\\w*\\d)(?=\\w*[A-Za-z])\\w*){6,12}$";
        CharSequence inputStr = strPsd;
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    public static boolean isNumberValid(String strPsd) {
        boolean isValid = false;
        CharSequence inputStr = strPsd;
        Pattern pattern = Pattern.compile("^+?[1-9][0-9]*$");
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    public static boolean isNumer(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }
}
