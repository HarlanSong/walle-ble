[![](https://jitpack.io/v/com.gitee.HarlanSong/walle-ble.svg)](https://jitpack.io/#com.gitee.HarlanSong/walle-ble)

* [English document](https://github.com/HarlanSong/walle-ble/blob/master/README.md)
* walle-ble是安卓低功耗蓝牙工具


## 功能及特点
* 简化蓝牙连接及操作
* 集成扫描设备功能及界面
* 支持多种蓝牙协议
* 支持命令队列执行

## 低功耗蓝牙流程图

 ![img](https://github.com/HarlanSong/walle-ble/blob/master/images/BluetoothLowEnergyFlow.png)


## 配置
**repositories**
```groovy
maven { url "https://jitpack.io" }
```

**Gradle**
```groovy
implementation 'com.gitee.HarlanSong:walle-ble:1.0.28'
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
String[] scanFilterName = {"NB-202"};
Intent intent = new Intent(this, DeviceScanActivity.class);
intent.putExtra("showSignalStrength", false);
intent.putExtra("scanFilterName", scanFilterName);
startActivityForResult(intent, REQUEST_BIND_DEVICE);
```
* REQUEST_BIND_DEVICE 自定义回调常量（int）*
* scanFilterName 名称过滤
* showSignalStrength 是否显示信号强度值

### 选择蓝牙设置成功回调,并连接设备

```java
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	super.onActivityResult(requestCode, resultCode, data);
	if (resultCode == RESULT_OK && REQUEST_BIND_DEVICE == requestCode) {
		String name = data.getStringExtra("name");
		String macAddress = data.getStringExtra("macAddress");
		Toast.makeText(this, "name:" + name + " macAddress:" + macAddress, Toast.LENGTH_LONG).show();
		BleUtil.connectDevice(this, name, macAddress);
	}
}
```

### 自定义扫描界面

**开始扫描**
```java
BleUtil.startScan(final Context context)
```

**停止扫描**
```java
BleUtil.stopScan(Context context)
```

**添加结果监听广播**
```java
IntentFilter intentFilter = new IntentFilter();
intentFilter.addAction(WalleBleService.ACTION_SCAN_RESULT);
intentFilter.addAction(WalleBleService.ACTION_SCAN_TIMEOUT);
registerReceiver(scanResultBroadcastReceiver, intentFilter);
```

**结果示例**
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

### 写入命令

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

### 读取数据

```java
BleUtil.readBle(Context context, String serviceUUID, String characteristicUUID);
```

*  context 上下文
*  serviceUUID 服务UUID
*  characteristicUUID  特征值UUID

### 获取连接状态
```java
getConnectStatus(Context context)
```

* CONNECT_STATUS_NOT_CONNECTED  未连接
* CONNECT_STATUS_CONNECTING 连接中
* CONNECT_STATUS_SUCCESS 连接成功
* CONNECT_STATUS_FAIL 连接失败

### 已连接设备MAC地址
```java
BleUtil.bleAddress
```

### 已连接设备名称
```java
BleUtil.bleName
```

### BleUtil.java 工具类的其他操作

```java
/**
* 连接设备
* @param context
* @param address MAC地址
*/
void connectDevice(Context context,String address)

/**
* 连接设备
* @param context
* @param address MAC 地址
* @param autoConnect 是否自动连接，默认为false
*/
void connectDevice(Context context, String address, boolean autoConnect)

/**
* 断开连接
* @param context
*/
void disConnect(Context context)

/**
* 读取蓝牙设备数据
* @param context
* @param serviceUUID 服务UUID
* @param characteristicUUID 特征值UUID
*/
void readBle(Context context, String serviceUUID, String characteristicUUID)


/**
* 蓝牙设备写入命令
* @param context
* @param notifyServiceUUID 订阅服务UUID
* @param notifyCharacteristicUUID 订阅特征值UUID
* @param writeServiceUUID 写入服务UUID
* @param writeCharacteristicUUID 写入特征值UUID
* @param bytes 写入内容
*/
void broadcastWriteBle(Context context, String notifyServiceUUID,
                                         String notifyCharacteristicUUID, String writeServiceUUID,
                                         String writeCharacteristicUUID, byte[] bytes) 

/**
* 蓝牙设备写入命令
*
* @param context
* @param notifyServiceUUID        订阅服务UUID
* @param notifyCharacteristicUUID 订阅特征UUID
* @param writeServiceUUID         写入服务UUID
* @param writeCharacteristicUUID  写入特征UUID
* @param bytes                    命令内容
* @param segmentation             是否分包发送，true  以最多20个字节会包发送
*/
void broadcastWriteBle(Context context, String notifyServiceUUID,
                                         String notifyCharacteristicUUID, String writeServiceUUID,
                                         String writeCharacteristicUUID, byte[] bytes, boolean segmentation)


/**
* 蓝牙设备写入命令
*
* @param context
* @param notifyServiceUUID        订阅服务UUID
* @param notifyCharacteristicUUID 订阅特征UUID
* @param writeServiceUUID         写入服务UUID
* @param writeCharacteristicUUID  写入特征UUID
* @param bytes                    命令内容
* @param segmentation             是否分包发送，true  以最多20个字节会包发送
* @param immediately              是否立即发送（因命令有队列机制，需要优选执行，如：测量终止测量手环心率等场景）
*/
void broadcastWriteBle(Context context, String notifyServiceUUID,
                                         String notifyCharacteristicUUID, String writeServiceUUID,
                                         String writeCharacteristicUUID, byte[] bytes, boolean segmentation,
                                         boolean immediately) 

/**
* 判断连接是否可用
* @return
*/
boolean bleIsEnabled()

/**
* 验证或开启蓝牙
*
* @param activity
* @param resultCode 结果码
* @return
*/
boolean validOrOpenBle(Activity activity, int resultCode)

 /**
* 开始扫描设备
*
* @param context
*/
void startScan(final Context context)

/**
* 开始扫描设备
* @param context
* @param scanFilterName 过滤名称
*/
void startScan(final Context context, final String[] scanFilterName)

 /**
* 停止扫描设备
* @param context
*/
void stopScan(Context context) 

/**
* 关闭蓝牙连接服务
* @param context
*/
void stopWalleBleService(Context context)

/**
* 命令有队列机制，成功返回结果后调用该方法可立即执行下一条命令。如不调用该方法则只能等待超时后执行自动执行下一个命令
* @param context
*/
void finishResult(Context context)
```


### WalleBleConfig配置 

```java
/**
 * Log前缀
 * @param 默认: WalleBle 
 **/
void setLogTag(String tag)

/**
 * 是否开户DEBUG模式 
 *  @param isDebug 默认:false
 **/
void setDebug(boolean isDebug)

/**
 * 命令发送失败重试次数
 * @param maxRetryNumber 默认:3
 **/ 
void setMaxRetryNumber(int maxRetryNumber)

/**
 * 分包时是否从第二个包开始在第0位添加序号，
 * @param segmentationAddIndex 默认:false
 **/
void setSegmentationAddIndex(boolean segmentationAddIndex)

/**
 * 分包发送间隔时间
 * @param segmentationSleepTime  单位：毫秒
 **/ 
void setSegmentationSleepTime(int segmentationSleepTime)

/**
 * 扫描设备超时时间
 *  @param scanBleTimeoutTime 单位：毫秒， 默认20000
 **/ 
void setScanBleTimeoutTime(int scanBleTimeoutTime)

/**
 * 设置命令发送后返回结果等待时间，默认2000毫秒，超过这个时间无返回数据则开始发送一下个命令
 *  @param bleResultWaitTime 单位：毫秒
 **/
void setBleResultWaitTime(int bleResultWaitTime)

/**
 * 时间间隔
 * @param reconnectTime （默认10000）毫秒
 */
void setReconnectTime(int reconnectTime)

 /**
  * 重连次数
  * @param maxReconnectNumber 默认3次
  */
void setMaxReconnectNumber(int maxReconnectNumber)
```



