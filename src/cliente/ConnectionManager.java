/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cliente;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Grover
 */
public class ConnectionManager implements Runnable{
    
    private Socket socket=null;
    private DataInputStream in=null;
    private boolean conected;
    
    public ConnectionManager(Socket socket){
        this.socket=socket;
        try {
            in = new DataInputStream(this.socket.getInputStream());
        } catch (IOException ex) {
            Logger.getLogger(ConnectionManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    @Override
    public void run() {
        conected=true;
        while (conected){
            try {
                String mensaje = in.readUTF();
            }catch (SocketException exSo){
                System.out.println("ERR:: "+ exSo.getMessage());
                conected=false;
            }catch (java.io.EOFException eofEx){
                System.out.println("ERR:: el servidor detuvo el servicio"+ eofEx.getMessage());
                conected=false;
            } catch (IOException ex) {
                Logger.getLogger(ConnectionManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    
    
}
