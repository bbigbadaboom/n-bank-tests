package Utils;

import org.apache.commons.lang3.RandomStringUtils;

public class Common {
    public static String generate() {
        String allowed = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789._-";
        int length = 3 + (int)(Math.random() * 13);
        return RandomStringUtils.random(length, allowed);
    }


}
