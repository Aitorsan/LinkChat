import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import link.net.message.Message;
import link.net.protocol.FrameType;
import link.net.service.*;
import view.AppMainView;
import view.ChatPanel;
import view.ContactsPanel;
import view.LoginPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;

public class ChatAppController extends WindowAdapter implements ActionListener, ReadCallBack {

    private enum Actions {
        CONNECTED,
        MESSAGE_SEND,
        OPEN_CHAT
    }

    //view
    AppMainView mainView;
    LoginPanel loginPanel;
    ContactsPanel contactsPanel;

    //model
    ChatService service;
    Map<String, ChatPanel> openConversations;  // keep a map of user and chat windows

    public ChatAppController() {
        this.openConversations = new HashMap<>();
        this.contactsPanel = new ContactsPanel(this);
        this.loginPanel = new LoginPanel();
        loginPanel.setEventListener(this, Actions.CONNECTED.name());
        this.mainView = new AppMainView(loginPanel, contactsPanel);
        this.mainView.addWindowListener(this);
    }

    protected void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
                service.shutDownService();
                System.exit(0);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getActionCommand() == Actions.CONNECTED.name()) {
            int port = 62662;
            try {
                port = Integer.parseInt(loginPanel.getPort());
            } catch (NumberFormatException numberException) {
                JOptionPane.showMessageDialog(null, "Invalid port number, please introduce a number", "Warning",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
        
            service = new ChatService(loginPanel.getServerAddress(), port);
            String userName = loginPanel.getUserName();
            if (userName.length() > 20 || userName.isEmpty()) {
                JOptionPane.showMessageDialog(null, "User name to long or empty, please introduce a valid name\n" + userName
                        + " is greater than 20 characters or empty", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            service.registerUser(userName);
            service.registerReadCallback(this);
            contactsPanel.setOwner(userName);

            System.out.println("Action performed thread:" + Thread.currentThread().getName());
            // Big tasks must be performed outisde the GUI thread to avoid freezing the GUI
            SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {
                @Override
                protected Void doInBackground() throws Exception {
                    publish("Connecting to the server");

                    try{
                        service.start();
                    }catch(Exception ex){
                        JOptionPane.showMessageDialog(null, "Failed to connect with the server, maybe is down", "ERROR", JOptionPane.ERROR_MESSAGE);
                    }
                    return null;
                }

                @Override
                protected void done() {

                    if (isCancelled()) {
                        service.shutDownService();
                    }
                }

            };

            worker.execute();

        } else if (e.getActionCommand() == Actions.MESSAGE_SEND.name()) {
            JTextField w = (JTextField) e.getSource();
            Message msg = new Message();
            msg.type = FrameType.MESSAGE;
            msg.from = service.getRegisteredUser();
            msg.destiny = w.getName();
            msg.message = w.getText();
            msg.length = msg.message.getBytes().length;
            // we only add a message localy if the message is not send to ourselves so
            // we don't duplicate the mesasge. We could avoid sending the message to the
            // server but that might have advantages. E.g if we want to save history data
            // or restore this account in different computer. NOTE:Feature Not supported yet
            if (!msg.destiny.equals(msg.from)){
                openConversations.get(msg.destiny).addLocalMessage(msg.from, msg.message);
            }
            service.sendMessage(msg);
   
      
            w.setText("");
        } else if (e.getActionCommand() == Actions.OPEN_CHAT.name()) {
            JButton w = (JButton) e.getSource();
            contactsPanel.openChat(w.getText());
        }
    }

    void executeActionOnNewUserConnected(String[] contacts) {
        // This method changes the gui so it needs to be updated on the event queue
        // thread
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                System.out.println("onUserConnected:" + Thread.currentThread().getName());

                for (String contact : contacts){
                    contactsPanel.addUser(contact, Actions.OPEN_CHAT.name());
                    createWindowIfNotExist(contact);
                }
                mainView.setResizable(true);
                mainView.showContactsView();
                mainView.setVisible(true);
                
            }
        });
    }

    void executeActionOnUserDisconnected(String user) {
        // This method changes the gui so it needs to be updated on the event queue
        // thread
  
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                System.out.println("onUserConnected:" + Thread.currentThread().getName());
                contactsPanel.removeUser(user);
                if (openConversations.containsKey(user)){
                    openConversations.remove(user);
                }
                JOptionPane.showMessageDialog(mainView,  user + " left the chat",user, JOptionPane.INFORMATION_MESSAGE);


                mainView.setVisible(true);
            }
        });
    }

    void executeOnMessageReceived(String from, String message) {
        // This method changes the gui so it needs to be updated on the event queue
        // thread
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                System.out.println("onMessageReceived:" + Thread.currentThread().getName());
                createWindowIfNotExist(from);
                openConversations.get(from).addIncomingMessage(from, message);
                contactsPanel.setNotificationColor(from);

            }
        });
    }

    public void createWindowIfNotExist(String user) {

        if (!openConversations.containsKey(user)) {
            ChatPanel win = new ChatPanel(user, this, Actions.MESSAGE_SEND.name());
            contactsPanel.addToChatPanel(win, user);
            openConversations.put(user, win);
        }
    }

    private void executeActionOnInvalidUserName(String message) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                System.out.println("executeActionOnInvalidUserName:" + Thread.currentThread().getName());
                JOptionPane.showMessageDialog(null, message,"Invalid user name", JOptionPane.ERROR_MESSAGE);
            }
        });

    }

    /**
     * Method callback called by the chat service when a new message arrives
     */
    @Override
    synchronized public void onMessageReceived(Message msg) {
        System.out.println("onMessageReceived" + Thread.currentThread().getName());
        switch (msg.type) {
            case MESSAGE: {
                executeOnMessageReceived(msg.from, msg.message);
            }
                break;
            case USER_CONNECTED: {
                executeActionOnNewUserConnected(msg.message.split(":"));

            }
                break;
            case USER_DISCONNECTED: {
                executeActionOnUserDisconnected(msg.message);
            }
                break;
            case INVALID_USER_NAME: {
                executeActionOnInvalidUserName(msg.message);
                service.shutDownService();
            }
                break;
            case ERROR: {

                service.shutDownService();
                System.exit(0);

            }
                break;
            default:
                break;
        }

    }

}
