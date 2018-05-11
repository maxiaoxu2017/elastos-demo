package com.example.freevideo.freevideo_mobile;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.freevideo.freevideo_mobile.elastoshelper.ElastosUtils;

import org.elastos.carrier.ConnectionStatus;
import org.elastos.carrier.FriendInfo;
import org.elastos.carrier.Log;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "MainActivity";

    private VideoView mVideoView;
    private FrameLayout mFrameLayout;
    private RelativeLayout mRelativeLayout;
    private RelativeLayout mRelativeLayout_video;
    private MediaController mMediaController;

    private Button btn_play;
    private Button btn_tv;
    private Button btn_bind;
    private int flag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mVideoView = findViewById(R.id.main_vv);
        mFrameLayout = findViewById(R.id.main_fl);
        mFrameLayout.setVisibility(View.VISIBLE);
        mFrameLayout.bringToFront();
        mRelativeLayout = findViewById(R.id.main_rl);
        mRelativeLayout_video = findViewById(R.id.main_rl_video);
        btn_play = findViewById(R.id.btn_playstatus);
        btn_play.setOnClickListener(this);
        btn_tv = findViewById(R.id.btn_tv);
        btn_tv.setOnClickListener(this);
        btn_bind = findViewById(R.id.btn_bind);
        btn_bind.setOnClickListener(this);

        if(!ElastosUtils.isInit) {
            ElastosUtils.init(this);
        }

//        //test only
        String url = "http://live.hkstv.hk.lxdns.com/live/hks/playlist.m3u8";
        mVideoView.setVideoURI(Uri.parse(url));
        ElastosUtils.playurl = url;
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
            }
        });
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                    @Override
                    public boolean onInfo(MediaPlayer mp, int what, int extra) {
                        if(what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {//第一帧
                            mFrameLayout.setVisibility(View.GONE);
                            btn_play.setBackgroundResource(R.drawable.player_pause_ic);
                            flag = 1;
                        }
                        return true;
                    }
                });


            }
        });
        mRelativeLayout_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int visible = mRelativeLayout.getVisibility()==View.GONE ? View.VISIBLE : View.GONE;
                mRelativeLayout.setVisibility(visible);
                if(visible == View.VISIBLE) {
                    mRelativeLayout.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mRelativeLayout.setVisibility(View.GONE);
                        }
                    }, 5000);
                }
            }
        });

        mVideoView.start();
    }


    //elastos sdk
    private void showSelectTvDevice() {
        StringBuffer sb = new StringBuffer("");

        try{
            List<FriendInfo> infoList = ElastosUtils.carrierInst.getFriends();
            for(FriendInfo friendInfo : infoList) {
                if(friendInfo.getConnectionStatus() == ConnectionStatus.Connected) {
                    if(sb.length() > 0) {
                        sb.append("##");
                    }
                    sb.append(friendInfo.getUserId());
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

        int breakpoint = 0;
        if(mVideoView != null) {
            breakpoint = mVideoView.getCurrentPosition();
        }
        Log.i("abcdefg", "breakpoint =" + breakpoint);
        String item = sb.toString().trim();
        Log.i("abcdefg", "item =" + item);
        final String[] items = sb.toString().trim().split("##");
        final int videobreakpoint = breakpoint;
        if(items.length > 0 && !TextUtils.isEmpty(item)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("选择投屏设备")
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            mVideoView.start();
                            btn_play.setBackgroundResource(R.drawable.player_pause_ic);
//                        }
                        }
                    })
                    .setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            final int index = i;
                            MainActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
//                                UiUtil.showMessage("click " + index);
                                    try {
                                        ElastosUtils.carrierInst.sendFriendMessage(items[index], ElastosUtils.playurl + "###" + videobreakpoint);
//                                        dialog.dismiss();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }
                            });
                        }
                    })
                    .show();
        }else {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("选择投屏设备")
                    .setMessage("没有找到在线设备,您可以重新尝试投屏或者先绑定设备")
                    .setNegativeButton("取消", null)
                    .setPositiveButton("扫码绑定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(MainActivity.this, ScanActivity.class);
                            startActivity(intent);
                        }
                    }).show();

        }

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btn_playstatus:
                if(flag == 1) {
                    btn_play.setBackgroundResource(R.drawable.player_play_ic);
                    mVideoView.pause();
                }else{
                    btn_play.setBackgroundResource(R.drawable.player_pause_ic);
                    mVideoView.start();
                }
                flag = 1-flag;
                break;
            case R.id.btn_tv:
                btn_play.setBackgroundResource(R.drawable.player_play_ic);
                mVideoView.pause();
                showSelectTvDevice();
                break;
            case R.id.btn_bind:
                mVideoView.pause();
                Intent intent = new Intent(MainActivity.this, ScanActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mVideoView != null) {
            mVideoView.start();
        }
        mFrameLayout.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mVideoView != null) {
            mVideoView.pause();
        }
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
