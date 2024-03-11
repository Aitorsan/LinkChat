import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Map;

import link.net.connection.Connection;
import link.net.message.Message;
import link.net.protocol.FrameType;
import link.net.protocol.Protocol;
import utils.concurrent.containers.MsgQueue;
import utils.concurrent.logging.LoggerPanel;

public class HandleNewClient implements Runnable {

    private Map<String, Connection> connections;
    private Socket socketToHandle;
    MsgQueue<Message> msgQueue;

    public HandleNewClient(Map<String, Connection> map, Socket socket, MsgQueue<Message> queue) {
        this.connections = map;
        this.socketToHandle = socket;
        this.msgQueue = queue;
    }

    @Override
    public void run() {

        boolean succeed = true;
        try {

            Message msg = Protocol.readMessage(new DataInputStream(socketToHandle.getInputStream()));
            if (msg.type == FrameType.REGISTRATION) {

                Connection c = new Connection(msg.message, socketToHandle, msgQueue);
                if (connections.containsKey(c.getName())) {
                    Connection old = connections.get(c.getName());
                    if (!old.isClosed()) {
                        LoggerPanel.getInstance().Log(LoggerPanel.LEVEL.ERROR,"Failed to connect because username "+c.getName()+" already exists");
                        Protocol.sendServerInvalidUserNameMessage("Failed to connect because username "+c.getName()+" already exists", c.getOutputStream());
                        c.closeConnection();
                        return;
                    }
                    connections.remove(old.getName());
                }

                String connectedUsersList = new String(c.getName().trim() + ":");

                for (Map.Entry<String, Connection> entry : connections.entrySet()) {
                    if (!entry.getValue().isClosed()) {
                        connectedUsersList += entry.getKey() + ":";
                    } else {
                        // notify clients this guy is disconnected
                        Connection diconnectedUser = connections.remove(entry.getKey());
                        for (Map.Entry<String, Connection> connection : connections.entrySet()) {
                            // could be there are more disconnected clients so we need to check
                            if (!connection.getValue().isClosed()){
                                Protocol.sendUserState(diconnectedUser.getName(), FrameType.USER_DISCONNECTED,
                                connection.getValue().getOutputStream());
                            }
                
                        }
                    }
                }

                // send the list of connected users to the new client
                Protocol.sendUserState(connectedUsersList,FrameType.USER_CONNECTED, c.getOutputStream());
                // notify other users of the new connected user
                for (Map.Entry<String, Connection> entry : connections.entrySet()) {
                    Protocol.sendUserState(c.getName(), FrameType.USER_CONNECTED, entry.getValue().getOutputStream());
                }
                connections.put(c.getName(), c);
                Thread t = new Thread(c);
                t.start();
            } else {
                String error = "NOT REGISTRATION MESSAGE RECEIVED CLOSING CLIENT!";
                LoggerPanel.getInstance().Log(LoggerPanel.LEVEL.ERROR, error);
                Protocol.sendServerErrorMessage(error, socketToHandle.getOutputStream());
                throw new IOException(error);
            }

        } catch (Exception e) {
            e.printStackTrace();
            succeed = false;
        }

        if (!succeed) {
            try {
                socketToHandle.close();
            } catch (Exception e) {

            }
        }
    }
}