
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingWorker;

import utils.concurrent.logging.LoggerPanel;

public class ConfigurationWindow  extends JFrame implements ActionListener{

    private static final int DEFAULT_SERVER_PORT = 62662;
    JButton start;
    JButton stop;
    JSpinner serverPort;
    JPanel portAndStartPanael;
    JScrollPane scroller ;
    Server server ;

    void InitGUI(){
        this.start = new JButton("start");
        this.stop = new JButton("stop");
        this.serverPort = new JSpinner(new SpinnerNumberModel(DEFAULT_SERVER_PORT, 0, 65535, 1));
        this.portAndStartPanael = new JPanel();
        
        this.scroller = new JScrollPane(LoggerPanel.getInstance().getArea());
        portAndStartPanael.add(serverPort);
        portAndStartPanael.add(start);
        portAndStartPanael.add(stop);
        start.addActionListener(this);
        stop.addActionListener(this);
        stop.setEnabled(false);

       // getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        getContentPane().add(portAndStartPanael,BorderLayout.NORTH);
        getContentPane().add(scroller, BorderLayout.CENTER);
  
        try 
		{
		   ImageIcon icon = new ImageIcon(ImageIO.read(new File("./resources/stack.png")));
           setIconImage(icon.getImage());
		} 
		catch (IOException e) 
		{
            try 
            {
                // windows installer path
               ImageIcon icon = new ImageIcon(ImageIO.read(new File("./app/resources/stack.png")));
               setIconImage(icon.getImage());
            } 
            catch (IOException e2) 
            {
                Toolkit.getDefaultToolkit().beep();
                       JOptionPane.showMessageDialog(this, "Sorry an error has happened, some images couldn't be loaded","Error",JOptionPane.ERROR_MESSAGE);
            }
		}
        pack();
        setTitle("Link Server");
        setSize(new Dimension(650,250));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        setLocationRelativeTo(null);
    }
    public ConfigurationWindow(){
        InitGUI();
        this.server = new Server();
    }


    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == start){
            int port = (int)serverPort.getValue();
     
    
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    System.out.println(Thread.currentThread().getName());
                    try {
                        start.setEnabled(false);
                        stop.setEnabled(true);
                        serverPort.setEnabled(false);
                        LoggerPanel.getInstance().Log(LoggerPanel.LEVEL.INFO, "starting server at port:"+ port);
                        server.run(port);
                    } catch (Exception except) {
                        server.ShutDown();
                        start.setEnabled(true);
                        stop.setEnabled(false);
                        serverPort.setEnabled(true);
                        LoggerPanel.getInstance().Log(LoggerPanel.LEVEL.ERROR, "Server can't run!");
                    }
                    return null;
                }
            }.execute();
        }
        else if (e.getSource() == stop){
            server.ShutDown();
            start.setEnabled(true);
            stop.setEnabled(false);
            serverPort.setEnabled(true);
        }
      
        
    }

    public void shutDown(){
        server.ShutDown();
    }
}
