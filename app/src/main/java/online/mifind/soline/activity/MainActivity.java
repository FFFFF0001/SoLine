package online.mifind.soline.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.vise.basebluetooth.utils.BluetoothUtil;
import com.vise.common_base.manager.AppManager;
import com.vise.common_base.utils.ToastUtil;
import com.vise.common_utils.utils.character.DateTime;
import com.vise.common_utils.utils.view.ActivityUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import online.mifind.soline.R;
import online.mifind.soline.adapter.GroupFriendAdapter;
import online.mifind.soline.common.AppConstant;
import online.mifind.soline.mode.FriendInfo;
import online.mifind.soline.mode.GroupInfo;
import online.mifind.soline.views.ExpandableButtonMenu;
import online.mifind.soline.views.ExpandableMenuOverlay;

public class MainActivity extends online.mifind.soline.activity.BaseChatActivity {

    private ExpandableListView mGroupFriendLv;
    private GroupFriendAdapter mGroupFriendAdapter;
    private List<GroupInfo> mGroupFriendListData = new ArrayList<>();
    private TextView mTxTitle;
    private ExpandableMenuOverlay mMenuBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void initWidget() {
        mGroupFriendLv = (ExpandableListView) findViewById(R.id.friend_group_list);
        mMenuBtn = (ExpandableMenuOverlay) findViewById(R.id.button_menu);
        mMenuBtn.setOnMenuButtonClickListener(new ExpandableButtonMenu.OnMenuButtonClick() {
            @Override
            public void onClick(ExpandableButtonMenu menu, ExpandableButtonMenu.MenuButton action) {
                switch (action) {
                    case MID:

                        break;
                    case LEFT:
                        ToastUtil.showToast(mContext, "");
                        break;
                    case RIGHT:
                        ActivityUtil.startForwardActivity(MainActivity.this, AddFriendActivity.class);
                        break;
                }
            }
        });
    }

    @Override
    protected void initData() {
        mGroupFriendAdapter = new GroupFriendAdapter(mContext, mGroupFriendListData);
        mGroupFriendLv.setAdapter(mGroupFriendAdapter);
        mGroupFriendLv.expandGroup(0);
        //监测GPS是否打开
        if (!BluetoothUtil.isGpsOpen(mContext)) {
            BluetoothUtil.openGps((Activity) mContext, 1);
        }

        if (BluetoothUtil.isSupportBle(mContext)) {
            BluetoothUtil.enableBluetooth((Activity) mContext, 1);
        } else {
            ToastUtil.showToast(mContext, getString(R.string.phone_not_support_bluetooth));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // 关闭App
//                    AppManager.getAppManager().appExit(mContext);
                }
            }, 3000);
        }
    }

    @Override
    protected void initEvent() {
        mGroupFriendLv.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                FriendInfo friendInfo = mGroupFriendListData.get(groupPosition).getFriendList().get(childPosition);
                Bundle bundle = new Bundle();
                bundle.putParcelable(AppConstant.FRIEND_INFO, friendInfo);
                ActivityUtil.startForwardActivity(MainActivity.this, ChatActivity.class, bundle, false);
                return true;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                findDevice();
            } else {
                AppManager.getAppManager().appExit(mContext);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onRestart() {
        //监测GPS是否打开
        if (!BluetoothUtil.isGpsOpen(mContext)) {
            BluetoothUtil.openGps((Activity) mContext, 1);
        }

        if (BluetoothUtil.isSupportBle(mContext)) {
            BluetoothUtil.enableBluetooth((Activity) mContext, 1);
        } else {
            ToastUtil.showToast(mContext, getString(R.string.phone_not_support_bluetooth));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    AppManager.getAppManager().appExit(mContext);
                }
            }, 3000);
        }
        super.onRestart();
    }

    private void findDevice() {
        // 获得已经保存的配对设备
        Set<BluetoothDevice> pairedDevices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
        if (pairedDevices.size() > 0) {
            mGroupFriendListData.clear();
            GroupInfo groupInfo = new GroupInfo();
            groupInfo.setGroupName(BluetoothAdapter.getDefaultAdapter().getName());
            List<FriendInfo> friendInfoList = new ArrayList<>();
            for (BluetoothDevice device : pairedDevices) {
                FriendInfo friendInfo = new FriendInfo();
                friendInfo.setIdentificationName(device.getName());
                friendInfo.setDeviceAddress(device.getAddress());
                friendInfo.setFriendNickName(device.getName());
//                friendInfo.setOnline(true);
                friendInfo.setJoinTime(DateTime.getStringByFormat(new Date(), DateTime.DEFYMDHMS));
                friendInfo.setBluetoothDevice(device);
                friendInfoList.add(friendInfo);
            }
            groupInfo.setFriendList(friendInfoList);
            groupInfo.setOnlineNumber(0);
            mGroupFriendListData.add(groupInfo);
            mGroupFriendAdapter.setGroupInfoList(mGroupFriendListData);
        }
    }

}
