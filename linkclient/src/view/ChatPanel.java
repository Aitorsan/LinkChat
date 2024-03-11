package view;

import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.Toolkit;

public class ChatPanel extends JPanel{
    
    String userName;
    JTextField textField;
    //html stuff
    HTMLEditorKit kit;
    JEditorPane editorpane;
    JScrollPane scrollPane ;

    ArrayList<String> historyMessages;
 
    void createStyleSheet(){
        StyleSheet styleSheet = kit.getStyleSheet();
        styleSheet.addRule("body { margin: 0 auto;max-width: 800px;padding: 0;}");
        styleSheet.addRule("p {font-weight: bold; }");
        styleSheet.addRule(".flexador {display: flex;}");
        styleSheet.addRule(".local {background-color:#128c7e; color: white; border: 2px solid #dedede;border-radius: 5px;padding: 10px;margin: 10px 0;}");
        styleSheet.addRule(".container {color:white; border: 2px solid #dedede;background-color: #1c2134; border-radius: 5px;padding: 10px;margin: 10px 0;}");
        styleSheet.addRule(".username {color: #25d366;}");
    }

    public ChatPanel(String name, ActionListener messageSendListener, String command){
        this.userName = name;
        this.historyMessages = new ArrayList<>();
        this.kit = new HTMLEditorKit();
        this.editorpane= new JEditorPane();
        this.scrollPane = new JScrollPane(editorpane);
        this.textField = new JTextField(30);
        textField.setName(name);
        textField.setActionCommand(command);
        textField.addActionListener(messageSendListener);
        Border border = BorderFactory.createLineBorder(Color.black, 1);
        textField.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(10, 10, 20, 0)));

       editorpane.setEditorKit(kit);
       editorpane.setDocument(kit.createDefaultDocument());
       editorpane.setEditable(false);
      
       createStyleSheet();
    
       setName(name);
       setLayout(new BorderLayout());
       JLabel l = new JLabel(name, SwingConstants.CENTER);
       Font bigFont = new Font(Font.SERIF, Font.PLAIN,  20);
       l.setBackground(new Color(07,94,84));
       l.setOpaque(true);
       l.setForeground(Color.white);
       l.setFont(bigFont);
       add(l, BorderLayout.NORTH);
       add(scrollPane, BorderLayout.CENTER);
       add(textField,BorderLayout.SOUTH);
    }


    public String getName(){
        return userName;
    }

    public void cacheMessage( String msg){
      
        historyMessages.add(msg);
    }

    public void addLocalMessage(String from, String message){

        String newMsg = "<div class=\"local\">"+ message+ "</div>";
    
        StringBuilder htmlBuilder = new StringBuilder();
        for (String m : historyMessages){
            htmlBuilder.append(m);
        }
        htmlBuilder.append(newMsg);
        
        String htmlString = htmlBuilder.toString();
        editorpane.setText( htmlString);
        cacheMessage(newMsg);
    }

    public void addIncomingMessage(String from, String message){
        // lets make notification noise
        Toolkit.getDefaultToolkit().beep();
        String newMsg = "<div class=\"container\">"+"<p class=\"username\">"+from+"</p>"+ message+ "</div>";
        StringBuilder htmlBuilder = new StringBuilder();
        for (String m : historyMessages){
            htmlBuilder.append(m);
        }
        htmlBuilder.append(newMsg);
        String htmlString = htmlBuilder.toString();
        editorpane.setText( htmlString);
        cacheMessage(newMsg);
    }
}
