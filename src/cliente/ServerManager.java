/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cliente;

import eventos.ServerManagerEvent;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import eventos.ServerManagerListener;

/**
 *
 * @author Grover
 */
public class ServerManager implements Runnable{
    
    private Socket socket=null;
    private DataInputStream in=null;
    //private String message;
    private ArrayList listeners;
    private boolean connected;
    
    public ServerManager(Socket socket){
        this.socket=socket;
        //message = "";
        connected = true;
        listeners= new ArrayList();
        try {
            in = new DataInputStream(this.socket.getInputStream());
        } catch (IOException ex) {
            Logger.getLogger(ServerManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void addListenerEvent(ServerManagerListener connectionManagertListener){
        listeners.add(connectionManagertListener);
    }
    
    public void removeListener(){
        listeners.clear();
    }
    
    public void detener(){
        try {
            connected=false;
            in.close();
        } catch (IOException ex) {
            Logger.getLogger(ServerManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void run() {
        connected=true;
        while (connected){
            try {
                String mensaje = in.readUTF();
                ListIterator li = listeners.listIterator();
                while (li.hasNext()) {
                    ServerManagerListener listener = (ServerManagerListener) li.next();
                    ServerManagerEvent evObj = new ServerManagerEvent(mensaje);
                    (listener).onReceiveMessage(evObj);
                }
            }catch (SocketException exSo){  // por cierre de applicacion del server
                System.out.println("El servidor detuvo el servicio:: "+ exSo.getMessage());
                ListIterator li = listeners.listIterator();
                while (li.hasNext()) {
                    ServerManagerListener listener = (ServerManagerListener) li.next();
                    ServerManagerEvent evObj = new ServerManagerEvent(exSo.getMessage());
                    (listener).onDisconnectClient(evObj);
                }
                
                connected=false;
            }catch (java.io.EOFException eofEx){    // cierre de socket del servidor
                System.out.println("ERR:: el servidor Cerro"+ eofEx.getMessage());
                /*ListIterator li = listeners.listIterator();
                while (li.hasNext()) {
                    ServerManagerListener listener = (ServerManagerListener) li.next();
                    ServerManagerEvent evObj = new ServerManagerEvent(eofEx.getMessage());
                    (listener).onDisconnectClient(evObj);
                }*/
                connected=false;
            } catch (IOException ex) {
                Logger.getLogger(ServerManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    
}
