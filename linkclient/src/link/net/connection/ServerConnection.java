package link.net.connection;
import java.io.DataInputStream;
import java.net.Socket;

import link.net.message.Message;
import link.net.protocol.Protocol;

 public class ServerConnection{

    Socket socket;
    String name;
    DataInputStream readStream;

    public ServerConnection(String name) {
        this.name = name;
    }

   public synchronized boolean isConnected(){
      return !socket.isClosed();
   }

    public Message readMessage() {
        
        try{
            return Protocol.readMessage(readStream);
        }
        catch(Exception e){

            System.out.println("ERROR:Can't read from server on socket connection:" + name);
            closeConnection();

        }
        return null;
    }

    public void SendMessage(Message message){
        try{
        
            Protocol.sendMessage(message, socket.getOutputStream());
        }catch(Exception e){
            System.out.println("ERROR: Failed to send the message from :" + name);
        }

    }

    public boolean connect(String server, int port) {
        try {

            socket = new Socket(server, port);
            readStream = new DataInputStream(socket.getInputStream());
            System.out.println("Connected to server...");
            Protocol.sendRegistrationMessage(name, socket.getOutputStream());
            System.out.println("verification messaged sent...");
            return true;
        } catch (Exception e) {
            closeConnection();
            System.out.println("ERROR:Can't connect to server: " + server + ":" + port);
        }

        return false;
    }

    public void closeConnection() {
        try {
            socket.close();
        } catch (Exception e) {
            System.out.println("ERROR:Failed on closing connection " + this.name);
        }
    }
}