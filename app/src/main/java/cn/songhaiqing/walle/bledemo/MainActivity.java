package cn.songhaiqing.walle.bledemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import cn.songhaiqing.walle.ble.activity.DeviceScanActivity;
import cn.songhaiqing.walle.ble.parse.CareeachNB202Parse;
import cn.songhaiqing.walle.ble.service.WalleBleService;
import cn.songhaiqing.walle.ble.utils.BleUtil;
import cn.songhaiqing.walle.ble.utils.LogUtil;
import cn.songhaiqing.walle.ble.utils.StringUtil;
import cn.songhaiqing.walle.ble.utils.WalleBleConfig;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = getClass().getName();
    private final int REQUEST_BIND_DEVICE = 1;

    private Button btnDisconnect;
    private Button btnScan;
    private TextView tvInfo;
    private TextView tvLog;

    private String bleName;
    private String bleAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnDisconnect = findViewById(R.id.btn_disconnect);
        btnScan = findViewById(R.id.btn_scan);
        tvInfo = findViewById(R.id.tv_info);
        tvLog = findViewById(R.id.tv_log);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WalleBleService.ACTION_CONNECTED_SUCCESS);
        intentFilter.addAction(WalleBleService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(WalleBleService.ACTION_DEVICE_RESULT);
        intentFilter.addAction(WalleBleService.ACTION_SERVICES_DISCOVERED_DONE);
        registerReceiver(bleReceiver, intentFilter);

        btnDisconnect.setOnClickListener(this);
        btnScan.setOnClickListener(this);

        WalleBleConfig.setDebug(true);
        //autoConnection();

        loadStatusInfo();

    }

    private CareeachNB202Parse.CareeachResult  careeachResult = new CareeachNB202Parse.CareeachResult() {
        @Override
        public void battery(int battery) {

        }

        @Override
        public void firmwareVersion(int versionCode, String versionName) {

        }

        @Override
        public void measurementHR(int heartRate) {

        }

        @Override
        public void findPhone(boolean start) {

        }

        @Override
        public void dataNow(int step, int calorie, int distance, int shallowSleepHour, int shallowSleepMinute, int deepSleepHour, int deepSleepMinute, int wakeUpNumber) {

        }

        @Override
        public void departedHeartRate(String time, int heartRate) {

        }

        @Override
        public void departedSleep(String time, int type, int sleepTime) {

        }

        @Override
        public void departedSport(String time, int step, int calorie, int distance) {

        }
    };

    private void loadStatusInfo() {
        int status = BleUtil.getConnectStatus(this);
        String info;
        if (status == BleUtil.CONNECT_STATUS_SUCCESS) {
            info = bleName + "(" + bleAddress + ") " + getString(R.string.device_status_connected);
        } else if (status == BleUtil.CONNECT_STATUS_CONNECTING) {
            info = bleName + "(" + bleAddress + ") " + getString(R.string.device_status_connecting);
        } else if (status == BleUtil.CONNECT_STATUS_FAIL) {
            info = bleName + "(" + bleAddress + ") " + getString(R.string.device_status_connection_fail);
        } else {
            info = getString(R.string.device_not_connected);
        }
        tvInfo.setText(info);
    }

   /* private void autoConnection() {
        String address = "C8:41:A5:F6:E8:74";
        if (TextUtils.isEmpty(address)) {
            return;
        }
        BleUtil.connectDevice(this, address);
    }*/

    private BroadcastReceiver bleReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, action);
            if (WalleBleService.ACTION_SERVICES_DISCOVERED_DONE.equals(action)) {
                addLog("连接成功");
                loadStatusInfo();

             /*   final String AT_SERVICE_UUID = "0000ffa0-0000-1000-8000-00805f9b34fb";
                final String AT_CHARACTERISTIC_WRITE_UUID = "0000ffa1-0000-1000-8000-00805f9b34fb";
                final String AT_CHARACTERISTIC_NOTIFY_UUID = "0000ffa2-0000-1000-8000-00805f9b34fb";
                byte[] bytes1 = StringUtil.hexToBytes("41 54 2b 42 54 53 3f 00");
                byte[] bytes2 = StringUtil.hexToBytes("41 54 2b 47 53 56 3f 00");
                BleUtil.broadcastWriteBle(getBaseContext(), AT_SERVICE_UUID, AT_CHARACTERISTIC_NOTIFY_UUID, AT_SERVICE_UUID, AT_CHARACTERISTIC_WRITE_UUID, bytes1);
                BleUtil.broadcastWriteBle(getBaseContext(), AT_SERVICE_UUID, AT_CHARACTERISTIC_NOTIFY_UUID, AT_SERVICE_UUID, AT_CHARACTERISTIC_WRITE_UUID, bytes2);
*/
            } else if (WalleBleService.ACTION_GATT_DISCONNECTED.equals(action)) {
                loadStatusInfo();
                addLog("断开连接");
            } else if (WalleBleService.ACTION_DEVICE_RESULT.equals(action)) {
                // 特征值UUID
                String uuid = intent.getStringExtra("uuid");
                // 十进制内容
                ArrayList<Integer> dataArray = intent.getIntegerArrayListExtra("data");
                // byte原内容
                byte[] srcData = intent.getByteArrayExtra("srcData");
                String hex = StringUtil.bytesToHexStr(srcData);
                LogUtil.d(TAG, "十六进制结果：" + hex);
                addLog(hex);
            }
        }
    };

    private void addLog(String content) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        tvLog.append("\n" + sdf.format(new Date()) + " " + content);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && REQUEST_BIND_DEVICE == requestCode) {
            bleName = data.getStringExtra("name");
            bleAddress = data.getStringExtra("macAddress");
            addLog("开始连接设备 " + bleName + "(" + bleAddress + ")");
            BleUtil.connectDevice(this, bleAddress, true);
            loadStatusInfo();
        }
    }

    @Override
    protected void onDestroy() {
        BleUtil.disConnect(this);
        unregisterReceiver(bleReceiver);
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        final int id = view.getId();
        switch (id) {
            case R.id.btn_scan:
                Intent intent = new Intent(this, DeviceScanActivity.class);
                intent.putExtra("showSignalStrength", false);
                startActivityForResult(intent, REQUEST_BIND_DEVICE);
                break;
            case R.id.btn_disconnect:
                BleUtil.disConnect(this);
                break;
        }
    }

    public void onClear(View v) {
        tvLog.setText("");
    }

    /**
     * 发送命令，示例内容根据实际情况修改测试，支持消息队列。
     *
     * @param v
     */
    public void onSendCmd(View v) {

    }


}
