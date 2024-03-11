package view;

import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;

public class WelcomePanel extends JPanel{

    HTMLEditorKit kit;
    JEditorPane editorpane;
    JScrollPane scrollPane ;
    StyleSheet styleSheet;
    String userName;

    public void createStyleSheet(){

        styleSheet = kit.getStyleSheet();
        styleSheet.addRule("body {margin: 0;padding: 0; font-weight: bold; }");
        styleSheet.addRule(".welcome {text-align: center;}");
    }

    public WelcomePanel(){
        this.kit = new HTMLEditorKit();
        this.editorpane= new JEditorPane();
       editorpane.setEditorKit(kit);
       editorpane.setDocument(kit.createDefaultDocument());
       editorpane.setEditable(false);

    
    
        createStyleSheet();
       setLayout(new BorderLayout());
       add(editorpane, BorderLayout.CENTER);


    }

    public void setWelcomeMessage(String name){
        this.userName = name;
        String newMsg = "<div class=\"welcome\"> <h1> Welcome to the chat "+name+"!</h1></div>";
       
        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append(newMsg);
        
 
         String htmlString = htmlBuilder.toString();
         editorpane.setText( htmlString);
    }
    
}
