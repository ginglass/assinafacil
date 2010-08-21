/*
 * AssinaFacilApp.java
 */

package assinafacil;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;
import assinafacil.Assinador;
import assinafacil.AssinadorMSCAPI;
import java.util.Set;
import javax.swing.JDialog;
/**
 * The main class of the application.
 */
public class AssinaFacilApp extends SingleFrameApplication {

    private Assinador assinador;
    private String arquivoSelecionado = null;
    private String certificateAlias = null;
    private AssinaFacilView mainWindow;
    /**
     * At startup create and show the main frame of the application.
     */
    @Override protected void startup() {
        try {
            assinador = new AssinadorMSCAPI();
            assinador.initialize(null);
            assinador.getAllKeyCertificates().keySet();
            mainWindow =  new AssinaFacilView(this);
            show(mainWindow);
        } catch (Exception ex) {
            if (mainWindow != null) {
                mainWindow.setStatusMessage(ex.getMessage());
            } else {
                Logger.getLogger(AssinaFacilApp.class.getName()).log(Level.SEVERE, null, ex);
                System.exit(1);
            }
        }
    }

    public void reinitialize() {
        try {
            assinador.initialize(null);
        } catch (Exception e) {
            mainWindow.setStatusMessage(e.getMessage());
        }
    }

    public Set<String> getAliases() throws Exception {
            return assinador.getAllKeyCertificates().keySet();
    }
    
    public void setArquivoSelecionado(String arquivo) {
        this.arquivoSelecionado = arquivo;
        mainWindow.setSelectedFile(arquivo);
    }

    @Override protected void initialize(String[] args) {
        
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of AssinaFacilApp
     */
    public static AssinaFacilApp getApplication() {
        return Application.getInstance(AssinaFacilApp.class);
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
        launch(AssinaFacilApp.class, args);
    }

    @Action
    public void assinaArquivoNovo() {
        if (arquivoSelecionado == null) {
            mainWindow.setStatusMessage("Selecione o arquivo.");
            return;
        }

        if (certificateAlias != null) {
            try {
                String retorno = assinador.signFile(this.arquivoSelecionado, null, certificateAlias);
                mainWindow.setStatusMessageOK(retorno);
            } catch (Exception ex) {
                mainWindow.setStatusMessage(ex.getMessage());
                Logger.getLogger(AssinaFacilApp.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
              mainWindow.setStatusMessage("Escolha seu certificado...");
        }
    }

    @Action
    public void setCertificateAlias() {
        certificateAlias = mainWindow.getSelectedAlias();
    }
    
}
