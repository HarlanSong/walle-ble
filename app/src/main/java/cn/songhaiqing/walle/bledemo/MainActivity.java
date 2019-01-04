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
import android.widget.Toast;
import cn.songhaiqing.walle.ble.activity.DeviceScanActivity;
import cn.songhaiqing.walle.ble.service.WalleBleService;
import cn.songhaiqing.walle.ble.utils.BleUtil;
import cn.songhaiqing.walle.ble.utils.StringUtil;
import cn.songhaiqing.walle.ble.utils.WalleBleConfig;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = getClass().getName();
    private final int REQUEST_BIND_DEVICE = 1;

    private Button btnDisconnect;
    private Button btnScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WalleBleService.ACTION_CONNECTED_SUCCESS);
        intentFilter.addAction(WalleBleService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(WalleBleService.ACTION_DEVICE_RESULT);
        registerReceiver(bleReceiver, intentFilter);

        btnDisconnect = findViewById(R.id.btn_disconnect);
        btnScan = findViewById(R.id.btn_scan);
        btnDisconnect.setOnClickListener(this);
        btnScan.setOnClickListener(this);

        WalleBleConfig.setDebug(true);
        autoConnection();

    }

    private void autoConnection() {
        //String address = "C8:41:A5:F6:E8:74";
        String address = "";
        if (TextUtils.isEmpty(address)) {
            return;
        }
        BleUtil.connectDevice(this, address);
    }

    private BroadcastReceiver bleReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, action);
            if (WalleBleService.ACTION_CONNECTED_SUCCESS.equals(action)) {

            } else if (WalleBleService.ACTION_GATT_DISCONNECTED.equals(action)) {

            } else if (WalleBleService.ACTION_DEVICE_RESULT.equals(action)) {

            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && REQUEST_BIND_DEVICE == requestCode) {
            String name = data.getStringExtra("name");
            String macAddress = data.getStringExtra("macAddress");
            Toast.makeText(this, "name:" + name + " macAddress:" + macAddress, Toast.LENGTH_LONG).show();
            BleUtil.connectDevice(this, macAddress);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(bleReceiver);
    }

    @Override
    public void onClick(View view) {
        final int id = view.getId();
        switch (id) {
            case R.id.btn_scan:
                Intent intent = new Intent(this, DeviceScanActivity.class);
                startActivityForResult(intent, REQUEST_BIND_DEVICE);
                break;
            case R.id.btn_disconnect:
                BleUtil.disConnect(this);
                break;
        }
    }

    /**
     * 发送命令，示例内容根据实际情况修改测试，支持消息队列。
     *
     * @param v
     */
    public void onSendCmd(View v) {
        final String AT_SERVICE_UUID = "0000ffa0-0000-1000-8000-00805f9b34fb";
        final String AT_CHARACTERISTIC_WRITE_UUID = "0000ffa1-0000-1000-8000-00805f9b34fb";
        final String AT_CHARACTERISTIC_NOTIFY_UUID = "0000ffa2-0000-1000-8000-00805f9b34fb";
        byte[] bytes1 = StringUtil.hexToBytes("41 54 2b 42 54 53 3f 00");
        byte[] bytes2 = StringUtil.hexToBytes("41 54 2b 42 54 53 3f 00");
        BleUtil.broadcastWriteBle(this, AT_SERVICE_UUID, AT_CHARACTERISTIC_NOTIFY_UUID, AT_SERVICE_UUID, AT_CHARACTERISTIC_WRITE_UUID, bytes1);
        BleUtil.broadcastWriteBle(this, AT_SERVICE_UUID, AT_CHARACTERISTIC_NOTIFY_UUID, AT_SERVICE_UUID, AT_CHARACTERISTIC_WRITE_UUID, bytes2);

        final String SERVICE_UUID = "0000ffb0-0000-1000-8000-00805f9b34fb";
        final String CHARACTERISTIC_WRITE_UUID = "0000ffb1-0000-1000-8000-00805f9b34fb";
        final String CHARACTERISTIC_NOTIFY_UUID = "0000ffb2-0000-1000-8000-00805f9b34fb";
        byte[] bytes3 = StringUtil.hexToBytes("ff ff 4e 42 01 00 01 06 00 00 00 00 00 00 00 00 00 00 fd 69");
        byte[] bytes4 = StringUtil.hexToBytes("ff ff 4e 42 01 00 01 06 00 00 01 01 00 00 00 00 00 00 fd 67");
        byte[] bytes5 = StringUtil.hexToBytes("ff ff 4e 42 01 00 01 06 00 00 02 02 00 00 00 00 00 00 fd 65");
        byte[] bytes6 = StringUtil.hexToBytes("ff ff 4e 42 01 00 01 06 00 00 03 03 00 00 00 00 00 00 fd 63");
        byte[] bytes7 = StringUtil.hexToBytes("ff ff 4e 42 01 00 01 06 00 00 04 04 00 00 00 00 00 00 fd 61");
        byte[] bytes8 = StringUtil.hexToBytes("ff ff 4e 42 01 00 01 06 00 00 06 06 00 00 00 00 00 00 fd 5d");
        byte[] bytes9 = StringUtil.hexToBytes("ff ff 4e 42 01 00 01 06 00 00 07 07 00 00 00 00 00 00 fd 5b");
        BleUtil.broadcastWriteBle(this, SERVICE_UUID, CHARACTERISTIC_NOTIFY_UUID, SERVICE_UUID, CHARACTERISTIC_WRITE_UUID, bytes3);
        BleUtil.broadcastWriteBle(this, SERVICE_UUID, CHARACTERISTIC_NOTIFY_UUID, SERVICE_UUID, CHARACTERISTIC_WRITE_UUID, bytes4);
        BleUtil.broadcastWriteBle(this, SERVICE_UUID, CHARACTERISTIC_NOTIFY_UUID, SERVICE_UUID, CHARACTERISTIC_WRITE_UUID, bytes5);
        BleUtil.broadcastWriteBle(this, SERVICE_UUID, CHARACTERISTIC_NOTIFY_UUID, SERVICE_UUID, CHARACTERISTIC_WRITE_UUID, bytes6);
        BleUtil.broadcastWriteBle(this, SERVICE_UUID, CHARACTERISTIC_NOTIFY_UUID, SERVICE_UUID, CHARACTERISTIC_WRITE_UUID, bytes7);
        BleUtil.broadcastWriteBle(this, SERVICE_UUID, CHARACTERISTIC_NOTIFY_UUID, SERVICE_UUID, CHARACTERISTIC_WRITE_UUID, bytes8);
        BleUtil.broadcastWriteBle(this, SERVICE_UUID, CHARACTERISTIC_NOTIFY_UUID, SERVICE_UUID, CHARACTERISTIC_WRITE_UUID, bytes9);
    }
}
