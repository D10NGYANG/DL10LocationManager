package com.dlong.rep.dlocationmanager;

/**
 * 返回消息状态值管理
 * @author  dlong
 * created at 2019/4/13 9:26 AM
 */
public class DLocationWhat {

    /**
     * 注册成功
     */
    public static final int OK = 0;

    /**
     * 缺少监听器
     */
    public static final int NO_LISTENER = 1;

    /**
     * 没有可用的位置提供器
     */
    public static final int NO_PROVIDER = 2;

    /**
     * 没有定位的系统服务
     */
    public static final int NO_LOCATIONMANAGER = 3;

    /**
     * 没有定位的系统服务
     */
    public static final int ONLY_GPS_WORK = 4;

    /**
     * 打开位置提供器
     */
    public static final int STATUS_ENABLE = 10;

    /**
     * 关闭位置提供器
     */
    public static final int STATUS_DISABLE = 11;


}
