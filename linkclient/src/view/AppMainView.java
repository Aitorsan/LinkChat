package view;

import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.awt.CardLayout;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class AppMainView extends JFrame{

    JPanel stackContainer;
    CardLayout stackLayout;

    public AppMainView( LoginPanel loginPanel, ContactsPanel contactsPanel){
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException e) {
            System.out.println("[WARN] native look and feel not suppported");
        }

        InitView();
        stackContainer.add(loginPanel, "1");
        stackContainer.add(contactsPanel, "2");
        stackLayout.show(stackContainer, "1");
    }

    void InitView(){
        this.stackContainer = new JPanel();
        this.stackLayout = new CardLayout();
        stackContainer.setLayout(this.stackLayout);
        // stack container to the frame 
        add(stackContainer);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.setResizable(false);
        this.pack();
        try 
		{
		   ImageIcon icon = new ImageIcon(ImageIO.read(new File("./resources/messages-icon.png")));
           setIconImage(icon.getImage());
		} 
		catch (IOException e) 
		{
            try 
            {
                // windows installer path
               ImageIcon icon = new ImageIcon(ImageIO.read(new File("./app/resources/messages-icon.png")));
               setIconImage(icon.getImage());
            } 
            catch (IOException e2) 
            {
                Toolkit.getDefaultToolkit().beep();
                       JOptionPane.showMessageDialog(this, "Sorry an error has happened, some images couldn't be loaded","Error",JOptionPane.ERROR_MESSAGE);
            }
		}
      
        setLocationRelativeTo(null);
        this.setSize(450,600);
        setTitle("LinkChat");
    }

 
    public void showContactsView(){

        stackLayout.show(stackContainer, "2");
        setVisible(true);

    }

    public void removePanel(String username){

    }
    

}
