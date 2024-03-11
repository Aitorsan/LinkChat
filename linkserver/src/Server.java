import java.io.IOException;
import java.lang.System.Logger;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import link.net.connection.Connection;
import link.net.message.Message;

import utils.concurrent.containers.MsgQueue;
import utils.concurrent.logging.LoggerPanel;
import utils.concurrent.logging.LoggerPanel.LEVEL;


public class Server {

    private Map<String, Connection> clientsConnected;
    private ServerSocket servSock;
    private MsgQueue<Message> queue;
    ExecutorService threadExecutor;

    public Server() {

        clientsConnected = Collections.synchronizedMap(new HashMap<String, Connection>());
        queue = new MsgQueue<Message>(new LinkedList<Message>());
    }

    public void ShutDown() {
        try {
            LoggerPanel.getInstance().Log(LEVEL.WARN, "Server closed");
            for (Map.Entry<String, Connection> connection : clientsConnected.entrySet()) {
                if (!connection.getValue().isClosed()){
                    connection.getValue().closeConnection();
                }
            }

            servSock.close();
            threadExecutor.shutdown();
        } catch (Exception e) {
            threadExecutor.shutdownNow();
        }
    }

    public void run(int port) throws Exception{
        // Create executor
        this.threadExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 2); 
        
        try {
            // Create a server socket to accept client connection requests
            servSock = new ServerSocket(port);
            // create reading thread
            Thread readThread = new Thread(new ReadThread(this));
            readThread.start();

            while (true) { // Run forever, accepting and servicing connections
                Socket clntSock = servSock.accept();
                threadExecutor.submit(new HandleNewClient(this.clientsConnected, clntSock, queue));
            }
        } catch (IOException e) {
            servSock.close();
            threadExecutor.shutdown();
        }
    }

    public boolean isClosed() {
        return servSock.isClosed();
    }

    public MsgQueue<Message> getQueue() {
        return queue;
    }

    public Map<String, Connection> getConnections() {
        return clientsConnected;
    }
}
