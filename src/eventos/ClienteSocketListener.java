/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eventos;

/**
 *
 * @author Grover
 */
public interface ClienteSocketListener {
    
    public void onConnected(ClienteSocketEvent ev);
    public void onLostConnection(ClienteSocketEvent ev);
    public void onFailConnection(ClienteSocketEvent ev);
    public void onMessageReceive(ClienteSocketEvent ev);
}
