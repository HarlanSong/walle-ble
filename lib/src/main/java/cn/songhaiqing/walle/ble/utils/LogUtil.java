package cn.songhaiqing.walle.ble.utils;

import android.util.Log;

public class LogUtil {
    public static void d(String tag, String message) {
        if (WalleBleConfig.isDebug()) {
            Log.d(tag, WalleBleConfig.getLogTag() + message);
        }
    }

    public static void i(String tag, String message) {
        if (WalleBleConfig.isDebug()) {
            Log.i(tag, WalleBleConfig.getLogTag() + message);
        }
    }

    public static void w(String tag, String message) {
        if (WalleBleConfig.isDebug()) {
            Log.w(tag, WalleBleConfig.getLogTag() + message);
        }
    }

    public static void e(String tag, String message) {
        if (WalleBleConfig.isDebug()) {
            Log.e(tag, WalleBleConfig.getLogTag() + message);
        }
    }
}
