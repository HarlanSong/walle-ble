package cn.songhaiqing.walle.ble.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import cn.songhaiqing.walle.ble.utils.BleUtil;
import cn.songhaiqing.walle.ble.utils.LogUtil;
import cn.songhaiqing.walle.ble.utils.StringUtil;
import cn.songhaiqing.walle.ble.utils.WalleBleConfig;

public class WalleBleService extends Service {
    private final String TAG = getClass().getName();

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothLeScanner bluetoothLeScanner;
    private int mConnectionState = STATE_DISCONNECTED;

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    public static final String ACTION_READ_BLE = "cn.songhaiqing.walle.ble.ACTION_READ_BLE";
    public static final String ACTION_WRITE_BLE = "cn.songhaiqing.walle.ble.ACTION_WRITE_BLE";
    public static final String ACTION_CONNECT_DEVICE = "cn.songhaiqing.walle.ble.ACTION_CONNECT_DEVICE";
    public final static String ACTION_START_SCAN = "cn.songhaiqing.walle.ble.ACTION_START_SCAN"; // 开始扫描设备
    public final static String ACTION_STOP_SCAN = "cn.songhaiqing.walle.ble.ACTION_STOP_SCAN"; // 结束扫描设备

    public static final String ACTION_DISCONNECT_DEVICE = "cn.songhaiqing.walle.ble.ACTION_DISCONNECT_DEVICE";
    public final static String ACTION_GATT_DISCONNECTED = "cn.songhaiqing.walle.ble.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED = "cn.songhaiqing.walle.ble.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_CONNECTED_SUCCESS = "cn.songhaiqing.walle.ble.ACTION_CONNECTED_SUCCESS";
    public static final String ACTION_CONNECT_FAIL = "cn.songhaiqing.walle.ble.ACTION_CONNECT_FAIL";
    public static final String ACTION_RECONNECTION = "cn.songhaiqing.walle.ble.ACTION_RECONNECTION";
    public final static String ACTION_EXECUTED_SUCCESSFULLY = "cn.songhaiqing.walle.ble.ACTION_EXECUTED_SUCCESSFULLY";
    public final static String ACTION_EXECUTED_FAILED = "cn.songhaiqing.walle.ble.ACTION_EXECUTED_FAILED";
    public final static String ACTION_DEVICE_RESULT = "cn.songhaiqing.walle.ble.ACTION_DEVICE_RESULT";
    public final static String ACTION_SCAN_RESULT = "cn.songhaiqing.walle.ble.ACTION_SCAN_RESULT"; // 搜索设备新结果
    public final static String ACTION_SCAN_TIMEOUT = "cn.songhaiqing.walle.ble.ACTION_SCAN_TIMEOUT"; // 搜索设备超时并结束
    public final static String ACTION_BLUETOOTH_NOT_OPEN = "cn.songhaiqing.walle.ble.ACTION_BLUETOOTH_NOT_OPEN"; // 蓝牙未开启

    public final static String EXTRA_DATA = "EXTRA_DATA";
    public final static String EXTRA_DATA_NOTIFY_SERVICE_UUID = "EXTRA_DATA_NOTIFY_SERVICE_UUID";
    public final static String EXTRA_DATA_NOTIFY_CHARACTERISTIC_UUID = "EXTRA_DATA_NOTIFY_CHARACTERISTIC_UUID";
    public final static String EXTRA_DATA_WRITE_SERVICE_UUID = "EXTRA_DATA_WRITE_SERVICE_UUID";
    public final static String EXTRA_DATA_WRITE_CHARACTERISTIC_UUID = "EXTRA_DATA_WRITE_CHARACTERISTIC_UUID";
    public final static String EXTRA_DATA_WRITE_SEGMENTATION = "EXTRA_DATA_WRITE_SEGMENTATION";
    public final static String EXTRA_DATA_READ_SERVICE_UUID = "EXTRA_DATA_READ_SERVICE_UUID";
    public final static String EXTRA_DATA_READ_CHARACTERISTIC_UUID = "EXTRA_DATA_READ_CHARACTERISTIC_UUID";

    private BluetoothGattCharacteristic notifyBluetoothGattCharacteristic;
    private Handler handler;

