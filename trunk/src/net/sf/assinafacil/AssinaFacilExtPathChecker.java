/*  This file is part of AssinaFacil.

    AssinaFacil is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    AssinaFacil is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with AssinaFacil.  If not, see <http://www.gnu.org/licenses/>.
*/

package net.sf.assinafacil;

import java.security.cert.CertPathValidatorException;
import java.security.cert.Certificate;
import java.security.cert.PKIXCertPathChecker;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Mario
 */
public class AssinaFacilExtPathChecker extends PKIXCertPathChecker {

    public final String supporterExtension = "2.5.29.37";
    Set<String> supported;

    @Override
    public void init(boolean forward) throws CertPathValidatorException {
        System.err.println("INIT => " + forward);
        supported = new HashSet<String>();
        supported.add(supporterExtension);
    }

    @Override
    public boolean isForwardCheckingSupported() {
        System.err.println("is FORWARD SUPPORTED");
        return false;
    }

    @Override
    public Set<String> getSupportedExtensions() {
        System.err.println("VERIFIC O EXTN");
        return supported;
    }

    @Override
    public void check(Certificate cert, Collection<String> unresolvedCritExts) throws CertPathValidatorException {
        if (cert instanceof X509Certificate) {
            X509Certificate x509Cert = (X509Certificate) cert;
            for (String unresolved: unresolvedCritExts) {
                System.err.println("Recebe o pedido de verificacao de "+unresolved+ " para o cert "+x509Cert.getSubjectX500Principal().getName());
            }
        }
        unresolvedCritExts.remove(this.supporterExtension);
    }
}
