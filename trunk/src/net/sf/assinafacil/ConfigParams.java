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

/**
 *
 * @author Mario
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Le os parametros do arquivo de configuracao
 * @author ginglass
 *
 */

public class ConfigParams{

        String prefix;
	Properties dbProperties = null;

        public ConfigParams(String prefix, String configFile) throws FileNotFoundException, IOException{

            InputStream inStream = new FileInputStream (new File (configFile));
            dbProperties = new Properties();
            dbProperties.load(inStream);
            inStream.close();
            this.prefix = prefix;

        }

	public String getICPRootCertificate() {
		return dbProperties.getProperty(prefix + ".icp_root_certificate");
	}

}
