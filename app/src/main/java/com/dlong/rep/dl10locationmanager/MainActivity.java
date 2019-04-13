package com.dlong.rep.dl10locationmanager;

import android.content.Context;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dlong.rep.dlocationmanager.DLocationTools;
import com.dlong.rep.dlocationmanager.DLocationUtils;
import com.dlong.rep.dlocationmanager.OnLocationChangeListener;

import static com.dlong.rep.dlocationmanager.DLocationWhat.NO_LOCATIONMANAGER;
import static com.dlong.rep.dlocationmanager.DLocationWhat.NO_PROVIDER;

public class MainActivity extends AppCompatActivity {
    private Context mContext = this;
    private final static String TAG = "哈哈哈哈啊哈";
    private TextView txt;

    private double mLat;
    private double mLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 初始化
        DLocationUtils.init(mContext);

        txt = findViewById(R.id.txt);
    }


    /**
     * 更新经纬度信息
     * @param location
     */
    public void updateGPSInfo(Location location) {
        if (location != null) {
            mLat = location.getLatitude();
            mLng = location.getLongitude();
            String str = "lat:" + mLat + "; lng:" + mLng;
            Toast.makeText(mContext, str, Toast.LENGTH_SHORT).show();
            Log.e("TAG", str);
            txt.setText(str);
        }
    }

    /**
     * 点击事件
     * @param view
     */
    public void doClick(View view) {
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
        }
    }

    /**
     * 监听器
     */
    private OnLocationChangeListener locationChangeListener = new OnLocationChangeListener() {
        @Override
        public void getLastKnownLocation(Location location) {
            Log.e(TAG, "onLocationChanged: " + location.getLatitude());
            updateGPSInfo(location);
        }

        @Override
        public void onLocationChanged(Location location) {
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

        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 注销
        DLocationUtils.getInstance().unregister();
    }
}

