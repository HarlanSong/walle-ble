package cn.songhaiqing.walle.ble.parse;

import android.text.TextUtils;
import java.util.List;
import cn.songhaiqing.walle.ble.utils.LogUtil;
import cn.songhaiqing.walle.ble.utils.StringUtil;

public class CareeachNB202Parse {
    private final String TAG = "CareeachNB202Parse";
    private CareeachResult careeachResult;


    public CareeachNB202Parse(CareeachResult careeachResult) {
        this.careeachResult = careeachResult;
    }

    public void parse(byte[] srcData) {
        final int BLE_RESULT_TYPE_BATTERY = 0X91; // 电量
        final int BLE_RESULT_TYPE_VERSION = 0X92; // 版本信息
        final int BLE_RESULT_TYPE_FIND_PHONE = 0x7D;    // 查找手机
        final int BLE_RESULT_TYPE_SLEEP = 0x52;             // 睡眠
        final int BLE_RESULT_TYPE_MEASUREMENT_HR = 0x84;  // 测量心率
        final int BLE_RESULT_TYPE_NOW = 0x51;
        final int BLE_RESULT_TYPE_HISTORY_HR = 0x11;
        final int BLE_RESULT_TYPE_INTEGRAL_POINT = 0x20; // 整点数据
        final int BLE_RESULT_TYPE_CONSTANTLY = 0x08;
        if (srcData == null || srcData.length == 0 || careeachResult == null) {
            return;
        }
        List<String> hexList = StringUtil.bytesToStringArrayList(srcData);
        List<Integer> intData = StringUtil.bytesToArrayList(srcData);
        int type = intData.get(4);
        LogUtil.i(TAG, "Ble result Type:" + Integer.toHexString(type));
        if (BLE_RESULT_TYPE_NOW == type) {
            if (intData.get(5) == BLE_RESULT_TYPE_HISTORY_HR) {
                parseDepartedHeartRate(intData);
            } else if (BLE_RESULT_TYPE_CONSTANTLY == intData.get(5)) {
                // 时时数据
                parseConstantly(hexList);
            } else if (BLE_RESULT_TYPE_INTEGRAL_POINT == intData.get(5)) {
                // 整点存储值
                parseDepartedSport(hexList);
            }
        } else if (type == BLE_RESULT_TYPE_SLEEP && intData.size() == 14) {
            parseDepartedSleep(intData);
        } else if (type == BLE_RESULT_TYPE_BATTERY) {
            parseBattery(intData);
        } else if (type == BLE_RESULT_TYPE_VERSION) {
            parseVersion(intData);
        } else if (type == BLE_RESULT_TYPE_MEASUREMENT_HR) {
            parseMeasurementHR(intData);
        } else if (type == BLE_RESULT_TYPE_FIND_PHONE) {
            parseFindPhone(intData);
        }
    }

    private void parseBattery(List<Integer> hexData) {
        int battery = hexData.get(7);
        careeachResult.battery(battery);
    }

    private void parseVersion(List<Integer> hexData) {
        int versionCode = hexData.get(8);
        double versionName = hexData.get(6) + hexData.get(7) / 100d;
        careeachResult.firmwareVersion(versionCode, String.valueOf(versionName));
    }

    /**
     * 整点数据
     *
     * @param hexData
     */
    private void parseDepartedSport(List<String> hexData) {
        StringBuilder time = new StringBuilder();
        time.append("20");
        time.append(StringUtil.fillZero(Integer.parseInt(hexData.get(6), 16), 2));
        time.append("-");
        time.append(StringUtil.fillZero(Integer.parseInt(hexData.get(7), 16), 2));
        time.append("-");
        time.append(StringUtil.fillZero(Integer.parseInt(hexData.get(8), 16), 2));
        time.append(" ");
        time.append(StringUtil.fillZero(Integer.parseInt(hexData.get(9), 16), 2));
        time.append(":00:00");
        int step = Integer.parseInt(TextUtils.join("", hexData.subList(10, 13)), 16);
        int calorie = Integer.parseInt(TextUtils.join("", hexData.subList(13, 16)), 16);
        int distance = (int) Math.round(step * 0.7d);
        careeachResult.departedSport(time.toString(), step, calorie, distance);
    }

