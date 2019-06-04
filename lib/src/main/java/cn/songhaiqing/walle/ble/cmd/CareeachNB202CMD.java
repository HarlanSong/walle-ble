package cn.songhaiqing.walle.ble.cmd;

import android.content.Context;
import java.util.Calendar;
import java.util.Date;
import cn.songhaiqing.walle.ble.utils.BleUtil;
import cn.songhaiqing.walle.ble.utils.WalleBleConfig;

/**
 *  易兴NB-202手环命令
 */
public class CareeachNB202CMD {

    private static final String SERVICE_UUID = "6e400001-b5a3-f393-e0a9-e50e24dcca9e";
    private static final String CHARACTERISTIC_WRITE_UUID = "6e400002-b5a3-f393-e0a9-e50e24dcca9e";
    private static final String CHARACTERISTIC_NOTIFY_UUID = "6e400003-b5a3-f393-e0a9-e50e24dcca9e";

    /**
     * 读取电量
     * @param context
     */
    public static void readBattery(Context context) {
        byte[] bytes = new byte[6];
        bytes[0] = (byte) 0xAB;
        bytes[1] = (byte) 0x00;
        bytes[2] = (byte) 0x03;
        bytes[3] = (byte) 0x00;
        bytes[4] = (byte) 0x91;
        bytes[5] = (byte) 0x80;
        BleUtil.broadcastWriteBle(context, SERVICE_UUID, CHARACTERISTIC_NOTIFY_UUID,
                SERVICE_UUID, CHARACTERISTIC_WRITE_UUID, bytes);
    }

    /**
     * 读取版本号
     * @param context
     */
    public static void readVersion(Context context) {
        byte[] bytes = new byte[6];
        bytes[0] = (byte) 0xAB;
        bytes[1] = (byte) 0x00;
        bytes[2] = (byte) 0x03;
        bytes[3] = (byte) 0xFF;
        bytes[4] = (byte) 0x92;
        bytes[5] = (byte) 0x80;
        BleUtil.broadcastWriteBle(context, SERVICE_UUID, CHARACTERISTIC_NOTIFY_UUID,
                SERVICE_UUID, CHARACTERISTIC_WRITE_UUID, bytes);
    }


