/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cliente;

import eventos.ConexionEvent;
import eventos.ConexionListener;
import eventos.ConnectionObserverEvent;
import eventos.ConnectionObserverListener;
import eventos.ServerManagerEvent;
import eventos.ServerManagerListener;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
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
    //private final int nroIntentos=4;
    private ConnectionObserver connectionObserver;
    private ServerManager serverManager;
    private boolean deteniendo;
    private boolean enable;
    
    private String msjrec = "";
    private JTextField msjrecibido = new JTextField();
    
    public ClienteSocket(String ip, int port){
        this.ip=ip;
        this.port=port;
        connectionObserver=null;
        serverManager=null;
        deteniendo=false;
        enable = false;
    }
    
    public Socket getSocket() {
        return socket;
    }
    
    public String getMsjrec() {
        return msjrec;
    }
    //////////////
    public JTextField getMsjrecibido() {
        return msjrecibido;
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
                //System.out.println("se Perdio la conexion con el servidoe servidor: "+ ev.toString());
            }
            @Override
            public void onReceiveMessage(ServerManagerEvent ev) {
                System.out.println("mensaje del servidor: "+ (String)ev.getSource());
                msjrec =  ( (String)ev.getSource() );
                msjrecibido.setText(msjrec);
            }
        });
        
        
        serverManager.iniciar();
        
        connectionObserver= new ConnectionObserver(socket);
        connectionObserver.addListener(new ConnectionObserverListener() {
            @Override
            public void onLostConnection(ConnectionObserverEvent ev) {
                if (!enable)
                    return;
                deteniendo=true;
                detener();
                iniciar();
                
            }
        });
        connectionObserver.iniciar();
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
            //this.finalze();
        } catch (IOException ex) {
            Logger.getLogger(ClienteSocket.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Throwable ex) {
            Logger.getLogger(ClienteSocket.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /*public void EnviarMenasaje(String mensaje){
        try {
            DataOutputStream out = new DataOutputStream (socket.getOutputStream());
            TaskSend messageSend = new TaskSend(out, mensaje);
            messageSend.start();
        } catch (IOException ex) {
            Logger.getLogger(ClienteSocket.class.getName()).log(Level.SEVERE, null, ex);
        }
    }*/
    
    public boolean EnviarMensaje(String mensaje){
        boolean enviado = false;
        try {
            DataOutputStream out = new DataOutputStream (socket.getOutputStream());
            TaskSend messageSend = new TaskSend(out, mensaje);
            messageSend.start();
            enviado =true;
        } catch (IOException ex) {
            Logger.getLogger(ClienteSocket.class.getName()).log(Level.SEVERE, null, ex);
        }
        return enviado;
    }
    
}
