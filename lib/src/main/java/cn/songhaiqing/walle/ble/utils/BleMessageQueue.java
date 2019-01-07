package cn.songhaiqing.walle.ble.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import cn.songhaiqing.walle.ble.bean.BleTaskMessage;

/**
 * 蓝牙任务对列
 */
public class BleMessageQueue {
    private final String TAG = getClass().getName();
    private List<BleTaskMessage> bleTaskMessages;
    private boolean isRunning;
    private BleExecute bleExecute;
    private Timer messageTimer;
    private TimerTask messageTimerTask;
    private Long executeUpdateTime;

    public BleMessageQueue(BleExecute bleExecute) {
        bleTaskMessages = new ArrayList<>();
        this.bleExecute = bleExecute;
        isRunning = false;
    }

    public synchronized void addTask(String writeServiceUUID, String writeCharacteristicUUID, String notifyServiceUUID,
                                     String notifyCharacteristicUUID, boolean write, byte[] content,boolean isSegmentation) {
        BleTaskMessage bleTaskMessage = new BleTaskMessage();
        bleTaskMessage.setWriteServiceUUID(writeServiceUUID);
        bleTaskMessage.setWriteCharacteristicUUID(writeCharacteristicUUID);
        bleTaskMessage.setNotifyServiceUUID(notifyServiceUUID);
        bleTaskMessage.setNotifyCharacteristicUUID(notifyCharacteristicUUID);
        bleTaskMessage.setWrite(write);
        bleTaskMessage.setContent(content);
        bleTaskMessage.setSegmentation(isSegmentation);
        if(bleTaskMessages.contains(bleTaskMessage)){
            LogUtil.d(TAG, "添加的重复命令，已跳过");
            return;
        }
        bleTaskMessages.add(bleTaskMessage);
        LogUtil.d(TAG, "新增命令任务，添加后任务数量：" + bleTaskMessages.size() + " 执行状态：" + isRunning);
        if (!isRunning) {
            execute();
        }
    }

    public void execute() {
        if(bleTaskMessages == null || bleTaskMessages.isEmpty() ){
            return;
        }
        isRunning = true;
        LogUtil.d(TAG, "开始发送命令，当前任务数量：" + bleTaskMessages.size());
        BleTaskMessage bleTaskMessage = bleTaskMessages.get(0);
        bleTaskMessages.remove(0);
        if (bleTaskMessage.isWrite()) {
            bleExecute.messageQueueWrite(bleTaskMessage.getNotifyServiceUUID(), bleTaskMessage.getNotifyCharacteristicUUID(),
                    bleTaskMessage.getWriteServiceUUID(), bleTaskMessage.getWriteCharacteristicUUID(),
                    bleTaskMessage.getContent(), bleTaskMessage.isSegmentation());
        } else {
            bleExecute.messageQueueRead(bleTaskMessage.getWriteServiceUUID(), bleTaskMessage.getWriteCharacteristicUUID());
        }
        executeUpdateTime = System.currentTimeMillis();
        startMessageTask();
    }

    /**
     * 已结束一个任务，进入下一个命令发送，如任务为空则结束
     */
    public void next() {
        isRunning = false;
        LogUtil.d(TAG, "next");
        stopMessageTask();
        if (bleTaskMessages.isEmpty()) {
            return;
        }
        isRunning = true;
        execute();
    }

    public void clear() {
        stopMessageTask();
        bleTaskMessages.clear();
        isRunning = false;
    }

    private void startMessageTask() {
        if (messageTimer == null) {
            messageTimer = new Timer();
        }
        if (messageTimerTask == null) {
            messageTimerTask = new TimerTask() {
                @Override
                public void run() {
                    if (System.currentTimeMillis() - executeUpdateTime < 2000) {
                        return;
                    }
                    next();
                }
            };
        }
        messageTimer.schedule(messageTimerTask, WalleBleConfig.getBleResultWaitTime(), WalleBleConfig.getBleResultWaitTime());
    }

    private void stopMessageTask() {
        if (messageTimerTask != null) {
            messageTimerTask.cancel();
            messageTimerTask = null;
        }
        if (messageTimer != null) {
            messageTimer.cancel();
            messageTimer = null;
        }
    }

    public void refreshExecuteUpdateTime() {
        executeUpdateTime = System.currentTimeMillis();
    }

    public interface BleExecute {
        void messageQueueRead(String serviceUUID, String characteristicUUID);

        void messageQueueWrite(String notifyServiceUUID, String notifyCharacteristicUUID,
                               String writeServiceUUID, String writeCharacteristicUUID,
                               byte[] content, boolean segmentationContent);
    }
}
