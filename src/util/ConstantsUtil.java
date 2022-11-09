package util;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;

import lombok.Getter;

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
	/**
	 * array que almacena instrumentos Avales
	 */
	@Getter
	private  ArrayList<String> avales = new ArrayList<>(Arrays.asList("AVAL COMERCIAL - NO COMPROMETIDO",
			"AVAL FINANCIERO - NO COMPROMETIDO", "AVAL NO DIRECTAMENTE DINERARIO - NO COMPROMETIDO",
			"AVAL TECNICO/PERFORMANCE - NO COMPROMETIDO", "GARANTIA LINE DE AVALES DISPONIBLES - NO USAR (3Q 2016)",
			"GARANTIA STANDBY LETTER OF CREDIT - NO USAR (3Q 2016)",
			"LINEA DE AVALES DISPONIBLES - NO COMPROMETIDO - NO USAR (3Q 2017)", "STANDBY LETTER OF CREDIT"));
	/**
	 * array que almacena instrumentos bonos
	 */
	@Getter
	private ArrayList<String> bonos = new ArrayList<>(
			Arrays.asList("BOND FORWARDS", "BOND FUTURES MMOO", "BOND OPTION MMOO", "BOND OPTIONS", "BOND REPOS",
					"BOND REPOS", "BOND REPOS UNDERLYING", "BOND SPOT", "COLLATERAL BOND", "COLLATERAL BOND UNDERLYING",
					"CORPORATE BONDS", "GOVERNMENT BOND LENDING FOP", "INFLATION SWAP PAYER BOND TYPE",
					"INFLATION SWAP RECEIVER BOND TYPE", "INITIAL MARGIN BOND", "INITIAL MARGIN BOND 3TH PARTY",
					"INITIAL MARGIN BOND UNDERLYING", "INITIAL MARGIN BOND UNDERLYING 3TH PARTY"));
	/**
	 * array que almacena instrumentos creditos
	 * documentarios
	 */
	@Getter
	private ArrayList<String> document = new ArrayList<>(Arrays.asList("APERTURA CREDITO DOCUMENTARIO",
			"CONFIRMACION CREDITO DOCUMENTARIO", "FINANCIACION CREDITO DOCUMENTARIO"));
	/**
	 * array que almacena instrumentos Importaciones/exportaciones
	 */
	@Getter
	private ArrayList<String> impExp = new ArrayList<>(Arrays.asList("FINANCIACION DE EXPORTACIONES - NO COMPROMETIDO",
			"FINANCIACION DE IMPORTACIONES - NO COMPROMETIDO"));
	/**
	 * array que almacena instrumentos Comex
	 */
	@Getter
	private ArrayList<String> comex = new ArrayList<>(
			Arrays.asList("OTRAS FINANCIACIONES COMEX - NO COMPROMETIDO - NO USAR (3Q 2016)",
					"OTRAS GARANTIAS COMEX  - NO USAR (3Q 2016)", "TRADE FINANCE - FORFAITING - NO COMPROMETIDO",
					"WAREHOUSING COMEX - NO COMPROMETIDO"));
	/**
	 * array que almacena instrumentos de
	 * creditos sindicados
	 */
	@Getter
	private ArrayList<String> sindi = new ArrayList<>(
			Arrays.asList("LINEA MULTIDEAL SINDICADO - COMPROMETIDO", "LINEA MULTIDEAL SINDICADO - NO COMPROMETIDO",
					"CREDITO SINDICADO - NO COMPROMETIDO", "CREDITO SINDICADO - COMPROMETIDO"));

	/**
	 * array que almacena instrumentos de confirming
	 */
	@Getter
	private ArrayList<String> confirm = new ArrayList<>(Arrays.asList("CONFIRMING - NO COMPROMETIDO"));

	/**
	 * array que almacena instrumentos descuentos
	 */
	@Getter
	private ArrayList<String> descuen = new ArrayList<>(
			Arrays.asList("DESCUENTOS - COMPROMETIDO", "DESCUENTOS - NO COMPROMETIDO"));
	
	/**
	 * array que almacena instrumentos factoring
	 */
	@Getter
	private ArrayList<String> factor = new ArrayList<>(Arrays.asList("FACTORING - NO COMPROMETIDO"));
	/**
	 * array que almacena instrumentos tarjetas de credito
	 */
	@Getter
	private ArrayList<String> tarjeta = new ArrayList<>(Arrays.asList("TARJETAS CREDITO - COMPROMETIDO"));
	
	
	
	/**
	 * contructor de la clase constantsUtils
	 */
	public ConstantsUtil() {
		// Constructor vacio para prevenir acceso ilegal
	}
}
