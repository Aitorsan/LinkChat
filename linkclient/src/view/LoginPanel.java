package view;

import java.awt.Dimension;
import java.awt.event.*;
import javax.swing.*;


public class LoginPanel extends JPanel {
    private JButton connectButton;
    private JTextField serverTextField;
    private JLabel serverLabel;
    private JTextField userNameTextField;
    private JLabel userNameLabel;
    private JTextField port;
    private JLabel portLabel;

    public LoginPanel() {
        //construct components
        port = new JTextField();
        port.setText("62662");
        portLabel = new JLabel("Port");
        connectButton = new JButton ("connect");
        serverTextField = new JTextField (5);
        serverTextField.setText("127.0.0.1");
        serverLabel = new JLabel ("Server");
        userNameTextField = new JTextField (5);
        userNameLabel = new JLabel ("user name");

        //adjust size and set layout
        setPreferredSize (new Dimension (284, 313));
        setLayout (null);

        //add components
        add(connectButton);
        add(serverTextField);
        add(serverLabel);
        add(userNameTextField);
        add(userNameLabel);
        add(portLabel);
        add(port);

        //set component bounds (only needed by Absolute Positioning)
        connectButton.setBounds (165, 185, 100, 25);
        serverTextField.setBounds (115, 130, 155, 25);
        serverLabel.setBounds (40, 135, 50, 20);
        userNameTextField.setBounds (115, 90, 155, 25);
        userNameLabel.setBounds (35, 90, 65, 20);
        port.setBounds(320, 130, 80,25);
        portLabel.setBounds(290, 130, 25, 20);
    }


    public void setEventListener(ActionListener listener, String command){
        connectButton.addActionListener(listener);
        connectButton.setActionCommand(command);
    }

    public String getUserName(){
       return userNameTextField.getText().trim();
    }

    public String getServerAddress(){
        return serverTextField.getText().trim();
    }

    public String getPort(){
        return port.getText().trim();
    }
}