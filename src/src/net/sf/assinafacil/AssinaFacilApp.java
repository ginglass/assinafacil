/*
 * AssinaFacilApp.java
 */

package net.sf.assinafacil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertPath;
import java.security.cert.CertPathBuilder;
import java.security.cert.CertPathBuilderException;
import java.security.cert.CertPathBuilderResult;
import java.security.cert.CertSelector;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateParsingException;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.PKIXCertPathBuilderResult;
import java.security.cert.PKIXCertPathChecker;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.cert.CertificateException;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;
import net.sf.assinafacil.Assinador;
import net.sf.assinafacil.AssinadorMSCAPI;
import java.util.Set;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationStore;
import org.bouncycastle.jce.X509Principal;

/**
 * A classe principal da aplica\u00e7\u00e3o.
 */
public class AssinaFacilApp extends SingleFrameApplication {

    private Assinador signer;
    private String selectedFile = null;
    String signedFileName = null;
    private String certificateAlias = null;
    private AssinaFacilView mainWindow;

    private ConfigParams configProperties;

    public static final String CONFIG_FILE = "assinafacil.properties";
    /**
     * Inicializa o assinador e mostra a tela principal da aplica\u00e7\u00e3o.
     */
    @Override protected void startup() {
      
            mainWindow =  new AssinaFacilView(this);
            show(mainWindow);
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
        try {
            signer = new AssinadorMSCAPI();
            signer.initialize(null);
            signer.getAllKeyCertificates().keySet();

            String configFileName = null;

            if (args.length == 0) {
                configFileName = this.CONFIG_FILE;
            } else {
                configFileName = args[0];
            }

            if (new File(configFileName).canRead()) {
                configProperties = new ConfigParams("assinafacil",configFileName);
            } else {
                Logger.getLogger(AssinaFacilApp.class.getName()).log(Level.SEVERE,"Arquivo de configuração não encontrado.");
                System.exit(1);
            }


        } catch (Exception ex) {

            Logger.getLogger(AssinaFacilApp.class.getName()).log(Level.SEVERE, null, ex);

            if (mainWindow != null) {
                mainWindow.setStatusMessage(ex.getMessage());
            } else {
                System.exit(1);
            }
        }
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
                this.setSelectedInputFile(signedFileName);
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

    @Action
    public void dumpSignersAndCersInfo() {

       Signature sig;
 
       String selectedFile = mainWindow.getSelectedFile();
       
       if ((selectedFile == null) || (selectedFile.startsWith("Selecione")) || (selectedFile.equals(""))) {
            mainWindow.setStatusMessage("Selecione o arquivo para verificação...");
            return;
       }
        try {
            SignerInformationStore signerInformationStore = signer.getSignatures(new File(selectedFile));
            CertStore certificateStore = signer.getCertificates(new File(selectedFile));
            
            Collection<? extends Certificate> certificates = certificateStore.getCertificates(null);
            Collection colSign = signerInformationStore.getSigners();

            HashMap<String,X509Certificate> uniqueCerts = new HashMap();

            HashSet<TrustAnchor> trustSet = new HashSet();

            // Pega os certificados sem repetição pois o CertBuilder erra na seleção...
            for (Certificate cert : certificates) {
                if (cert instanceof X509Certificate) {
                    X509Certificate x509Cert = (X509Certificate) cert;
                    // Pega só os root's CA
                    if ( ( x509Cert.getBasicConstraints()!= -1) && (x509Cert.getIssuerDN().getName().equals(x509Cert.getSubjectDN().getName())))
                        trustSet.add(new TrustAnchor(x509Cert,null));

                    // Monta uma estrutura com a lista de certificados unicos
                    uniqueCerts.put(x509Cert.getSubjectX500Principal().getName()+"|"+x509Cert.getSerialNumber()+"|"+x509Cert.getIssuerDN(),x509Cert);
                }
            }

            /*****
             *  Trecho de aprendizado... muito interessante para criacao de keystore
            // Cria um certstore com os certificados unicos que serão utilizados para montar a cadeia
            CollectionCertStoreParameters certStoreParams = new CollectionCertStoreParameters(uniqueCerts.values());
            CertStore uniqueCertStore = CertStore.getInstance("Collection", certStoreParams, "BC");

            // Monta um keystore com base numa lista de certificados raiz confiavis
            File dirAc = new File(configProperties.getICPRootCertificate());

            FilenameFilter filter = new FilenameFilter() {
                public boolean accept(File dir, String name) {
                   return !name.endsWith(".");
                }
            };

            KeyStore.PasswordProtection passKey = new KeyStore.PasswordProtection("casecret".toCharArray());
            KeyStore trustKeyStore =  KeyStore.Builder.newInstance("JKS",null,passKey).getKeyStore();
            
            String[] caFiles = dirAc.list(filter);
            for (String file : caFiles) {
                 InputStream inStream = new FileInputStream(configProperties.getICPRootCertificate() + "/" + file);
                 CertificateFactory cf = CertificateFactory.getInstance("X.509");
                 X509Certificate x509Cert = (X509Certificate)cf.generateCertificate(inStream);
                 inStream.close();
//                 if ( ( x509Cert.getBasicConstraints()!= -1) && (x509Cert.getIssuerDN().getName().equals(x509Cert.getSubjectDN().getName())))
                    trustKeyStore.setCertificateEntry(x509Cert.getSubjectDN().getName(), x509Cert);
                 inStream.close();
                 Logger.getLogger(AssinaFacilApp.class.getName()).log(Level.INFO, "INCLUINDO => " + x509Cert.getSubjectDN().getName() );
            }

            // Grava um jks com tudo
            trustKeyStore.store(new FileOutputStream("acraiz.jks"), "casecret".toCharArray());

            ****/

            Iterator iteradorSigners = colSign.iterator();

            while (iteradorSigners.hasNext()) {
                Object signObject = iteradorSigners.next();

                if (signObject instanceof SignerInformation) {

                    SignerInformation signerInfo = (SignerInformation) signObject;

                    /**** Não precisa mais criar o target
                        // Pega o serialnumber e o issue (emissor) do signer
                        BigInteger signerSerialNumber = signerInfo.getSID().getSerialNumber();
                        X500Principal signerIssuer = signerInfo.getSID().getIssuer();
                    ***/

                    Logger.getLogger(AssinaFacilApp.class.getName()).log(Level.INFO, signerInfo.getSID().getSerialNumber() + " => assinou com algoritmo " +signerInfo.getDigestAlgOID());
                    
                    try {

                        /** Não precisa selecionar - fica por enquanto para aprendizado...
                           X509CertSelector target = new X509CertSelector();
                           target.setSerialNumber(signerSerialNumber);
                           target.setIssuer(signerIssuer);
                        ****/

                        PKIXBuilderParameters params = new PKIXBuilderParameters(trustSet, signerInfo.getSID());

                        params.addCertStore(certificateStore);
                        params.setRevocationEnabled(false);

                        // Trata politica especifica da ICP
                        params.addCertPathChecker(new AssinaFacilExtPathChecker());

                        CertPathBuilder builder = CertPathBuilder.getInstance("PKIX","BC");

                        PKIXCertPathBuilderResult certChainResult = (PKIXCertPathBuilderResult) builder.build(params);

                        CertPath cp = certChainResult.getCertPath();

                        Logger.getLogger(AssinaFacilApp.class.getName()).log(Level.INFO, "VALIDADO OK ["+cp+"]\n");

                    } catch (CertPathBuilderException ex) {
                        // Não foi validado
                        Logger.getLogger(AssinaFacilApp.class.getName()).log(Level.SEVERE, "Nao Validado " + ex.getMessage()+" ");

                    } catch (InvalidAlgorithmParameterException ex) {
                        // Não foi validado
                        Logger.getLogger(AssinaFacilApp.class.getName()).log(Level.SEVERE, "Nao Validado " + ex.getMessage()+" ");

                    } catch (Exception ex) {
                        // Não foi validado
                        Logger.getLogger(AssinaFacilApp.class.getName()).log(Level.SEVERE, "Nao Validado " + ex.getMessage()+" ");
                    }

                }
            }

        } catch (IOException ex) {
            Logger.getLogger(AssinaFacilApp.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SignatureException ex) {
            Logger.getLogger(AssinaFacilApp.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CertStoreException ex) {
                Logger.getLogger(AssinaFacilApp.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    
}
