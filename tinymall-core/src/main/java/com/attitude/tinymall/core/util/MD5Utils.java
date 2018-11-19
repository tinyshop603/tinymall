package com.attitude.tinymall.core.util;
import java.security.MessageDigest;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
public class MD5Utils {
        public static String MD5Encode(String origin) {
            StringBuilder sb = new StringBuilder();
            try{
                MessageDigest md = MessageDigest.getInstance("MD5");
                byte[] array = md.digest(origin.getBytes("UTF-8"));
                byte[] var4 = array;
                int var5 = array.length;

                for(int var6 = 0; var6 < var5; ++var6) {
                    byte item = var4[var6];
                    sb.append(Integer.toHexString(item & 255 | 256).substring(1, 3));
                }
            }catch(Exception e){
                e.printStackTrace();
            }
            return sb.toString().toUpperCase();
        }
}
