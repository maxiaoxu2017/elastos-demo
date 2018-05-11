package com.example.freevideo.freevideo_mobile;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.MediaController;
import android.widget.VideoView;

import org.elastos.carrier.Log;

public class PlayerActivity extends Activity {

    private VideoView mVideoView;
    private FrameLayout mFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_player);
        mVideoView = (VideoView)findViewById(R.id.videoview);
        mFrameLayout = (FrameLayout)findViewById(R.id.fl_cover);

        String url = getIntent().getStringExtra("playurl");
        if(TextUtils.isEmpty(url)) {
            finish();
        }
        Log.i("PlayerActivity", "url..." + url);
        String breakpoint = getIntent().getStringExtra("breakpoint");
        final int breakvalue = Integer.valueOf(breakpoint);
//        mVideoView.setVideoURI(Uri.parse("http://newtv.a030.ottcn.com/depository_yf/asset/zhengshi/5102/005/073/5102005073/media/5102005073_5002727199_92.mp4.m3u8?msisdn=3132209543933&mdspid=&spid=600266&netType=4&sid=5500454871&pid=2028595851&timestamp=20180510051744&Channel_ID=0116_22080304-99000-102200000000004&promotionId=&mvid=&mcid=&mpid=&ProgramID=639394378&ParentNodeID=-99&cc=639394371&preview=1&playseek=000000-000600&client_ip=183.232.228.217&assertID=5500454871&imei=71c306a414c369cf6c53c481a5a88dbb83cbee4ab1b7249b48d5dfcc4543a539&chargePhone=&SecurityKey=20180510051744&encrypt=7ca4776e65487748747d11787c9e956b&jid=37e9e6387a4cdb0dfac013eb655ae686"));
        mVideoView.setVideoURI(Uri.parse(url));
        mVideoView.setMediaController(new MediaController(this));
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                PlayerActivity.this.finish();
            }
        });
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                    @Override
                    public boolean onInfo(MediaPlayer mp, int what, int extra) {
                        if(what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {//第一帧
//                            mVideoView.setBackgroundColor(Color.TRANSPARENT);
                            mFrameLayout.setVisibility(View.GONE);
                            mVideoView.seekTo(breakvalue);
                        }
                        return true;
                    }
                });
            }
        });

        mVideoView.start();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        String url = getIntent().getStringExtra("playurl");
        String breakpoint = getIntent().getStringExtra("breakpoint");
        int breakvalue = Integer.valueOf(breakpoint);
        mVideoView.setVideoURI(Uri.parse(url));
        mVideoView.seekTo(breakvalue);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mVideoView != null) {
            mVideoView.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mVideoView != null) {
            mVideoView.pause();
        }
        PlayerActivity.this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
