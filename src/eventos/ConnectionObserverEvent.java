/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eventos;

import java.util.EventObject;

/**
 *
 * @author Grover
 */
public class ConnectionObserverEvent extends EventObject{
    
    public ConnectionObserverEvent(Object source) {
        super(source);
    }
    
}
