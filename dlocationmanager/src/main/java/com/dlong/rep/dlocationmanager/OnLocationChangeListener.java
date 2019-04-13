package com.dlong.rep.dlocationmanager;

import android.location.Location;
import android.os.Bundle;

/**
 * 定位接口回调
 * @author  dlong
 * created at 2019/4/13 9:22 AM
 */
public interface OnLocationChangeListener {
    /**
     * 获取最后一次保留的坐标
     *
     * @param location 坐标
     */
    void getLastKnownLocation(Location location);

    /**
     * 当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
     *
     * @param location 坐标
     */
    void onLocationChanged(Location location);

    /**
     * provider的在可用、暂时不可用和无服务三个状态直接切换时触发此函数
     *
     * @param provider 提供者
     * @param status   状态
     * @param extras   provider可选包
     */
    void onStatusChanged(String provider, int status, Bundle extras);
}
