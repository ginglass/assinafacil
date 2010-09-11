package net.sf.assinafacil;

/**
 * Interface para permitir generalizar a classe responsavel pela implementação 
 * do assinador.
 */

import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.Map;

/**
 *
 * @author ginglass
 */
public interface Assinador {

    public boolean isInitialized();

    public boolean isSignedFile(String fileName);

    public void initialize(String password) throws Exception;
    
    public KeyStore getKeyStore() throws Exception;
    
    public Map<String, X509Certificate> getAllKeyCertificates() throws Exception;
    
    public X509Certificate getCertificate(String alias) throws Exception;
   
    public String signFile(String fileInput, String signedFileName, String password, String certificateAlias) throws Exception;

}
