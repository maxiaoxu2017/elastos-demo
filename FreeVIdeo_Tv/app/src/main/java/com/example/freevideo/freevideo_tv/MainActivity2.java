package com.example.freevideo.freevideo_tv;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.freevideo.freevideo_tv.elastoshelper.ElastosUtils;

import org.elastos.carrier.ConnectionStatus;
import org.elastos.carrier.FriendInfo;
import org.elastos.carrier.Log;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity2 extends AppCompatActivity{

    private static final String TAG = "MainActivity";
    public static MainActivity2 instance;

    private String appAbsPath;
    private ImageView mImageView;

    private RelativeLayout tips_scan;
    private RelativeLayout tips_success;

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(tips_scan != null && tips_success != null) {
                tips_scan.setVisibility(View.GONE);
                tips_success.setVisibility(View.VISIBLE);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main2);
        instance = this;
        appAbsPath = getAppAbsPath();

        if(!ElastosUtils.isInit) {
            ElastosUtils.init(this);
        }
        createQRcodeImage();

        mImageView = findViewById(R.id.iv_qrcode);
        Drawable drawable = Drawable.createFromPath(appAbsPath + "qrcode.png");
        mImageView.setBackground(drawable);

        tips_scan = findViewById(R.id.tips_scan);
        tips_success = findViewById(R.id.tips_success);
    }

    public String getAppAbsPath() {
        if (TextUtils.isEmpty(appAbsPath)) {
            PackageManager pm = getPackageManager();
            PackageInfo info;
            try {
                info = pm.getPackageInfo(getApplicationContext().getPackageName(), PackageManager.GET_ACTIVITIES);
                if (info != null) {
                    ApplicationInfo appInfo = info.applicationInfo;
                    appAbsPath = appInfo.dataDir.concat("/");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return appAbsPath;
    }

    /**
     * @desc  elastos sdk: qrcode used for add friend
     * @param content: Carrier address
     */
    private void createQRcodeImage() {
        if(TextUtils.isEmpty(ElastosUtils.carrierAddr)) {
            return;
        }
        QRCodeUtil.createQRImage("elastossdk_" + ElastosUtils.carrierAddr, null, appAbsPath + "qrcode.png" );
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    /**
     * 双击返回退出应用
     */
    private boolean mBackKeyPressed = false;
    @Override
    public void onBackPressed() {

        if (!mBackKeyPressed) {
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            mBackKeyPressed = true;
            new Timer().schedule(new TimerTask() {// 延时两秒，如果超出则取消取消上一次的记录

                @Override
                public void run() {
                    mBackKeyPressed = false;

                }
            }, 2000);
        } else {// 退出程序
            this.finish();
        }
    }

}
