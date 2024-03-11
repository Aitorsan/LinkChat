package utils.concurrent.logging;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JTextArea;


public final class LoggerPanel extends JTextArea {

    static public enum LEVEL{
        WARN,
        INFO,
        ERROR
    }  
     
    static final String WARN_LEVEL = "[WARNING] ";
    static final String INFO_LEVEL = "[INFO] ";
    static final String ERROR_LEVEL = "[ERROR] ";

    private JTextArea logArea;

    private LoggerPanel(){
        logArea = new JTextArea();
        logArea.setLineWrap(true);
        logArea.setFont(new Font(Font.MONOSPACED, Font.BOLD,  15));
        logArea.setForeground(Color.white);
        logArea.setBackground(Color.black);

    }

    protected static LoggerPanel INSTANCE;
    public static LoggerPanel getInstance(){
        if (INSTANCE == null){
            INSTANCE = new LoggerPanel();
        }

        return INSTANCE;
    }

     public synchronized void Log(LEVEL level, String message){

        switch(level){
            case ERROR:{ message = ERROR_LEVEL + message; }break;
            case INFO:{  message = INFO_LEVEL + message;  }break;
            case WARN: { message = WARN_LEVEL + message; }break;
            default:  break;
        }

        getArea().append(message+ "\n");

    }

    public JTextArea getArea() {
        return INSTANCE.logArea;
    }
    
}
