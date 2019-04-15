# DL10LocationManager
一个基于Android原生定位API的管理工具，倾向于模糊网络定位，而不是GPS定位，主要为了app只是需要根据定位获得当地天气信息的功能制作。不适合精准定位！对于精准定位需求，高德和百度才是王道

# 说明链接：
CSDN：https://blog.csdn.net/sinat_38184748/article/details/89280289


# 前言

> 项目要求在首页要能显示当地的天气信息。

> 好了，要定位。 

> 去找高德，一瞧，嘿，好麻烦啊！！！！ 

> 注册key这一步就没耐心了。。。

> 上帝啊，我只是要一个模糊定位，能到区信息就可以了，

> 就要省市区三个信息，你要我搞这么多接入。。。太蛋疼

> 好吧，救世主就锁定在了 **Android原生API。**

> 经过一顿操作，参考了一下别人的代码，结合自己的习惯，整理出一个依赖库
下面介绍一下用法
# 使用说明
**这个库遵循单一职责原则，也就是只负责获取Location；不管错误信息的处理和权限的请求，但是会有相对应的回调。使用者可以根据回调信息来做权限请求或者其他弹窗提示。这样子就可以在不同项目中使用不同的弹窗样式，大大提高自由度和降低耦合度。**
## 添加依赖
Step 1. Add the JitPack repository to your build file 
Add it in your root build.gradle at the end of repositories:

```java
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
Step 2. Add the dependency

```java
	dependencies {
	        implementation 'com.github.D10NGYANG:DL10LocationManager:1.0.2'
	}
```
## 初始化
首先要在AndroidManifest.xml中声明权限

```java
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />
    <uses-permission android:name="android.permission.INTERNET" />
```
接着需要在Application中初始化（如果只有一个Activity用到也可以只在该Activity的onCreate中初始化

```java
// 初始化
DLocationUtils.init(mContext);
```
## 添加监听器

```java
    /**
     * 监听器
     */
    private OnLocationChangeListener locationChangeListener = new OnLocationChangeListener() {
        @Override
        public void getLastKnownLocation(Location location) {
            // 获取上一次获得的定位
            Log.e(TAG, "onLocationChanged: " + location.getLatitude());
            updateGPSInfo(location);
        }

        @Override
        public void onLocationChanged(Location location) {
            // 定位改变
            Log.e(TAG, "定位方式：" + location.getProvider());
            Log.e(TAG, "纬度：" + location.getLatitude());
            Log.e(TAG, "经度：" + location.getLongitude());
            Log.e(TAG, "海拔：" + location.getAltitude());
            Log.e(TAG, "时间：" + location.getTime());
            Log.e(TAG, "国家：" + DLocationTools.getCountryName(mContext, location.getLatitude(), location.getLongitude()));
            Log.e(TAG, "获取地理位置：" + DLocationTools.getAddress(mContext, location.getLatitude(), location.getLongitude()));
            Log.e(TAG, "所在地：" + DLocationTools.getLocality(mContext, location.getLatitude(), location.getLongitude()));
            Log.e(TAG, "所在街道：" + DLocationTools.getStreet(mContext, location.getLatitude(), location.getLongitude()));
            updateGPSInfo(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // 状态改变
            // 比如GPS的开关，DLocationWhat.STATUS_ENABLE/DLocationWhat.STATUS_DISABLE

        }
    };
```
## 注册
注册接口是DLocationUtils.getInstance().register();
注册时可以填三个参数，前面两个不填会有默认值；

```java
     * @param minTime     位置信息更新周期（单位：毫秒）
     * @param minDistance 位置变化最小距离：当位置距离变化超过此值时，将更新位置信息（单位：米）
     * @param listener    位置刷新的回调接口
```

会有一个状态值返回，直接判断返回值即可；

```java
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
     * 定位模式只有GPS工作；只能在有网络定位的条件下工作
     * 所以需要去切换定位模式到【高精确度】或【节电】
     */
    public static final int ONLY_GPS_WORK = 4;
```
在DLocationTools类里还有两个方法，分别跳转系统App设置界面和系统GPS设置界面，如有需要可以直接调用；
```java
        int status = DLocationUtils.getInstance().register(locationChangeListener);
        switch (status){
            case NO_LOCATIONMANAGER:
                txt.setText("没有定位权限");
                // TODO: 2019/4/13 请求权限
                DLocationTools.openAppSetting(mContext);

                break;
            case NO_PROVIDER:
                txt.setText("没有可用的定位提供器或尚未打开定位");
                // TODO: 2019/4/13 打开定位
                DLocationTools.openGpsSettings(mContext);
                break;
	    case ONLY_GPS_WORK:
                // TODO: 2019/4/15 切换定位模式到【高精确度】或【节电】
                DLocationTools.openGpsSettings(mContext, GO_TO_GPS);
                break;
        }
```
## 反注册
最后记得在不用的时候注销掉监听器
```java
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 注销
        DLocationUtils.getInstance().unregister();
    }
```
## 混淆
keep了就行了，反正都开源的。
```java
-keep class com.dlong.rep.dlocationmanager.** {*;}
```

# 感谢
参考：
[@总有刁民想杀寡人----android 定位功能的实现](https://blog.csdn.net/qq_36699930/article/details/81945401)
这位老哥的博客风格挺整洁的，有兴趣的朋友可以关注下。
