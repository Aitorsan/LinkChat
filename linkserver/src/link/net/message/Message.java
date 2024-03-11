package link.net.message;

import link.net.protocol.*;


public class Message {
     public int length;// in bytes really importatn
     public FrameType type;
     public String from;
     public String message;
     public String destiny;
    
    public Message(){

    }
    String getType(){

        switch(type){
            case ERROR: return "ERROR";
            case MESSAGE: return "MESSAGE";
            case REGISTRATION: return "REGISTRATION";
            case USER_CONNECTED: return "USER_CONNECTED";
            case USER_DISCONNECTED: return "USER_DISCONNECTED";
            case INVALID_USER_NAME: return "INVALID_USER_NAME";
        }
        return "UNKNOWN";
    }
}