    /**
     * 读取运动数据
     * @param context
     * @param time
     */
    public static void readStep(Context context, Date time) {
        if (context == null) {
            return;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        byte[] bytes = new byte[17];
        bytes[0] = (byte) 0xAB;
        bytes[1] = (byte) 0x00;
        bytes[2] = (byte) 0x0E;
        bytes[3] = (byte) 0xff;
        bytes[4] = (byte) 0x51;
        bytes[5] = (byte) 0x80;
        bytes[6] = (byte) 0x00;
        bytes[7] = (byte) (year - 2000);
        bytes[8] = (byte) (month & 0xff);
        bytes[9] = (byte) (day & 0xff);
        bytes[10] = (byte) (hour & 0xff);
        bytes[11] = (byte) (minute & 0xff);
        bytes[12] = (byte) ((year - 2000) & 0xff);
        bytes[13] = (byte) (month & 0xff);
        bytes[14] = (byte) (day & 0xff);
        bytes[15] = (byte) (hour & 0xff);
        bytes[16] = (byte) (minute & 0xff);
        BleUtil.broadcastWriteBle(context, SERVICE_UUID, CHARACTERISTIC_NOTIFY_UUID,
                SERVICE_UUID, CHARACTERISTIC_WRITE_UUID, bytes);
    }

    /**
     * 读取睡眠
     * @param context
     * @param beforeDate
     */
    public static void readSleep(Context context, Date beforeDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(beforeDate);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        byte[] bytes = new byte[10];
        bytes[0] = (byte) 0xAB;
        bytes[1] = (byte) 0x00;
        bytes[2] = (byte) 7;
        bytes[3] = (byte) 0xff;
        bytes[4] = (byte) 0x52;
        bytes[5] = (byte) 0x80;
        bytes[6] = (byte) 0;
        bytes[7] = (byte) (year - 2000);
        bytes[8] = (byte) (month);
        bytes[9] = (byte) (day);
        BleUtil.broadcastWriteBle(context, SERVICE_UUID, CHARACTERISTIC_NOTIFY_UUID,
                SERVICE_UUID, CHARACTERISTIC_WRITE_UUID, bytes);
    }

    /**
     * 测量心率
     *
     * @param context
     * @param open    true 开；false 关
     */
    public static void measurementHR(Context context, boolean open) {
        byte[] bytes = new byte[7];
        bytes[0] = (byte) 0xAB;
        bytes[1] = (byte) 0;
        bytes[2] = (byte) 0x04;
        bytes[3] = (byte) 0xFF;
        bytes[4] = (byte) 0x84;
        bytes[5] = (byte) 0x80;
        if (open) {
            bytes[6] = (byte) 0x01;
        } else {
            bytes[6] = (byte) 0x00;//0关  1开
        }
        BleUtil.broadcastWriteBle(context, SERVICE_UUID, CHARACTERISTIC_NOTIFY_UUID,
                SERVICE_UUID, CHARACTERISTIC_WRITE_UUID, bytes, true, true);
    }

    /**
     * 查找手环
     * @param context
     */
    public static void findBracelet(Context context) {
        byte[] bytes = new byte[6];
        bytes[0] = (byte) 0xAB;
        bytes[1] = (byte) 0x00;
        bytes[2] = (byte) 0x03;
        bytes[3] = (byte) 0xFF;
        bytes[4] = (byte) 0x71;
        bytes[5] = (byte) 0x80;
        BleUtil.broadcastWriteBle(context, SERVICE_UUID, CHARACTERISTIC_NOTIFY_UUID,
                SERVICE_UUID, CHARACTERISTIC_WRITE_UUID, bytes, true, true);
    }

    /**
     * 遥控拍照
     * @param context
     * @param open
     */
    public static void remoteCamera(Context context, boolean open) {
        byte[] bytes = new byte[7];
        bytes[0] = (byte) 0xAB;
        bytes[1] = (byte) 0x00;
        bytes[2] = (byte) 0x04;
        bytes[3] = (byte) 0xFF;
        bytes[4] = (byte) 0x79;
        bytes[5] = (byte) 0x80;
        bytes[6] = (byte) (open ? 1 : 0);
        BleUtil.broadcastWriteBle(context, SERVICE_UUID, CHARACTERISTIC_NOTIFY_UUID,
                SERVICE_UUID, CHARACTERISTIC_WRITE_UUID, bytes);
    }

    /**
     * 同步时间
     * @param context
     */
    public static void syncTime(Context context) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        byte[] bytes = new byte[14];
        bytes[0] = (byte) 0xAB;
        bytes[1] = (byte) 0x00;
        bytes[2] = (byte) 0x0B;
        bytes[3] = (byte) 0xff;
        bytes[4] = (byte) 0x93;
        bytes[5] = (byte) 0x80;
        bytes[6] = (byte) 0x00;
        bytes[7] = (byte) ((year & 0xff00) >> 8);
        bytes[8] = (byte) (year);
        bytes[9] = (byte) (month);
        bytes[10] = (byte) (day);
        bytes[11] = (byte) (hour);
        bytes[12] = (byte) (minute);
        bytes[13] = (byte) (second);
        BleUtil.broadcastWriteBle(context, SERVICE_UUID, CHARACTERISTIC_NOTIFY_UUID,
                SERVICE_UUID, CHARACTERISTIC_WRITE_UUID, bytes);
    }

    /**
     * 抬腕亮屏
     * @param context
     * @param turn
     */
    public static void brightenTheScreen(Context context, boolean turn) {
        byte[] bytes = new byte[7];
        bytes[0] = (byte) 0xAB;
        bytes[1] = (byte) 0x00;
        bytes[2] = (byte) 0x04;
        bytes[3] = (byte) 0xFF;
        bytes[4] = (byte) 0x77;
        bytes[5] = (byte) 0x80;
        bytes[6] = turn ? (byte) 0x01 : (byte) 0x00;
        BleUtil.broadcastWriteBle(context, SERVICE_UUID, CHARACTERISTIC_NOTIFY_UUID,
                SERVICE_UUID, CHARACTERISTIC_WRITE_UUID, bytes);
    }

