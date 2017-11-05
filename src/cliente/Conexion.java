/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cliente;

import eventos.ConexionListener;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Grover
 */
public class Conexion extends Thread {
    
    private Socket socket=null;
    private final String ip;
    private final int nroIntentos;
    private final int port;
    private ArrayList listeners;
    
    public Conexion(String ip, int port, int nroIntentos){
        this.ip=ip;
        this.port=port;
        this.nroIntentos=nroIntentos;
    }
    
    public void Conectar(){
        this.start();
    }
    
    public void Detener(){
        
    }
    
    public void addListenerEvent(ConexionListener conexionListener){
        listeners.add(conexionListener);
    }
    
    @Override
    public void run() {
        int i=1;
        while (socket == null && i <= nroIntentos){
            try {
                socket = new Socket(this.ip, this.port);
                //lanzar evento de conexion entrengando el socket;
            } catch (IOException ex) {
                i++;
                //lanzar evento de intento fallido
                //sleep(3000)
                //volver al run de nuevo
            }
        }
    }
    
}
