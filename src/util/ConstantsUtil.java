package util;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

/**
 * Clase de constantes se utilizan durante la ejecucion del programa y para la
 * configuracion del mismo
 */
public final class ConstantsUtil {

	/** Constante el formato de la fecha */

	/** Constante para validaciones de las cargas */
	public static final String NOCARGA = "No hay ultima carga";
	/**
	 * Contante de formato
	 */
	public static final SimpleDateFormat sdf3 = new SimpleDateFormat("ddMMyyyy");

	/**
	 * Contante de formato
	 */
	public static final SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");

	/**
	 * Contante de formato
	 */
	public static final DecimalFormat DFORMATO = new DecimalFormat("####.########");
	
	/** Obtencion del nombre de log */
	public static final String LOG_NAME = "/Bau_log4j.xml";
	
	
	/** Constante para validaciones de las cargas */
	public static final String NOREGISTROS = "No existen registros para este grupo en la interfaz CONSULTA";
	/** Constante para validaciones de las cargas */
	public static final String CIERRA = "Se Cierra CallableStatement";
	
	/**
	 * contructor de la clase constantsUtils
	 */
	  private ConstantsUtil() {
		    throw new IllegalStateException("Utility class");
		  }
}
