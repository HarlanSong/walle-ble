package cn.songhaiqing.walle.ble.utils;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;

import cn.songhaiqing.walle.ble.service.WalleBleService;

public class BleUtil {
    private static int connectStatus = 0;
    public static final int CONNECT_STATUS_NOT_CONNECTED = 0; // 未连接
    public static final int CONNECT_STATUS_CONNECTING = 1; // 连接中
    public static final int CONNECT_STATUS_SUCCESS = 2; // 已连接
    public static final int CONNECT_STATUS_FAIL = 3; // 连接失败

    public static String bleAddress;
    public static String bleName;

    @Deprecated
    public static boolean connectDevice(final Context context, String name, final String address) {
        setConnectStatus(CONNECT_STATUS_CONNECTING);
        if (!ToolUtil.isServiceRunning(WalleBleService.class.getName(), context)) {
            Intent intent = new Intent(context, WalleBleService.class);
            context.startService(intent);
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    try {
                        sleep(100);
                    } catch (InterruptedException e) {
                        LogUtil.e("BleUtil", e.getMessage());
                    }
                    Intent intent = new Intent(WalleBleService.ACTION_CONNECT_DEVICE);
                    intent.putExtra(WalleBleService.EXTRA_DATA, address);
                    context.sendBroadcast(intent);
                }
            }.start();
        } else {
            Intent intent = new Intent(WalleBleService.ACTION_CONNECT_DEVICE);
            intent.putExtra(WalleBleService.EXTRA_DATA, address);
            context.sendBroadcast(intent);
        }
        return true;
    }

    /**
     * 连接设备
     *
     * @param context
     * @param address MAC地址
     */
    public static void connectDevice(final Context context, final String address) {
        setConnectStatus(CONNECT_STATUS_CONNECTING);
        if (!ToolUtil.isServiceRunning(WalleBleService.class.getName(), context)) {
            Intent intent = new Intent(context, WalleBleService.class);
            context.startService(intent);
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    try {
                        sleep(100);
                    } catch (InterruptedException e) {
                        LogUtil.e("BleUtil", e.getMessage());
                    }
                    Intent intent = new Intent(WalleBleService.ACTION_CONNECT_DEVICE);
                    intent.putExtra(WalleBleService.EXTRA_DATA, address);
                    context.sendBroadcast(intent);
                }
            }.start();
        } else {
            Intent intent = new Intent(WalleBleService.ACTION_CONNECT_DEVICE);
            intent.putExtra(WalleBleService.EXTRA_DATA, address);
            context.sendBroadcast(intent);
        }
    }

    public static void disConnect(Context context) {
        context.sendBroadcast(new Intent(WalleBleService.ACTION_GATT_DISCONNECTED));
        Intent intent = new Intent(context, WalleBleService.class);
        context.stopService(intent);
    }

    public static void broadcastReadBle(Context context, byte[] bytes, String serviceUUID,
                                        String characteristicUUID) {
        if (getConnectStatus(context) != CONNECT_STATUS_SUCCESS) {
            return;
        }
        Intent intent = new Intent(WalleBleService.ACTION_READ_BLE);
        intent.putExtra(WalleBleService.EXTRA_DATA_READ_SERVICE_UUID, serviceUUID);
        intent.putExtra(WalleBleService.EXTRA_DATA_READ_CHARACTERISTIC_UUID, characteristicUUID);
        intent.putExtra(WalleBleService.EXTRA_DATA, bytes);
        context.sendBroadcast(intent);
    }

    public static void broadcastWriteBle(Context context, String notifyServiceUUID,
                                         String notifyCharacteristicUUID, String writeServiceUUID,
                                         String writeCharacteristicUUID, byte[] bytes) {
        if (getConnectStatus(context) != CONNECT_STATUS_SUCCESS) {
            return;
        }
        broadcastWriteBle(context, notifyServiceUUID, notifyCharacteristicUUID, writeServiceUUID, writeCharacteristicUUID, bytes, true);
    }

    /**
     * 发送写入命令
     *
     * @param context
     * @param notifyServiceUUID        订阅服务UUID
     * @param notifyCharacteristicUUID 订阅特征UUID
     * @param writeServiceUUID         写入服务UUID
     * @param writeCharacteristicUUID  写入特征UUID
     * @param bytes                    命令内容
     * @param segmentation             是否分包发送，true  以最多20个字节会包发送
     */
    public static void broadcastWriteBle(Context context, String notifyServiceUUID,
                                         String notifyCharacteristicUUID, String writeServiceUUID,
                                         String writeCharacteristicUUID, byte[] bytes, boolean segmentation) {
        if (getConnectStatus(context) != CONNECT_STATUS_SUCCESS) {
            return;
        }
        Intent intent = new Intent(WalleBleService.ACTION_WRITE_BLE);
        intent.putExtra(WalleBleService.EXTRA_DATA_NOTIFY_SERVICE_UUID, notifyServiceUUID);
        intent.putExtra(WalleBleService.EXTRA_DATA_NOTIFY_CHARACTERISTIC_UUID, notifyCharacteristicUUID);
        intent.putExtra(WalleBleService.EXTRA_DATA_WRITE_SERVICE_UUID, writeServiceUUID);
        intent.putExtra(WalleBleService.EXTRA_DATA_WRITE_CHARACTERISTIC_UUID, writeCharacteristicUUID);
        intent.putExtra(WalleBleService.EXTRA_DATA, bytes);
        intent.putExtra(WalleBleService.EXTRA_DATA_WRITE_SEGMENTATION, segmentation);
        context.sendBroadcast(intent);
    }

    /**
     * 获取连接状态
     *
     * @param context
     * @return {@link BleUtil#CONNECT_STATUS_NOT_CONNECTED}
     * {@link BleUtil#CONNECT_STATUS_CONNECTING}
     * {@link BleUtil#CONNECT_STATUS_SUCCESS}
     * {@link BleUtil#CONNECT_STATUS_FAIL}
     */
    public static int getConnectStatus(Context context) {
        if (connectStatus == CONNECT_STATUS_SUCCESS && !ToolUtil.isServiceRunning(WalleBleService.class.getName(), context)) {
            connectStatus = CONNECT_STATUS_NOT_CONNECTED;
        }
        return connectStatus;
    }

    public static void setConnectStatus(int connectStatus) {
        BleUtil.connectStatus = connectStatus;
    }

    /**
     * 判断连接是否可用
     *
     * @return
     */
    public static boolean bleIsEnabled() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            return false;
        }
        return bluetoothAdapter.isEnabled();
    }

    /**
     * 验证或开始蓝牙
     *
     * @param activity
     * @param resultCode
     * @return
     */
    public static boolean validOrOpenBle(Activity activity, int resultCode) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            return false;
        }
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBtIntent, resultCode);
            return false;
        } else {
            return true;
        }
    }

    /**
     * 开始扫描设备
     *
     * @param context
     */
    public static void startScan(final Context context) {
        if (!ToolUtil.isServiceRunning(WalleBleService.class.getName(), context)) {
            Intent intent = new Intent(context, WalleBleService.class);
            context.startService(intent);
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    try {
                        sleep(100);
                    } catch (InterruptedException e) {
                        LogUtil.e("BleUtil", e.getMessage());
                    }
                    Intent intent = new Intent(WalleBleService.ACTION_START_SCAN);
                    context.sendBroadcast(intent);
                }
            }.start();
        } else {
            Intent intent = new Intent(WalleBleService.ACTION_START_SCAN);
            context.sendBroadcast(intent);
        }
    }

    /**
     * 停止扫描设备
     *
     * @param context
     */
    public static void stopScan(Context context) {
        Intent intent = new Intent(WalleBleService.ACTION_STOP_SCAN);
        context.sendBroadcast(intent);
    }

    public static void stopWalleBleService(Context context){
        Intent intent = new Intent(context,WalleBleService.class);
        context.stopService(intent);
    }
}
