package sftp;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

import lombok.Getter;
import lombok.Setter;

/**
 * Clase que se encarga de cargar el archivo properties
 * Que realizara la conexion con el servidor de GBO 
 * para cargar RTRA's y generar un reporte 
 */
public class ConfigProperties {
	// Se crean variables privadas para poder cargarlas con los valores del archivo
	// hostProperties.properties

	private static final Logger LOGGER = Logger.getLogger(ConfigProperties.class.getName());

	/**
	 * getter and setter host
	 */
	@Getter
	@Setter
	private String host;
	/**
	 * getter and setter usuario
	 */
	@Getter
	@Setter
	private String usuario;
	/**
	 * getter and setter password
	 */
	@Getter
	@Setter
	private String password;
	/**
	 * getter and setter key
	 */
	@Getter
	@Setter
	private String key;
	/**
	 * getter and setter knowhost
	 */
	@Getter
	@Setter
	private String knowhost;
	/**
	 * getter and setter puerto
	 */
	@Getter
	@Setter
	private int puerto;
	/**
	 * getter and setter strictHKC
	 */
	@Getter
	@Setter
	private boolean strictHKC;
	/**
	 * getter and setter conectKnowHost
	 */
	@Getter
	@Setter
	private boolean conectKnowHost;

	// Se crea clase donde se carga el archivo properties, crea una instancia de
	// objeto Properties y retorna un arreglo con los datos del archivo properties
	private Properties getProperties() {
		// instance de un objeto Properties
		Properties prop = new Properties();

		// Obtiene la ruta absoluta del archivo hostProperties.properties para leerlo de
		// manera externa.
		try (InputStream input = new FileInputStream(
				System.getProperty("user.dir") + "\\" + "hostProperties.properties")) {
			// Carga propiedades de archivo properties
			prop.load(input);
		} catch (IOException ex) {
			conexionFtp conFtp = new conexionFtp();
			// Excepción para generar un fichero con un log de información de error de
			// conexión
			try {
				conFtp.makeFichero("ErrorPropertiesFile_",
						"Ha sucedido un error al querer cargar el archivo properties\n" + ex.toString()
								+ "\nSugerencia:Coloca el archivo hostProperties.properties en la misma ruta que tu componente .jar");
			} catch (IOException e) {
				LOGGER.info(e);
			}
			LOGGER.info(ex);
		}
		return prop;
	}

	/**
	 * método público que debe de ser instanciado para acceder desde otra clase para
	 * cargar las variables con valores del archivo properties
	 */

	public void createVar() {
		Properties getPr = getProperties();
		this.host = getPr.getProperty("sftp.host");
		this.usuario = getPr.getProperty("sftp.usuario");
		this.puerto = Integer.parseInt(getPr.getProperty("sftp.puerto"));
		this.password = getPr.getProperty("sftp.password");
		this.key = getPr.getProperty("sftp.key");
		this.knowhost = getPr.getProperty("sftp.knowhost");
		this.strictHKC = Boolean.parseBoolean(getPr.getProperty("exist.key"));
		this.conectKnowHost = Boolean.parseBoolean(getPr.getProperty("exist.knowhost"));
	}
}
