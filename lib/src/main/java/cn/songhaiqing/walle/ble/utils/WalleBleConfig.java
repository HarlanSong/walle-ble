package cn.songhaiqing.walle.ble.utils;


public class WalleBleConfig {
    private static boolean debug = false;
    private static String LOG_TAG = "WalleBle ";
    private static int segmentationSleepTime = 2000;
    private static boolean segmentationAddIndex = false;
    private static int retrySleepTime = 1000;
    private static int maxRetryNumber = 3;

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
}