    /**
     * 清除数据
     * @param context
     */
    public static void clear(Context context) {
        byte[] bytes = new byte[6];
        bytes[0] = (byte) 0xAB;
        bytes[1] = (byte) 0x00;
        bytes[2] = (byte) 0x03;
        bytes[3] = (byte) 0xFF;
        bytes[4] = (byte) 0x23;
        bytes[5] = (byte) 0x80;
        BleUtil.broadcastWriteBle(context, SERVICE_UUID, CHARACTERISTIC_NOTIFY_UUID,
                SERVICE_UUID, CHARACTERISTIC_WRITE_UUID, bytes,true, true);
    }

    /**
     * 重置手环
     * @param context
     */
    public static void reset(Context context) {
        byte[] bytes = new byte[6];
        bytes[0] = (byte) 0xAB;
        bytes[1] = (byte) 0x00;
        bytes[2] = (byte) 0x03;
        bytes[3] = (byte) 0xFF;
        bytes[4] = (byte) 0xff;
        bytes[5] = (byte) 0x80;
        BleUtil.broadcastWriteBle(context, SERVICE_UUID, CHARACTERISTIC_NOTIFY_UUID,
                SERVICE_UUID, CHARACTERISTIC_WRITE_UUID, bytes,true, true);
    }

    /**
     * 提醒
     *
     * @param context
     * @param type    1-来电话；2-挂电话；3-短信；7-QQ; 9-微信；
     * @param msg     消息内容
     */
    public static void notify(final Context context, int type, String msg) {
        WalleBleConfig.setSegmentationAddIndex(true);
        int maxLength = 42;
        if (msg.length() > maxLength) {
            msg = msg.substring(0, maxLength);
        }
        byte[] messageBytes = msg.getBytes();
        byte[] bytes = new byte[8 + messageBytes.length];
        bytes[0] = (byte) 0xAB;
        bytes[1] = (byte) 0x00;
        bytes[2] = (byte) (5 + messageBytes.length);
        bytes[3] = (byte) 0xFF;
        bytes[4] = (byte) 0x72;
        bytes[5] = (byte) 0x80;
        bytes[6] = (byte) type;
        bytes[7] = (byte) 0x02;
        for (int i = 0; i < messageBytes.length; i++) {
            bytes[8 + i] = messageBytes[i];
        }
        BleUtil.broadcastWriteBle(context, SERVICE_UUID, CHARACTERISTIC_NOTIFY_UUID,
                SERVICE_UUID, CHARACTERISTIC_WRITE_UUID, bytes, true, true);
    }

    /**
     * 防丢功能
     *
     * @param context
     * @param on
     */
    public static void antiLost(Context context, boolean on) {
        byte[] bytes = new byte[7];
        bytes[0] = (byte) 0xAB;
        bytes[1] = (byte) 0x00;
        bytes[2] = (byte) 0x04;
        bytes[3] = (byte) 0xFF;
        bytes[4] = (byte) 0x7A;
        bytes[5] = (byte) 0x80;
        bytes[6] = (byte) (on ? 0x01 : 0x00);
        BleUtil.broadcastWriteBle(context, SERVICE_UUID, CHARACTERISTIC_NOTIFY_UUID,
                SERVICE_UUID, CHARACTERISTIC_WRITE_UUID, bytes,true, true);
    }

    /**
     * 久坐提醒
     *
     * @param context
     * @param open
     * @param beginHour
     * @param beginMinute
     * @param endHour
     * @param endMinute
     */
    public static void sedentaryReminder(Context context, boolean open, int beginHour, int beginMinute,
                                         int endHour, int endMinute) {
        byte[] bytes = new byte[11];
        bytes[0] = (byte) 0xAB;
        bytes[1] = (byte) 0x00;
        bytes[2] = (byte) 0x08;
        bytes[3] = (byte) 0xFF;
        bytes[4] = (byte) 0x75;
        bytes[5] = (byte) 0x80;
        bytes[6] = (byte) (open ? 0x01 : 0x00);
        bytes[7] = (byte) beginHour;
        bytes[8] = (byte) beginMinute;
        bytes[9] = (byte) endHour;
        bytes[10] = (byte) endMinute;
        BleUtil.broadcastWriteBle(context, SERVICE_UUID, CHARACTERISTIC_NOTIFY_UUID,
                SERVICE_UUID, CHARACTERISTIC_WRITE_UUID, bytes, true, true);
    }

