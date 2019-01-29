/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package centralcapturasjudicial.sisbb;

//import organograma.*;
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyVetoException;
import java.util.Properties;
import javax.swing.JFrame;
import br.com.bb.jibm3270.*;


public class JanelaSisbb extends Term3270{
    private JFrame janela=null;

    public JanelaSisbb() throws PropertyVetoException {
        super();
        janela = new JFrame("SISBB Automatizado");
        janela.setLayout(new BorderLayout());
        janela.add(this, BorderLayout.CENTER);
        janela.setVisible(true);
     

        janela.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        janela.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                rotinaEncerramento();
             }
        });

    }
        public JanelaSisbb(Properties p) throws PropertyVetoException {
        super(p);
        janela = new JFrame("SISBB Automatizado");
        janela.setLayout(new BorderLayout());
        janela.add(this, BorderLayout.CENTER);
        janela.setVisible(true);


        janela.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        janela.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                rotinaEncerramento();
             }
        });

    }

    public void setTamanho(int largura, int altura){
        janela.setSize(largura, altura);
    }
    public void rotinaEncerramento(){
        this.paradaForcada();
        janela.remove(this);
        janela.dispose();
        janela=null;
    }
}
