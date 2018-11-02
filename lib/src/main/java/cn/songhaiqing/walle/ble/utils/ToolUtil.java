package cn.songhaiqing.walle.ble.utils;

import android.app.ActivityManager;
import android.content.Context;
import java.util.List;

public class ToolUtil {

    public static boolean isServiceRunning(String serviceName, Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServices = am.getRunningServices(100);
        for (ActivityManager.RunningServiceInfo runningServiceInfo : runningServices) {
            String className = runningServiceInfo.service.getClassName();
            if (className.equals(serviceName)) {
                return true;
            }
        }
        return false;
    }
}
