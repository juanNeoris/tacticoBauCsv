package util;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

/**
 * Clase de constantes se utilizan durante la ejecucion del programa y para la
 * configuracion del mismo
 */
public final class ConstantsUtil {

	
	


	
	/** Constante para la ruta del archivo repal.properties. */
	public static final String PATH_PROPERTIES = "./";
	
	/** Constante el formato de la fecha */
	
	
	/**
	 * Contante de formato
	 */
	public static final SimpleDateFormat sdf3 = new SimpleDateFormat("ddMMyyyy");
	
	/**
	 * Contante de formato
	 */
	public static final SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");
	
	
	/** Constante error */
	public static final String ERROR = " ERROR: ";
	
	/**
	 * Contante de formato
	 */
	public static final DecimalFormat DFORMATO = new DecimalFormat("####.########");
	
	private ConstantsUtil() {
		// Constructor vacio para prevenir acceso ilegal
	}
}
