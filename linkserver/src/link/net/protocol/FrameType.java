package link.net.protocol;

public enum FrameType {
    ERROR , // 0x00
    MESSAGE, // 0x01
    REGISTRATION, // 0x02
    USER_CONNECTED, // 0x03
    USER_DISCONNECTED,//0x04
    INVALID_USER_NAME//0x05
}