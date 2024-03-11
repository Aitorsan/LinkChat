package utils.concurrent.containers;


import java.util.Queue;

public class MsgQueue <T>{
    
    private Queue<T> queue;
    
    public MsgQueue(Queue<T> q){
        this.queue = q;
    }

    public synchronized void addMessage(T msg){
       queue.add(msg);
    }

    public synchronized T getMessage(){
           return  queue.poll();
    } 

    public synchronized boolean hasPendingMessages(){
        return !queue.isEmpty();
    }


}
