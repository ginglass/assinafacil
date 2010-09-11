/*
 * AssinaFacilApp.java
 */

package net.sf.assinafacil;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;
import net.sf.assinafacil.Assinador;
import net.sf.assinafacil.AssinadorMSCAPI;
import java.util.Set;

/**
 * A classe principal da aplica\u00e7\u00e3o.
 */
public class AssinaFacilApp extends SingleFrameApplication {

    private Assinador signer;
    private String selectedFile = null;
    String signedFileName = null;
    private String certificateAlias = null;
    private AssinaFacilView mainWindow;
    /**
     * Inicializa o assinador e mostra a tela principal da aplica\u00e7\u00e3o.
     */
    @Override protected void startup() {
        try {
            signer = new AssinadorMSCAPI();
            signer.initialize(null);
            signer.getAllKeyCertificates().keySet();
            mainWindow =  new AssinaFacilView(this);
            show(mainWindow);
        } catch (Exception ex) {
            Logger.getLogger(AssinaFacilApp.class.getName()).log(Level.SEVERE, null, ex);
            if (mainWindow != null) {
                mainWindow.setStatusMessage(ex.getMessage());
            } else {
                System.exit(1);
            }
        }
    }

    /***
     * utilizado para forcar a leitura a toda hora do keystore
     * O ideal \u00e9 implementar algum tipo de callback function para 
     * interceptar a inser\u00e7\u00e3o do token e apenas nesse caso for\u00e7ar a 
     * leitura dos certificados no dispositivo...
     * 
     * TODO: Implementar uma callback para evitar a enumera\u00e7\u00e3o dos certificados a 
     * cada evento relacionado com o objeto de interface que exibe a lista de certificados.
     */
    public void reinitialize() {
        try {
            signer.initialize(null);
        } catch (Exception e) {
            mainWindow.setStatusMessage(e.getMessage());
        }
    }

    /**
     * Pega os aliases (apelidos) disponiveis para o assinador.
     * @return lista de apelidos (aliases) de certificados.
     * @throws Exception
     */
    public Set<String> getAliases() throws Exception {
            return signer.getAllKeyCertificates().keySet();
    }
    
    /**
     * Controla o arquivo de entrada que ser\u00e1 assinado (ou - TODO - verificado)
     * @param arquivo de entrada.
     */
    public void setSelectedInputFile(String arquivo) {
        this.selectedFile = arquivo;
        mainWindow.setSelectedFile(arquivo);
    }

    /**
     * Controla o arquivo de saida...
     * @param arquivo
     */
    public void setSelectedOutputFile(String arquivo) {
        this.signedFileName = arquivo;
        mainWindow.setSelectedOutputFile(arquivo);
    }

    /**
     * Coisa do framework...
     */
    @Override protected void initialize(String[] args) {
        
    }

    /**
     * Coisa do framework...
     */
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

    /**
     * Fun\u00e7\u00e3o que verifica caso a arquivo de destino nao tenha sido escolhido, 
     * verifica se o arquivo de origem sufixado de .p7s existe para evitar sobrescrever 
     * algum arquivo existente sem o consentimento do usu\u00e1rio.
     * Caso o arquivo escolhido j\u00e1 possua alguma assinatura, o sistema incluir\u00e1 nesse
     * arquivo mais uma (co-assinatura). Pode ser um problema caso queia assinar um arquivo assinado e n\u00e3o seu conte\u00fado.
     * Caso o usu\u00e1rio escolha um arquivo de saida que exista, ele ser\u00e1 sobrescrito.
     */
    @Action
    public void appSignFile() {

        if (selectedFile == null) {
            mainWindow.setStatusMessage("Selecione o arquivo.");
            return;
        } else {
            /// Verifica se \u00e9 um arquivo e se j\u00e1 est\u00e1 assinado
            if (signer.isSignedFile(selectedFile)) {
                signedFileName = selectedFile;
            } else {
                File testFile = new File(selectedFile + ".p7s");
                if ((signedFileName == null ) && (testFile.exists())) {
                    mainWindow.setStatusMessage("J\u00e1 existe um arquivo de saida com esse nome... escolha o arquivo de saida.");
                    return;
                } else {
                    if (!testFile.exists()) {
                        signedFileName = selectedFile + ".p7s";
                    }
                }

            }
        }

        if (certificateAlias != null) {
            try {  
                String returnStatus = signer.signFile(this.selectedFile, signedFileName, null, certificateAlias);
                mainWindow.setStatusMessageOK(returnStatus);
                signedFileName = null;

            } catch (Exception ex) {
                mainWindow.setStatusMessage(ex.getMessage());
                signedFileName = null;
                Logger.getLogger(AssinaFacilApp.class.getName()).log(Level.SEVERE, ex.getMessage());
            }
        } else {
              mainWindow.setStatusMessage("Escolha seu certificado...");
        }
    }

    @Action
    public void setCertificateAlias() {
        certificateAlias = mainWindow.getSelectedAlias();
    }

    /**
     * Action provis\u00c3\u00b3ria at\u00c3\u00a9 que alguma alma bonsoda implemente a verifica\u00c3\u00a7\u00c3\u00a3o e
     * a extra\u00c3\u00a7\u00c3\u00a3o do conte\u00c3\u00bado... por ora utilizarei uma chamada ao programa
     * CADIC do Banco Central do Brasil... Copyright?
     */
    @Action
    public void inicializaCADIC() {
        String selectedFile = mainWindow.getSelectedFile();
        try {
            if ((selectedFile!=null) && (!selectedFile.startsWith("Selecione")) && (!selectedFile.equals(""))) {
                Runtime.getRuntime().exec("CADIC.exe \""+selectedFile+"\"");
            	Logger.getLogger(AssinaFacilApp.class.getName()).log(Level.INFO,"Excecutando CADIC.exe \""+selectedFile+"\"");
        	} else {
                Runtime.getRuntime().exec("CADIC.exe");
                Logger.getLogger(AssinaFacilApp.class.getName()).log(Level.INFO,"Excecutando CADIC.exe");
            }
            mainWindow.setStatusMessage("Executando o CADIC do BACEN que faz isso...");
        } catch (IOException ex) {
            mainWindow.setStatusMessage("Erro ao executar o CADIC... coloque no Path do usu\u00c3\u00a1rio");
            Logger.getLogger(AssinaFacilApp.class.getName()).log(Level.SEVERE, "Erro ao executar o CADIC. Coloque no Path do usu\u00c3\u00a1rio.");
        }
    }
    
}
