package link.net.connection;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketAddress;

import link.net.message.Message;
import link.net.protocol.FrameType;
import link.net.protocol.Protocol;
import utils.concurrent.containers.MsgQueue;
import utils.concurrent.logging.LoggerPanel;


public class Connection implements Runnable {

    private String name;
    private Socket clientSocket;
    private SocketAddress address;
    private OutputStream out;
    private DataInputStream in;
    private MsgQueue<Message> queue;

    public Connection(String name, Socket socket, MsgQueue<Message> queue) throws Exception {
        this.name = name;
        this.clientSocket = socket;
        this.out = socket.getOutputStream();
        this.in = new DataInputStream(socket.getInputStream());
        this.address = socket.getRemoteSocketAddress();
        this.queue = queue;
        LoggerPanel.getInstance().Log(LoggerPanel.LEVEL.INFO,"handling client at " + this.address.toString());
    }

    public synchronized OutputStream  getOutputStream() {
        return this.out;
    }

    public void closeConnection() {
        try {
            clientSocket.close();
        } catch (Exception e) {
            LoggerPanel.getInstance().Log(LoggerPanel.LEVEL.INFO,"Failed to close socket: " + address.toString() + ", name:" + name);
        }
    }

    public String getName() {
        return name;
    }

    @Override
    public void run() {

        while (!isClosed()) {

            try {
                Message msg = Protocol.readMessage(in);
                queue.addMessage(msg);
             
            } catch (IOException e) {
                closeConnection();
                Message m = new Message();
                m.message = name;
                m.length = m.message.length();
                m.type = FrameType.USER_DISCONNECTED;
                queue.addMessage(m);
                LoggerPanel.getInstance().Log(LoggerPanel.LEVEL.WARN,"Closing socket" + address + ", Failed to read name:" + name);
            }
            finally
            {
                synchronized(queue){
                    queue.notify();
                }
            }
        }

    }

    public synchronized boolean isClosed() {
        return clientSocket.isClosed();
    }

}