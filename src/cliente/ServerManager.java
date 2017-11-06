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
    
    public void Detener(){
        connected=false;
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
            }catch (SocketException exSo){
                ListIterator li = listeners.listIterator();
                while (li.hasNext()) {
                    ServerManagerListener listener = (ServerManagerListener) li.next();
                    ServerManagerEvent evObj = new ServerManagerEvent(exSo.getMessage());
                    (listener).onDisconnectClient(evObj);
                }
                System.out.println("ERR xxGG:: "+ exSo.getMessage());
                connected=false;
            }catch (java.io.EOFException eofEx){
                System.out.println("ERR:: el servidor detuvo el servicio"+ eofEx.getMessage());
                connected=false;
            } catch (IOException ex) {
                Logger.getLogger(ServerManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    
}
