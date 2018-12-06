[![](https://jitpack.io/v/HarlanSong/walle-ble.svg)](https://jitpack.io/#HarlanSong/walle-ble)
# walle-ble
 低功耗蓝牙辅助库

## 使用
**repositories中添加源**
```groovy
maven { url "https://jitpack.io" }
```

**Gradle 引入库**
```groovy
implementation 'com.github.HarlanSong:walle-ble:1.0.9'
```

**添加权限**
```xml
<uses-permission android:name="android.permission.BLUETOOTH" />
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```

## 文档
### 打开扫描界面

 ![img](https://github.com/HarlanSong/walle-ble/blob/master/images/ScanDevice.jpg)

```java
Intent intent = new Intent(this, DeviceScanActivity.class);
startActivityForResult(intent, REQUEST_BIND_DEVICE);
```
*REQUEST_BIND_DEVICE 自行定义回调常量（int）*

### 或者自这义界面

利用监听广播实现结果展示


**开始扫描设备**
```java
BleUtil.startScan(final Context context)
```

*停止扫描设备**
```java
BleUtil.stopScan(Context context)
```

**添加监听扫描结果广告**
```java
IntentFilter intentFilter = new IntentFilter();
intentFilter.addAction(WalleBleService.ACTION_SCAN_RESULT);
intentFilter.addAction(WalleBleService.ACTION_SCAN_TIMEOUT);
registerReceiver(scanResultBroadcastReceiver, intentFilter);
```

**收听结果示例**
```java
BroadcastReceiver scanResultBroadcastReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (WalleBleService.ACTION_SCAN_RESULT.equals(intent.getAction())) {
            BluetoothDeviceEntity device = new BluetoothDeviceEntity();
            device.setRssi(intent.getIntExtra("rssi", 0));
            device.setName(intent.getStringExtra("name"));
            device.setAddress(intent.getStringExtra("address"));
            addBluetoothDeviceEntity(device);
        } else if (WalleBleService.ACTION_SCAN_TIMEOUT.equals(intent.getAction())) {
            mScanning = false;
            refreshOptionStatus();
        }
    }
};
```

### 选择蓝牙设置成功回调,并连接设备

```java
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	super.onActivityResult(requestCode, resultCode, data);
	if (resultCode != RESULT_OK && REQUEST_BIND_DEVICE == requestCode) {
		String name = data.getStringExtra("name");
		String macAddress = data.getStringExtra("macAddress");
		Toast.makeText(this, "name:" + name + " macAddress:" + macAddress, Toast.LENGTH_LONG).show();
		BleUtil.connectDevice(this, name, macAddress);
	}
}
```

### 断开连接

```java

BleUtil.disConnect(this);
```

### 监听设备连接状态

```java
 private BroadcastReceiver bleReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (WalleBleService.ACTION_CONNECTED_SUCCESS.equals(action)) {

            } else if (WalleBleService.ACTION_GATT_DISCONNECTED.equals(action)) {

            } else if (WalleBleService.ACTION_DEVICE_RESULT.equals(action)) {
				      String uuid = intent.getStringExtra("uuid");
							ArrayList<Integer> dataArray = intent.getIntegerArrayListExtra("data");
							byte[] srcData = intent.getByteArrayExtra("srcData");
            }
        }
    };
```

* ACTION_CONNECTED_SUCCESS 连接成功
* ACTION_GATT_DISCONNECTED 断开连接
* ACTION_DEVICE_RESULT 设备有数据返回，`uuid`为返回特征值UUID;`data`为解析后的数组。`srcData`为原数据；

### 发送命令到设备，并监听

```java
  BleUtil.broadcastWriteBle(Context context, String notifyServiceUUID,
                                         String notifyCharacteristicUUID, String writeServiceUUID,
                                         String writeCharacteristicUUID, byte[] bytes);
```
*  context 上下文
*  notifyServiceUUID 通知服务UUID
*  notifyCharacteristicUUID  通知特征值UUID
*  writeServiceUUID 写入服务UUID
*  writeCharacteristicUUID 写入特殊值UUID
*  bytes 命令内容

### 仅读取取设备数据

```java
BleUtil.broadcastReadBle(Context context, byte[] bytes, String serviceUUID,String characteristicUUID);
```

*  context 上下文
*  serviceUUID 服务UUID
*  characteristicUUID  特征值UUID

### 判断设备是否连接
```java
/**
* 读取连接状态
* 返回：BleUtil
* CONNECT_STATUS_NOT_CONNECTED  未连接
* CONNECT_STATUS_CONNECTING 连接中
* CONNECT_STATUS_SUCCESS 连接成功
* CONNECT_STATUS_FAIL 连接失败
**/

getConnectStatus(Context context)

// 已连接设备MAC地址
BleUtil.bleAddress

// 已连接设备名称
BleUtil.bleName
```

### 配置

```java
// Log前缀，默认为“Walle”
WalleBleConfig.setLogTag(String tag)

// 是否开户DEBUG模式，默认false
WalleBleConfig.setDebug(boolean isDebug)

// 命令发送失败最多重试次数(默认 3)
WalleBleConfig.setMaxRetryNumber(int maxRetryNumber)

// 分包时是否从第二个包开始在第0位添加序号，默认false
WalleBleConfig.setSegmentationAddIndex(boolean segmentationAddIndex)

// 分包发送间隔时间（毫秒）
WalleBleConfig.setSegmentationSleepTime(int segmentationSleepTime)

// 设置扫描设备超时时间（毫秒） 默认20秒
WalleBleConfig.setScanBleTimeoutTime(int scanBleTimeoutTime) 
```

