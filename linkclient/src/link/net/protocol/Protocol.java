package link.net.protocol;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import link.net.message.Message;



public class Protocol {
    public static final int MAX_MESSAGE_LENGTH = 65535;

    static private FrameType fromTypeByteToEnum(byte t) {
        FrameType type = FrameType.ERROR;
        switch (t) {
            case 0x01:
                type = FrameType.MESSAGE;
                break;
            case 0x02:
                type = FrameType.REGISTRATION;
                break;
            case 0x03:
                type = FrameType.USER_CONNECTED;
                break;
            case 0x04:
                type = FrameType.USER_DISCONNECTED;
                break;
            case 0x05:
                type = FrameType.INVALID_USER_NAME;
                break;
        }
        return type;

    }

    static private byte fromTypeEnumToByte(FrameType msgType) {
        byte type = 0;
        if (msgType == FrameType.MESSAGE) {
            type = 0x01;
        } else if (msgType == FrameType.REGISTRATION) {
            type = 0x02;
        } else if (msgType == FrameType.USER_CONNECTED) {
            type = 0x03;
        } else if (msgType == FrameType.USER_DISCONNECTED) {
            type = 0x04;
        }
        else if (msgType == FrameType.INVALID_USER_NAME) {
            type = 0x05;
        }
        return type;
    }

    static public void sendRegistrationMessage(String registrationName, OutputStream out) throws IOException{
        
        if (registrationName.isEmpty()){
            throw new IOException("Can't send empty verification message");
        }
        // write length prefix
        if (registrationName.length() > MAX_MESSAGE_LENGTH) {
            throw new IOException("message too long");
        }
        // 4 bytes lenght | 1 byte code | Registartion name
        ByteBuffer buffer = ByteBuffer.allocate(5 + registrationName.getBytes().length);
        buffer.putInt(registrationName.getBytes().length);
        buffer.put(fromTypeEnumToByte(FrameType.REGISTRATION));
        buffer.put(registrationName.getBytes());
        out.write(buffer.array());
        out.flush();

    }

    static public void sendMessage(Message msg , OutputStream out) throws IOException {

        if (msg.message.isEmpty()){
            throw new IOException("Can't send message.The message is empty");
        }
         // write length prefix
         if (msg.length > MAX_MESSAGE_LENGTH) {
            throw new IOException("message too long");
        }

        if ( msg.type == FrameType.MESSAGE && msg.destiny.isEmpty() && msg.from.isEmpty()){
            throw new IOException("MESSAGE type does not contain destinatary");
        }
        // 4 bytes lenght | 1 byte code | 4 byte destiny size name| 4 bytes from sisze name|  to + from +  Message
        ByteBuffer buffer = ByteBuffer.allocate(13 + msg.from.getBytes().length + msg.destiny.getBytes().length  + msg.length);
        buffer.putInt(msg.destiny.getBytes().length + msg.from.getBytes().length + msg.length);
        buffer.put(fromTypeEnumToByte(msg.type));
        buffer.putInt(msg.destiny.getBytes().length);
        buffer.putInt(msg.from.getBytes().length);
        buffer.put(msg.destiny.getBytes());
        buffer.put(msg.from.getBytes());
        buffer.put(msg.message.getBytes());
        out.write(buffer.array());
        out.flush();
    }

    static public Message readMessage(DataInputStream in) throws IOException {

        Message msg = new Message();
        msg.length = in.readInt();

        if (msg.length > MAX_MESSAGE_LENGTH) {
            System.out.println("ERROR on reading message size, is too long: " + msg.length);
            return null;
        }
        msg.type = fromTypeByteToEnum(in.readByte());

        if (msg.type == FrameType.MESSAGE) {
            int destinyNameSize = in.readInt();
            if (destinyNameSize > MAX_MESSAGE_LENGTH){
                System.out.println("ERROR on reading destiny name size: " + destinyNameSize);
                return null;
            }

            int fromNameSize = in.readInt();
            if (fromNameSize > MAX_MESSAGE_LENGTH){
                throw new IOException("Size of the destination name is too long: " + fromNameSize);
            }

            byte[] nameBytes = new byte[destinyNameSize];
            in.read(nameBytes, 0, destinyNameSize);
            msg.destiny = new String(nameBytes).trim();

            byte[] fromBytes = new byte[fromNameSize];
            in.read(fromBytes, 0, fromNameSize);
            msg.from = new String(fromBytes).trim();
        }
        byte[] body = new byte[msg.length];
        in.read(body, 0,msg.length );
        msg.message = new String(body).trim();

        return msg;
    }

}
