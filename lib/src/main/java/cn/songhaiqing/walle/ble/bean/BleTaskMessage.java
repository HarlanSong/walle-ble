package cn.songhaiqing.walle.ble.bean;

import java.util.Arrays;
import java.util.Objects;

/**
 * 蓝牙任务对列内容
 */
public class BleTaskMessage {
    private String writeServiceUUID;
    private String writeCharacteristicUUID;
    private String notifyServiceUUID;
    private String notifyCharacteristicUUID;
    private boolean write; // write or read
    private boolean segmentationContent;
    private byte[] content;

    public String getWriteServiceUUID() {
        return writeServiceUUID;
    }

    public void setWriteServiceUUID(String writeServiceUUID) {
        this.writeServiceUUID = writeServiceUUID;
    }

    public String getWriteCharacteristicUUID() {
        return writeCharacteristicUUID;
    }

    public void setWriteCharacteristicUUID(String writeCharacteristicUUID) {
        this.writeCharacteristicUUID = writeCharacteristicUUID;
    }

    public String getNotifyServiceUUID() {
        return notifyServiceUUID;
    }

    public void setNotifyServiceUUID(String notifyServiceUUID) {
        this.notifyServiceUUID = notifyServiceUUID;
    }

    public String getNotifyCharacteristicUUID() {
        return notifyCharacteristicUUID;
    }

    public void setNotifyCharacteristicUUID(String notifyCharacteristicUUID) {
        this.notifyCharacteristicUUID = notifyCharacteristicUUID;
    }

    public boolean isWrite() {
        return write;
    }

    public void setWrite(boolean write) {
        this.write = write;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public boolean isSegmentationContent() {
        return segmentationContent;
    }

    public void setSegmentationContent(boolean segmentationContent) {
        this.segmentationContent = segmentationContent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BleTaskMessage that = (BleTaskMessage) o;
        return write == that.write &&
                segmentationContent == that.segmentationContent &&
                Objects.equals(writeServiceUUID, that.writeServiceUUID) &&
                Objects.equals(writeCharacteristicUUID, that.writeCharacteristicUUID) &&
                Objects.equals(notifyServiceUUID, that.notifyServiceUUID) &&
                Objects.equals(notifyCharacteristicUUID, that.notifyCharacteristicUUID) &&
                Arrays.equals(content, that.content);
    }

    @Override
    public int hashCode() {

        int result = Objects.hash(writeServiceUUID, writeCharacteristicUUID, notifyServiceUUID, notifyCharacteristicUUID, write, segmentationContent);
        result = 31 * result + Arrays.hashCode(content);
        return result;
    }
}
