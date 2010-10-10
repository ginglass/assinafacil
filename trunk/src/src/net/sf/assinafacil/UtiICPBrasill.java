package net.sf.assinafacil;

import java.io.IOException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bouncycastle.asn1.*;

public class UtiICPBrasill {

	  
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

    public static String showCertDetail(X509Certificate x509Cert) {
        String returnInfo = "Dados sobre o certificado: \n";
        try {
            HashMap subjectAlt = getCertificateInfos(x509Cert);
            String subjectDN = x509Cert.getSubjectDN().getName();
            returnInfo = subjectDN.replaceAll(",", "\n");
            returnInfo += "\n";
            returnInfo += "Número de série convertido: "+x509Cert.getSerialNumber()+"\n";
            returnInfo += "Válido a partir de "+x509Cert.getNotBefore()+"\n";
            returnInfo += "Válido até: "+x509Cert.getNotAfter()+"\n";
            if (! subjectAlt.keySet().isEmpty())
                returnInfo += "Detalhes do certificado: \n";

            for (String oid : (Set<String>)subjectAlt.keySet()) {
                String valor = subjectAlt.get(oid).toString();
                if (oid.equals("2.16.76.1.3.1")) {
                    returnInfo += "\t Certificado emitido para pessoa física\n";
                    String resultado[] = parserPessoaFisica(valor);

                    returnInfo += "\t  Data de Nascimento   : "+resultado[0] + "\n";
                    returnInfo += "\t  CPF                  : "+resultado[1] + "\n";
                    returnInfo += "\t  PIS / PASEP          : "+resultado[2] + "\n";
                    returnInfo += "\t  Identidade (RG)      : "+resultado[3] + " ("+ resultado[4]+")\n";


                } else if (oid.equals("2.16.76.1.3.5")) {
                    // Titulo de Eleitor
                    returnInfo += "\t  Titulo de Eleitor    : "+valor.substring(0,11) + "\n";
                    returnInfo += "\t  Zona Eleitoral       : "+valor.substring(11,14) + "\n";
                    returnInfo += "\t  Seção Eleitoral      : "+valor.substring(14,18) + "\n";
                    returnInfo += "\t  Municipio Eleitoral  : "+valor.substring(18) + "\n";
                } else if (oid.equals("2.16.76.1.3.4")) {
                    // Emitido para pessoa juridica -> dados do responsavel
                    returnInfo += "\t  Dados do responsavel pela PJ =>" + oid + "\t" + valor + "\n";

                } else if (oid.equals("2.16.76.1.3.2")) {
                    // Pessoa juridica Nome do responsavel
                    returnInfo += "\t  Nome do responsavel PJ =>" + oid + "\t" + valor + "\n";

                } else if (oid.equals("2.16.76.1.3.3")) {
                    // Pessoa juridica CNPJ
                    returnInfo += "\t  CNPJ =>" + oid + "\t" + valor + "\n";
                } else {
                    returnInfo += "\t OID =>" + oid + "\t" + valor + "\n";
                }
            }

            returnInfo += "Assinado por: "+x509Cert.getIssuerDN().getName()+"\n";

        } catch (IOException ex) {
            Logger.getLogger(UtiICPBrasill.class.getName()).log(Level.SEVERE, null, ex);
            returnInfo = "Erro ao processar o certificado.";
        } catch (CertificateParsingException ex) {
            Logger.getLogger(UtiICPBrasill.class.getName()).log(Level.SEVERE, null, ex);
            returnInfo = "Erro ao processar o certificado.";
        }
        return returnInfo;

    }

    public static String[] parserPessoaFisica(String valor) {
        String dtnasc = valor.substring(0,8);
        String cpf = valor.substring(8,19);
        String pispasep = valor.substring(19,30);
        String identidade = valor.substring(30,41);
        String emissor = valor.substring(41);
        return new String[] {  dtnasc, cpf,pispasep,identidade,emissor  };
    }
}