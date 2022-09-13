package util;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JProgressBar;

import com.jcraft.jsch.SftpProgressMonitor;

public class EventoProgreso implements PropertyChangeListener {
    private final JProgressBar barraProgreso;
     
    public EventoProgreso(JProgressBar barra) {
        barraProgreso = barra;
    }
 
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        // Checamos si la propiedad modificada es progress
        if (evt.getPropertyName().compareTo("progress") == 0) {
            int progress = (Integer) evt.getNewValue();
             
            System.out.println("********* Progreso: " + progress);
             
            barraProgreso.setValue(progress);
        }
    }    
}