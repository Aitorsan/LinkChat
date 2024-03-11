package link.net.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import link.net.connection.ServerConnection;
import link.net.message.Message;
import link.net.protocol.FrameType;

public class ChatService implements Runnable{

    String userName;
    int port;
    String serverAddress;
    ServerConnection connection;
    List<ReadCallBack> readCallbacks;

    public ChatService(String serverAddress, int port){
        this.serverAddress = serverAddress;
        this.port = port;
        this.readCallbacks = Collections.synchronizedList(new ArrayList<ReadCallBack>());
    }
    public ChatService(String serverAddress){
        this.serverAddress = serverAddress;
        this.port = 7;
        this.readCallbacks = Collections.synchronizedList(new ArrayList<ReadCallBack>());
    }

    public void registerUser(String name){
        this.userName = name;
    }

     // We need to use a synchronizes list because there exists the posibility to add callbacks
     // while the read thread is iterating through the list
    public void registerReadCallback(ReadCallBack callback){
        if (callback != null) this.readCallbacks.add(callback);
    }

    public void start() throws Exception{

        if (userName.isEmpty()) {
            throw new Exception(
                    "[ERROR]Can't start service without registering User please call registerUser before starting the service");
        }
        connection = new ServerConnection(this.userName);
        if (!connection.connect(this.serverAddress, this.port)) {
            throw new Exception("Faile to connect with the server");
        }

        Thread readingThread = new Thread(this);
        readingThread.setName("ChatService reading thread");
        readingThread.start();
        

    }

    @Override
    public void run() {

        while (isRunning()) {

            Message m = connection.readMessage();
            if (m == null) {
                m = new Message();
                m.type = FrameType.ERROR;
            }

            for (ReadCallBack callback : readCallbacks) {
                callback.onMessageReceived(m);
            }
        }

    }

    public void sendMessage(Message msg){
        if (connection.isConnected()){
            connection.SendMessage(msg);
        }
    }

    public String getRegisteredUser(){
        return userName;
    }

    public boolean isRunning(){
        return connection.isConnected();
    }

    public void shutDownService(){
        connection.closeConnection();
    }

}