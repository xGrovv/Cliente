/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cliente;

import eventos.ConexionEvent;
import eventos.ConexionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Grover
 */
public class ClienteSocket {
    
    private Socket socket=null;
    private final String ip;
    private final int port;
    private final int nroIntentos=4;
    
    public ClienteSocket(String ip, int port){
        this.ip=ip;
        this.port=port;
    }
    
    public void Connect_Action(ConexionEvent ev){
        socket = (Socket)ev.getSource();
        Runnable connectionManager = new ConnectionManager(socket);
        Thread hilo = new Thread(connectionManager);
        hilo.start();
    }
    
    public void NoConnect_Action(ConexionEvent ev){
        System.err.println(ev.toString());
    }
    
    public void iniciarConnectionManager(){
        Conexion conexion = new Conexion(ip, port, nroIntentos);
        conexion.addListenerEvent(new ConexionListener() {
            @Override
            public void onConnect(ConexionEvent ev) {
                Connect_Action(ev);
            }

            @Override
            public void onNotConnect(ConexionEvent ev) {
                NoConnect_Action(ev);
            }
        });
        
        conexion.Conectar(); // metodo que lanza un hilo
        System.out.println("prueba si pasa el metodo");
        
        
    }
    
}
