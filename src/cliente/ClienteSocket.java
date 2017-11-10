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

/**
 *
 * @author Grover
 */
public class ClienteSocket {
    
    private Socket socket=null;
    private final String ip;
    private final int port;
    private final int nroIntentos=4;
    private ConnectionObserver connectionObserver;
    private ServerManager serverManager;
    private boolean deteniendo;
    
    public ClienteSocket(String ip, int port){
        this.ip=ip;
        this.port=port;
        connectionObserver=null;
        serverManager=null;
        deteniendo=false;
    }
    
    public void Connect_Action(ConexionEvent ev){
        socket = (Socket)ev.getSource();
        serverManager = new ServerManager(socket);
        serverManager.addListenerEvent(new ServerManagerListener() {
            @Override
            public void onDisconnectClient(ServerManagerEvent ev) {
                // mandar a reconectar
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
            }
        });
        
        Runnable serverManagerRun = serverManager;
        Thread hilo = new Thread(serverManagerRun);
        hilo.start();
        
        connectionObserver= new ConnectionObserver(socket);
        connectionObserver.addListener(new ConnectionObserverListener() {
            @Override
            public void onLostConnection(ConnectionObserverEvent ev) {
                deteniendo=true;
                detener();
                iniciar();
                
            }
        });
        connectionObserver.iniciar();
    }
    
    public void NoConnect_Action(ConexionEvent ev){
        System.err.println(ev.toString());
    }
    
    public void iniciar(){
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
    
    public void EnviarMenasaje(String mensaje){
        try {
            DataOutputStream out = new DataOutputStream (socket.getOutputStream());
            TaskSend messageSend = new TaskSend(out, mensaje);
            messageSend.start();
        } catch (IOException ex) {
            Logger.getLogger(ClienteSocket.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
