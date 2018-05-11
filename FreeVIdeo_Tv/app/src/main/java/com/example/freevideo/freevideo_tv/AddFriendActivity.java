package com.example.freevideo.freevideo_tv;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.freevideo.freevideo_tv.elastoshelper.ElastosUtils;

/**
 * Created by PC on 2018/5/9.
 */

public class AddFriendActivity extends AppCompatActivity {

    private static final String TAG = "AddFriendActivity";

    TextView tv_friendId;
    Button btn_addFriend;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    protected void initView() {
        setContentView(R.layout.activity_scan_addfriend);
        tv_friendId = findViewById(R.id.tv_friendid);
        if(getIntent()==null) return;
        final String friendAddr = getIntent().getStringExtra("address");
        if(TextUtils.isEmpty(friendAddr)) {
            tv_friendId.setText("");
        }
        final String friendId = ElastosUtils.carrierInst.getIdFromAddress(friendAddr);
        tv_friendId.setText(friendId);

        btn_addFriend = findViewById(R.id.btn_addFriend);
        btn_addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Log.i(TAG, "start add Frind");
                    if (ElastosUtils.carrierInst.isFriend(friendId)) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(AddFriendActivity.this, "设备已绑定", Toast.LENGTH_SHORT).show();
//                                try{
//                                    ElastosUtils.carrierInst.sendFriendMessage(friendId, "http://gslbmgspvod.miguvideo.com/depository_yf/asset/zhengshi/5102/024/589/5102024589/media/5102024589_5002929750_56.mp4.m3u8?msisdn=13905182515&mdspid=&spid=699051&netType=4&sid=5500481076&pid=2028597139&timestamp=20180509184311&Channel_ID=24000110-99000-100300010010001&ProgramID=640049013&ParentNodeID=-99&assertID=5500481076&client_ip=221.181.101.37&SecurityKey=20180509184311&imei=355905074585739&promotionId=&mvid=5102024589&mcid=1001&mpid=5102016211&encrypt=b6a03cc537d6bcba451fbbe28d45c0c1");
//                                }catch (Exception e) {
//                                    e.printStackTrace();
//                                }
                            }
                        });
                    } else {
                        ElastosUtils.carrierInst.addFriend(friendAddr, "auto-accepted");
                        ElastosUtils.carrierInst.getFriend(friendId).setLabel(friendId);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(AddFriendActivity.this, "设备绑定成功", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    Log.i(TAG, "end add Frind");
                    btn_addFriend.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 2000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
            this.finish();
    }

}
