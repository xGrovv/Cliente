/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cliente;

import eventos.ConexionEvent;
import eventos.ConexionListener;
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
    
    public ClienteSocket(String ip, int port){
        this.ip=ip;
        this.port=port;
    }
    
    public void Connect_Action(ConexionEvent ev){
        socket = (Socket)ev.getSource();
        ServerManager server = new ServerManager(socket);
        server.addListenerEvent(new ServerManagerListener() {
            @Override
            public void onDisconnectClient(ServerManagerEvent ev) {
                // mandar a reconectar
                System.out.println("se Perdio la conexion con el servidoe servidor: "+ ev.toString());
            }
            @Override
            public void onReceiveMessage(ServerManagerEvent ev) {
                System.out.println("mensaje del servidor: "+ (String)ev.getSource());
            }
        });
        
        Runnable serverManager = server;
        Thread hilo = new Thread(serverManager);
        hilo.start();
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
