package cn.songhaiqing.walle.ble.bean;

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
}
