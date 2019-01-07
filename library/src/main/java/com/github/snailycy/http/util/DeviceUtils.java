package com.github.snailycy.http.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;


public class DeviceUtils {
    private static String[] abis;

    private static String IMEI;


    public static String getIP() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && (inetAddress instanceof Inet4Address)) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return "";
    }

    public static String getDisplayResolution(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;
        return height + "*" + width;
    }

    public static int getDisplayWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            wm.getDefaultDisplay().getRealMetrics(displaymetrics);
        } else {
            wm.getDefaultDisplay().getMetrics(displaymetrics);
        }
        return displaymetrics.widthPixels;
    }

    public static int getDisplayHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            wm.getDefaultDisplay().getRealMetrics(displaymetrics);
        } else {
            wm.getDefaultDisplay().getMetrics(displaymetrics);
        }
        return displaymetrics.heightPixels;
    }

    public static String getBrand() {
        return Build.MANUFACTURER + " " + Build.BRAND + " " + Build.MODEL;
    }

    public static boolean isOverMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    public static String getIMEI(Context context) {
        if (IMEI == null || IMEI.isEmpty()) {
            TelephonyManager telephonyManager = (TelephonyManager)
                    context.getSystemService(Context.TELEPHONY_SERVICE);
            String id = "";
            if (!isOverMarshmallow()) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                    id = telephonyManager.getDeviceId();
                    return id;
                }

            } else if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                id = telephonyManager.getDeviceId();
            }
            IMEI = id;
            return TextUtils.isEmpty(id) ? "" : id;
        } else {
            return IMEI;
        }
    }

    /**
     * @param context
     * @return 16进制的字符串就是ANDROID_ID
     */
    public static String getAndroidId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * 获取Mac
     *
     * @param context
     * @return
     */
    public static String getMac(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo winfo = wifi.getConnectionInfo();
            String mac = winfo.getMacAddress();
            return mac;
        }
        return "";
    }

    /**
     * Check if the CPU architecture is X64
     *
     * @return
     */
    public static boolean isX64() {
        String arch = System.getProperty("os.arch").toLowerCase();
        return arch.contains("64");
    }

    /**
     * Check the application is installed or not
     *
     * @param context
     * @param packageName the application package name
     * @return
     */
    public static boolean isPackageInstalled(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        try {
            synchronized (context) {
                pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isX86() {
        return (getAbis() != null && abis.length > 0 && "x86".equals(abis[0]));
    }

    public static String[] getAbis() {
        if (abis != null) {
            return abis;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            abis = Build.SUPPORTED_ABIS;
        } else {
            abis = new String[]{Build.CPU_ABI, Build.CPU_ABI2};
        }
        return abis;
    }


    /**
     * 检查是否为模拟器 AOSP on ARM Emulator
     *
     * @return
     */
    public static boolean checkIsEmulator() {
        String brand = getBrand();
        return !TextUtils.isEmpty(brand) && brand.toLowerCase().contains("emulator");
    }
}
