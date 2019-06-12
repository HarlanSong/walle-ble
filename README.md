[![](https://jitpack.io/v/HarlanSong/walle-ble.svg)](https://jitpack.io/#HarlanSong/walle-ble)

 walle-ble is Android Bluetooth Low Energy tool. 

[中文文档](https://github.com/HarlanSong/walle-ble/blob/master/README_CN.md)

## Function & Features
* Simplify bluetooth connection and operation.
* Scanning equipment function.
* Supports multiple bluetooth solutions.
* Support for command queue execution.

## Bluetooth low energy flow 

 ![img](https://github.com/HarlanSong/walle-ble/blob/master/images/BluetoothLowEnergyFlow.png)


## Configuration
**repositories**
```groovy
maven { url "https://jitpack.io" }
```

**Gradle**
```groovy
implementation 'com.github.HarlanSong:walle-ble:1.0.21'
```

**Add permission**
```xml
<uses-permission android:name="android.permission.BLUETOOTH" />
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```

## Document
### Scanning page

 ![img](https://github.com/HarlanSong/walle-ble/blob/master/images/ScanDevice.jpg)

```java
String[] scanFilterName = {"NB-202"};
Intent intent = new Intent(this, DeviceScanActivity.class);
intent.putExtra("showSignalStrength", false);
intent.putExtra("scanFilterName", scanFilterName);
startActivityForResult(intent, REQUEST_BIND_DEVICE);
```
* REQUEST_BIND_DEVICE Custom callback constants（int）*
* scanFilterName Filter Name
* showSignalStrength Whether to display signal strength value

### Device selection successful

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


### Custom scan page.

**Start scan**
```java
BleUtil.startScan(final Context context)
```

**Stop scan**
```java
BleUtil.stopScan(Context context)
```

**Add results to listen for broadcasts**
```java
IntentFilter intentFilter = new IntentFilter();
intentFilter.addAction(WalleBleService.ACTION_SCAN_RESULT);
intentFilter.addAction(WalleBleService.ACTION_SCAN_TIMEOUT);
registerReceiver(scanResultBroadcastReceiver, intentFilter);
```

**Example results**
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



### Disconnect

```java
BleUtil.disConnect(this);
```

### Listen for device connection status

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

* ACTION_CONNECTED_SUCCESS Connection successful
* ACTION_GATT_DISCONNECTED Disconnected
* ACTION_DEVICE_RESULT The device has data to return, 'uuid' is the return characteristic value uuid; 'data' is the parsed array. 'srcData' is the original data;

### Write the content

```java
  BleUtil.broadcastWriteBle(Context context, String notifyServiceUUID,
                                         String notifyCharacteristicUUID, String writeServiceUUID,
                                         String writeCharacteristicUUID, byte[] bytes);
```

### Read the content

```java
BleUtil.readBle(Context context, String serviceUUID, String characteristicUUID);
```

### Determine whether the device is connected
```java
getConnectStatus(Context context)
```
**Result**
* BleUtil.CONNECT_STATUS_NOT_CONNECTED    Not connected
* BleUtil.CONNECT_STATUS_CONNECTING       Connecting
* BleUtil.CONNECT_STATUS_SUCCESS          Connected
* BleUtil.CONNECT_STATUS_FAIL             Connection fail

### MAC address of connected device
```java
BleUtil.bleAddress
```

### Connected device name
```JAVA
BleUtil.bleName
```

### BleUtil Other Use

```java
/**
* Connecting device
* @param context
* @param address MAC address
*/
void connectDevice(Context context,String address)

/**
* Connecting device
* @param context
* @param address MAC address
* @param autoConnect Auto connect(Default false)
*/
void connectDevice(Context context, String address, boolean autoConnect)

/**
* Disconnect
* @param context
*/
void disConnect(Context context)

/**
* Read content
* @param context
* @param serviceUUID
* @param characteristicUUID
*/
void readBle(Context context, String serviceUUID, String characteristicUUID)


/**
* Write content
* @param context
* @param notifyServiceUUID
* @param notifyCharacteristicUUID
* @param writeServiceUUID
* @param writeCharacteristicUUID
* @param bytes
*/
void broadcastWriteBle(Context context, String notifyServiceUUID,
                                         String notifyCharacteristicUUID, String writeServiceUUID,
                                         String writeCharacteristicUUID, byte[] bytes) 

/**
* Write content
*
* @param context
* @param notifyServiceUUID
* @param notifyCharacteristicUUID
* @param writeServiceUUID
* @param writeCharacteristicUUID
* @param bytes                    Content
* @param segmentation             Whether to subcontract the send,true will packet the send with up to 20 bytes
*/
void broadcastWriteBle(Context context, String notifyServiceUUID,
                                         String notifyCharacteristicUUID, String writeServiceUUID,
                                         String writeCharacteristicUUID, byte[] bytes, boolean segmentation)


/**
* Write content
*
* @param context
* @param notifyServiceUUID
* @param notifyCharacteristicUUID
* @param writeServiceUUID
* @param writeCharacteristicUUID
* @param bytes
* @param segmentation             Whether to subcontract the send,true will packet the send with up to 20 bytes
* @param immediately              Whether to send it immediately or not (it needs to be optimized for the queue mechanism of the command, such as: measurement termination, measurement of the heart rate of the bracelet, etc.)
*/
void broadcastWriteBle(Context context, String notifyServiceUUID,
                                         String notifyCharacteristicUUID, String writeServiceUUID,
                                         String writeCharacteristicUUID, byte[] bytes, boolean segmentation,
                                         boolean immediately) 

/**
* Whether bluetooth is available
* @return
*/
boolean bleIsEnabled()

/**
* Verify or enable bluetooth
*
* @param activity
* @param resultCode
* @return
*/
boolean validOrOpenBle(Activity activity, int resultCode)

 /**
* Start scan
*
* @param context
*/
void startScan(final Context context)

/**
* Start scan
* @param context
* @param scanFilterName Filter name
*/
void startScan(final Context context, final String[] scanFilterName)

 /**
* Stop scan
* @param context
*/
void stopScan(Context context) 

/**
*  Stop WalleBleService
* @param context
*/
void stopWalleBleService(Context context)

/**
* The command has queue mechanism. After successfully returning the result, the method can be called to execute the next command immediately. If the method is not called, the next command can only be executed automatically after the timeout
* @param context
*/
void finishResult(Context context)
```


### WalleBleConfig.java Config class 

```java

/**
 * Open debug model
 **/
void setDebug(boolean isDebug)

/**
 * Command send retries
 * @param maxRetryNumber  Default 3
 **/ 
void setMaxRetryNumber(int maxRetryNumber)

/**
 * Whether to add a sequence number at the 0th bit from the second package when subcontracting
 * @param segmentationAddIndex Default false
 **/
void setSegmentationAddIndex(boolean segmentationAddIndex)

/**
 * Packet transmission interval
 * @param segmentationSleepTime  millisecond(Default 500ms)
 **/ 
void setSegmentationSleepTime(int segmentationSleepTime)

/**
 * Scan device timeout
 *  @param scanBleTimeoutTime millisecond(Default 20000ms)
 **/ 
void setScanBleTimeoutTime(int scanBleTimeoutTime)

/**
 * Set the wait time after the command is sent to return the result. The default time is 2000 milliseconds
 *  @param bleResultWaitTime millisecond
 **/
void setBleResultWaitTime(int bleResultWaitTime)

/**
 * Reconnection interval
 * @param reconnectTime millisecond(Default 10000ms)
 */
void setReconnectTime(int reconnectTime)

 /**
  * Reconnection number
  * @param maxReconnectNumber  default 3
  */
void setMaxReconnectNumber(int maxReconnectNumber)
```