    /**
     * 勿扰模试
     *
     * @param context
     * @param open        是否开启
     * @param beginHour   开始时
     * @param beginMinute 开始分
     * @param endHour     结束时
     * @param endMinute   结果分
     */
    public static void doNotDisturb(Context context, boolean open, int beginHour, int beginMinute,
                                    int endHour, int endMinute) {
        byte[] bytes = new byte[11];
        bytes[0] = (byte) 0xAB;
        bytes[1] = (byte) 0x00;
        bytes[2] = (byte) 0x08;
        bytes[3] = (byte) 0xFF;
        bytes[4] = (byte) 0x76;
        bytes[5] = (byte) 0x80;
        bytes[6] = (byte) (open ? 0x01 : 0x00);
        bytes[7] = (byte) beginHour;
        bytes[8] = (byte) beginMinute;
        bytes[9] = (byte) endHour;
        bytes[10] = (byte) endMinute;
        BleUtil.broadcastWriteBle(context, SERVICE_UUID, CHARACTERISTIC_NOTIFY_UUID,
                SERVICE_UUID, CHARACTERISTIC_WRITE_UUID, bytes, true, true);
    }

    /**
     * 闹钟
     * @param context
     * @param clockId
     * @param isOpen
     * @param hour
     * @param minute
     * @param repeat
     */
    public static void alarm(Context context, int clockId, boolean isOpen, int hour, int minute, int repeat) {
        byte[] bytes = new byte[11];
        bytes[0] = (byte) 0xAB;
        bytes[1] = (byte) 0x00;
        bytes[2] = (byte) 0x08;
        bytes[3] = (byte) 0xFF;
        bytes[4] = (byte) 0x73;
        bytes[5] = (byte) 0x80;
        bytes[6] = (byte) clockId;
        bytes[7] = isOpen ? (byte) 0x01 : (byte) 0x00;
        bytes[8] = (byte) hour;
        bytes[9] = (byte) minute;
        bytes[10] = (byte) repeat;
        BleUtil.broadcastWriteBle(context, SERVICE_UUID, CHARACTERISTIC_NOTIFY_UUID,
                SERVICE_UUID, CHARACTERISTIC_WRITE_UUID, bytes, true, true);
    }

    /**
     * 设置用户信息
     * @param context
     * @param stepDistance
     * @param age
     * @param height
     * @param weight
     * @param distanceUnitIsKm
     * @param targetStep
     * @param maxHR
     * @param minHR
     */
    public static void setUserInfo(Context context, int stepDistance, int age, int height, int weight,
                                   boolean distanceUnitIsKm, int targetStep, int maxHR, int minHR) {
        byte[] bytes = new byte[14];
        bytes[0] = (byte) 0xAB;
        bytes[1] = (byte) 0x00;
        bytes[2] = (byte) 0x0B;
        bytes[3] = (byte) 0xFF;
        bytes[4] = (byte) 0x74;
        bytes[5] = (byte) 0x80;
        bytes[6] = (byte) stepDistance;
        bytes[7] = (byte) age;
        bytes[8] = (byte) height;
        bytes[9] = (byte) weight;
        bytes[10] = (byte) (distanceUnitIsKm ? 1 : 0);
        bytes[11] = (byte) targetStep;
        bytes[12] = (byte) minHR;
        bytes[13] = (byte) maxHR;
        BleUtil.broadcastWriteBle(context, SERVICE_UUID, CHARACTERISTIC_NOTIFY_UUID,
                SERVICE_UUID, CHARACTERISTIC_WRITE_UUID, bytes);
    }

}
