package com.dlong.rep.dlocationmanager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import java.util.List;

/**
 * 定位工具类
 * ----------------------------
 * 添加权限
 * <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
 * <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
 * <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
 * <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
 * <uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />
 * <uses-permission android:name="android.permission.INTERNET" />
 * ----------------------------
 * 使用Android原生API
 * 优先使用网络定位
 * GPS定位时间较长，受环境影响较大
 * ----------------------------
 * 单一实例
 * ----------------------------
 * 1、注册
 * 2、反注册
 * @author  dlong
 * created at 2019/4/13 9:01 AM
 */
public class DLocationUtils {
    private static final String TAG = "DLocationUtils";

    /** 声明单一实例 */
    private volatile static DLocationUtils instance;
    /** 上下文 */
    private static Context mContext;
    /** 定位管理器 */
    private static LocationManager mLocationManager;
    /** 定位监听暴露接口 */
    private static OnLocationChangeListener mListener;
    /** 定位监听 */
    private static MyLocationListener myLocationListener;

    private static final int MINI_TIME = 2000;
    private static final int MINI_DISTANCE = 20;

    /**
     * 初始化，应该在Application中执行
     *
     * @param context 上下文
     */
    public static void init(Context context) {
        mContext = context;
    }

    /**
     * 获得实例
     *
     * @return {@link DLocationUtils}
     */
    public static DLocationUtils getInstance() {
        if (instance == null) {
            synchronized (DLocationUtils.class) {
                if (instance == null) {
                    instance = new DLocationUtils();
                }
            }
        }
        return instance;
    }

    /**
     * 设置定位参数
     *
     * @return {@link Criteria}
     */
    private static Criteria getCriteria() {
        Criteria criteria = new Criteria();
        //设置定位精确度 Criteria.ACCURACY_COARSE比较粗略，Criteria.ACCURACY_FINE则比较精细
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        //设置是否要求速度
        criteria.setSpeedRequired(false);
        // 设置是否允许运营商收费
        criteria.setCostAllowed(false);
        //设置是否需要方位信息
        criteria.setBearingRequired(false);
        //设置是否需要海拔信息
        criteria.setAltitudeRequired(false);
        // 设置对电源的需求
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        return criteria;
    }

    /**
     * 判断定位是否可用
     *
     * @return {@code true}: 是<br>{@code false}: 否
     */
    private static boolean isLocationEnabled() {
        LocationManager lm = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        return lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER) || lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * 注册
     *
     * @param listener    位置刷新的回调接口
     * @return {@link int}
     */
    public int register(OnLocationChangeListener listener){
        return register(MINI_TIME, MINI_DISTANCE, listener);
    }


    /**
     * 注册
     *
     * @param minTime     位置信息更新周期（单位：毫秒）
     * @param minDistance 位置变化最小距离：当位置距离变化超过此值时，将更新位置信息（单位：米）
     * @param listener    位置刷新的回调接口
     * @return {@link int}
     */
    @SuppressLint("MissingPermission")
    public int register(long minTime, long minDistance, OnLocationChangeListener listener) {
        if (null == listener)
            return DLocationWhat.NO_LISTENER;
        mListener = listener;

        mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        if (null == mLocationManager)
            return DLocationWhat.NO_LOCATIONMANAGER;

        if (!isLocationEnabled()) {
            return DLocationWhat.NO_PROVIDER;
        }

        String provider;
        // 获取可用的位置提供器，GPS或是NetWork
        List<String> providers = mLocationManager.getProviders( getCriteria(), true );
        if (providers.contains( LocationManager.NETWORK_PROVIDER )) {
            Log.i(TAG, "使用网络定位");
            provider = LocationManager.NETWORK_PROVIDER;
        } else if (providers.contains( LocationManager.GPS_PROVIDER )) {
            Log.i( TAG, "使用GPS定位" );
            provider = LocationManager.GPS_PROVIDER;
        } else {
            Log.i( TAG, "没有可用的位置提供器" );
            return DLocationWhat.NO_LOCATIONMANAGER;
        }

        // 获取上一次的定位记录
        Location location = mLocationManager.getLastKnownLocation(provider);

        if (location != null)
            mListener.getLastKnownLocation(location);
        if (myLocationListener == null)
            myLocationListener = new MyLocationListener();

        mLocationManager.requestLocationUpdates(provider, minTime, minDistance, myLocationListener);
        return DLocationWhat.OK;
    }

    /**
     * 反注册
     */
    @SuppressLint("MissingPermission")
    public void unregister() {
        if (mLocationManager != null) {
            if (myLocationListener != null) {
                mLocationManager.removeUpdates(myLocationListener);
                myLocationListener = null;
            }
            mLocationManager = null;
        }
    }

    /**
     * 定位监听
     *
     * @author  dlong
     * created at 2019/4/13 9:45 AM
     */
    private static class MyLocationListener implements LocationListener {
        /**
         * 当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
         *
         * @param location 坐标
         */
        @Override
        public void onLocationChanged(Location location) {
            if (mListener != null) {
                mListener.onLocationChanged(location);
            }
        }

        /**
         * provider的在可用、暂时不可用和无服务三个状态直接切换时触发此函数
         *
         * @param provider 提供者
         * @param status   状态
         * @param extras   provider可选包
         */
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            if (mListener != null) {
                mListener.onStatusChanged(provider, status, extras);
            }
            switch (status) {
                case LocationProvider.AVAILABLE:
                    Log.e("onStatusChanged", "当前GPS状态为可见状态");
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    Log.e("onStatusChanged", "当前GPS状态为服务区外状态");
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.e("onStatusChanged", "当前GPS状态为暂停服务状态");
                    break;
            }
        }

        /**
         * provider被enable时触发此函数，比如GPS被打开
         */
        @Override
        public void onProviderEnabled(String provider) {
            if (mListener != null) {
                mListener.onStatusChanged(provider, DLocationWhat.STATUS_ENABLE, null);
            }
        }

        /**
         * provider被disable时触发此函数，比如GPS被关闭
         */
        @Override
        public void onProviderDisabled(String provider) {
            if (mListener != null) {
                mListener.onStatusChanged(provider, DLocationWhat.STATUS_DISABLE, null);
            }
        }
    }

}

