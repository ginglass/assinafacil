package net.sf.assinafacil;

/***
 * Implementa o Assinador para o provider SunMSCAPI.
 * S\u00f3 funciona com a vers\u00e3o 32bits pois a biblioteca nativa n\u00e3o foi distribuida
 * pela oracle na versao 64bits.
 * 
 * author: ginglass
 */

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.SignatureException;
import java.security.cert.CertStore;
import java.security.cert.Certificate;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bouncycastle.cms.CMSException;

import org.bouncycastle.cms.CMSProcessable;
import org.bouncycastle.cms.CMSProcessableFile;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.SignerId;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationStore;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class AssinadorMSCAPI implements Assinador {

	public KeyStore keyStore = null;
	public static final String KEYSTORE_STRING = "Windows-MY";
	public static final String PROVIDER_STRING = "SunMSCAPI";

	public AssinadorMSCAPI() throws Exception {
		Security.addProvider(new BouncyCastleProvider());
		this.initialize(null);
	}
	
	/**
	 * 	
	 * @return true se suporta o provider SunMSCAPI
	 */
	public boolean supportsMSCAPI() {
		if (Security.getProvider(PROVIDER_STRING)!= null) 
			return true;
		else {
			Logger.getLogger(AssinadorMSCAPI.class.getName()).log(Level.INFO,"Ambiente n\u00e3o disp\u00f5e do SunMSCAPI");
			return false;			
		}
	}
	
	/**
	 * 
	 * @return Mapa de certificados relacionados com seu respectivo apelido no keystore.
	 */
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
	/**
	 * @return KeyStore utilizado por esse assinador
	 */
	public KeyStore getKeyStore() throws Exception {
		return keyStore;
	}

	@Override
	/***
	 * Inicializa o assinador. verifica o suporte ao provider e instancia 
	 * o keystore que ser\u00e1 utilizado pelo assinador.
	 */
	public void initialize(String password) throws Exception {
		if (!this.supportsMSCAPI()) {
				throw new java.security.NoSuchProviderException("N\u00ef\u00bf\u00bdo suporta criptografia nativa Microsoft. \u00ef\u00bf\u00bd java 1.6 43 bits?");
		}
		keyStore = KeyStore.getInstance(KEYSTORE_STRING);
		keyStore.load(null, null) ;
                
	}
	

	@Override
	/***
	 * @return true caso o Assinador j\u00e1 tenha sido inicializado
	 */
	public boolean isInitialized() {
		if (this.keyStore != null) 
			return true;
		else 
			return false;
	}

	@Override
	/***
	 * Assina digitalmente o arquivo de entrada e gera o arquivo de sa\u00edda.
	 * nesse caso a senha n\u00e3o \u00e9 utilizada pois o keystore \u00e9 um token suja senha 
	 * ser\u00e1 requerida pelo MSCAPI.
	 * 
	 * @return Mensagem de status que ser\u00e1 exibida na interface.
	 */
	public String signFile(String fileInput, String signedFileName, String password, String certificateAlias) throws Exception {
        if (!isInitialized()) {
                throw new java.security.KeyException("Chaveiro n\u00c3\u00a3o inicializado ou erro ao acess\u00c3\u00a1-lo.");
        }

        PrivateKey priv = null;
        Certificate storecert = null;
        Certificate[] certChain = null;
        ArrayList<Certificate> certList = new ArrayList<Certificate>();
        CertStore certs = null;
        CMSSignedData signedData = null;
        CMSProcessable content = null;
        byte[] signeddata = null;
   
        String retorno;

        if (signedFileName == null)
            signedFileName = fileInput;

        certChain = keyStore.getCertificateChain(certificateAlias);

        if ( certChain == null ) {
                throw new GeneralSecurityException("Cadeia do certificado "+ certificateAlias + " n\u00c3\u00a3o encontrada.");
        }
        certList.addAll(Arrays.asList(certChain));
	   
	    certs = CertStore.getInstance("Collection", new CollectionCertStoreParameters(certList));

	    storecert = keyStore.getCertificate(certificateAlias);
	    priv = (PrivateKey)(keyStore.getKey(certificateAlias, null));
	    if (priv == null) {
	    	throw new java.security.AccessControlException("Acesso \u00c3\u00a0 chave foi negado... senha inv\u00c3\u00a1lida?");
	    }

        CMSSignedDataGenerator signGen = new CMSSignedDataGenerator();
        signGen.addSigner(priv, (X509Certificate)storecert, CMSSignedDataGenerator.DIGEST_SHA1);
        signGen.addCertificatesAndCRLs(certs);

        try {
            signedData = new CMSSignedData(new FileInputStream(fileInput));
            content = signedData.getSignedContent();
            signGen.addSigners(signedData.getSignerInfos());
            signGen.addCertificatesAndCRLs(signedData.getCertificatesAndCRLs("Collection", "BC"));
            CMSSignedData signedData2 = signGen.generate(content,true,PROVIDER_STRING);
            signeddata = signedData2.getEncoded();
   
            retorno = "Arquivo " + signedFileName + " foi assinado novamente.";
            
        } catch (CMSException e ) {
            content = new CMSProcessableFile(new File(fileInput));
            signedData = signGen.generate(content,true,PROVIDER_STRING);
            signeddata = signedData.getEncoded();

   
            retorno = "Arquivo " + signedFileName + " foi assinado.";
        }

        FileOutputStream  fileOutput = new FileOutputStream(signedFileName);
	    fileOutput.write(signeddata);
	    fileOutput.close();

	    Logger.getLogger(AssinadorMSCAPI.class.getName()).log(Level.INFO, retorno);
        
	    return retorno;
	}
	
	@Override
	public SignerInformationStore getSignatures(File fileInput) throws java.security.SignatureException, FileNotFoundException{
		CMSSignedData signedData = null;

		SignerInformationStore signers = null;
		
		try {
			signedData = new CMSSignedData(new FileInputStream(fileInput));
			signers = signedData.getSignerInfos();
			
			return signers;
		
		}  catch (CMSException e) {
			throw new SignatureException("Arquivo n�o assinado ou formato inv�lido");
		}
	}

	@Override
	public CertStore getCertificates(File fileInput) throws java.security.SignatureException, FileNotFoundException{
		CMSSignedData signedData = null;

		CertStore certs = null;

		try {
			signedData = new CMSSignedData(new FileInputStream(fileInput));
                        certs = signedData.getCertificatesAndCRLs("Collection", "BC");
			return certs;

                } catch (NoSuchAlgorithmException ex) {
                    Logger.getLogger(AssinadorMSCAPI.class.getName()).log(Level.SEVERE, null, ex);
                    return null;
                } catch (NoSuchProviderException ex) {
                    Logger.getLogger(AssinadorMSCAPI.class.getName()).log(Level.SEVERE, null, ex);
                    return null;
		}  catch (CMSException e) {
			throw new SignatureException("Arquivo n�o assinado ou formato inv�lido");
		}
	}


	@Override 
	public byte[] getSignedContent(File fileInput) throws GeneralSecurityException, IOException {
		CMSSignedData signedData = null;
        CMSProcessable content = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
		try {
			signedData = new CMSSignedData(new FileInputStream(fileInput));
			content = signedData.getSignedContent();
			content.write(baos);
			return baos.toByteArray();
			
		} catch (CMSException e ) {
			throw new GeneralSecurityException("Arquivo n�o assinado ou formata��o inv�lida.");
		}
	}

	@Override 
	public boolean extractSignedContent(File fileInput, File fileOutput) throws GeneralSecurityException, IOException {
		CMSSignedData signedData = null;
        CMSProcessable content = null;
        FileOutputStream fos = new FileOutputStream(fileOutput);
        
		try {
			signedData = new CMSSignedData(new FileInputStream(fileInput));
			content = signedData.getSignedContent();
			content.write(fos);
			fos.close();
			return true;
			
		} catch (CMSException e ) {
			throw new GeneralSecurityException("Arquivo n�o assinado ou formata��o inv�lida.");
		}
	}

	@Override
	/***
	 * Pega do keystore o certificado com determinado apelido
	 */
	public X509Certificate getCertificate(String alias) throws Exception {
        return (X509Certificate) keyStore.getCertificate(alias);
    }

	/***
	 * Verifica se o arquivo j\u00e1 foi assinado... feio pois identifica via exception...
	 * mas \u00e9 r\u00e1pido e sei que desenvolvedores mais cuidadosos me ajudar\u00e3o a melhorar
	 * inclusive esse trecho do c\u00f3digo.
	 */
    public boolean isSignedFile(String fileName) {
        CMSSignedData signedData = null;
         try {
            signedData = new CMSSignedData(new FileInputStream(fileName));
            return signedData.getContentInfo().getContentType().equals(org.bouncycastle.asn1.cms.CMSObjectIdentifiers.signedData);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(AssinadorMSCAPI.class.getName()).log(Level.INFO, "Arquivo " + fileName +" n\u00e3o encontrado", ex);
            return false;
        } catch (CMSException ex ) {
            // Malformed content.
            // DEBUG? Logger.getLogger(AssinadorMSCAPI.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
}