    private void parseDepartedSleep(List<Integer> hexData) {
        StringBuilder time = new StringBuilder();
        time.append("20");
        time.append(hexData.get(6));
        time.append("-");
        time.append(StringUtil.fillZero(hexData.get(7), 2));
        time.append("-");
        time.append(StringUtil.fillZero(hexData.get(8), 2));
        time.append(" ");
        time.append(StringUtil.fillZero(hexData.get(9), 2));
        time.append(":");
        time.append(StringUtil.fillZero(hexData.get(10), 2));
        time.append(":00");
        int type = hexData.get(11);
        int sleepTime = hexData.get(12) * 256 + hexData.get(13);
        careeachResult.departedSleep(time.toString(), type, sleepTime);
    }

    /**
     * 历史心率
     *
     * @param hexData
     */
    private void parseDepartedHeartRate(List<Integer> hexData) {
        StringBuilder time = new StringBuilder();
        time.append("20");
        time.append(hexData.get(6));
        time.append("-");
        time.append(StringUtil.fillZero(hexData.get(7), 2));
        time.append("-");
        time.append(StringUtil.fillZero(hexData.get(8), 2));
        time.append(" ");
        time.append(StringUtil.fillZero(hexData.get(9), 2));
        time.append(":");
        time.append(StringUtil.fillZero(hexData.get(10), 2));
        time.append(":00");
        int hr = hexData.get(11);
        careeachResult.departedHeartRate(time.toString(), hr);
    }

    private void parseConstantly(List<String> hexData) {
        int step = Integer.parseInt(TextUtils.join("", hexData.subList(6, 9)), 16);
        int calorie = Integer.parseInt(TextUtils.join("", hexData.subList(9, 12)), 16);
        int distance = (int) Math.round(step * 0.7d);
        int shallowSleepHour = Integer.parseInt(hexData.get(12), 16);
        int shallowSleepMinute = Integer.parseInt(hexData.get(13), 16);
        int deepSleepHour = Integer.parseInt(hexData.get(14), 16);
        int deepSleepMinute = Integer.parseInt(hexData.get(15), 16);
        int wakeUpNumber = Integer.parseInt(hexData.get(16), 16);
        careeachResult.dataNow(step, calorie, distance, shallowSleepHour, shallowSleepMinute, deepSleepHour, deepSleepMinute, wakeUpNumber);
    }

    private void parseMeasurementHR(List<Integer> hexData) {
        int heartRate = hexData.get(6);
        careeachResult.measurementHR(heartRate);
    }

    private void parseFindPhone(List<Integer> hexData) {
        int type = hexData.get(6);
        if (type == 0) {
            careeachResult.findPhone(false);
        } else {
            careeachResult.findPhone(true);
        }
    }

    public interface CareeachResult {

        /**
         * 电量
         *
         * @param battery 电量百分比
         */
        void battery(int battery);

        /**
         * 固件版本
         *
         * @param versionCode 版本号
         * @param versionName 版本名
         */
        void firmwareVersion(int versionCode, String versionName);

        /**
         * 时时测量心率
         *
         * @param heartRate 心率
         */
        void measurementHR(int heartRate);

        /**
         * 查询手机
         *
         * @param start true 开始；false结束
         */
        void findPhone(boolean start);

        /**
         * 时时数据
         *
         * @param step               步数
         * @param calorie            卡路里
         * @param distance           距离
         * @param shallowSleepHour   浅睡小时
         * @param shallowSleepMinute 浅睡分钟
         * @param deepSleepHour      深睡小时
         * @param deepSleepMinute    沉睡分钟
         * @param wakeUpNumber       清醒次数
         */
        void dataNow(int step, int calorie, int distance, int shallowSleepHour, int shallowSleepMinute,
                     int deepSleepHour, int deepSleepMinute, int wakeUpNumber);

        /**
         * 历史心率数据
         *
         * @param time      时间（yyyy-MM-dd HH:mm:ss）
         * @param heartRate 心率
         */
        void departedHeartRate(String time, int heartRate);

        /**
         * 历史睡眠
         *
         * @param time      时间（yyyy-MM-dd HH:mm:ss）
         * @param type      类型
         * @param sleepTime 持续时间
         */
        void departedSleep(String time, int type, int sleepTime);

        /**
         * 历史运动数据
         *
         * @param time     时间（yyyy-MM-dd HH:mm:ss）
         * @param step     步数
         * @param calorie  卡路里
         * @param distance 距离（米）
         */
        void departedSport(String time, int step, int calorie, int distance);
    }
}
