package com.example.freevideo.freevideo_tv;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Toast;

import com.qingmei2.library.view.QRCodeScannerView;
import com.qingmei2.library.view.QRCoverView;


public class ScanActivity extends AppCompatActivity {

    private final int PERMISSION_REQUEST_CAMERA = 0;



    boolean isProcess = false;
    Context mContext;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        initView();
    }

    QRCodeScannerView scanner;
    QRCoverView cover;

    protected void initView() {
        setContentView(R.layout.activity_scan);
        mContext =this;
        scanner=findViewById(R.id.scanner);
        cover=findViewById(R.id.cover);
        //自动聚焦间隔2s
        scanner.setAutofocusInterval(2000L);
        //扫描结果监听处理
        scanner.setOnQRCodeReadListener(new QRCodeScannerView.OnQRCodeScannerListener() {
            @Override
            public void onDecodeFinish(String text, PointF[] points) {
                if (!isProcess) {
                    isProcess = true;
                    judgeResult(text, points);
                }

            }
        });
        //相机权限监听
        scanner.setOnCheckCameraPermissionListener(new QRCodeScannerView.OnCheckCameraPermissionListener() {
            @Override
            public boolean onCheckCameraPermission() {
                if (ContextCompat.checkSelfPermission(ScanActivity.this, Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED) {
                    return true;
                } else {
                    ActivityCompat.requestPermissions(ScanActivity.this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
                    return false;
                }
            }
        });
        //开启后置摄像头
        scanner.setBackCamera();
    }

    /**
     * 权限请求回调
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != PERMISSION_REQUEST_CAMERA) {
            return;
        }

        if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            scanner.grantCameraPermission();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        scanner.stopCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        scanner.startCamera();
    }

    private void judgeResult(String result, PointF[] points) {
//        UIUtils.showToast(ScanActivity.this, result);
        if(TextUtils.isEmpty(result)) {
            Toast.makeText(ScanActivity.this, "未扫描到二维码，请重试", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if(result.startsWith("elastossdk_")) {
            String friendAddr = result.substring(11);
//            Toast.makeText(ScanActivity.this, friendAddr, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ScanActivity.this, AddFriendActivity.class);
            intent.putExtra("address", friendAddr);
            startActivity(intent);
            finish();
            return;
        }

    }


}
