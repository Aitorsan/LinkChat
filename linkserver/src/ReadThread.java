import java.io.IOException;
import java.util.Map;

import link.net.connection.Connection;
import link.net.message.Message;
import link.net.protocol.FrameType;
import link.net.protocol.Protocol;
import utils.concurrent.containers.MsgQueue;
import utils.concurrent.logging.LoggerPanel;

class ReadThread implements Runnable {

    private Server server;

    public ReadThread(Server server) {
        this.server = server;
    }

    @Override
    public void run() {

        MsgQueue<Message> queue = server.getQueue();
        Map<String, Connection> clientsConnected = server.getConnections();
        while (!server.isClosed()) {

            if (queue.hasPendingMessages()) {
                Message m = queue.getMessage();

                switch (m.type) {

                    case MESSAGE: {

                        LoggerPanel.getInstance().Log(LoggerPanel.LEVEL.INFO,m.length + ":" + m.destiny + ":" + m.message);
                        if (clientsConnected.containsKey(m.destiny)) {
                            Connection c = clientsConnected.get(m.destiny);
                            if (!c.isClosed()) {
                                try {
                                    Protocol.sendMessage(m, c.getOutputStream());
                                } catch (Exception e) {
                                    LoggerPanel.getInstance().Log(LoggerPanel.LEVEL.ERROR,"delivering message to " + c.getName());
                                }
                            } else {
                                clientsConnected.remove(c.getName());
                                for (Map.Entry<String, Connection> entry : clientsConnected.entrySet()) {
                                    try {
                                        Protocol.sendUserState(c.getName(), FrameType.USER_DISCONNECTED,
                                                entry.getValue().getOutputStream());
                                    } catch (IOException e) {
                                        LoggerPanel.getInstance().Log(LoggerPanel.LEVEL.ERROR,"delivering message to " + entry.getKey());
                                    }
                                }
                            }

                        }

                    }
                        break;
                    case USER_DISCONNECTED: {
                        clientsConnected.remove(m.message);
                        for (Map.Entry<String, Connection> entry : clientsConnected.entrySet()) {
                            try {
                                Protocol.sendUserState(m.message, FrameType.USER_DISCONNECTED,
                                        entry.getValue().getOutputStream());
                            } catch (IOException e) {
                                LoggerPanel.getInstance().Log(LoggerPanel.LEVEL.ERROR, "delivering message to " + entry.getKey());
                            }
                        }
                    }
                        break; default: { /* do nothing discard the message*/ }break;
                }
            }
            else
            {
                synchronized(queue){
                    try {
                        queue.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
             
            }

        }
       
        LoggerPanel.getInstance().Log(LoggerPanel.LEVEL.INFO,"Socket closed");
    }
}