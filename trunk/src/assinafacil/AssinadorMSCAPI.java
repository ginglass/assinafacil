package assinafacil;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.Security;
import java.security.cert.CertStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.cms.CMSException;

import org.bouncycastle.cms.CMSProcessable;
import org.bouncycastle.cms.CMSProcessableFile;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class AssinadorMSCAPI implements Assinador {

	public KeyStore keyStore = null;
	public static final String keyStoreString = "Windows-MY";
	public static final String providerString = "SunMSCAPI";


	public AssinadorMSCAPI() throws Exception {
		Security.addProvider(new BouncyCastleProvider());
		this.initialize(null);
	}
	
	
	public boolean supportsMSCAPI() {
		if (Security.getProvider("SunMSCAPI")!= null) 
			return true;
		else
			return false;
	}
	
	@Override
	public Map<String, X509Certificate> getAllKeyCertificates() throws Exception {
		Map<String, X509Certificate> res = new HashMap<String, X509Certificate>();
        Enumeration<String> aliases = keyStore.aliases();
        while (aliases.hasMoreElements()) {
            String alias = aliases.nextElement();
            if (keyStore.isKeyEntry(alias)) {
                res.put(alias, getCertificate(alias));
            }
        }
        return res;
	}

	@Override
	public KeyStore getKeyStore() throws Exception {
		return keyStore;
	}

	@Override
	public void initialize(String password) throws Exception {
		if (!this.supportsMSCAPI()) {
				throw new java.security.NoSuchProviderException("N�o suporta criptografia nativa Microsoft. � java 1.6 43 bits?");
		}
		keyStore = KeyStore.getInstance(keyStoreString);
		keyStore.load(null, null) ;
                
	}
	

	@Override
	public boolean isInitialized() {
		if (this.keyStore != null) 
			return true;
		else 
			return false;
	}

	@Override
	public String signFile(String fileInput, String password, String certificateAlias) throws Exception {
        if (!isInitialized()) {
                throw new java.security.KeyException("Chaveiro não inicializado ou erro ao acessá-lo.");
        }

        PrivateKey priv = null;
        Certificate storecert = null;
        Certificate[] certChain = null;
        ArrayList<Certificate> certList = new ArrayList<Certificate>();
        CertStore certs = null;
        CMSSignedData signedData = null;
        CMSProcessable content = null;
        byte[] signeddata = null;
        String signedFileName = null;
        String retorno;

        certChain = keyStore.getCertificateChain(certificateAlias);

        if ( certChain == null ) {
                throw new GeneralSecurityException("Cadeia do certificado "+ certificateAlias + " não encontrada.");
        }
		
	    for ( int i = 0; i < certChain.length;i++)
	    	certList.add(certChain[i]);      
	   
	    certs = CertStore.getInstance("Collection", new CollectionCertStoreParameters(certList));

	    storecert = keyStore.getCertificate(certificateAlias);
	    priv = (PrivateKey)(keyStore.getKey(certificateAlias, null));
	    if (priv == null) {
	    	throw new java.security.AccessControlException("Acesso à chave foi negado... senha inválida?");
	    }

        CMSSignedDataGenerator signGen = new CMSSignedDataGenerator();
        signGen.addSigner(priv, (X509Certificate)storecert, CMSSignedDataGenerator.DIGEST_SHA1);
        signGen.addCertificatesAndCRLs(certs);

        try {
            signedData = new CMSSignedData(new FileInputStream(fileInput));
            content = signedData.getSignedContent();
            signGen.addSigners(signedData.getSignerInfos());
            signGen.addCertificatesAndCRLs(signedData.getCertificatesAndCRLs("Collection", "BC"));
            CMSSignedData signedData2 = signGen.generate(content,true,providerString);
            signeddata = signedData2.getEncoded();
            signedFileName = fileInput;
            retorno = "Arquivo " + signedFileName + " foi assinado novamente.";
        } catch (CMSException e ) {
            content = new CMSProcessableFile(new File(fileInput));
            signedData = signGen.generate(content,true,providerString);
            signeddata = signedData.getEncoded();
            signedFileName = fileInput + ".p7s";
            retorno = "Arquivo " + signedFileName + " foi assinado.";
        }

        FileOutputStream  fileOutput = new FileOutputStream(signedFileName);
	    fileOutput.write(signeddata);
	    fileOutput.close();
        return retorno;
	}

	@Override
	public X509Certificate getCertificate(String alias) throws Exception {
        return (X509Certificate) keyStore.getCertificate(alias);
    }
}
