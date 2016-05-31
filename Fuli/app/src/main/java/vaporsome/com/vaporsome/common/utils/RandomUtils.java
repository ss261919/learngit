package vaporsome.com.vaporsome.common.utils;

/**
 * Created by ${Bash} on 2016/4/15.
 */
public class RandomUtils {
    public static String getRandomIntString() {
        java.util.Random r=new java.util.Random();
        StringBuffer stringBuffer = new StringBuffer();
        for(int i=0;i<5;i++){
            stringBuffer.append(r.nextInt());
        }
        return String.valueOf(stringBuffer);
    }
}
