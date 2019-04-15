package cn.songhaiqing.walle.ble.utils;


public class WalleBleConfig {
    private static boolean debug = false;
    private static String LOG_TAG = "WalleBle ";
    private static int segmentationSleepTime = 500;
    private static boolean segmentationAddIndex = false;
    private static int bleWriteDelayedTime = 500;
    private static int retrySleepTime = 1000;
    private static int maxRetryNumber = 3;
    private static int reconnectTime = 10000; // 重连时间（毫秒）
    private static int maxReconnectNumber = 3; // 重连次数
    private static int scanBleTimeoutTime = 20000;
    private static int bleResultWaitTime = 2000;
    private static int autConnectTime = 30000; // 自动重连时间


    public static void setLogTag(String tag) {
        if (tag == null) {
            return;
        }
        LOG_TAG = tag;
    }

    public static void setDebug(boolean isDebug) {
        debug = isDebug;
    }

    public static boolean isDebug() {
        return debug;
    }

    public static String getLogTag() {
        return LOG_TAG;
    }

    public static int getSegmentationSleepTime() {
        return segmentationSleepTime;
    }

    public static void setSegmentationSleepTime(int segmentationSleepTime) {
        WalleBleConfig.segmentationSleepTime = segmentationSleepTime;
    }

    public static int getRetrySleepTime() {
        return retrySleepTime;
    }

    public static void setRetrySleepTime(int retrySleepTime) {
        WalleBleConfig.retrySleepTime = retrySleepTime;
    }

    public static int getMaxRetryNumber() {
        return maxRetryNumber;
    }

    public static void setMaxRetryNumber(int maxRetryNumber) {
        WalleBleConfig.maxRetryNumber = maxRetryNumber;
    }

    public static boolean isSegmentationAddIndex() {
        return segmentationAddIndex;
    }

    public static void setSegmentationAddIndex(boolean segmentationAddIndex) {
        WalleBleConfig.segmentationAddIndex = segmentationAddIndex;
    }

    public static int getBleWriteDelayedTime() {
        return bleWriteDelayedTime;
    }

    public static void setBleWriteDelayedTime(int bleWriteDelayedTime) {
        WalleBleConfig.bleWriteDelayedTime = bleWriteDelayedTime;
    }

    public static int getScanBleTimeoutTime() {
        return scanBleTimeoutTime;
    }

    public static void setScanBleTimeoutTime(int scanBleTimeoutTime) {
        if (scanBleTimeoutTime < 1000) {
            return;
        }
        WalleBleConfig.scanBleTimeoutTime = scanBleTimeoutTime;
    }

    public static int getBleResultWaitTime() {
        return bleResultWaitTime;
    }

    /**
     * 设置命令发送后返回结果等待时间，默认2000毫秒，超过这个时间无返回数据则开始发送一下个命令
     *
     * @param bleResultWaitTime
     */
    public static void setBleResultWaitTime(int bleResultWaitTime) {
        WalleBleConfig.bleResultWaitTime = bleResultWaitTime;
    }

    public static int getReconnectTime() {
        return reconnectTime;
    }

    /**
     * 重连时间间隔
     * @param reconnectTime （默认10000）毫秒
     */
    public static void setReconnectTime(int reconnectTime) {
        WalleBleConfig.reconnectTime = reconnectTime;
    }

    public static int getMaxReconnectNumber() {
        return maxReconnectNumber;
    }


    /**
     * 配置重连次数
     * @param maxReconnectNumber 默认3次
     */
    public static void setMaxReconnectNumber(int maxReconnectNumber) {
        WalleBleConfig.maxReconnectNumber = maxReconnectNumber;
    }

    public static int getAutConnectTime() {
        return autConnectTime;
    }

    public static void setAutConnectTime(int autConnectTime) {
        WalleBleConfig.autConnectTime = autConnectTime;
    }
}
