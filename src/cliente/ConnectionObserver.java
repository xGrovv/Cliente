/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cliente;

import eventos.ConnectionObserverEvent;
import eventos.ConnectionObserverListener;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Grover
 */
public class ConnectionObserver extends Thread{
    
    private Socket socket;
    private ArrayList listeners;
    private boolean enable;
    
    public ConnectionObserver(Socket socket){
        this.socket=socket;
        listeners = new ArrayList();
        enable = false;
    }
    
    public void addListener(ConnectionObserverListener conListener){
        listeners.add(conListener);
    }
    
    public void iniciar(){
        enable=true;
        start();
    }
    
    public void detener(){
        enable=false;
    }

    @Override
    public void run() {
        String iptext=socket.getInetAddress().toString();                     
        String address =iptext.substring(1, iptext.length());
        while (enable){
            if(!isReachable(address)){
                ListIterator li = listeners.listIterator();
                while (li.hasNext()) {
                    System.out.println(".......servidor perdido.......");
                    ConnectionObserverListener listener = (ConnectionObserverListener) li.next();
                    ConnectionObserverEvent evObj = new ConnectionObserverEvent(socket);
                    (listener).onLostConnection(evObj);
                }
            }
        }
    }
    
    public boolean isReachable(String address){        
        try {          
              boolean reachable;
               try {
                   reachable = (java.lang.Runtime.getRuntime().exec("ping -n 1 "+address).waitFor()==0);
                    if(reachable)
                        System.out.println("activo funca ip isReachable -->"+address);
                    else
                        System.out.println("no funciona ip isReachable -->"+address);
                    return reachable;
               } catch (InterruptedException ex) {
                   Logger.getLogger(ConnectionObserver.class.getName()).log(Level.SEVERE, null, ex);
               }          
        } catch (IOException ex) {
            Logger.getLogger(ConnectionObserver.class.getName()).log(Level.SEVERE, null, ex);
        }   return false;        
    }
    
    
}
