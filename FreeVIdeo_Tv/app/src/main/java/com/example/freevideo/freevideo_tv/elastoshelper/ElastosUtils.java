package com.example.freevideo.freevideo_tv.elastoshelper;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.example.freevideo.freevideo_tv.MainActivity2;
import com.example.freevideo.freevideo_tv.PlayerActivity;

import org.elastos.carrier.AbstractCarrierHandler;
import org.elastos.carrier.Carrier;
import org.elastos.carrier.ConnectionStatus;
import org.elastos.carrier.FriendInfo;
import org.elastos.carrier.UserInfo;

import java.io.File;
import java.util.List;

/**
 * Created by PC on 2018/5/9.
 */

public class ElastosUtils {

    private static final String TAG = "ElastosUtils";

    public static Carrier carrierInst = null;
    public static String carrierAddr = null;
    public static String carrierUserID = null;
    public static TestHandler carrierHandler = null;
    public static boolean isInit = false;
    public static String playurl = "";
    public static Context mContext = null;

    public static void init(Context context) {
        isInit = true;
        mContext = context;
        TestOptions options = new TestOptions(getAppPath(context));
        if(carrierHandler == null) {
            carrierHandler = new TestHandler();
        }

        //1.初始化实例，获得相关信息
        try {
            //1.1获得Carrier的实例
            ElastosUtils.carrierInst = Carrier.getInstance(options, carrierHandler);

            //1.2获得Carrier的地址
            ElastosUtils.carrierAddr = ElastosUtils.carrierInst.getAddress();
            Log.i(TAG,"address: " + ElastosUtils.carrierAddr);

            //1.3获得Carrier的用户ID
            ElastosUtils.carrierUserID = ElastosUtils.carrierInst.getUserId();
            Log.i(TAG,"userID: " + ElastosUtils.carrierUserID);

            //1.4启动网络
            ElastosUtils.carrierInst.start(1000);
//            handler.synch.await();
            Log.i(TAG,"carrier client is ready now");


//            //for test only , remove all friend
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        Thread.sleep(5000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//
//                    try {
//                        List<FriendInfo> list = carrierInst.getFriends();
//                        for(FriendInfo friendInfo : list) {
//                            carrierInst.removeFriend(friendInfo.getUserId());
//                        }
//                    } catch (org.elastos.carrier.exceptions.ElastosException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }).start();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static String getAppPath(Context context) {
        File file=context.getFilesDir();
        String path=file.getAbsolutePath();
        return path;
    }

    public static class TestHandler extends AbstractCarrierHandler {
        Synchronizer synch = new Synchronizer();
        String from;
        ConnectionStatus friendStatus;
        String CALLBACK="call back";

        public void onReady(Carrier carrier) {
//            synch.wakeup();
        }

        public void onFriendConnection(Carrier carrier, String friendId, ConnectionStatus status) {
            Log.i(TAG, "onFriendConnection: ");
            Log.i(TAG,"friendid:" + friendId + ",connection changed to: " + status);
            from = friendId;
            friendStatus = status;
//            if (friendStatus == ConnectionStatus.Connected)
//                synch.wakeup();
        }

        //2.2 通过好友验证
        public void onFriendRequest(Carrier carrier, String userId, UserInfo info, String hello) {
            Log.i(TAG, "onFriendRequest: ");
            try {

                if (hello.equals("auto-accepted")) {
                    carrier.AcceptFriend(userId);
                }
                MainActivity2.instance.mHandler.sendEmptyMessage(100);
            } catch (Exception e) {
                if(e instanceof ElastosException) {
                    Log.e(TAG, "onFriendRequest: ElastosException" + ((ElastosException) e).getErrorCode() + e.getMessage());
                }
                e.printStackTrace();
            }
        }

        @Override
        //3.2 接受好友信息
        public void onFriendMessage(Carrier carrier,String fromId, String message) {
            Log.i(TAG, "onFriendMessage: ");
            Log.i(TAG,"address:" + fromId + ",connection changed to: " + message);

            if(TextUtils.isEmpty(message) || !message.startsWith("http")) {
                return;
            }

            String[] data = message.split("###");

            Intent intent = new Intent();
            intent.setClass(mContext, PlayerActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra("playurl", data[0]);
            /**直播没有断点*/
//            intent.putExtra("breakpoint", data[1]);
            mContext.startActivity(intent);

        }

    }
}
