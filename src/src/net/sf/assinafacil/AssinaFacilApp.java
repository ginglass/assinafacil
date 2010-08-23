/*
 * AssinaFacilApp.java
 */

package net.sf.assinafacil;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;
import net.sf.assinafacil.Assinador;
import net.sf.assinafacil.AssinadorMSCAPI;
import java.util.Set;
import javax.swing.JDialog;
/**
 * The main class of the application.
 */
public class AssinaFacilApp extends SingleFrameApplication {

    private Assinador signer;
    private String selectedFile = null;
    String signedFileName = null;
    private String certificateAlias = null;
    private AssinaFacilView mainWindow;
    /**
     * At startup create and show the main frame of the application.
     */
    @Override protected void startup() {
        try {
            signer = new AssinadorMSCAPI();
            signer.initialize(null);
            signer.getAllKeyCertificates().keySet();
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
            signer.initialize(null);
        } catch (Exception e) {
            mainWindow.setStatusMessage(e.getMessage());
        }
    }

    public Set<String> getAliases() throws Exception {
            return signer.getAllKeyCertificates().keySet();
    }
    
    public void setSelectedInputFile(String arquivo) {
        this.selectedFile = arquivo;
        mainWindow.setSelectedFile(arquivo);
    }

    public void setSelectedOutputFile(String arquivo) {
        this.signedFileName = arquivo;
        mainWindow.setSelectedOutputFile(arquivo);
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
    public void appSignFile() {

        if (selectedFile == null) {
            mainWindow.setStatusMessage("Selecione o arquivo.");
            return;
        } else {
            // Verifica se é um arquivo e se já está assinado
            if (signer.isSignedFile(selectedFile)) {
                signedFileName = selectedFile;
            } else {
                File testFile = new File(selectedFile + ".p7s");
                if ((signedFileName == null ) && (testFile.exists())) {
                    mainWindow.setStatusMessage("Já existe um arquivo de saida com esse nome... escolha o arquivo de saida.");
                    return;
                } else {
                    if (!testFile.exists()) {
                        signedFileName = selectedFile + ".p7s";
                    }
                }

            }

            //Senao verifica se arquivo de saida é null e o nome do arquivo de entrada com o sufixo .p7s referencia arquivo que já exista
                //Se sim Avisa que deve escolher um arquivo de saida

        }

        if (certificateAlias != null) {
            try {  
                String returnStatus = signer.signFile(this.selectedFile, signedFileName, null, certificateAlias);
                mainWindow.setStatusMessageOK(returnStatus);
                signedFileName = null;

            } catch (Exception ex) {
                mainWindow.setStatusMessage(ex.getMessage());
                signedFileName = null;
                // Logger.getLogger(AssinaFacilApp.class.getName()).log(Level.SEVERE, null, ex);
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
