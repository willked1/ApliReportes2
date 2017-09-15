/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aplireportes2;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import views.GUIReporte;

/**
 *
 * @author William Duarte
 */
public class ApliReportes2 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            UIManager.LookAndFeelInfo[] lfs = UIManager.getInstalledLookAndFeels();
            boolean encontro = false;
            for (UIManager.LookAndFeelInfo lf : lfs) {
                if(lf.getName().equals("GTK+")){
                    UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
                    encontro = true;
                }
            }
            if(!encontro)
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ApliReportes2.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(ApliReportes2.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(ApliReportes2.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(ApliReportes2.class.getName()).log(Level.SEVERE, null, ex);
        }
        GUIReporte gr = new GUIReporte();
        gr.setVisible(true);
    }
    
}
