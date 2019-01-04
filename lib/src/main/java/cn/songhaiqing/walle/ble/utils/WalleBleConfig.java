package cn.songhaiqing.walle.ble.utils;


public class WalleBleConfig {
    private static boolean debug = false;
    private static String LOG_TAG = "WalleBle ";
    private static int segmentationSleepTime = 2000;
    private static boolean segmentationAddIndex = false;
    private static int bleWriteDelayedTime = 1000;
    private static int retrySleepTime = 1000;
    private static int maxRetryNumber = 3;
    private static int scanBleTimeoutTime = 20000;
    private static int bleResultWaitTime = 2000;


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
        if (bleWriteDelayedTime <= 300) {
            bleWriteDelayedTime = 300;
        }
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
     * @param bleResultWaitTime
     */
    public static void setBleResultWaitTime(int bleResultWaitTime) {
        WalleBleConfig.bleResultWaitTime = bleResultWaitTime;
    }
}
