package assinafacil;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Properties;

/**
 *
 * @author ricky
 */
public interface Assinador {

    public boolean isInitialized();

    public void initialize(String password) throws Exception;
    
    public KeyStore getKeyStore() throws Exception;
    
    public Map<String, X509Certificate> getAllKeyCertificates() throws Exception;
    
    public X509Certificate getCertificate(String alias) throws Exception;
   
    public String signFile(String fileInput, String password, String certificateAlias) throws Exception;

//    public String coSignFile(String fileInput, String password, String certificateAlias) throws Exception;

}
