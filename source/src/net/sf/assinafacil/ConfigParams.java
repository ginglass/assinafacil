/*
 * Esse arquivo fará referencia a imagem de inicializacao, diretorio da CRL, diretorio 
 * da CA (onde ficarão os certificados CA), etc...  
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
