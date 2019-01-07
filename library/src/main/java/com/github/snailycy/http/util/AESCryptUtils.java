package com.github.snailycy.http.util;

import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AESCryptUtils {
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/ECB/PKCS7Padding";
    private static final String DEFUALT_ENCODING = "UTF-8";

    /**
     * 加密
     *
     * @param sSrc
     * @return
     * @throws Exception
     */
    public static String encrypt(String sSrc, String secret) {
        try {
            SecretKeySpec skeySpec = new SecretKeySpec(secret.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION, "BC");//"算法/模式/补码方式"
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            byte[] encrypted = cipher.doFinal(sSrc.getBytes(DEFUALT_ENCODING));
            return new String(Base64.encode(encrypted, Base64.NO_WRAP));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 解密
     *
     * @param sSrc
     * @return
     */
    public static String decrypt(String sSrc, String secret) {
        try {
            SecretKeySpec skeySpec = new SecretKeySpec(secret.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION, "BC");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            byte[] decodeByte = Base64.decode(sSrc.getBytes(), Base64.NO_WRAP);
            byte[] originalByte = cipher.doFinal(decodeByte);
            String originalString = new String(originalByte, DEFUALT_ENCODING);
            return originalString;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
