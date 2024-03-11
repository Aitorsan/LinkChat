package view;

import java.util.HashMap;
import java.util.Map;
import java.awt.CardLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Font;

public class ContactsPanel extends JPanel {

    Map<String, JButton> usersButtons;
    Map<String, ChatPanel> usersPanels;
    JPanel contactsPanel;
    JPanel chatPanel;
    JLabel ownerLabel;
    WelcomePanel welcomePanel;
    CardLayout cardLayout;
    ActionListener listener;
    int next = 0;

    public ContactsPanel(ActionListener listener){
       this.listener = listener;
       this.contactsPanel = new JPanel();
       this.chatPanel = new JPanel();
       this.ownerLabel = new JLabel();
       this.usersButtons = new HashMap<>();
       this.usersPanels = new HashMap<>();
       this.cardLayout = new CardLayout();
       chatPanel.setLayout(cardLayout);
       this.welcomePanel = new WelcomePanel();
       chatPanel.add(welcomePanel, "default");
       contactsPanel.setLayout(new GridBagLayout());
       cardLayout.show(chatPanel, "default");

       ownerLabel.setBorder(new EmptyBorder(10,10,10,0));
       Font bigFont = new Font(Font.SERIF, Font.PLAIN,  20);
       ownerLabel.setFont(bigFont);

     

       setLayout(new BorderLayout());
       JPanel j = new JPanel();
       JScrollPane s = new JScrollPane(j);
       j.add(contactsPanel);
       add(ownerLabel, BorderLayout.NORTH);
       add(s,BorderLayout.WEST);
       add(chatPanel,BorderLayout.CENTER);
    }

    public void addToChatPanel(ChatPanel win, String user){
        chatPanel.add( user, win);
        usersPanels.put(user, win);
    }

    public void setOwner(String owner){
        ownerLabel.setText(owner);
        welcomePanel.setWelcomeMessage(owner);
    }


    public boolean containsUser(String userName){
        return usersButtons.containsKey(userName);
    }

    public void removeUser(String userName){
        if (containsUser(userName)){
            JButton b  = usersButtons.remove(userName);
            contactsPanel.remove(b);
            if (usersPanels.containsKey(userName)){
                chatPanel.remove(usersPanels.get(userName));
            }
        }
        revalidate();
        repaint();
    }

    public void setNotificationColor(String user){
        if (containsUser(user)){
            usersButtons.get(user).setBackground(new Color(07,94,84));
            usersButtons.get(user).setContentAreaFilled(false);
            usersButtons.get(user).setOpaque(true);
        }
    }

    public void addUser(String user, String command){
        if(!usersButtons.containsKey(user)){
            JButton b = new JButton(user);
            int padding = 50-user.length() > 0? 50 - user.length(): 5; 
            b.setBorder(new EmptyBorder(5,padding,5, padding));
            b.setFocusable(false);
            b.addActionListener(listener);
            b.setActionCommand(command);
            b.setOpaque(true);
            
            GridBagConstraints c = new GridBagConstraints();
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridy = next;
            next++;
            contactsPanel.add(b,c);
            usersButtons.put(user, b);

        }    
    }

     public void openChat(String user) {
        if (containsUser(user)){
            usersButtons.get(user).setContentAreaFilled(true);
            usersButtons.get(user).setBackground(null);
        }
        cardLayout.show(chatPanel, user);
    }
}
