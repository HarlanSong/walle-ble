package cn.songhaiqing.walle.ble.utils;

import android.content.Context;
import android.content.Intent;
import cn.songhaiqing.walle.ble.service.WalleBleService;

public class BleUtil {
    private static int connectStatus = 0;
    public static final int CONNECT_STATUS_NOT_CONNECTED = 0;
    public static final int CONNECT_STATUS_CONNECTING = 1;
    public static final int CONNECT_STATUS_SUCCESS = 2;
    public static final int CONNECT_STATUS_FAIL = 3;

    public static String bleAddress;
    public static String bleName;

    public static boolean connectDevice(final Context context, String name, final String address) {
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

    public static void disConnect(Context context) {
        Intent intent = new Intent(WalleBleService.ACTION_DISCONNECT_DEVICE);
        context.sendBroadcast(intent);
        intent = new Intent(context, WalleBleService.class);
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

    public static int getConnectStatus(Context context) {
        if (connectStatus == CONNECT_STATUS_SUCCESS && !ToolUtil.isServiceRunning(WalleBleService.class.getName(), context)) {
            connectStatus = CONNECT_STATUS_NOT_CONNECTED;
        }
        return connectStatus;
    }

    public static void setConnectStatus(int connectStatus) {
        BleUtil.connectStatus = connectStatus;
    }
}
