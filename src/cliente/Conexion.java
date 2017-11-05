/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cliente;

import eventos.ConexionEvent;
import eventos.ConexionListener;
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
public class Conexion extends Thread {
    
    private Socket socket=null;
    private final String ip;
    private final int port;
    private final int nroIntentos;
    private final int tiempoEntreIntento=3000;
    private ArrayList listeners;
    
    public Conexion(String ip, int port, int nroIntentos){
        this.ip=ip;
        this.port=port;
        this.nroIntentos=nroIntentos;
        listeners= new ArrayList();
    }
    
    public void Conectar(){
        this.start();
    }
    
    public void Detener(){
        
    }
    
    public void addListenerEvent(ConexionListener conexionListener){
        listeners.add(conexionListener);
    }
    
    public void pasatiempo(){
        try {
            sleep(tiempoEntreIntento);
        } catch (InterruptedException ex1) {
            System.out.println("Conexion.pasatiempo: Esperando para intentar conectar");
            Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex1);
        }
    }
    
    @Override
    public void run() {
        int i=1;
        while (socket == null && i <= nroIntentos){
            try {
                socket = new Socket(this.ip, this.port);
                ListIterator li = listeners.listIterator();
                while (li.hasNext()) {
                    ConexionListener listener = (ConexionListener) li.next();
                    ConexionEvent evObj = new ConexionEvent(socket);
                    (listener).onConnect(evObj);
                }
            } catch (IOException ex) {
                i++;
                ListIterator li = listeners.listIterator();
                while (li.hasNext()) {
                    ConexionListener listener = (ConexionListener) li.next();
                    ConexionEvent evObj = new ConexionEvent(new String(ex.getMessage()));
                    (listener).onNotConnect(evObj);
                }
                pasatiempo();
            }
        }
    }
    
}
