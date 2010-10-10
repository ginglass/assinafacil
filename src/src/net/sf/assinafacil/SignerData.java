/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sf.assinafacil;

import java.security.cert.CertPath;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.DERUTCTime;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.cms.SignerInformation;

/**
 *
 * @author Mario
 */
class SignerData {
    private X509Certificate signCertificate;
    private String name;
    private Boolean isICPValidate;
    private Date dateSign;
    private CertPath certificatePath;
    private X509Certificate trustCert;
    private String signatureState;


    public SignerData (SignerInformation signerInfo, CertPath certPath, X509Certificate trust, boolean isICP, String signatureState) {
        signCertificate = (X509Certificate) certPath.getCertificates().get(0);
        name = signCertificate.getSubjectX500Principal().getName();
        StringTokenizer iteradorValores = new StringTokenizer(name,",");
        while (iteradorValores.hasMoreElements()) {
            String valor = iteradorValores.nextToken();
            if (valor.startsWith("CN"))
                name = valor.substring(valor.indexOf("=")+1,valor.length());
        }

        this.trustCert = trust;

        try {
            Attribute atributo = (Attribute) signerInfo.getSignedAttributes().get(new DERObjectIdentifier("1.2.840.113549.1.9.5"));
            dateSign = new DERUTCTime(atributo.getAttrValues().getObjectAt(0).toString()).getDate();
        } catch (ParseException ex) {
            Logger.getLogger(SignerData.class.getName()).log(Level.SEVERE, null, ex);
        }

        certificatePath = certPath;
        this.isICPValidate = isICP;
        this.signatureState = signatureState;

    }

        public SignerData (SignerInformation signerInfo, X509Certificate signerCertificate, String signatureState) {
        signCertificate = signerCertificate;
        name = signCertificate.getSubjectX500Principal().getName();

        StringTokenizer iteradorValores = new StringTokenizer(name,",");

        while (iteradorValores.hasMoreElements()) {
            String valor = iteradorValores.nextToken();
            if (valor.startsWith("CN"))
                name = valor.substring(valor.indexOf("=")+1,valor.length());
        }

        this.trustCert = signerCertificate;

        try {
            Attribute atributo = (Attribute) signerInfo.getSignedAttributes().get(new DERObjectIdentifier("1.2.840.113549.1.9.5"));
            dateSign = new DERUTCTime(atributo.getAttrValues().getObjectAt(0).toString()).getDate();
        } catch (ParseException ex) {
            Logger.getLogger(SignerData.class.getName()).log(Level.SEVERE, null, ex);
        }

        certificatePath = null;
        this.isICPValidate = false;
        this.signatureState = signatureState;
    }

    public Date getDateSign() {
        return dateSign;
    }

    public void setDateSign(Date dateSign) {
        this.dateSign = dateSign;
    }

    public String getIsICPValidate() {
        if (isICPValidate) 
            return "Válido para ICP-Brasil";
        else
            return "Inválido para ICP Brasil";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public X509Certificate getSignCertificate() {
        return signCertificate;
    }

    public void setSignCertificate(X509Certificate signCertificate) {
        this.signCertificate = signCertificate;
    }

    public String getCertificateValidity() {
        try {
            signCertificate.checkValidity();
            return "Válido";
        } catch (CertificateExpiredException ex) {
            //Logger.getLogger(SignerData.class.getName()).log(Level.SEVERE, null, ex);
            return "Expirado";
        } catch (CertificateNotYetValidException ex) {
            //Logger.getLogger(SignerData.class.getName()).log(Level.SEVERE, null, ex);
            return "Ainda não é válido";
        }

    }

    X509Certificate getTrust() {
        return this.trustCert;
    }

    public String getSignerState() {
        return signatureState;
    }

    public void setSignerState(String signerState) {
        this.signatureState = signerState;
    }

    public CertPath getCertificatePath() {
        return certificatePath;
    }

    public void setCertificatePath(CertPath certificatePath) {
        this.certificatePath = certificatePath;
    }

}
