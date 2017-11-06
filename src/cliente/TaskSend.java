/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cliente;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Grover
 */
public class TaskSend {
    
    private DataOutputStream out=null;
    private String mensaje;
    
    public TaskSend(DataOutputStream dataOut, String mensaje){
        this.out=dataOut;
        this.mensaje=mensaje;
        try {
            out.writeUTF(this.mensaje);
        } catch (IOException ex) {
            Logger.getLogger(TaskSend.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
