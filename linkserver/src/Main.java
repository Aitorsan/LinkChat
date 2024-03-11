import javax.swing.SwingUtilities;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Main extends WindowAdapter{

    ConfigurationWindow win = null;
    public static void main(String[] args) {
        new Main();
    }

    public Main(){
        SwingUtilities.invokeLater(new Runnable(){

            @Override
            public void run() {
               win = new ConfigurationWindow();
            }
    
         });
    }

    @Override
    public void windowClosing(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            win.shutDown();
            System.exit(0);
        }
    }
}
