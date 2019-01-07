package com.github.snailycy.http.util;

import android.text.TextUtils;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * MD5密码加密算法.
 */
public final class MD5Utils {

    private static final int LO_BYTE = 0x0f;
    private static final int MOVE_BIT = 4;
    private static final int HI_BYTE = 0xf0;
    private static final String[] HEX_DIGITS = {"0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};

    /**
     * 防止被构建.
     */
    private MD5Utils() {

    }

    /**
     * 转换字节数组为16进制字串
     *
     * @param b 字节数组
     * @return 16进制字串
     */

    private static String byteArrayToHexString(byte[] b) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            buf.append(byteToHexString(b[i]));
        }
        return buf.toString();
    }

    /**
     * 字节转成字符.
     *
     * @param b 原始字节.
     * @return 转换后的字符.
     */
    private static String byteToHexString(byte b) {
        return HEX_DIGITS[(b & HI_BYTE) >> MOVE_BIT] + HEX_DIGITS[b & LO_BYTE];
    }

    /**
     * 进行加密.
     *
     * @param origin 原始字符串.
     * @return 加密后的结果.
     */
    public static String encode(String origin) {
        if (TextUtils.isEmpty(origin)) {
            return "";
        }
        String resultString = null;

        resultString = new String(origin);
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            resultString = byteArrayToHexString(md.digest(resultString
                    .getBytes()));
        } catch (NoSuchAlgorithmException e) {
            return "";
        }

        return resultString;
    }


    /**
     * 对输入流生成校验码.
     *
     * @param in 输入流.
     * @return 生成的校验码.
     */
    public static String encode(InputStream in) {
        if (in == null) {
            return "";
        }
        String resultString = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[1024 * 1024];
            int len = 0;
            while ((len = in.read(buffer)) > 0) {
                md.update(buffer, 0, len);
            }

            resultString = byteArrayToHexString(md.digest());
        } catch (NoSuchAlgorithmException e) {
            return "";
        } catch (IOException e) {
            return "";
        }
        return resultString;
    }
}
