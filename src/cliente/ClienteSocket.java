/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cliente;

import eventos.ClienteSocketEvent;
import eventos.ClienteSocketListener;
import eventos.ConexionEvent;
import eventos.ConexionListener;
import eventos.ConnectionObserverEvent;
import eventos.ConnectionObserverListener;
import eventos.ServerManagerEvent;
import eventos.ServerManagerListener;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextField;

/**
 *
 * @author Grover
 */
public class ClienteSocket {
    
    private Socket socket=null;
    private final String ip;
    private final int port;
    private ConnectionObserver connectionObserver;
    private ServerManager serverManager;
    private boolean deteniendo;
    private boolean enable;
    private ArrayList listeners;
    
    private JTextField msjrecibido = new JTextField();
    
    public ClienteSocket(String ip, int port){
        this.ip=ip;
        this.port=port;
        connectionObserver=null;
        serverManager=null;
        deteniendo=false;
        enable = false;
        listeners= new ArrayList();
    }
    
    public Socket getSocket() {
        return socket;
    }
    
    public void addListenerEvent(ClienteSocketListener clienteSocketListener){
        listeners.add(clienteSocketListener);
    }
    
    public void Connect_Action(ConexionEvent ev){
        socket = (Socket)ev.getSource();
        serverManager = new ServerManager(socket);
        serverManager.addListenerEvent(new ServerManagerListener() {
            @Override
            public void onDisconnectClient(ServerManagerEvent ev) {
                // mandar a reconectar
                if (!enable)
                    return;
                if (!deteniendo){
                    detener();
                    iniciar();
                }
                deteniendo=false;
                ListIterator li = listeners.listIterator();
                while (li.hasNext()) {
                    ClienteSocketListener listener = (ClienteSocketListener) li.next();
                    ClienteSocketEvent evObj = new ClienteSocketEvent(ev);
                    (listener).onLostConnection(evObj);
                }
            }
            
            @Override
            public void onReceiveMessage(ServerManagerEvent ev) {
                ListIterator li = listeners.listIterator();
                while (li.hasNext()) {
                    ClienteSocketListener listener = (ClienteSocketListener) li.next();
                    ClienteSocketEvent evObj = new ClienteSocketEvent((String)ev.getSource());
                    (listener).onMessageReceive(evObj);
                }
            }
            
        });
        serverManager.iniciar();
        connectionObserver= new ConnectionObserver(socket);
        if(!"localhost".equals(ip)){
            connectionObserver.addListener(new ConnectionObserverListener() {
                @Override
                public void onLostConnection(ConnectionObserverEvent ev) {
                    if (!enable)
                        return;
                    deteniendo=true;
                    detener();
                    iniciar();
                    ListIterator li = listeners.listIterator();
                    while (li.hasNext()) {
                        ClienteSocketListener listener = (ClienteSocketListener) li.next();
                        ClienteSocketEvent evObj = new ClienteSocketEvent(ev);
                        (listener).onLostConnection(evObj);
                    }
                }
            });
            connectionObserver.iniciar();
        }
        ListIterator li = listeners.listIterator();
        while (li.hasNext()) {
            ClienteSocketListener listener = (ClienteSocketListener) li.next();
            ClienteSocketEvent evObj = new ClienteSocketEvent(this);
            (listener).onConnected(evObj);
        }
        
    }
    
    public void NoConnect_Action(ConexionEvent ev){
        System.err.println("intento de conexion fallido::" +ev.toString());
    }
    
    public void iniciar(){
        enable = true;
        Conexion conexion = new Conexion(ip, port);
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
    }
    
    public void detenerCliente(){
        enable=false;
        detener();
    }
    
    public void detener(){
        try {
            connectionObserver.detener();
            serverManager.detener();
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(ClienteSocket.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Throwable ex) {
            Logger.getLogger(ClienteSocket.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void EnviarMensaje(String mensaje){
        try {
            DataOutputStream out = new DataOutputStream (socket.getOutputStream());
            MessageSend messageSend = new MessageSend(out, mensaje);
            messageSend.start();
        } catch (IOException ex) {
            Logger.getLogger(ClienteSocket.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
