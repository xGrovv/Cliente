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
    private String ip;
    private int port;
    private final int nroIntentos=4;
    
    public ClienteSocket(String ip, int port){
        this.ip=ip;
        this.port=port;
    }
    
    public void Connect_Action(){
        
    }
    
    public void NoConnect_Action(){
        
    }
    
    private void iniciarConnectionManager(){
        Conexion conexion = new Conexion(ip, port, nroIntentos);
        conexion.addListenerEvent(new ConexionListener() {
            
            @Override
            public void onConnect(ConexionEvent ev) {
                Connect_Action();
            }

            @Override
            public void onNotConnect(ConexionEvent ev) {
                NoConnect_Action();
            }
        });
        
        conexion.Conectar();
        
        Runnable conRun = new ConnectionManager(socket);
        Thread hilo = new Thread(conRun);
        hilo.start();
    }
    
}
