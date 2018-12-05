package cn.songhaiqing.walle.ble.utils;

import android.app.ActivityManager;
import android.content.Context;
import java.util.List;

public class ToolUtil {

    public static boolean isServiceRunning(String serviceName, Context context) {
        if(context == null){
            return false;
        }
        Object service =  context.getSystemService(Context.ACTIVITY_SERVICE);
        if(service == null){
            return false;
        }
        ActivityManager am = (ActivityManager) service;
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
