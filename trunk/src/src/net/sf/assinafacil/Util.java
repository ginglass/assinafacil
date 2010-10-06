package net.sf.assinafacil;

import java.io.IOException;
import java.security.cert.CertificateParsingException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import org.bouncycastle.asn1.*;

public class Util {

	  
    /** 
     * Interpreta um dado do tipo otherName.  
     * Obs. O JDK 5.0 n�o tem classes que lidem com um dado do tipo OtherName. 
     * � necess�rio usar o BouncyCastle. 
     * @param encoded O dado em ASN.1. 
     * @return Um par contendo o OID e o conte�do. 
     */  
    private static Pair<DERObjectIdentifier, String> getOtherName(byte[] encoded) throws IOException {
        // O JDK 5.0 n�o tem classes que lidem com um dado do tipo OtherName.  
        // � necess�rio usar o BouncyCastle.  
        ASN1InputStream inps = new ASN1InputStream(encoded);  
        DERSequence seq = null;  
        DERObjectIdentifier oid = null;  
        String conteudo = "";  
        seq = (DERSequence) inps.readObject();  
        inps.close();  
        Enumeration en = seq.getObjects();  
        oid = (DERObjectIdentifier) en.nextElement();  
        DERObject obj = ((ASN1TaggedObject) ((ASN1TaggedObject) en.nextElement()).getObject()).getObject();  
        if (obj instanceof DERString) { // Certificados antigos SERASA - incorretos  
            conteudo = ((DERString) obj).getString();  
        } else if (obj instanceof DEROctetString) { // Certificados corretos  
            conteudo = new String(((DEROctetString) obj).getOctets(), "ISO-8859-1");  
        }  
        return new Pair<DERObjectIdentifier, String>(oid, conteudo);  
    }
    
    public static HashMap<String,String> getCertificateInfos(java.security.cert.X509Certificate cert) throws IOException, CertificateParsingException {
    	HashMap<String,String> props = new HashMap<String,String>();
            if (cert.getSubjectAlternativeNames() == null) return props;
	    for (List<?> subjectAlternativeName : cert.getSubjectAlternativeNames()) {  
	        String email;  
	        Pair<DERObjectIdentifier, String> otherName;  
	        int pos;  
	        // O primeiro elemento � um Integer com o valor 0 = otherName, 1 = rfc822name etc.  
	        // O segundo valor � um byte array ou uma String. Veja o javadoc de  
	        // getSubjectAlternativeNames.  
	        switch (((Number) subjectAlternativeName.get(0)).intValue()) {  
	        case 0: // OtherName - cont�m CPF, CNPJ etc.  
	            otherName = getOtherName((byte[]) subjectAlternativeName.get(1));
	            props.put(otherName.first.getId(), otherName.second);
	            // o OID fica em otherName.first   
	            break;  
	        case 1: // rfc822Name - usado para email  
	            email = (String) subjectAlternativeName.get(1);  
	            props.put("email", email);  
	            break;  
	        default:  
	            break;  
	        }  
	        
	    }  
	    return props;
    }
    
}