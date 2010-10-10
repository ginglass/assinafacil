package net.sf.assinafacil;

/**
 * Interface para permitir generalizar a classe responsavel pela implementa��o 
 * do assinador.
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.SignatureException;
import java.security.cert.CertStore;
import java.security.cert.X509Certificate;
import java.util.Map;

import org.bouncycastle.cms.SignerInformationStore;

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
