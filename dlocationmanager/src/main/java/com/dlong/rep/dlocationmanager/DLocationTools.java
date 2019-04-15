package com.dlong.rep.dlocationmanager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.provider.Settings;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * 定位数据处理工具
 *
 * @author  dlong
 * created at 2019/4/13 10:04 AM
 */
public class DLocationTools {

    /**
     * 打开Gps设置界面
     */
    public static void openGpsSettings(Context context, int what) {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ((Activity) context).startActivityForResult(intent, what);
    }

    /**
     * 跳转到应用设置界面
     */
    public static void openAppSetting(Context context) {
        openAppSetting(context, -1);
    }

    /**
     * 跳转到应用设置界面
     */
    public static void openAppSetting(Context context, int what) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", ((Activity) context).getPackageName(), null);
        intent.setData(uri);
        ((Activity) context).startActivityForResult(intent, what);
    }

    /**
     * 根据经纬度获取地理位置
     *
     * @param context   上下文
     * @param latitude  纬度
     * @param longitude 经度
     * @return {@link Address}
     */
    public static Address getAddress(Context context, double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0)
                return addresses.get(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据经纬度获取所在国家
     *
     * @param context   上下文
     * @param latitude  纬度
     * @param longitude 经度
     * @return 所在国家
     */
    public static String getCountryName(Context context, double latitude, double longitude) {
        Address address = getAddress(context, latitude, longitude);
        return address == null ? "unknown" : address.getCountryName();
    }

    /**
     * 根据经纬度获取所在地
     *
     * @param context   上下文
     * @param latitude  纬度
     * @param longitude 经度
     * @return 所在地
     */
    public static String getLocality(Context context, double latitude, double longitude) {
        Address address = getAddress(context, latitude, longitude);
        return address == null ? "unknown" : address.getLocality();
    }

    /**
     * 根据经纬度获取所在街道
     *
     * @param context   上下文
     * @param latitude  纬度
     * @param longitude 经度
     * @return 所在街道
     */
    public static String getStreet(Context context, double latitude, double longitude) {
        Address address = getAddress(context, latitude, longitude);
        return address == null ? "unknown" : address.getAddressLine(0);
    }
}