    private boolean operationDone = true;
    private boolean artificialDisconnect = true;
    private final int maxReconnectionNumber = 3;
    private int reconnectionNumber = 0;
    private final int maxLength = 20;
    private long connectTimeTag;
    private Runnable reconnectionRunnable;
    private Map<String, BluetoothDevice> deviceMap;

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.d(TAG, "onCreate");
        handler = new Handler();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_READ_BLE);
        intentFilter.addAction(ACTION_WRITE_BLE);
        intentFilter.addAction(ACTION_CONNECT_DEVICE);
        intentFilter.addAction(ACTION_DISCONNECT_DEVICE);
        intentFilter.addAction(ACTION_START_SCAN);
        intentFilter.addAction(ACTION_STOP_SCAN);
        registerReceiver(broadcastReceiver, intentFilter);

        deviceMap = new HashMap<>();
        initialize();
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            LogUtil.d(TAG, "BroadcastReceiver Action:" + action);
            if (ACTION_DISCONNECT_DEVICE.equals(action)) {
                disconnect();
            } else if (ACTION_CONNECT_DEVICE.equals(action)) {
                String address = intent.getStringExtra(EXTRA_DATA);
                reconnectionNumber = 0;
                connectTimeTag = System.currentTimeMillis();
                connect(address);
            } else if (ACTION_READ_BLE.equals(action)) {
                String serviceUUID = intent.getStringExtra(EXTRA_DATA_READ_SERVICE_UUID);
                String characteristicUUID = intent.getStringExtra(EXTRA_DATA_READ_CHARACTERISTIC_UUID);
                readBluetooth(serviceUUID, characteristicUUID);
            } else if (ACTION_WRITE_BLE.equals(action)) {
                String notifyServiceUUID = intent.getStringExtra(EXTRA_DATA_NOTIFY_SERVICE_UUID);
                String notifyCharacteristicUUID = intent.getStringExtra(EXTRA_DATA_NOTIFY_CHARACTERISTIC_UUID);
                String writeServiceUUID = intent.getStringExtra(EXTRA_DATA_WRITE_SERVICE_UUID);
                String writeCharacteristicUUID = intent.getStringExtra(EXTRA_DATA_WRITE_CHARACTERISTIC_UUID);
                boolean segmentationContent = intent.getBooleanExtra(EXTRA_DATA_WRITE_SEGMENTATION, true);
                byte[] data = intent.getByteArrayExtra(EXTRA_DATA);
                writeBluetooth(notifyServiceUUID, notifyCharacteristicUUID, writeServiceUUID, writeCharacteristicUUID, data, segmentationContent);
            } else if (ACTION_START_SCAN.equals(action)) {
                startScan();
            } else if (ACTION_STOP_SCAN.equals(action)) {
                stopScan();
            }
        }
    };

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_CONNECTED_SUCCESS;
                mConnectionState = STATE_CONNECTED;
                String bleName = gatt.getDevice().getName();
                String bleAddress = gatt.getDevice().getAddress();
                BleUtil.bleName = bleName;
                BleUtil.bleAddress = bleName;
                LogUtil.i(TAG, "成功连接设备 ,设备名称:" + bleName + " MAC地址:" + bleAddress);
                LogUtil.d(TAG, "Attempting to start service discovery:" + mBluetoothGatt.discoverServices());
                sendBroadcast(new Intent(intentAction));
                BleUtil.setConnectStatus(BleUtil.CONNECT_STATUS_SUCCESS);
                LogUtil.i(TAG, "本次连接耗时：" + (System.currentTimeMillis() - connectTimeTag) + " 毫秒");
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED && status != 133) {
                intentAction = ACTION_GATT_DISCONNECTED;
                mConnectionState = STATE_DISCONNECTED;
                BleUtil.bleName = null;
                BleUtil.bleAddress = null;
                BleUtil.setConnectStatus(BleUtil.CONNECT_STATUS_NOT_CONNECTED);
                LogUtil.i(TAG, "Disconnected from GATT server.");
                broadcastUpdate(intentAction);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            LogUtil.d(TAG, "onServicesDiscovered status:" + status);
            if (status == BluetoothGatt.GATT_SUCCESS && isConnected()) {
                broadcastUpdate(ACTION_CONNECTED_SUCCESS);
            } else {
                LogUtil.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            LogUtil.d(TAG, "onCharacteristicRead Characteristic UUID : " + characteristic.getUuid().toString());
            if (status == BluetoothGatt.GATT_SUCCESS) {
                bluetoothUpdate(characteristic);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            LogUtil.d(TAG, "onCharacteristicChanged Characteristic UUID : " + characteristic.getUuid().toString());
            bluetoothUpdate(characteristic);
        }
    };

    public boolean isConnected() {
        return mConnectionState == STATE_CONNECTED;
    }

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private boolean initialize() {
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                LogUtil.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            LogUtil.w(TAG, "蓝牙不可用");
            return false;
        }
        if (!bluetoothAdapter.isEnabled()) {
            LogUtil.w(TAG, "蓝牙未打开");
            sendBroadcast(new Intent(ACTION_BLUETOOTH_NOT_OPEN));
            return false;
        }

        if (mBluetoothAdapter == null) {
            mBluetoothAdapter = mBluetoothManager.getAdapter();
        }
        if (mBluetoothAdapter == null) {
            LogUtil.e(TAG, "无法获得蓝牙适配器");
            return false;
        }
        bluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        return true;
    }

    private boolean connect(final String address) {
        if (!initialize()) {
            BleUtil.setConnectStatus(BleUtil.CONNECT_STATUS_FAIL);
            return false;
        }
        LogUtil.d(TAG, "开始连接设备MAC地址:" + address);
        artificialDisconnect = false;
        if (mBluetoothAdapter == null || address == null) {
            LogUtil.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            if (!initialize()) {
                return false;
            }
        }
        if (BleUtil.getConnectStatus(getBaseContext()) == BleUtil.CONNECT_STATUS_SUCCESS && address.equals(mBluetoothDeviceAddress)) {
            LogUtil.d(TAG, "当前设备已连接，无需要重复连接");
            return true;
        } else if (isConnected() && !address.equals(mBluetoothDeviceAddress)) {
            disconnect();
            LogUtil.d(TAG, "蓝牙已连接其他设备，正在断开现有连接，并连接新设备。");
        }
        BluetoothDevice device = deviceMap.get(address);
        if (device == null) {
            device = mBluetoothAdapter.getRemoteDevice(address);
        }
        if (device == null) {
            LogUtil.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
        mBluetoothDeviceAddress = address;
        mConnectionState = STATE_CONNECTING;
        checkConnectStatus();
        return true;
    }

    private void checkConnectStatus() {
        if (artificialDisconnect || isConnected()) {
            return;
        }
        reconnectionRunnable = new Runnable() {
            @Override
            public void run() {
                if (!artificialDisconnect && !isConnected() && !TextUtils.isEmpty(mBluetoothDeviceAddress) && reconnectionNumber < maxReconnectionNumber) {
                    reconnectionNumber++;
                    LogUtil.i(TAG, "正在重连，重连次数:" + reconnectionNumber);
                    Intent intent = new Intent(ACTION_CONNECT_FAIL);
                    intent.putExtra("reconnectionNumber", reconnectionNumber);
                    sendBroadcast(intent);
                    connect(mBluetoothDeviceAddress);
                } else if (BleUtil.getConnectStatus(getBaseContext()) != BleUtil.CONNECT_STATUS_SUCCESS) {
                    BleUtil.setConnectStatus(BleUtil.CONNECT_STATUS_FAIL);
                    LogUtil.w(TAG, "连接失败");
                    sendBroadcast(new Intent(ACTION_CONNECT_FAIL));
                }
            }
        };
        handler.postDelayed(reconnectionRunnable, 10000);
    }

    private void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            LogUtil.w(TAG, "BluetoothAdapter not initialized (disconnect)");
            return;
        }
        if (mBluetoothGatt != null) {
            mBluetoothGatt.disconnect();
        }
        artificialDisconnect = true;
        notifyBluetoothGattCharacteristic = null;
    }

    private void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    public void readCharacteristic(final BluetoothGattCharacteristic characteristic, final int retryNumber) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            LogUtil.w(TAG, "BluetoothAdapter not initialized(readCharacteristic)");
            return;
        }
        boolean status = mBluetoothGatt.readCharacteristic(characteristic);
        if (status) {
            LogUtil.d(TAG, "Bluetooth read success");
            if (!operationDone) {
                broadcastUpdate(ACTION_EXECUTED_SUCCESSFULLY);
                operationDone = true;
            }
            return;
        }
        if (WalleBleConfig.getMaxRetryNumber() > 0 && retryNumber <= WalleBleConfig.getMaxRetryNumber()) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    readCharacteristic(characteristic, retryNumber + 1);
                }
            }, WalleBleConfig.getRetrySleepTime());
        } else if (!operationDone) {
            broadcastUpdate(ACTION_EXECUTED_FAILED);
            operationDone = true;
        }
    }

    private void bluetoothUpdate(BluetoothGattCharacteristic characteristic) {
        String uuid = characteristic.getUuid().toString();
        String dataUINT16Str = StringUtil.bytesToHexStr(characteristic.getValue());
        ArrayList<Integer> dataArray = new ArrayList<>(StringUtil.bytesToArrayList(characteristic.getValue()));
        LogUtil.i(TAG, "Result Data:" + dataUINT16Str + " size:" + dataArray.size());
        Intent intent = new Intent(ACTION_DEVICE_RESULT);
        intent.putExtra("uuid", uuid);
        intent.putExtra("data", dataArray);
        intent.putExtra("srcData", characteristic.getValue());
        sendBroadcast(intent);
    }

    protected void readBluetooth(final String serviceUUID, final String characteristicUUID) {
        if (!isConnected()) {
            LogUtil.w(TAG, "Bluetooth  not connected");
            return;
        }
        BluetoothGattService bluetoothGattService = mBluetoothGatt.getService(UUID.fromString(serviceUUID));
        if (bluetoothGattService == null) {
            return;
        }
        BluetoothGattCharacteristic bluetoothGattCharacteristic = bluetoothGattService.getCharacteristic(UUID.fromString(characteristicUUID));
        if (bluetoothGattCharacteristic == null) {
            return;
        }
        readCharacteristic(bluetoothGattCharacteristic, 0);
    }

    private void writeAndNotify(String notifyServiceUUID, String notifyCharacteristicUUID, final String writeServiceUUID,
                                final String writeCharacteristicUUID, final byte[] writeData) {
        LogUtil.d(TAG, "Write Data:" + StringUtil.bytesToHexStr(writeData));
        if (!isConnected()) {
            LogUtil.w(TAG, "Bluetooth  not connected");
            return;
        }
        BluetoothGattService bluetoothGattServiceNotify = mBluetoothGatt.getService(UUID.fromString(notifyServiceUUID));
        if (bluetoothGattServiceNotify == null) {
            return;
        }
        BluetoothGattCharacteristic bluetoothGattCharacteristicNotify = bluetoothGattServiceNotify.getCharacteristic(UUID.fromString(notifyCharacteristicUUID));
        if (bluetoothGattCharacteristicNotify == null) {
            return;
        }
        if (notifyBluetoothGattCharacteristic != null && notifyBluetoothGattCharacteristic.getUuid().equals(bluetoothGattCharacteristicNotify.getUuid())) {
            BluetoothGattCharacteristic bluetoothGattCharacteristicWrite = mBluetoothGatt.getService(UUID.fromString(writeServiceUUID))
                    .getCharacteristic(UUID.fromString(writeCharacteristicUUID));
            bluetoothGattCharacteristicWrite.setValue(writeData);
            writeCharacteristic(bluetoothGattCharacteristicWrite, 0);
            return;
        } else if (notifyBluetoothGattCharacteristic != null) {
            mBluetoothGatt.setCharacteristicNotification(notifyBluetoothGattCharacteristic, false);
            notifyBluetoothGattCharacteristic = null;
        }
        mBluetoothGatt.setCharacteristicNotification(bluetoothGattCharacteristicNotify, true);
        notifyBluetoothGattCharacteristic = bluetoothGattCharacteristicNotify;
        for (BluetoothGattDescriptor bluetoothGattDescriptor : bluetoothGattCharacteristicNotify.getDescriptors()) {
            bluetoothGattDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            boolean status = mBluetoothGatt.writeDescriptor(bluetoothGattDescriptor);
            if (!status) {
                LogUtil.e(TAG, "Change notification status to enable failed");
            }
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mBluetoothGatt == null) {
                    return;
                }
                BluetoothGattCharacteristic bluetoothGattCharacteristicWrite = mBluetoothGatt.getService(UUID.fromString(writeServiceUUID))
                        .getCharacteristic(UUID.fromString(writeCharacteristicUUID));
                bluetoothGattCharacteristicWrite.setValue(writeData);
                writeCharacteristic(bluetoothGattCharacteristicWrite, 0);
            }
        }, WalleBleConfig.getBleWriteDelayedTime());
    }

    protected void writeBluetooth(final String notifyServiceUUID, final String notifyCharacteristicUUID,
                                  final String writeServiceUUID, final String writeCharacteristicUUID,
                                  final byte[] content, boolean segmentationContent) {

        if (content.length <= maxLength || !segmentationContent) {
            writeAndNotify(notifyServiceUUID, notifyCharacteristicUUID, writeServiceUUID, writeCharacteristicUUID, content);
            return;
        }
        new Thread() {
            @Override
            public void run() {
                super.run();
                boolean exist = true;
                int index = 0;
                int segmentationIndex = 0;
                while (exist) {
                    if (index > 0 && WalleBleConfig.getSegmentationSleepTime() > 0) {
                        try {
                            sleep(WalleBleConfig.getSegmentationSleepTime());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    int size;
                    byte[] byteTag;
                    if (WalleBleConfig.isSegmentationAddIndex() && index > 0) {
                        if (index + maxLength - 1 <= content.length) {
                            size = maxLength;
                        } else {
                            size = content.length - index + 1;
                        }
                        byteTag = new byte[size];
                        byteTag[0] = (byte) segmentationIndex;
                        System.arraycopy(content, index, byteTag, 1, byteTag.length - 1);
                        segmentationIndex++;
                        index += size - 1;
                    } else {
                        if (index + maxLength <= content.length) {
                            size = maxLength;
                        } else {
                            size = content.length - index;
                        }
                        byteTag = new byte[size];
                        System.arraycopy(content, index, byteTag, 0, byteTag.length);
                        index += size;
                    }
                    writeAndNotify(notifyServiceUUID, notifyCharacteristicUUID, writeServiceUUID, writeCharacteristicUUID, byteTag);
                    if (index >= content.length) {
                        exist = false;
                    }
                }
            }
        }.start();
    }

    private void writeCharacteristic(final BluetoothGattCharacteristic bluetoothGattCharacteristic, final int retryNumber) {
        boolean status = mBluetoothGatt.writeCharacteristic(bluetoothGattCharacteristic);
        if (status) {
            LogUtil.d(TAG, "Bluetooth write success");
            if (!operationDone) {
                broadcastUpdate(ACTION_EXECUTED_SUCCESSFULLY);
                operationDone = true;
            }
            return;
        }
        LogUtil.e(TAG, "Bluetooth write failed , retryNumber:" + retryNumber);
        if (WalleBleConfig.getMaxRetryNumber() > 0 && retryNumber <= WalleBleConfig.getMaxRetryNumber()) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    writeCharacteristic(bluetoothGattCharacteristic, retryNumber + 1);
                }
            }, WalleBleConfig.getRetrySleepTime());
        } else if (!operationDone) {
            broadcastUpdate(ACTION_EXECUTED_FAILED);
            operationDone = true;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 开始扫描设备
     */
    private void startScan() {
        initialize();
        if(bluetoothLeScanner == null){
            LogUtil.w(TAG, "bluetoothLeScanner is null");
            return ;
        }
        bluetoothLeScanner.startScan(scanCallback);
        deviceMap.clear();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                sendBroadcast(new Intent(ACTION_SCAN_TIMEOUT));
                stopScan();
            }
        }, WalleBleConfig.getScanBleTimeoutTime());
    }

    private void stopScan() {
        if(bluetoothLeScanner == null){
            return ;
        }
        bluetoothLeScanner.stopScan(scanCallback);
    }

    ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            String name = result.getDevice().getName();
            String address = result.getDevice().getAddress();
            int rssi = result.getRssi();
            LogUtil.d("ScanResult", "address:" + address + " name:" + name + " rssi:" + rssi);
            if (name == null || name.length() == 0) {
                return;
            }
            //super.onScanResult(callbackType, result);
            deviceMap.put(address, result.getDevice());
            Intent intent = new Intent(ACTION_SCAN_RESULT);
            intent.putExtra("rssi", rssi);
            intent.putExtra("address", address);
            intent.putExtra("name", name);
            sendBroadcast(intent);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }
    };

    @Override
    public void onDestroy() {
        BleUtil.setConnectStatus(BleUtil.CONNECT_STATUS_NOT_CONNECTED);
        BleUtil.bleAddress = null;
        BleUtil.bleName = null;
        unregisterReceiver(broadcastReceiver);
        disconnect();
        close();
        if (reconnectionRunnable != null && handler != null) {
            handler.removeCallbacks(reconnectionRunnable);
        }
        LogUtil.d(TAG, "WalleBleService onDestroy");
        super.onDestroy();
    }
}
