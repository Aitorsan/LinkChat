import javax.swing.SwingUtilities;

public class LinkApp  {
    public static void main(String[] args) throws Exception {


    SwingUtilities.invokeLater(new Runnable(){

        @Override
        public void run() {
            new ChatAppController();
        }
       
    });
       

    }
}
